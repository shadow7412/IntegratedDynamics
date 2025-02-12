package org.cyclops.integrateddynamics.core.item;

import com.google.common.collect.Maps;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.common.MinecraftForge;
import org.cyclops.integrateddynamics.api.client.model.IVariableModelBaked;
import org.cyclops.integrateddynamics.api.evaluate.EvaluationException;
import org.cyclops.integrateddynamics.api.evaluate.expression.VariableAdapter;
import org.cyclops.integrateddynamics.api.evaluate.variable.IValue;
import org.cyclops.integrateddynamics.api.evaluate.variable.IValueType;
import org.cyclops.integrateddynamics.api.evaluate.variable.IVariable;
import org.cyclops.integrateddynamics.api.item.IVariableFacade;
import org.cyclops.integrateddynamics.api.item.IVariableFacadeHandler;
import org.cyclops.integrateddynamics.api.item.IVariableFacadeHandlerRegistry;
import org.cyclops.integrateddynamics.api.network.IPartNetwork;
import org.cyclops.integrateddynamics.core.evaluate.variable.ValueHelpers;
import org.cyclops.integrateddynamics.core.evaluate.variable.ValueTypeBoolean;
import org.cyclops.integrateddynamics.core.evaluate.variable.ValueTypes;
import org.cyclops.integrateddynamics.core.helper.L10NValues;
import org.cyclops.integrateddynamics.core.logicprogrammer.event.LogicProgrammerVariableFacadeCreatedEvent;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * The variable facade handler registry.
 * @author rubensworks
 */
public class VariableFacadeHandlerRegistry implements IVariableFacadeHandlerRegistry {

    private static VariableFacadeHandlerRegistry INSTANCE = new VariableFacadeHandlerRegistry();
    public static DummyVariableFacade DUMMY_FACADE = new DummyVariableFacade(L10NValues.VARIABLE_ERROR_INVALIDITEM);

    private final Map<String, IVariableFacadeHandler> handlers = Maps.newHashMap();

    private VariableFacadeHandlerRegistry() {

    }

    /**
     * @return The unique instance.
     */
    public static VariableFacadeHandlerRegistry getInstance() {
        return INSTANCE;
    }

    @Override
    public void registerHandler(IVariableFacadeHandler variableFacadeHandler) {
        handlers.put(variableFacadeHandler.getUniqueName().toString(), variableFacadeHandler);
    }

    @Override
    public IVariableFacade handle(ItemStack itemStack) {
        if(itemStack.isEmpty() || !itemStack.hasTag()) {
            return DUMMY_FACADE;
        }
        return handle(itemStack.getTag());
    }

    @Override
    public IVariableFacade handle(CompoundTag tagCompound) {
        if(tagCompound == null) {
            return DUMMY_FACADE;
        }
        if(!tagCompound.contains("_type", Tag.TAG_STRING)
                || !tagCompound.contains("_id", Tag.TAG_INT)) {
            return DUMMY_FACADE;
        }
        String type = tagCompound.getString("_type");
        int id = tagCompound.getInt("_id");
        IVariableFacadeHandler handler = getHandler(new ResourceLocation(type));
        if(handler != null) {
            return handler.getVariableFacade(id, tagCompound);
        }
        return DUMMY_FACADE;
    }

    @Nullable
    @Override
    public IVariableFacadeHandler getHandler(ResourceLocation type) {
        return handlers.get(type.toString());
    }

    @Override
    public Collection<String> getHandlerNames() {
        return handlers.keySet();
    }

    @Override
    public <F extends IVariableFacade> void write(CompoundTag tagCompound, F variableFacade, IVariableFacadeHandler<F> handler) {
        tagCompound.putString("_type", handler.getUniqueName().toString());
        tagCompound.putInt("_id", variableFacade.getId());
        handler.setVariableFacade(tagCompound, variableFacade);
    }

    @Override
    public <F extends IVariableFacade> ItemStack writeVariableFacadeItem(ItemStack itemStack, F variableFacade, IVariableFacadeHandler<F> variableFacadeHandler) {
        if(itemStack.isEmpty()) {
            return ItemStack.EMPTY;
        }
        itemStack = itemStack.copy();
        CompoundTag tag = itemStack.getOrCreateTag();
        this.write(tag, variableFacade, variableFacadeHandler);
        return itemStack;
    }

    @Override
    public <F extends IVariableFacade> ItemStack writeVariableFacadeItem(boolean generateId, ItemStack itemStack, IVariableFacadeHandler<F> variableFacadeHandler, IVariableFacadeFactory<F> variableFacadeFactory, @Nullable Player player, @Nullable BlockState blockState) {
        if(itemStack.isEmpty()) {
            return ItemStack.EMPTY;
        }
        itemStack = itemStack.copy();
        CompoundTag tag = itemStack.getOrCreateTag();
        F variableFacade = writeVariableFacade(generateId, itemStack, variableFacadeHandler, variableFacadeFactory);
        if (player != null) {
            MinecraftForge.EVENT_BUS.post(new LogicProgrammerVariableFacadeCreatedEvent(player, variableFacade, blockState));
        }
        this.write(tag, variableFacade, variableFacadeHandler);
        return itemStack;
    }

    @Override
    public <F extends IVariableFacade> F writeVariableFacade(boolean generateId, ItemStack itemStack, IVariableFacadeHandler<F> variableFacadeHandler, IVariableFacadeFactory<F> variableFacadeFactory) {
        if(itemStack.isEmpty()) {
            return null;
        }
        CompoundTag tag = itemStack.getOrCreateTag();
        IVariableFacade previousVariableFacade = this.handle(tag);
        F variableFacade;
        if(generateId && previousVariableFacade.getId() > -1) {
            variableFacade = variableFacadeFactory.create(previousVariableFacade.getId());
        } else {
            variableFacade = variableFacadeFactory.create(generateId);
        }
        return variableFacade;
    }

    @Override
    public <F extends IVariableFacade> ItemStack copy(boolean generateId, ItemStack itemStack) {
        ItemStack copy = itemStack.copy();
        int newId = generateId ? VariableFacadeBase.generateId() : -1;
        copy.getTag().putInt("_id", newId);
        return copy;
    }

    /**
     * Variable facade used for items that have no (valid) information on them.
     */
    public static class DummyVariableFacade extends VariableFacadeBase {

        private static final IVariable VARIABLE_TRUE = new VariableAdapter<ValueTypeBoolean.ValueBoolean>() {
            @Override
            public IValueType<ValueTypeBoolean.ValueBoolean> getType() {
                return ValueTypes.BOOLEAN;
            }

            @Override
            public ValueTypeBoolean.ValueBoolean getValue() throws EvaluationException {
                return ValueTypeBoolean.ValueBoolean.of(true);
            }
        };

        private final String unlocalizedError;

        public DummyVariableFacade(String unlocalizedError) {
            super(false);
            this.unlocalizedError = unlocalizedError;
        }

        @Override
        public <V extends IValue> IVariable<V> getVariable(IPartNetwork network) {
            return VARIABLE_TRUE;
        }

        @Override
        public boolean isValid() {
            return false;
        }

        @Override
        public void validate(IPartNetwork network, IValidator validator, IValueType containingValueType) {
            if (!ValueHelpers.correspondsTo(containingValueType, ValueTypes.BOOLEAN)) {
                validator.addError(Component.translatable(unlocalizedError));
            }
        }

        @Override
        public IValueType getOutputType() {
            return ValueTypes.CATEGORY_ANY;
        }

        @Override
        public void appendHoverText(List<Component> list, Level world) {

        }

        @Override
        public void addModelOverlay(IVariableModelBaked variableModelBaked, List<BakedQuad> quads, RandomSource random, ModelData modelData) {

        }
    }

}
