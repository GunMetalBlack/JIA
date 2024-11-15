package org.gunmetalblack.events.listeners;

import org.gunmetalblack.entity.Entity;
import org.gunmetalblack.events.JIAEventListener;
import org.gunmetalblack.events.JIAEventManager;
import org.gunmetalblack.events.JIAGenticEvent;

public class CollisionEventListener implements JIAEventListener<Entity> {
    private Entity collidedEntity = null;
    private boolean hasRecivedEvent = false;

    public Entity getCollidedEntityEventResult()
    {
        Entity toReturn = collidedEntity;
        collidedEntity = null;
        hasRecivedEvent = false;
        return toReturn;
    }

    @Override
    public void OnJIAEvent(JIAGenticEvent<Entity> event) {
        collidedEntity = event.getEvent();
        hasRecivedEvent = true;
    }

    public boolean hasRecivedEvent() {
        return hasRecivedEvent;
    }
}
