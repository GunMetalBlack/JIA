package org.gunmetalblack.events;

import org.gunmetalblack.entity.Entity;
import org.gunmetalblack.events.listeners.CollisionEventListener;

public class GlobalEventManager {
        public static final JIAEventManager<Entity> collisionEventManager = new JIAEventManager<>();

        public static final CollisionEventListener collisionEventListener = new CollisionEventListener();
}
