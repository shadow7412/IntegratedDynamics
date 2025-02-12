package org.cyclops.integrateddynamics.core.evaluate.variable;

import net.minecraft.ChatFormatting;
import org.cyclops.cyclopscore.helper.Helpers;
import org.cyclops.integrateddynamics.api.evaluate.EvaluationException;
import org.cyclops.integrateddynamics.api.evaluate.variable.IValue;
import org.cyclops.integrateddynamics.api.evaluate.variable.IValueType;
import org.cyclops.integrateddynamics.api.evaluate.variable.IValueTypeUniquelyNamed;
import org.cyclops.integrateddynamics.api.evaluate.variable.IVariable;

/**
 * Value type category with values that have a unique name.
 * @author rubensworks
 */
public class ValueTypeCategoryUniquelyNamed extends ValueTypeCategoryBase<IValue> {

    public ValueTypeCategoryUniquelyNamed() {
        super("uniquely_named", Helpers.RGBToInt(250, 10, 13), ChatFormatting.RED, IValue.class);
    }

    public String getUniqueName(IVariable a) throws EvaluationException {
        return ((IValueTypeUniquelyNamed) a.getType()).getUniqueName(a.getValue());
    }

    @Override
    public boolean correspondsTo(IValueType<?> valueType) {
        return super.correspondsTo(valueType) && valueType instanceof IValueTypeUniquelyNamed;
    }
}
