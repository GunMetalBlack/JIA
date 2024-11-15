package org.gunmetalblack.events;
/*
This is inspired by minecraft's event system hopefully implemented correctly XD
* */
public class JIAGenticEvent <E>{
    private E event;

    public JIAGenticEvent(E event)
    {
        this.event = event;
    }

    public E getEvent()
    {
        return this.event;
    }
}
