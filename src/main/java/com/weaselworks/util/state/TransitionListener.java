package com.weaselworks.util.state;

import java.util.*;

/**
 *  An interface to be implemented by objects interested in registered for notification
 *  of {@link StateMachine} transitions.
 */

public interface TransitionListener
    extends EventListener
{
    /**
     *  A callback interface called to report that a transition has occurred in the
     *  associated {@link StateMachine}. The event object carries details about the
     *  start and end {@link State States} associated with the transition.
     *
     *  @param evt carries addition information about the transition
     */

    void transitionOccurred (final TransitionEvent evt);
}

// EOF