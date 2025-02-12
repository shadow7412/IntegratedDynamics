package org.cyclops.integrateddynamics.core.evaluate.variable;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import lombok.ToString;
import net.minecraft.ResourceLocationException;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.cyclops.cyclopscore.helper.MinecraftHelpers;
import org.cyclops.integrateddynamics.api.advancement.criterion.ValuePredicate;
import org.cyclops.integrateddynamics.api.evaluate.variable.IValue;
import org.cyclops.integrateddynamics.api.evaluate.variable.IValueType;
import org.cyclops.integrateddynamics.api.evaluate.variable.IValueTypeNamed;
import org.cyclops.integrateddynamics.api.evaluate.variable.IValueTypeNullable;
import org.cyclops.integrateddynamics.api.evaluate.variable.IValueTypeUniquelyNamed;
import org.cyclops.integrateddynamics.core.logicprogrammer.ValueTypeLPElementBase;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.UUID;

/**
 * Value type with values that are itemstacks.
 * @author rubensworks
 */
public class ValueObjectTypeEntity extends ValueObjectTypeBase<ValueObjectTypeEntity.ValueEntity> implements
        IValueTypeNamed<ValueObjectTypeEntity.ValueEntity>, IValueTypeUniquelyNamed<ValueObjectTypeEntity.ValueEntity>,
        IValueTypeNullable<ValueObjectTypeEntity.ValueEntity> {

    public ValueObjectTypeEntity() {
        super("entity", ValueObjectTypeEntity.ValueEntity.class);
    }

    @Override
    public ValueEntity getDefault() {
        return ValueEntity.of((UUID) null);
    }

    @Override
    public MutableComponent toCompactString(ValueEntity value) {
        Optional<UUID> uuid = value.getUuid();
        if (uuid.isPresent()) {
            Optional<Entity> entity = value.getRawValue();
            if(entity.isPresent()) {
                Entity e = entity.get();
                if(e instanceof ItemEntity) {
                    return (MutableComponent) ((ItemEntity) e).getItem().getHoverName();
                } else {
                    return (MutableComponent) e.getName();
                }
            }
            return Component.literal("unknown");
        }
        return Component.literal("");
    }

    @Override
    public Tag serialize(ValueEntity value) {
        Optional<UUID> uuid = value.getUuid();
        if(uuid.isPresent()) {
            return StringTag.valueOf(uuid.get().toString());
        }
        return StringTag.valueOf("");
    }

    @Override
    public ValueEntity deserialize(Tag value) {
        try {
            return ValueEntity.of(UUID.fromString(value.getAsString()));
        } catch (IllegalArgumentException e) {}
        return ValueEntity.of((UUID) null);
    }

    @Override
    public String getName(ValueEntity a) {
        return toCompactString(a).getString();
    }

    @Override
    public boolean isNull(ValueEntity a) {
        return !a.getRawValue().isPresent();
    }

    @Override
    public ValueTypeLPElementBase createLogicProgrammerElement() {
        return null;
    }

    @Override
    public ValuePredicate<ValueEntity> deserializeValuePredicate(JsonObject element, @Nullable IValue value) {
        JsonElement jsonElement = element.get("entity");
        String entityTypeName = jsonElement != null && !jsonElement.isJsonNull() ? jsonElement.getAsString() : null;
        EntityType<? extends Entity> entityType = null;
        if (entityTypeName != null) {
            try {
                entityType = ForgeRegistries.ENTITY_TYPES.getValue(new ResourceLocation(entityTypeName));
            } catch (ResourceLocationException e) {
                throw new JsonSyntaxException("Invalid entity type name '" + entityTypeName + "'");
            }
            if (entityType == null) {
                throw new JsonSyntaxException("Could not find the entity type '" + entityTypeName + "'");
            }
        }
        return new ValueEntityPredicate(this, value, entityType);
    }

    @Override
    public String getUniqueName(ValueEntity value) {
        Optional<UUID> uuid = value.getUuid();
        if (uuid.isPresent()) {
            UUID id = uuid.get();
            String entityName = value.getRawValue()
                    .map(entity -> ForgeRegistries.ENTITY_TYPES.getKey(entity.getType()).toString())
                    .orElse("unknown");
            return id.toString() + " (" + entityName + ")";
        }
        return "";
    }

    @ToString
    public static class ValueEntity extends ValueBase {

        private final Optional<UUID> value;

        protected ValueEntity(@Nullable Entity value) {
            super(ValueTypes.OBJECT_ENTITY);
            this.value = value == null ? Optional.<UUID>empty() : Optional.of(value.getUUID());
        }

        private ValueEntity(@Nullable UUID entityUuid) {
            super(ValueTypes.OBJECT_ENTITY);
            this.value = Optional.ofNullable(entityUuid);
        }

        /**
         * @return The raw value in an optional holder.
         */
        public Optional<Entity> getRawValue() {
            Optional<UUID> uuid = getUuid();
            if (uuid.isPresent()) {
                Optional<Entity> optionalEntity = DistExecutor.callWhenOn(Dist.CLIENT, ()->()-> {
                    if (MinecraftHelpers.isClientSideThread()) {
                        for (Entity entity : Minecraft.getInstance().level.entitiesForRendering()) {
                            if (entity.getUUID().equals(uuid.get())) {
                                return Optional.of(entity);
                            }
                        }
                        return Optional.empty();
                    }
                    return null;
                });
                if (optionalEntity == null) {
                    for (ServerLevel world : ServerLifecycleHooks.getCurrentServer().getAllLevels()) {
                        Entity entity = world.getEntity(uuid.get());
                        if (entity != null) {
                            return Optional.of(entity);
                        }
                    }
                } else {
                    return optionalEntity;
                }
            }
            return Optional.empty();
        }

        public Optional<UUID> getUuid() {
            return value;
        }

        @Override
        public boolean equals(Object o) {
            if(o instanceof ValueEntity) {
                if (((ValueEntity) o).value.isPresent() && value.isPresent()) {
                    return ((ValueEntity) o).value.get().equals(value.get());
                } else if (!((ValueEntity) o).value.isPresent() && !value.isPresent()) {
                    return true;
                }
            }
            return false;
        }

        @Override
        public int hashCode() {
            return getType().hashCode() + (getRawValue().isPresent() ? getRawValue().get().hashCode() : 0);
        }

        public static ValueEntity of(@Nullable Entity entity) {
            return new ValueEntity(entity);
        }

        public static ValueEntity of(@Nullable UUID entityUuid) {
            return new ValueEntity(entityUuid);
        }

    }

    public static class ValueEntityPredicate extends ValuePredicate<ValueEntity> {

        private final EntityType<? extends Entity> entityType;

        public ValueEntityPredicate(@Nullable IValueType valueType, @Nullable IValue value, @Nullable EntityType<? extends Entity> entityType) {
            super(valueType, value);
            this.entityType = entityType;
        }

        @Override
        protected boolean testTyped(ValueEntity value) {
            return super.testTyped(value)
                    && (entityType == null
                        || (value.getRawValue().isPresent() && value.getRawValue().get().getType() == entityType));
        }
    }

}
