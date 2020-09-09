// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.thirst.event;

import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.entitySystem.event.Event;
import org.terasology.engine.logic.common.ActivateEvent;

/**
 * This event is sent to an entity to indicate that it has drunk a drink.
 */
public class DrinkConsumedEvent implements Event {
    private final EntityRef instigator;
    private final EntityRef target;

    public DrinkConsumedEvent(ActivateEvent event) {
        this.instigator = event.getInstigator();
        this.target = event.getTarget();
    }

    public EntityRef getInstigator() {
        return instigator;
    }

    public EntityRef getTarget() {
        return target;
    }
}
