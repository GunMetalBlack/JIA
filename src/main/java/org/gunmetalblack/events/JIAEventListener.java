package org.gunmetalblack.events;

public interface JIAEventListener <E>{
    void OnJIAEvent(JIAGenticEvent<E> event);
}
