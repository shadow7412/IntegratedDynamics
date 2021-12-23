package org.cyclops.integrateddynamics.core.network.diagnostics;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.cyclops.integrateddynamics.api.part.PartPos;
import org.cyclops.integrateddynamics.core.helper.PartHelpers;

import java.util.List;
import java.util.Set;

/**
 * @author rubensworks
 */
public class NetworkDiagnosticsPartOverlayRenderer {

    private static final NetworkDiagnosticsPartOverlayRenderer _INSTANCE = new NetworkDiagnosticsPartOverlayRenderer();
    private final Set<PartPos> partPositions = Sets.newHashSet();

    private NetworkDiagnosticsPartOverlayRenderer() {

    }

    public static NetworkDiagnosticsPartOverlayRenderer getInstance() {
        return _INSTANCE;
    }

    public synchronized void addPos(PartPos pos) {
        partPositions.add(pos);
    }

    public synchronized void removePos(PartPos pos) {
        partPositions.remove(pos);
    }

    public synchronized void clearPositions() {
        partPositions.clear();
    }

    public synchronized boolean hasPartPos(PartPos pos) {
        return partPositions.contains(pos);
    }

    @SubscribeEvent
    public void onRender(RenderWorldLastEvent event) {
        if (!partPositions.isEmpty()) {
            PlayerEntity player = Minecraft.getInstance().player;
            float partialTicks = event.getPartialTicks();

            double offsetX = player.xOld + (player.getX() - player.xOld) * (double) partialTicks;
            double offsetY = player.yOld + (player.getY() - player.yOld) * (double) partialTicks;
            double offsetZ = player.zOld + (player.getZ() - player.zOld) * (double) partialTicks;

            RenderSystem.enableBlend();
            RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            RenderSystem.lineWidth(6.0F);
            RenderSystem.disableTexture();
            RenderSystem.depthMask(false);

            List<PartPos> partList = Lists.newArrayList(partPositions);
            for (PartPos partPos : partList) {
                if (partPos.getPos().getWorldKey().location().equals(player.level.dimension().location()) && partPos.getPos().getBlockPos().distSqr(player.blockPosition()) < 10000) {
                    PartHelpers.PartStateHolder<?, ?> partStateHolder = PartHelpers.getPart(partPos);
                    final VoxelShape shape;
                    if (partStateHolder != null) {
                        shape = partStateHolder.getPart().getPartRenderPosition().getBoundingBox(partPos.getSide());
                    } else {
                        shape = VoxelShapes.BLOCK;
                    }

                    AxisAlignedBB bb = shape
                            .bounds()
                            .move(partPos.getPos().getBlockPos())
                            .move(-offsetX, -offsetY, -offsetZ)
                            .inflate(0.05, 0.05, 0.05)
                            .inflate(-0.05, -0.05, -0.05);
                    /*WorldRenderer.renderLineBox(event.getMatrixStack(), Minecraft.getInstance().getRenderTypeBuffers().getOutlineBufferSource().getBuffer(RenderType.getLines()),
                            bb, 1.0F, 0.2F, 0.1F, 0.8F);*/
                }
            }

            RenderSystem.depthMask(true);
            RenderSystem.enableTexture();
            RenderSystem.disableBlend();
        }
    }

}
