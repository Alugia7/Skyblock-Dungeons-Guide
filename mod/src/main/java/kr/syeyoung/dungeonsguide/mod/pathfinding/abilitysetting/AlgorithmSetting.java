package kr.syeyoung.dungeonsguide.mod.pathfinding.abilitysetting;

import lombok.*;
import net.minecraft.nbt.*;

import java.io.DataInputStream;
import java.io.IOException;

@Getter
@Data
@With
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class AlgorithmSetting implements Cloneable {
    
    private final boolean stonkDown;
    private final boolean stonkTeleport;
    private final boolean stonkEChest;
    private final boolean dungeonBreaker;

    private final boolean routeEtherwarp;

    private final int maxStonk;
    private final boolean enderpearl;
    private final boolean tntpearl;

    private final double etherwarpOffset;
    private final int etherwarpRadius;
    private final double etherwarpLeeway;


    public AlgorithmSetting(NBTTagCompound nbt) {
        if (nbt.getInteger("version") != 2) throw new IllegalArgumentException("Unexpected Algo Settings version: "+nbt.getInteger("version")+" / Expected: 2");


        this.stonkDown = nbt.getBoolean("stonkDown");
        this.stonkTeleport = nbt.getBoolean("stonkTeleport");
        this.stonkEChest = nbt.getBoolean("stonkEChest");
        this.routeEtherwarp = nbt.getBoolean("routeEtherwarp");
        this.maxStonk = nbt.getInteger("maxStonk");
        this.enderpearl = nbt.getBoolean("enderpearl");
        this.tntpearl = nbt.getBoolean("tntpearl");
        this.etherwarpOffset = nbt.getDouble("etherwarpOffset");
        this.etherwarpRadius = nbt.getInteger("etherwarpRadius");
        this.etherwarpLeeway = nbt.getDouble("etherwarpLeeway");
        this.dungeonBreaker = nbt.getBoolean("dungeonBreaker");
    }

    public AlgorithmSetting(boolean stonkDown, boolean stonkTeleport, boolean stonkEChest, boolean routeEtherwarp, int maxStonk, boolean enderpearl, boolean tntpearl, double etherwarpOffset, int etherwarpRadius, double etherwarpLeeway, boolean dungeonBreaker) {
        this.stonkDown = stonkDown;
        this.stonkTeleport = stonkTeleport;
        this.stonkEChest = stonkEChest;
        this.routeEtherwarp = routeEtherwarp;
        this.maxStonk = maxStonk;
        this.enderpearl = enderpearl;
        this.tntpearl = tntpearl;
        this.etherwarpOffset = etherwarpOffset;
        this.etherwarpRadius = etherwarpRadius;
        this.etherwarpLeeway = etherwarpLeeway;
        this.dungeonBreaker = dungeonBreaker;


    }


    public NBTTagCompound serializeToNBT() {
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setInteger("version", 2);

        nbt.setBoolean("stonkDown", stonkDown);
        nbt.setBoolean("stonkTeleport", stonkTeleport);
        nbt.setBoolean("stonkEChest", stonkEChest);
        nbt.setBoolean("routeEtherwarp", routeEtherwarp);
        nbt.setInteger("maxStonk", maxStonk);
        nbt.setBoolean("enderpearl", enderpearl);
        nbt.setBoolean("tntpearl", tntpearl);
        nbt.setDouble("etherwarpOffset", etherwarpOffset);
        nbt.setInteger("etherwarpRadius", etherwarpRadius);
        nbt.setDouble("etherwarpLeeway", etherwarpLeeway);
        nbt.setBoolean("dungeonBreaker", dungeonBreaker);
        return nbt;
    }

    public static AlgorithmSetting deserialize(DataInputStream dataInputStream) throws IOException {
        NBTTagCompound nbtTagCompound = CompressedStreamTools.read(dataInputStream, new NBTSizeTracker(10000));
        return new AlgorithmSetting(nbtTagCompound);
    }

    @Override
    public AlgorithmSetting clone() {
        try {
            AlgorithmSetting clone = (AlgorithmSetting) super.clone();
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
