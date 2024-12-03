package org.gunmetalblack.jiaframework.events;

public interface JIAEventListener <E>{
    void OnJIAEvent(JIAGenericEvent<E> event);
}
