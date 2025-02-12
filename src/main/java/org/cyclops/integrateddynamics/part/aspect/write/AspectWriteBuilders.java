package org.cyclops.integrateddynamics.part.aspect.write;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.Tag;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraftforge.event.level.NoteBlockEvent;
import net.minecraftforge.fluids.FluidStack;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.cyclops.commoncapabilities.api.capability.recipehandler.IRecipeDefinition;
import org.cyclops.integrateddynamics.api.evaluate.variable.IValue;
import org.cyclops.integrateddynamics.api.evaluate.variable.IValueType;
import org.cyclops.integrateddynamics.api.evaluate.variable.IVariable;
import org.cyclops.integrateddynamics.api.part.PartTarget;
import org.cyclops.integrateddynamics.api.part.aspect.property.IAspectProperties;
import org.cyclops.integrateddynamics.api.part.aspect.property.IAspectPropertyTypeInstance;
import org.cyclops.integrateddynamics.api.part.write.IPartStateWriter;
import org.cyclops.integrateddynamics.api.part.write.IPartTypeWriter;
import org.cyclops.integrateddynamics.core.evaluate.variable.*;
import org.cyclops.integrateddynamics.core.part.aspect.build.AspectBuilder;
import org.cyclops.integrateddynamics.core.part.aspect.build.IAspectValuePropagator;
import org.cyclops.integrateddynamics.core.part.aspect.build.IAspectWriteDeactivator;
import org.cyclops.integrateddynamics.core.part.aspect.property.AspectProperties;
import org.cyclops.integrateddynamics.core.part.aspect.property.AspectPropertyTypeInstance;
import org.cyclops.integrateddynamics.part.aspect.read.AspectReadBuilders;
import org.cyclops.integrateddynamics.part.aspect.write.redstone.IWriteRedstoneComponent;
import org.cyclops.integrateddynamics.part.aspect.write.redstone.WriteRedstoneComponent;

import java.util.Optional;

/**
 * Collection of aspect write builders and value propagators.
 * @author rubensworks
 */
public class AspectWriteBuilders {

    // --------------- Value type builders ---------------
    public static final AspectBuilder<ValueTypeBoolean.ValueBoolean, ValueTypeBoolean, Triple<PartTarget, IAspectProperties, ValueTypeBoolean.ValueBoolean>>
            BUILDER_BOOLEAN = getValue(AspectBuilder.forWriteType(ValueTypes.BOOLEAN));
    public static final AspectBuilder<ValueTypeInteger.ValueInteger, ValueTypeInteger, Triple<PartTarget, IAspectProperties, ValueTypeInteger.ValueInteger>>
            BUILDER_INTEGER = getValue(AspectBuilder.forWriteType(ValueTypes.INTEGER));
    public static final AspectBuilder<ValueTypeDouble.ValueDouble, ValueTypeDouble, Triple<PartTarget, IAspectProperties, ValueTypeDouble.ValueDouble>>
            BUILDER_DOUBLE = getValue(AspectBuilder.forWriteType(ValueTypes.DOUBLE));
    public static final AspectBuilder<ValueTypeString.ValueString, ValueTypeString, Triple<PartTarget, IAspectProperties, ValueTypeString.ValueString>>
            BUILDER_STRING = getValue(AspectBuilder.forWriteType(ValueTypes.STRING));
    public static final AspectBuilder<ValueTypeList.ValueList, ValueTypeList, Triple<PartTarget, IAspectProperties, ValueTypeList.ValueList>>
            BUILDER_LIST = getValue(AspectBuilder.forWriteType(ValueTypes.LIST));
    public static final AspectBuilder<ValueObjectTypeItemStack.ValueItemStack, ValueObjectTypeItemStack, Triple<PartTarget, IAspectProperties, ValueObjectTypeItemStack.ValueItemStack>>
            BUILDER_ITEMSTACK = getValue(AspectBuilder.forWriteType(ValueTypes.OBJECT_ITEMSTACK));
    public static final AspectBuilder<ValueObjectTypeFluidStack.ValueFluidStack, ValueObjectTypeFluidStack, Triple<PartTarget, IAspectProperties, ValueObjectTypeFluidStack.ValueFluidStack>>
            BUILDER_FLUIDSTACK = getValue(AspectBuilder.forWriteType(ValueTypes.OBJECT_FLUIDSTACK));
    public static final AspectBuilder<ValueTypeOperator.ValueOperator, ValueTypeOperator, Triple<PartTarget, IAspectProperties, ValueTypeOperator.ValueOperator>>
            BUILDER_OPERATOR = getValue(AspectBuilder.forWriteType(ValueTypes.OPERATOR));
    public static final AspectBuilder<ValueTypeNbt.ValueNbt, ValueTypeNbt, Triple<PartTarget, IAspectProperties, ValueTypeNbt.ValueNbt>>
            BUILDER_NBT = getValue(AspectBuilder.forWriteType(ValueTypes.NBT));
    public static final AspectBuilder<ValueObjectTypeRecipe.ValueRecipe, ValueObjectTypeRecipe, Triple<PartTarget, IAspectProperties, ValueObjectTypeRecipe.ValueRecipe>>
            BUILDER_RECIPE = getValue(AspectBuilder.forWriteType(ValueTypes.OBJECT_RECIPE));

    // --------------- Value type propagators ---------------
    public static final IAspectValuePropagator<Triple<PartTarget, IAspectProperties, ValueTypeBoolean.ValueBoolean>, Triple<PartTarget, IAspectProperties, Boolean>>
            PROP_GET_BOOLEAN = input -> Triple.of(input.getLeft(), input.getMiddle(), input.getRight().getRawValue());

    public static final IAspectValuePropagator<Triple<PartTarget, IAspectProperties,ValueTypeInteger.ValueInteger>, Triple<PartTarget, IAspectProperties, Integer>>
            PROP_GET_INTEGER = input -> Triple.of(input.getLeft(), input.getMiddle(), input.getRight().getRawValue());

    public static final IAspectValuePropagator<Triple<PartTarget, IAspectProperties, ValueTypeDouble.ValueDouble>, Triple<PartTarget, IAspectProperties, Double>>
            PROP_GET_DOUBLE = input -> Triple.of(input.getLeft(), input.getMiddle(), input.getRight().getRawValue());

    public static final IAspectValuePropagator<Triple<PartTarget, IAspectProperties, ValueTypeLong.ValueLong>, Triple<PartTarget, IAspectProperties, Long>>
            PROP_GET_LONG = input -> Triple.of(input.getLeft(), input.getMiddle(), input.getRight().getRawValue());

    public static final IAspectValuePropagator<Triple<PartTarget, IAspectProperties, ValueObjectTypeItemStack.ValueItemStack>, Triple<PartTarget, IAspectProperties, ItemStack>>
            PROP_GET_ITEMSTACK = input -> Triple.of(input.getLeft(), input.getMiddle(), input.getRight().getRawValue());

    public static final IAspectValuePropagator<Triple<PartTarget, IAspectProperties, ValueTypeString.ValueString>, Triple<PartTarget, IAspectProperties, String>>
            PROP_GET_STRING = input -> Triple.of(input.getLeft(), input.getMiddle(), input.getRight().getRawValue());

    public static final IAspectValuePropagator<Triple<PartTarget, IAspectProperties, ValueObjectTypeBlock.ValueBlock>, Triple<PartTarget, IAspectProperties, BlockState>>
            PROP_GET_BLOCK = input -> Triple.of(input.getLeft(), input.getMiddle(), input.getRight().getRawValue().orElse(null));

    public static final IAspectValuePropagator<Triple<PartTarget, IAspectProperties, ValueObjectTypeFluidStack.ValueFluidStack>, Triple<PartTarget, IAspectProperties, FluidStack>>
            PROP_GET_FLUIDSTACK = input -> Triple.of(input.getLeft(), input.getMiddle(), input.getRight().getRawValue());

    public static final IAspectValuePropagator<Triple<PartTarget, IAspectProperties, ValueTypeNbt.ValueNbt>, Triple<PartTarget, IAspectProperties, Optional<Tag>>>
            PROP_GET_NBT = input -> Triple.of(input.getLeft(), input.getMiddle(), input.getRight().getRawValue());

    public static final IAspectValuePropagator<Triple<PartTarget, IAspectProperties, ValueObjectTypeRecipe.ValueRecipe>, Triple<PartTarget, IAspectProperties, IRecipeDefinition>>
            PROP_GET_RECIPE = input -> Triple.of(input.getLeft(), input.getMiddle(), input.getRight().getRawValue().orElse(null));

    public static final class Audio {

        public static final IAspectPropertyTypeInstance<ValueTypeDouble, ValueTypeDouble.ValueDouble> PROP_VOLUME =
                new AspectPropertyTypeInstance<>(ValueTypes.DOUBLE, "aspect.aspecttypes.integrateddynamics.double.volume", AspectReadBuilders.VALIDATOR_DOUBLE_POSITIVE);
        public static final IAspectPropertyTypeInstance<ValueTypeDouble, ValueTypeDouble.ValueDouble> PROP_FREQUENCY =
                new AspectPropertyTypeInstance<>(ValueTypes.DOUBLE, "aspect.aspecttypes.integrateddynamics.double.frequency", AspectReadBuilders.VALIDATOR_DOUBLE_POSITIVE);
        public static final IAspectPropertyTypeInstance<ValueTypeInteger, ValueTypeInteger.ValueInteger> PROP_RANGE =
                new AspectPropertyTypeInstance<>(ValueTypes.INTEGER, "aspect.aspecttypes.integrateddynamics.integer.range", AspectReadBuilders.VALIDATOR_INTEGER_POSITIVE);
        public static final IAspectProperties PROPERTIES_NOTE = new AspectProperties(Sets.<IAspectPropertyTypeInstance>newHashSet(
                PROP_VOLUME
        ));
        public static final IAspectProperties PROPERTIES_SOUND = new AspectProperties(ImmutableList.<IAspectPropertyTypeInstance>of(
                PROP_VOLUME,
                PROP_FREQUENCY
        ));
        public static final IAspectProperties PROPERTIES_TEXT = new AspectProperties(ImmutableList.<IAspectPropertyTypeInstance>of(
                PROP_RANGE
        ));
        static {
            PROPERTIES_NOTE.setValue(PROP_VOLUME, ValueTypeDouble.ValueDouble.of(3D));
            PROPERTIES_SOUND.setValue(PROP_VOLUME, ValueTypeDouble.ValueDouble.of(3D));
            PROPERTIES_SOUND.setValue(PROP_FREQUENCY, ValueTypeDouble.ValueDouble.of(1D));
            PROPERTIES_TEXT.setValue(PROP_RANGE, ValueTypeInteger.ValueInteger.of(32));
        }

        public static final IAspectValuePropagator<Triple<PartTarget, IAspectProperties, Pair<NoteBlockInstrument, Integer>>, Void> PROP_SET = input -> {
            IAspectProperties properties = input.getMiddle();
            BlockPos pos = input.getLeft().getTarget().getPos().getBlockPos();

            NoteBlockInstrument instrument = input.getRight().getLeft();
            int eventParam = input.getRight().getRight();
            if(eventParam >= 0 && eventParam <= 24) {
                Level world = input.getLeft().getTarget().getPos().getLevel(false);
                if (world != null) { // If a block falls in a world when there's no one around, does it make any sound?
                    NoteBlockEvent.Play e = new NoteBlockEvent.Play(world, pos, world.getBlockState(pos), eventParam, instrument);
                    if (!net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(e)) {
                        float f = (float) Math.pow(2.0D, (double) (eventParam - 12) / 12.0D);
                        float volume = (float) properties.getValue(PROP_VOLUME).getRawValue();
                        world.playSound(null, pos, instrument.getSoundEvent(), SoundSource.RECORDS, volume, f);
                    }
                }
            }
            return null;
        };

        public static IAspectValuePropagator<Triple<PartTarget, IAspectProperties, Integer>, Triple<PartTarget, IAspectProperties, Pair<NoteBlockInstrument, Integer>>>
            propWithInstrument(final NoteBlockInstrument instrument) {
            return input -> Triple.of(input.getLeft(), input.getMiddle(), Pair.of(instrument, input.getRight()));
        }

        public static final AspectBuilder<ValueTypeInteger.ValueInteger, ValueTypeInteger, Triple<PartTarget, IAspectProperties, Integer>>
                BUILDER_INTEGER = AspectWriteBuilders.BUILDER_INTEGER.appendKind("audio").handle(PROP_GET_INTEGER);
        public static final AspectBuilder<ValueTypeInteger.ValueInteger, ValueTypeInteger, Triple<PartTarget, IAspectProperties, Integer>>
                BUILDER_INTEGER_INSTRUMENT = BUILDER_INTEGER.appendKind("instrument").withProperties(PROPERTIES_NOTE);
        public static final AspectBuilder<ValueTypeString.ValueString, ValueTypeString, Triple<PartTarget, IAspectProperties, String>>
                BUILDER_STRING = AspectWriteBuilders.BUILDER_STRING.appendKind("audio").handle(PROP_GET_STRING);

    }

    public static final class Effect {

        public static final IAspectPropertyTypeInstance<ValueTypeDouble, ValueTypeDouble.ValueDouble> PROP_OFFSET_X =
                new AspectPropertyTypeInstance<>(ValueTypes.DOUBLE, "aspect.aspecttypes.integrateddynamics.double.offset_x");
        public static final IAspectPropertyTypeInstance<ValueTypeDouble, ValueTypeDouble.ValueDouble> PROP_OFFSET_Y =
                new AspectPropertyTypeInstance<>(ValueTypes.DOUBLE, "aspect.aspecttypes.integrateddynamics.double.offset_y");
        public static final IAspectPropertyTypeInstance<ValueTypeDouble, ValueTypeDouble.ValueDouble> PROP_OFFSET_Z =
                new AspectPropertyTypeInstance<>(ValueTypes.DOUBLE, "aspect.aspecttypes.integrateddynamics.double.offset_z");
        public static final IAspectPropertyTypeInstance<ValueTypeInteger, ValueTypeInteger.ValueInteger> PROP_PARTICLES =
                new AspectPropertyTypeInstance<>(ValueTypes.INTEGER, "aspect.aspecttypes.integrateddynamics.integer.particles", AspectReadBuilders.VALIDATOR_INTEGER_POSITIVE);
        public static final IAspectPropertyTypeInstance<ValueTypeDouble, ValueTypeDouble.ValueDouble> PROP_SPREAD_X =
                new AspectPropertyTypeInstance<>(ValueTypes.DOUBLE, "aspect.aspecttypes.integrateddynamics.double.spread_x", AspectReadBuilders.VALIDATOR_DOUBLE_POSITIVE);
        public static final IAspectPropertyTypeInstance<ValueTypeDouble, ValueTypeDouble.ValueDouble> PROP_SPREAD_Y =
                new AspectPropertyTypeInstance<>(ValueTypes.DOUBLE, "aspect.aspecttypes.integrateddynamics.double.spread_y", AspectReadBuilders.VALIDATOR_DOUBLE_POSITIVE);
        public static final IAspectPropertyTypeInstance<ValueTypeDouble, ValueTypeDouble.ValueDouble> PROP_SPREAD_Z =
                new AspectPropertyTypeInstance<>(ValueTypes.DOUBLE, "aspect.aspecttypes.integrateddynamics.double.spread_z", AspectReadBuilders.VALIDATOR_DOUBLE_POSITIVE);
        public static final IAspectPropertyTypeInstance<ValueTypeBoolean, ValueTypeBoolean.ValueBoolean> PROP_FORCE =
                new AspectPropertyTypeInstance<>(ValueTypes.BOOLEAN, "aspect.aspecttypes.integrateddynamics.boolean.force_particle");
        public static final IAspectProperties PROPERTIES_PARTICLE = new AspectProperties(ImmutableList.<IAspectPropertyTypeInstance>of(
                PROP_OFFSET_X,
                PROP_OFFSET_Y,
                PROP_OFFSET_Z,
                PROP_PARTICLES,
                PROP_SPREAD_X,
                PROP_SPREAD_Y,
                PROP_SPREAD_Z
        ));

        static {
            PROPERTIES_PARTICLE.setValue(PROP_OFFSET_X, ValueTypeDouble.ValueDouble.of(0.5D));
            PROPERTIES_PARTICLE.setValue(PROP_OFFSET_Z, ValueTypeDouble.ValueDouble.of(0.5D));
            PROPERTIES_PARTICLE.setValue(PROP_OFFSET_Y, ValueTypeDouble.ValueDouble.of(0.5D));
            PROPERTIES_PARTICLE.setValue(PROP_PARTICLES, ValueTypeInteger.ValueInteger.of(1));
            PROPERTIES_PARTICLE.setValue(PROP_SPREAD_X, ValueTypeDouble.ValueDouble.of(0.0D));
            PROPERTIES_PARTICLE.setValue(PROP_SPREAD_Y, ValueTypeDouble.ValueDouble.of(0.0D));
            PROPERTIES_PARTICLE.setValue(PROP_SPREAD_Z, ValueTypeDouble.ValueDouble.of(0.0D));
        }

        public static final AspectBuilder<ValueTypeDouble.ValueDouble, ValueTypeDouble, Triple<PartTarget, IAspectProperties, Double>>
                BUILDER_DOUBLE = AspectWriteBuilders.BUILDER_DOUBLE.appendKind("effect").handle(PROP_GET_DOUBLE);
        public static final AspectBuilder<ValueTypeDouble.ValueDouble, ValueTypeDouble, Triple<PartTarget, IAspectProperties, Double>>
                BUILDER_DOUBLE_PARTICLE = BUILDER_DOUBLE.withProperties(PROPERTIES_PARTICLE);

    }

    public static final class Redstone {

        private static final IWriteRedstoneComponent WRITE_REDSTONE_COMPONENT = new WriteRedstoneComponent();

        public static final IAspectPropertyTypeInstance<ValueTypeBoolean, ValueTypeBoolean.ValueBoolean> PROP_STRONG_POWER =
                new AspectPropertyTypeInstance<>(ValueTypes.BOOLEAN, "aspect.aspecttypes.integrateddynamics.boolean.strong_power");
        public static final IAspectPropertyTypeInstance<ValueTypeInteger, ValueTypeInteger.ValueInteger> PROP_PULSE_EMIT_VALUE =
                new AspectPropertyTypeInstance<>(ValueTypes.INTEGER, "aspect.aspecttypes.integrateddynamics.integer.pulse_emit_value",
                        (v) -> v.getRawValue() >= 0 && v.getRawValue() <= 15);
        public static final IAspectProperties PROPERTIES_REDSTONE = new AspectProperties(ImmutableList.<IAspectPropertyTypeInstance>of(
                PROP_STRONG_POWER
        ));
        public static final IAspectProperties PROPERTIES_REDSTONE_PULSE = new AspectProperties(ImmutableList.<IAspectPropertyTypeInstance>of(
                PROP_STRONG_POWER,
                PROP_PULSE_EMIT_VALUE
        ));

        static {
            PROPERTIES_REDSTONE.setValue(PROP_STRONG_POWER, ValueTypeBoolean.ValueBoolean.of(false));

            PROPERTIES_REDSTONE_PULSE.setValue(PROP_STRONG_POWER, ValueTypeBoolean.ValueBoolean.of(false));
            PROPERTIES_REDSTONE_PULSE.setValue(PROP_PULSE_EMIT_VALUE, ValueTypeInteger.ValueInteger.of(15));
        }

        public static final IAspectValuePropagator<Triple<PartTarget, IAspectProperties, Integer>, Void> PROP_SET = input -> {
            boolean strongPower = input.getMiddle().getValue(PROP_STRONG_POWER).getRawValue();
            WRITE_REDSTONE_COMPONENT.setRedstoneLevel(input.getLeft(), input.getRight(), strongPower);
            return null;
        };
        public static final IAspectValuePropagator<Triple<PartTarget, IAspectProperties, Integer>, Void> PROP_SET_PULSE = input -> {
            PartTarget target = input.getLeft();
            boolean strongPower = input.getMiddle().getValue(PROP_STRONG_POWER).getRawValue();
            int pulseValue = input.getRight();
            int emitLevel = input.getMiddle().getValue(PROP_PULSE_EMIT_VALUE).getRawValue();
            int lastPulseValue = WRITE_REDSTONE_COMPONENT.getLastPulseValue(target);
            if (lastPulseValue != pulseValue) {
                WRITE_REDSTONE_COMPONENT.setLastPulseValue(target, pulseValue);
                WRITE_REDSTONE_COMPONENT.setRedstoneLevel(target, emitLevel, strongPower);
            } else {
                WRITE_REDSTONE_COMPONENT.setRedstoneLevel(target, 0, strongPower);
            }
            return null;
        };

        public static final IAspectWriteDeactivator DEACTIVATOR = new IAspectWriteDeactivator() {
            @Override
            public <P extends IPartTypeWriter<P, S>, S extends IPartStateWriter<P>> void onDeactivate(P partType, PartTarget target, S state) {
                WRITE_REDSTONE_COMPONENT.deactivate(target);
            }
        };

        public static final AspectBuilder<ValueTypeBoolean.ValueBoolean, ValueTypeBoolean, Triple<PartTarget, IAspectProperties, Boolean>>
                BUILDER_BOOLEAN = AspectWriteBuilders.BUILDER_BOOLEAN.appendKind("redstone").handle(PROP_GET_BOOLEAN).appendDeactivator(DEACTIVATOR).withProperties(PROPERTIES_REDSTONE);
        public static final AspectBuilder<ValueTypeInteger.ValueInteger, ValueTypeInteger, Triple<PartTarget, IAspectProperties, Integer>>
                BUILDER_INTEGER = AspectWriteBuilders.BUILDER_INTEGER.appendKind("redstone").handle(PROP_GET_INTEGER).appendDeactivator(DEACTIVATOR).withProperties(PROPERTIES_REDSTONE);

    }

    public static <V extends IValue, T extends IValueType<V>> AspectBuilder<V, T, Triple<PartTarget, IAspectProperties, V>> getValue(AspectBuilder<V, T, Triple<PartTarget, IAspectProperties, IVariable<V>>> builder) {
        return builder.handle(input -> Triple.of(input.getLeft(), input.getMiddle(), input.getRight().getValue()));
    }

}
