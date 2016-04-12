package com.weaselworks.util.state;

import java.util.*;

import com.weaselworks.util.*;


/**
 *  Utility class used to aid in the implementation of classes that need to maintain a list
 *  of {@link TransitionListener TransitionListeners} for notification. </p>
 */

class TransitionListenerSupport<T extends State>
    extends ListenerSupport
{
    public
    TransitionListenerSupport ()
    {
        return;
    }

    /**
     *  @see ListenerSupport#perform
     */

    public
    void perform (EventListener ler, EventObject evt)
    {
        final TransitionListener tler = (TransitionListener) ler;
        final TransitionEvent tevt = (TransitionEvent) evt;

        tler.transitionOccurred (tevt);
        return;
    }
}

// EOF