package org.cyclops.integrateddynamics.modcompat.refinedstorage.aspect;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import org.cyclops.cyclopscore.datastructure.DimPos;
import org.cyclops.cyclopscore.persist.nbt.INBTProvider;
import org.cyclops.cyclopscore.persist.nbt.NBTPersist;
import org.cyclops.integrateddynamics.core.evaluate.variable.ValueObjectTypeItemStack;
import org.cyclops.integrateddynamics.core.evaluate.variable.ValueTypeListProxyBase;
import org.cyclops.integrateddynamics.core.evaluate.variable.ValueTypes;
import org.cyclops.integrateddynamics.core.helper.CableHelpers;
import org.cyclops.integrateddynamics.modcompat.refinedstorage.RefinedStorageModCompat;
import refinedstorage.api.network.INetworkMaster;
import refinedstorage.api.storage.item.IItemStorage;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

/**
 * A list proxy for a network's grouped inventory. inventory at a certain position.
 */
public class ValueTypeListProxyPositionedNetworkMasterInventory extends ValueTypeListProxyBase<ValueObjectTypeItemStack, ValueObjectTypeItemStack.ValueItemStack> implements INBTProvider {

    @NBTPersist
    private DimPos pos;

    public ValueTypeListProxyPositionedNetworkMasterInventory() {
        this(null);
    }

    public ValueTypeListProxyPositionedNetworkMasterInventory(DimPos pos) {
        super(RefinedStorageModCompat.POSITIONED_MASTERINVENTORY.getName(), ValueTypes.OBJECT_ITEMSTACK);
        this.pos = pos;
    }

    protected Optional<INetworkMaster> getNetworkMaster() {
        return Optional.fromNullable(CableHelpers.getInterface(pos, INetworkMaster.class));
    }

    protected Optional<List<ItemStack>> getInventory() {
        return getNetworkMaster().transform(new Function<INetworkMaster, List<ItemStack>>() {
            @Nullable
            @Override
            public List<ItemStack> apply(@Nullable INetworkMaster networkMaster) {
                if (networkMaster == null) {
                    return null;
                }
                List<List<ItemStack>> itemStacksLists = Lists.transform(networkMaster.getItemStorage().getStorages(), new Function<IItemStorage, List<ItemStack>>() {
                    @Nullable
                    @Override
                    public List<ItemStack> apply(@Nullable IItemStorage itemStorage) {
                        return itemStorage.getItems();
                    }
                });
                return new LazyCompositeList<>(itemStacksLists);
            }
        });
    }

    @Override
    public int getLength() {
        return getInventory().or(Collections.<ItemStack>emptyList()).size();
    }

    @Override
    public ValueObjectTypeItemStack.ValueItemStack get(int index) {
        return ValueObjectTypeItemStack.ValueItemStack.of(getInventory().or(Collections.<ItemStack>emptyList()).get(index));
    }

    @Override
    public void writeGeneratedFieldsToNBT(NBTTagCompound tag) {

    }

    @Override
    public void readGeneratedFieldsFromNBT(NBTTagCompound tag) {

    }
}
