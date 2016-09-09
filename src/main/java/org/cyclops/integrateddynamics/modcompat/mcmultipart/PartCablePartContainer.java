package org.cyclops.integrateddynamics.modcompat.mcmultipart;

import mcmultipart.multipart.IMultipart;
import mcmultipart.multipart.IMultipartContainer;
import mcmultipart.multipart.MultipartHelper;
import mcmultipart.multipart.PartSlot;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.cyclops.integrateddynamics.api.network.IPartNetwork;
import org.cyclops.integrateddynamics.api.part.IPartContainerFacade;
import org.cyclops.integrateddynamics.api.part.IPartState;
import org.cyclops.integrateddynamics.api.part.IPartType;
import org.cyclops.integrateddynamics.capability.DefaultPartContainer;

/**
 * Part container for a {@link PartCable}.
 * @author rubensworks
 */
public class PartCablePartContainer extends DefaultPartContainer {

    private final PartCable partCable;

    public PartCablePartContainer(PartCable partCable) {
        this.partCable = partCable;
    }

    protected PartPartType getPartPart(EnumFacing side) {
        IMultipartContainer container = partCable.getContainer();
        if(container != null) {
            IMultipart multipart = container.getPartInSlot(PartSlot.getFaceSlot(side));
            if (multipart instanceof PartPartType) {
                return (PartPartType) multipart;
            }
        }
        return null;
    }

    @Override
    public <P extends IPartType<P, S>, S extends IPartState<P>> boolean canAddPart(EnumFacing side, IPartType<P, S> part) {
        return super.canAddPart(side, part) && MultipartHelper.canAddPart(getWorld(), getPos(), new PartPartType(side, part));
    }

    @Override
    public <P extends IPartType<P, S>, S extends IPartState<P>> void setPart(final EnumFacing side, IPartType<P, S> part, IPartState<P> partState) {
        final PartPartType partPart = new PartPartType(side, part);
        super.setPart(side, part, partState);
        MultipartHelper.addPart(getWorld(), getPos(), partPart);
    }

    @Override
    public IPartType removePart(EnumFacing side, EntityPlayer player) {
        PartPartType partPartType = getPartPart(side);
        IPartType removed = super.removePart(side, player);
        if (removed != null) {
            partCable.getContainer().removePart(partPartType);
        }
        return removed;
    }

    @Override
    protected void markDirty() {
        partCable.markDirty();
    }

    @Override
    protected void sendUpdate() {
        partCable.sendUpdate();
    }

    @Override
    protected World getWorld() {
        return partCable.getWorld();
    }

    @Override
    protected BlockPos getPos() {
        return partCable.getPos();
    }

    @Override
    protected IPartNetwork getNetwork() {
        return partCable.getNetwork();
    }

    @Override
    protected IPartContainerFacade getPartContainerFacade() {
        return partCable;
    }

}
