package org.gunmetalblack.jiaframework.events;
/*
This is inspired by minecraft's forges event system hopefully implemented correctly XD
* */
public class JIAGenericEvent<E>{
    private E event;

    public JIAGenericEvent(E event)
    {
        this.event = event;
    }

    public E getEvent()
    {
        return this.event;
    }
}
