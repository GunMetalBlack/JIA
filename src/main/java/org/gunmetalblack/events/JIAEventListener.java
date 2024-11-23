package org.gunmetalblack.events;

public interface JIAEventListener <E>{
    void OnJIAEvent(JIAGenericEvent<E> event);
}
