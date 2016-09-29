package org.cyclops.integrateddynamics.core.network;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import lombok.Getter;
import lombok.Setter;
import org.cyclops.integrateddynamics.api.network.INetwork;
import org.cyclops.integrateddynamics.api.network.IPositionedAddonsNetwork;
import org.cyclops.integrateddynamics.api.part.PartPos;

import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

/**
 * A network that can hold prioritized positions.
 * @author rubensworks
 */
public class PositionedAddonsNetwork implements IPositionedAddonsNetwork {

    @Getter
    @Setter
    private INetwork network;
    private TreeSet<PrioritizedPartPos> positions = Sets.newTreeSet();

    @Override
    public Set<PrioritizedPartPos> getPositions() {
        return ImmutableSet.copyOf(positions);
    }

    @Override
    public boolean addPosition(PartPos pos, int priority) {
        return positions.add(PrioritizedPartPos.of(pos, priority));
    }

    @Override
    public void removePosition(PartPos pos) {
        Iterator<PrioritizedPartPos> it = positions.iterator();
        while (it.hasNext()) {
            if (it.next().getPartPos().equals(pos)) {
                it.remove();
            }
        }
    }
}
