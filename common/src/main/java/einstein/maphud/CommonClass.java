package einstein.maphud;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import me.fzzyhmstrs.fzzy_config.api.ConfigApiJava;
import me.fzzyhmstrs.fzzy_config.api.RegisterType;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.level.saveddata.maps.MapId;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;

public class CommonClass {

    private static final ResourceLocation MAP_BACKGROUND = ResourceLocation.withDefaultNamespace("textures/map/map_background.png");
    private static final ResourceLocation MAP_BACKGROUND_CHECKERBOARD = ResourceLocation.withDefaultNamespace("textures/map/map_background_checkerboard.png");
    public static final ModConfigs CONFIGS = ConfigApiJava.registerAndLoadConfig(ModConfigs::new, RegisterType.CLIENT);

    public static void init() {
    }

    public static void renderMapHUD(GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
        Minecraft minecraft = Minecraft.getInstance();
        Player player = minecraft.player;
        if (player == null) {
            return;
        }

        ClientLevel level = minecraft.level;
        if (level == null) {
            return;
        }

        if (!CONFIGS.visibleInAllPerspectives && minecraft.options.getCameraType().isFirstPerson()) {
            return;
        }

        MapId mapId = getMapID(player);
        if (mapId != null) {
            MapItemSavedData mapData = MapItem.getSavedData(mapId, level);
            PoseStack poseStack = guiGraphics.pose();
            MultiBufferSource.BufferSource buffer = guiGraphics.bufferSource();

            poseStack.pushPose();
            poseStack.scale(CONFIGS.mapScale.get(), CONFIGS.mapScale.get(), 0);

            int packedLight = LightTexture.FULL_BRIGHT;
            VertexConsumer consumer = buffer.getBuffer(RenderType.text(mapData == null ? MAP_BACKGROUND : MAP_BACKGROUND_CHECKERBOARD));
            Matrix4f matrix4f = poseStack.last().pose();

            consumer.addVertex(matrix4f, 0, 141.9F, 0).setColor(-1).setUv(0, 1).setLight(packedLight);
            consumer.addVertex(matrix4f, 141.9F, 141.9F, 0).setColor(-1).setUv(1, 1).setLight(packedLight);
            consumer.addVertex(matrix4f, 141.9F, 0, 0).setColor(-1).setUv(1, 0).setLight(packedLight);
            consumer.addVertex(matrix4f, 0, 0, 0).setColor(-1).setUv(0, 0).setLight(packedLight);

            if (mapData != null) {
                poseStack.translate(7, 7, 0);
                minecraft.gameRenderer.getMapRenderer().render(poseStack, buffer, mapId, mapData, false, packedLight);
                guiGraphics.flush();
            }
            poseStack.popPose();
        }
    }

    @Nullable
    private static MapId getMapID(Player player) {
        for (InteractionHand hand : InteractionHand.values()) {
            ItemStack stack = player.getItemInHand(hand);
            if (stack.isEmpty()) {
                continue;
            }

            MapId mapId = stack.get(DataComponents.MAP_ID);
            if (mapId != null) {
                return mapId;
            }
        }
        return null;
    }
}
