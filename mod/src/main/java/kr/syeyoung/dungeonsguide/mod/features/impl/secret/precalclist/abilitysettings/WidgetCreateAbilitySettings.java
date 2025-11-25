package kr.syeyoung.dungeonsguide.mod.features.impl.secret.precalclist.abilitysettings;

import kr.syeyoung.dungeonsguide.mod.pathfinding.abilitysetting.AlgorithmSetting;
import kr.syeyoung.dungeonsguide.mod.gui.BindableAttribute;
import kr.syeyoung.dungeonsguide.mod.gui.DomElement;
import kr.syeyoung.dungeonsguide.mod.gui.elements.popups.PopupMgr;
import kr.syeyoung.dungeonsguide.mod.gui.xml.AnnotatedImportOnlyWidget;
import kr.syeyoung.dungeonsguide.mod.gui.xml.annotations.Bind;
import kr.syeyoung.dungeonsguide.mod.gui.xml.annotations.On;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.util.ResourceLocation;

import java.util.function.Predicate;

public class WidgetCreateAbilitySettings extends AnnotatedImportOnlyWidget {

    @Bind(variableName = "txtEtherwarp")
    public final BindableAttribute<String> _etherwarp = new BindableAttribute<>(String.class);


    @Bind(variableName = "pickaxeButton")
    public final BindableAttribute<DomElement> pickaxeButton = new BindableAttribute<>(DomElement.class);


    @Bind(variableName = "txtMaxEtherwarp")
    public final BindableAttribute<String> txtMaxEtherwarp = new BindableAttribute<>(String.class, "");
    @Bind(variableName = "txtEtherwarpOffset")
    public final BindableAttribute<String> txtEtherwarpOffset = new BindableAttribute<>(String.class, "");
    @Bind(variableName = "txtEtherwarpLeeway")
    public final BindableAttribute<String> txtEtherwarpLeeway = new BindableAttribute<>(String.class, "");

    @Bind(variableName = "maxEtherwarp")
    public final BindableAttribute<Integer> maxEtherwarp = new BindableAttribute<>(Integer.class, 0);
    @Bind(variableName = "etherwarpOffset")
    public final BindableAttribute<Double> etherwarpOffset = new BindableAttribute<>(Double.class, 0.4);
    @Bind(variableName = "etherwarpLeeway")
    public final BindableAttribute<Double> etherwarpLeeway = new BindableAttribute<>(Double.class, 0.0625);


    @Bind(variableName = "etherwarp")
    public final BindableAttribute<Boolean> etherwarp = new BindableAttribute<>(Boolean.class);
    @Bind(variableName = "stair")
    public final BindableAttribute<Boolean> stair = new BindableAttribute<>(Boolean.class);
    @Bind(variableName = "teleportdown")
    public final BindableAttribute<Boolean> teleportdown = new BindableAttribute<>(Boolean.class);
    @Bind(variableName = "enderchest")
    public final BindableAttribute<Boolean> enderchest = new BindableAttribute<>(Boolean.class);
    @Bind(variableName = "tntpearl")
    public final BindableAttribute<Boolean> tntpearl = new BindableAttribute<>(Boolean.class);
    @Bind(variableName = "enderpearl")
    public final BindableAttribute<Boolean> enderpearl = new BindableAttribute<>(Boolean.class);
    @Bind(variableName = "stonkLength")
    public final BindableAttribute<String> stonkLength = new BindableAttribute<>(String.class);
    @Bind(variableName = "dungeonBreaker")
    public final BindableAttribute<Boolean> dungeonBreaker = new BindableAttribute<>(Boolean.class);

    @Bind(variableName = "offsetValidator")
    public final BindableAttribute offsetValidator = new BindableAttribute(Predicate.class, (Predicate<String>) val -> {
        double dbl = Double.parseDouble(val);
        return 0 <= dbl && dbl <= 0.5;
    });
    @Bind(variableName = "etherwarpValidator")
    public final BindableAttribute etherwarpValidator = new BindableAttribute(Predicate.class, (Predicate<String>) val -> {
        int dbl = Integer.parseInt(val);
        return 0 < dbl && dbl <= 99;
    });

    public WidgetCreateAbilitySettings(AlgorithmSetting defaultAlgorithm) {
        super(new ResourceLocation("dungeonsguide:gui/features/precalclist/abilityedit/abilitycreate.gui"));

        txtEtherwarpLeeway.addOnUpdate((old, neu) -> {
            etherwarpLeeway.setValue(Double.parseDouble(neu));
        });
        txtEtherwarpOffset.addOnUpdate((old, neu) -> {
            etherwarpOffset.setValue(Double.parseDouble(neu));
        });
        txtMaxEtherwarp.addOnUpdate((old, neu) -> {
            maxEtherwarp.setValue(Integer.parseInt(neu));
        });

        etherwarp.setValue(defaultAlgorithm.isRouteEtherwarp());
        stair.setValue(defaultAlgorithm.isStonkDown());
        teleportdown.setValue(defaultAlgorithm.isStonkTeleport());
        enderchest.setValue(defaultAlgorithm.isStonkEChest());
        tntpearl.setValue(defaultAlgorithm.isTntpearl());
        enderpearl.setValue(defaultAlgorithm.isEnderpearl());
        stonkLength.setValue(defaultAlgorithm.getMaxStonk()+"");

        txtMaxEtherwarp.setValue(String.valueOf(defaultAlgorithm.getEtherwarpRadius()));
        txtEtherwarpOffset.setValue(String.valueOf(defaultAlgorithm.getEtherwarpOffset()));
        txtEtherwarpLeeway.setValue(String.valueOf(defaultAlgorithm.getEtherwarpLeeway()));

        etherwarp.addOnUpdate((old, neu) -> Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("gui.button.press"), 1.0F)));
        stair.addOnUpdate((old, neu) -> Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("gui.button.press"), 1.0F)));
        teleportdown.addOnUpdate((old, neu) -> Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("gui.button.press"), 1.0F)));
        enderchest.addOnUpdate((old, neu) -> Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("gui.button.press"), 1.0F)));
        tntpearl.addOnUpdate((old, neu) -> Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("gui.button.press"), 1.0F)));
        enderpearl.addOnUpdate((old, neu) -> Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("gui.button.press"), 1.0F)));
    }
    

    @On(functionName = "create")
    public void create() {
        Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("gui.button.press"), 1.0F));

        AlgorithmSetting algorithmSetting = new AlgorithmSetting(
                stair.getValue(),
                teleportdown.getValue(),
                enderchest.getValue(),
                etherwarp.getValue(),
                Integer.parseInt(stonkLength.getValue()),
                enderpearl.getValue(),
                tntpearl.getValue(),
                etherwarpOffset.getValue(),
                maxEtherwarp.getValue(),
                etherwarpLeeway.getValue(),
                dungeonBreaker.getValue()
        );
        PopupMgr.getPopupMgr(getDomElement()).closePopup(algorithmSetting);
    }
}
