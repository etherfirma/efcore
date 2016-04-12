package com.weaselworks.util.state;

import java.util.*;

import com.weaselworks.util.*;


/**
 *  Utility class used to aid in the implementation of classes that need to maintain a list
 *  of {@link StateExitListener StateExitListeners} for notification. </p>
 */

class StateExitListenerSupport<T extends State>
    extends ListenerSupport
{
    public
    StateExitListenerSupport ()
    {
        return;
    }

    /**
     *  @see ListenerSupport#perform(java.util.EventListener, java.util.EventObject)
     */

    public
    void perform (final EventListener ler, final EventObject evt)
    {
        final StateExitListener sler = (StateExitListener) ler;
        final TransitionEvent tevt = (TransitionEvent) evt;
        sler.stateExited (tevt);
        return;
    }
}

// EOF