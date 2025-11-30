package kr.syeyoung.dungeonsguide.mod.pathfinding.precalculation;

import kr.syeyoung.dungeonsguide.mod.DungeonsGuide;
import kr.syeyoung.dungeonsguide.mod.pathfinding.abilitysetting.AlgorithmSettingRegistry;
import lombok.Getter;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;

public class PathfindPrecalculationRegistry {

    @Getter
    private List<PathfindPrecalculation> loaded = new ArrayList<>();
    private Map<String, List<PathfindPrecalculation>> byId = new HashMap<>();
    private Map<String, PathfindPrecalculation> byFile = new HashMap<>();
    @Getter
    private Map<UUID, List<PathfindPrecalculation>> byRoom = new HashMap<>();
    private Map<String, List<PathfindPrecalculation>> byHash = new HashMap<>();
    private Map<String, List<PathfindPrecalculation>> byId2 = new HashMap<>();

    @Getter
    private static PathfindPrecalculationRegistry INSTANCE;

    private Thread watcherThread;
    private WatchService watchService;

    // Shutdown singleton safely
    public static void shutdown() {
        if (INSTANCE != null) {
            INSTANCE.stopWatcher();
            INSTANCE.clearAll();
            INSTANCE = null;
        }
    }

    private void stopWatcher() {
        try {
            if (watcherThread != null) watcherThread.interrupt();
        } catch (Throwable ignored) {}
        try {
            if (watchService != null) watchService.close();
        } catch (Throwable ignored) {}
    }

    private void clearAll() {
        loaded.clear();
        byId.clear();
        byFile.clear();
        byRoom.clear();
        byHash.clear();
        byId2.clear();
    }

    public PathfindPrecalculationRegistry(File dir) throws IOException {
        if (INSTANCE != null) {
            INSTANCE.stopWatcher();
            INSTANCE.clearAll();
        }
        INSTANCE = this;

        loadAll(dir);

        // Initialize WatchService
        this.watchService = FileSystems.getDefault().newWatchService();

        // Watcher thread
        watcherThread = new Thread(DungeonsGuide.THREAD_GROUP, () -> {
            try {
                WatchEvent.Kind<?>[] kinds = {
                        StandardWatchEventKinds.ENTRY_CREATE,
                        StandardWatchEventKinds.ENTRY_DELETE,
                        StandardWatchEventKinds.ENTRY_MODIFY
                };

                // Register all directories recursively
                Files.walkFileTree(dir.toPath(), new SimpleFileVisitor<Path>() {
                    @Override
                    public FileVisitResult preVisitDirectory(Path d, BasicFileAttributes attrs) throws IOException {
                        d.register(watchService, kinds);
                        return FileVisitResult.CONTINUE;
                    }
                });

                while (!Thread.currentThread().isInterrupted()) {
                    WatchKey key;
                    try {
                        key = watchService.take();
                    } catch (InterruptedException e) {
                        break;
                    }

                    Path parent = (Path) key.watchable();

                    for (WatchEvent<?> event : key.pollEvents()) {
                        Path relative = (Path) event.context();
                        Path full = parent.resolve(relative);

                        if (Files.isDirectory(full) && event.kind() == StandardWatchEventKinds.ENTRY_CREATE) {
                            full.register(watchService, kinds);
                            continue;
                        }

                        if (!full.toString().endsWith(".pfres")) continue;

                        if (event.kind() == StandardWatchEventKinds.ENTRY_CREATE) {
                            register(new PathfindPrecalculation(full.toFile()));
                        } else if (event.kind() == StandardWatchEventKinds.ENTRY_DELETE) {
                            unregister(getByFile(full.toFile().getAbsolutePath()));
                        } else if (event.kind() == StandardWatchEventKinds.ENTRY_MODIFY) {
                            unregister(getByFile(full.toFile().getAbsolutePath()));
                            register(new PathfindPrecalculation(full.toFile()));
                        }
                    }
                    key.reset();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }, "DG-PFRES-Watcher");

        watcherThread.setDaemon(true);
        watcherThread.start();
    }

    public void register(PathfindPrecalculation precalculation) {
        byId.computeIfAbsent(precalculation.getId(), k -> new ArrayList<>()).add(precalculation);
        byId2.computeIfAbsent(precalculation.getTargetId(), k -> new ArrayList<>()).add(precalculation);
        byHash.computeIfAbsent(precalculation.getTargetHash(), k -> new ArrayList<>()).add(precalculation);
        byRoom.computeIfAbsent(precalculation.getRoomUID(), k -> new ArrayList<>()).add(precalculation);
        byFile.put(precalculation.getFile(), precalculation);
        loaded.add(precalculation);

        AlgorithmSettingRegistry.registerAlgorithmSetting(precalculation.getAlgorithmSetting());
    }

    public void unregister(PathfindPrecalculation precalculation) {
        if (precalculation == null) return;

        byId.getOrDefault(precalculation.getId(), Collections.emptyList()).remove(precalculation);
        byId2.getOrDefault(precalculation.getTargetId(), Collections.emptyList()).remove(precalculation);
        byHash.getOrDefault(precalculation.getTargetHash(), Collections.emptyList()).remove(precalculation);
        byRoom.getOrDefault(precalculation.getRoomUID(), Collections.emptyList()).remove(precalculation);
        byFile.remove(precalculation.getFile());
        loaded.remove(precalculation);
    }

    public void loadAll(File dir) throws IOException {
        clearAll();
        Files.walk(dir.toPath(), FileVisitOption.FOLLOW_LINKS).forEach(path -> {
            if (!path.getFileName().toString().endsWith(".pfres")) return;
            try {
                register(new PathfindPrecalculation(path.toFile()));
            } catch (Exception e) {
                System.out.println("Failed to load: " + path.getFileName());
                e.printStackTrace();
            }
        });
    }

    public PathfindPrecalculation getById(String id) {
        List<PathfindPrecalculation> list = byId.get(id);
        return list == null || list.isEmpty() ? null : list.get(0);
    }

    public PathfindPrecalculation getByTargetId(String targetid) {
        List<PathfindPrecalculation> list = byId2.get(targetid);
        return list == null || list.isEmpty() ? null : list.get(0);
    }

    public List<PathfindPrecalculation> getsById(String id) {
        return byId.getOrDefault(id, Collections.emptyList());
    }

    public List<PathfindPrecalculation> getsByTargetId(String targetid) {
        return byId2.getOrDefault(targetid, Collections.emptyList());
    }

    public List<PathfindPrecalculation> getsByHash(String hash) {
        return byHash.getOrDefault(hash, Collections.emptyList());
    }

    public List<PathfindPrecalculation> getByRoom(UUID roomUID) {
        return byRoom.getOrDefault(roomUID, Collections.emptyList());
    }

    public PathfindPrecalculation getByFile(String file) {
        return byFile.get(file);
    }
}