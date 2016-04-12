package com.weaselworks.util.state;

import java.util.*;

import com.weaselworks.util.*;


/**
 *  Utility class used to aid in the implementation of classes that need to maintain a list
 *  of {@link StateEntryListener StateEntryListeners} for notification. </p>
 */

class StateEntryListenerSupport<T extends State>
    extends ListenerSupport
{
    public
    StateEntryListenerSupport ()
    {
        return;
    }

    /**
     *  @see ListenerSupport#perform(java.util.EventListener, java.util.EventObject)
     */

    public
    void perform (final EventListener ler, final EventObject evt)
    {
        final StateEntryListener sler = (StateEntryListener) ler;
        final TransitionEvent tevt = (TransitionEvent) evt;
        sler.stateEntered (tevt);
        return;
    }
}

// EOF