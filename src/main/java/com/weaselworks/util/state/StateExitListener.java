package com.weaselworks.util.state;

import java.util.*;

/**
 *  An interface to be implemented by classes that wish to receive notification of {@link State}
 *  entry events.
 */

public interface StateExitListener<T extends State>
    extends EventListener
{
    /**
     *  Called to report that the current {@link State} has been exited.
     *
     *  @param tevt an event object carrying details about the original and new {@link State} of the
     *              associated {@link StateMachine}
     */

    public
    void stateExited (TransitionEvent tevt);
}

// EOF