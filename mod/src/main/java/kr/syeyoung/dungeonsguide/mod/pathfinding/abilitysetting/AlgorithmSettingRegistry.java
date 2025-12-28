package kr.syeyoung.dungeonsguide.mod.pathfinding.abilitysetting;

import java.util.HashSet;
import java.util.Set;

public class AlgorithmSettingRegistry {
    private static final Set<AlgorithmSetting> ALGORITHM_SETTINGS = new HashSet<>();

    public static final AlgorithmSetting STANDARD_DEFAULT_ALGORITHM_SETTING = new AlgorithmSetting(false, false, false, true, 14, false, false, 0.4, 61, 0.0625, true);

    static {
        registerAlgorithmSetting(STANDARD_DEFAULT_ALGORITHM_SETTING);
    }
    public static void registerAlgorithmSetting(AlgorithmSetting algorithmSetting) {
        if (algorithmSetting == null) throw new IllegalArgumentException("Algorithm Setting is null!");
        ALGORITHM_SETTINGS.add(algorithmSetting);
    }

    public static void unregisterAlgorithmSetting(AlgorithmSetting algorithmSetting) {
        if (algorithmSetting == null) return;

        // Never remove the default
        if (algorithmSetting == STANDARD_DEFAULT_ALGORITHM_SETTING) return;

        ALGORITHM_SETTINGS.remove(algorithmSetting);
    }

    public static Set<AlgorithmSetting> getAlgorithmSettings() {
        return ALGORITHM_SETTINGS;
    }
}
