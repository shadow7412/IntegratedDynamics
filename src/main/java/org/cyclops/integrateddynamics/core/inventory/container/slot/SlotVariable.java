package org.cyclops.integrateddynamics.core.inventory.container.slot;

import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import org.cyclops.cyclopscore.inventory.slot.SlotSingleItem;
import org.cyclops.integrateddynamics.Reference;
import org.cyclops.integrateddynamics.RegistryEntries;

/**
 * Slot for a variable item.
 * @author rubensworks
 */
public class SlotVariable extends SlotSingleItem {

    public static ResourceLocation VARIABLE_EMPTY = new ResourceLocation(Reference.MOD_ID, "slot/variable_empty");

    /**
     * Make a new instance.
     *
     * @param inventory The inventory this slot will be in.
     * @param index     The index of this slot.
     * @param x         X coordinate.
     * @param y         Y coordinate.
     */
    public SlotVariable(IInventory inventory, int index, int x, int y) {
        super(inventory, index, x, y, RegistryEntries.ITEM_VARIABLE);
        DistExecutor.callWhenOn(Dist.CLIENT, () -> () -> setBackground(AtlasTexture.LOCATION_BLOCKS, SlotVariable.VARIABLE_EMPTY));
    }
}
