package com.weaselworks.util.state;

import java.util.*;

/**
 *  An event object representing the occurrence of a {@link Transition} within a {@link StateMachine}. </p>
 *
 *  Each {@link TransitionEvent} contains a reference to the originating or 'start' {@link State} and a reference
 *  to the target or 'end' {@link State}. Transition events are sent to all {@link StateEntryListener
 *  StateEntryListeners}, {@link TransitionListener TransitionListeners}, and {@link StateExitListener
 *  StateExitListeners} during state machine operation. </p>
 *
 *   Note that When a {@link StateMachine} is first started a transition from no
 *  {@link State} to the initial state occurs. In this case any {@link StateEntryListener StateEntryListeners}
 *  attached to the initial state will receive a {@link TransitionEvent} in which the {@link #getStartState start
 *  state} in null. </p>
 *
 *  @see com.weaselworks.util.state.StateEntryListener
 *  @see com.weaselworks.util.state.StateExitListener
 *  @see com.weaselworks.util.state.TransitionListener
 */

@SuppressWarnings ("serial")
public class TransitionEvent
    extends EventObject
{
    public
    TransitionEvent (final State startState, final State endState)
    {
        super (endState.getStateMachine ());
        setStartState (startState);
        setEndState (endState);
        return;
    }

    protected State startState;

    public
    State getStartState ()
    {
        return this.startState;
    }

    protected
    void setStartState (final State startState)
    {
        this.startState = startState;
        return;
    }

    protected State endState;

    public
    State getEndState ()
    {
        return this.endState;
    }

    protected
    void setEndState (final State endState)
    {
        this.endState = endState;
        return;
    }

    protected String bounceState;
    public String getBounceState () { return this.bounceState; }
    public void setBounceState (final String bounceState) { this.bounceState = bounceState; return; }
}

// EOF