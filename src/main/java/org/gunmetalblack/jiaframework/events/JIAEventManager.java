package org.gunmetalblack.jiaframework.events;

import java.util.ArrayList;
import java.util.List;

public class JIAEventManager<E> {

    private final List<JIAEventListener<E>> listeners = new ArrayList<>();

    // Register a listener
    public void addEventListener(JIAEventListener<E> listener) {
        listeners.add(listener);
    }

    // Remove a listener
    public void removeEventListener(JIAEventListener<E> listener) {
        listeners.remove(listener);
    }

    // Execute an event to notify all listeners
    public void ExecuteEvent(E source) {
        JIAGenericEvent<E> event = new JIAGenericEvent<>(source);
        for (JIAEventListener<E> listener : listeners) {
            listener.OnJIAEvent(event);
        }
    }
}

