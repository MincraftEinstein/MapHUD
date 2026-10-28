package einstein.maphud;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.datafixers.util.Pair;
import einstein.maphud.mixin.AbstractContainerScreenAccessor;
import me.fzzyhmstrs.fzzy_config.api.ConfigApiJava;
import me.fzzyhmstrs.fzzy_config.api.RegisterType;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.level.saveddata.maps.MapId;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MapHUD {

    public static final String MOD_ID = "maphud";
    public static final String MOD_NAME = "MapHUD";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAME);
    private static final ResourceLocation MAP_BACKGROUND = ResourceLocation.withDefaultNamespace("textures/map/map_background.png");
    private static final ResourceLocation MAP_BACKGROUND_CHECKERBOARD = ResourceLocation.withDefaultNamespace("textures/map/map_background_checkerboard.png");
    private static final ModConfigs CONFIGS = ConfigApiJava.registerAndLoadConfig(ModConfigs::new, RegisterType.CLIENT);
    private static boolean KEY_PRESSED = false;
    private static Pair<MapId, ItemStack> PERSISTENT_MAP_ID = null;
    private static MapId SELECTED_MAP_ID = null;

    // TODO logo, maybe like a stylized pixel-art of the hud, but really focused in on a map in the corner
    public static void init() {
    }

    public static void tick(Minecraft minecraft) {
        Player player = minecraft.player;
        if (minecraft.level == null || player == null) {
            KEY_PRESSED = false;
            PERSISTENT_MAP_ID = null;
            SELECTED_MAP_ID = null;
            return;
        }

        if (CONFIGS.selectSlotKey.isPressed()) {
            if (!KEY_PRESSED) {
                KEY_PRESSED = true;
                keyPressed(minecraft, player);
            }
        }
        else {
            KEY_PRESSED = false;
        }

        if (PERSISTENT_MAP_ID != null) {
            if (!contains(player.inventoryMenu, PERSISTENT_MAP_ID.getSecond())) {
                PERSISTENT_MAP_ID = null;
            }
        }

        for (InteractionHand hand : InteractionHand.values()) {
            MapId mapId = getMapId(player.getItemInHand(hand));
            if (mapId != null) {
                SELECTED_MAP_ID = mapId;
                return;
            }
        }

        SELECTED_MAP_ID = null;
    }

    private static boolean contains(AbstractContainerMenu menu, ItemStack stack) {
        if (stack.isEmpty()) {
            return false;
        }

        if (ItemStack.isSameItemSameComponents(menu.getCarried(), stack)) {
            return true;
        }

        for (Slot slot : menu.slots) {
            if (ItemStack.isSameItemSameComponents(slot.getItem(), stack)) {
                return true;
            }
        }
        return false;
    }

    private static void keyPressed(Minecraft minecraft, Player player) {
        if (minecraft.screen instanceof AbstractContainerScreenAccessor accessor) {
            Window window = minecraft.getWindow();
            MouseHandler mouseHandler = minecraft.mouseHandler;
            double mouseX = mouseHandler.xpos() * window.getGuiScaledWidth() / window.getScreenWidth();
            double mouseY = mouseHandler.ypos() * window.getGuiScaledHeight() / window.getScreenHeight();

            Slot slot = accessor.maphud$findSlot(mouseX, mouseY);
            if (slot != null && slot.container == player.getInventory()) {
                if (togglePersistent(slot.getItem())) {
                    return;
                }
            }
        }
        else if (minecraft.screen == null) {
            for (InteractionHand hand : InteractionHand.values()) {
                if (togglePersistent(player.getItemInHand(hand))) {
                    return;
                }
            }
        }
        PERSISTENT_MAP_ID = null;
    }

    private static boolean togglePersistent(ItemStack stack) {
        MapId mapId = getMapId(stack);
        if (mapId != null) {
            if (PERSISTENT_MAP_ID != null && mapId == PERSISTENT_MAP_ID.getFirst()) {
                PERSISTENT_MAP_ID = null;
                return true;
            }

            stack = stack.copy();
            PERSISTENT_MAP_ID = new Pair<>(mapId, stack);
            return true;
        }
        return false;
    }

    @Nullable
    private static MapId getMapId(ItemStack stack) {
        if (stack.isEmpty()) {
            return null;
        }

        return stack.get(DataComponents.MAP_ID);
    }

    public static void renderMapHUD(GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
        Minecraft minecraft = Minecraft.getInstance();
        ClientLevel level = minecraft.level;

        if (level == null) {
            return;
        }

        MapId mapId = SELECTED_MAP_ID != null ? SELECTED_MAP_ID : (PERSISTENT_MAP_ID != null ? PERSISTENT_MAP_ID.getFirst() : null);
        if (mapId != null) {
            if (!CONFIGS.visibleInAllPerspectives && minecraft.options.getCameraType().isFirstPerson()) {
                return;
            }

            MapItemSavedData mapData = MapItem.getSavedData(mapId, level);
            PoseStack poseStack = guiGraphics.pose();
            MultiBufferSource.BufferSource buffer = guiGraphics.bufferSource();
            float scale = CONFIGS.mapScale.get() / 2F;
            int bgSize = 142;
            float bgSizeScaled = bgSize * scale;

            poseStack.pushPose();
            poseStack.translate(Math.round(CONFIGS.mapXPos.get() * (guiGraphics.guiWidth() - bgSizeScaled)), Math.round(CONFIGS.mapYPos.get() * (guiGraphics.guiHeight() - bgSizeScaled)), 0);
            poseStack.scale(scale, scale, -1);

            int packedLight = LightTexture.FULL_BRIGHT;
            VertexConsumer consumer = buffer.getBuffer(RenderType.text(mapData == null ? MAP_BACKGROUND : MAP_BACKGROUND_CHECKERBOARD));
            Matrix4f matrix4f = poseStack.last().pose();

            if (CONFIGS.renderMapBackground) {
                consumer.addVertex(matrix4f, 0, bgSize, 0).setColor(-1).setUv(0, 1).setLight(packedLight);
                consumer.addVertex(matrix4f, bgSize, bgSize, 0).setColor(-1).setUv(1, 1).setLight(packedLight);
                consumer.addVertex(matrix4f, bgSize, 0, 0).setColor(-1).setUv(1, 0).setLight(packedLight);
                consumer.addVertex(matrix4f, 0, 0, 0).setColor(-1).setUv(0, 0).setLight(packedLight);
                poseStack.translate(7, 7, 0);
            }

            if (mapData != null) {
                minecraft.gameRenderer.getMapRenderer().render(poseStack, buffer, mapId, mapData, false, packedLight);
                guiGraphics.flush();
            }
            poseStack.popPose();
        }
    }
}
