package com.weaselworks.util.state;

import java.util.*;

/**
 *  Represents a finite state machine containing {@link State States} and a set of allowable
 *  {@link Transition Transitions} between those states. </p>
 *
 *  An example {@link StateMachine} representing the human life-cycle: </p>
 *
 *  <blockquote><pre>
 *  // Create a new 'lifecycle' state machine
 *
 *  final StateMachine fsm = new StateMachine ();
 *  final State birth = new State ("birth", fsm);
 *  final State school = new State ("school", fsm);
 *  final State work = new State ("work", fsm);
 *  final State death = new State ("death", fsm);
 *  fsm.setInitialState (birth);
 *
 *  // Add the permissible transitions
 *
 *  birth.addTransition (school);
 *  birth.addTransition (death);
 *  //birth.addTransition (work);  // third-world mode
 *  school.addTransition (work);
 *  school.addTransition (death);
 *  work.addTransition (death);
 *
 *  // Start the state machine
 *
 *  fsm.start ();
 *
 *  // Execute some transitions (starts in birth)
 *
 *  fsm.transition (school);
 *  fsm.transition (work);
 *  fsm.transition (death);
 *  </pre></blockquote>
 */

public class StateMachine
{
    public
    StateMachine ()
    {
        return;
    }

    protected State initialState;

    public
    State getInitialState ()
    {
        return this.initialState;
    }

    public
    void setInitialState (final String name)
    {
        final State state = getState (name);
        if (state == null) {
            throw new IllegalArgumentException ("State '" + name + "' unknown.");
        }
        setInitialState (state);
        return;
    }

    public
    void setInitialState (final State state)
    {
        if (state.getStateMachine () != this) {
            throw new IllegalArgumentException ("State not from this machine.");
        }
        this.initialState = state;
        return;
    }

    protected boolean started = false;

    public
    boolean isStarted ()
    {
        return this.started;
    }

    public
    void start ()
    {
        // Make sure we're not already started.

        if (isStarted ()) {
            throw new IllegalStateException ("Already started.");
        }

        // Initialize the current state from the specified initial state

        if (getInitialState () == null) {
            throw new IllegalStateException ("No initial state specified.");
        }
        final State state = getInitialState ();
        if (state == null) {
            throw new IllegalStateException ("No initial state specified.");
        }
        setCurrentState (state);
        this.started = true;

        // Send a TransitionEvent to any StateEntryListeners registered with the
        // state

        final TransitionEvent tevt = new TransitionEvent (null, state);
        state.notifyEntryListeners (tevt);
        if (tevt.getBounceState () != null) {
            transition (tevt.getBounceState ());
        }
        return;
    }

    protected State currentState;

    public
    State getCurrentState ()
    {
        return this.currentState;
    }

    public
    void setCurrentState (final State currentState)
    {
        this.currentState = currentState;
        return;
    }

    public
    void transition (final State newState)
    {
        if (newState.getStateMachine () != this) {
            throw new IllegalArgumentException ("State not in this machine.");
        }
        transition (newState.getName ());
        return;
    }
    
    /**
     * Transitions to the specified state iff it isn't already in that state. 
     * 
     * @param newState
     */
    
    public
    void transitionIfNotAlready (final String newState)
    {
    	System.err.println ("transitionIfNotAlready (" + newState + ")"); 
    	if (! getCurrentState ().getName ().equals (newState)) { 
    		transition (newState); 
    	}
    	return; 
    }

    public
    void transition (final String newState)
    {
        // Make sure the state machine is started

        if (! isStarted()) {
            throw new IllegalStateException ("State machine not started.");
        }

        // Make the transition via the current state

        final State state = getCurrentState ();
        final Transition trans = state.getTransition (newState);

        if (trans == null) {
            throw new IllegalStateException ("No transition to state '" + newState + "'.");
        }

        final State endState = trans.getEndState ();
        final TransitionEvent tevt = new TransitionEvent (state, endState);

        state.exiting (tevt);
        trans.notifyTransitionListeners (tevt);
        setCurrentState (endState);
        endState.entering (tevt);
        if (tevt.getBounceState () != null) {
            transition (tevt.getBounceState ());
        }
        return;
    }

    protected Map<String, State> states = new HashMap<String, State> ();

    public
    void addStates (final State... states)
    {
        for (final State state : states) {
            addState (state);
        }
        return;
    }

    public
    void addState (final State state)
    {
        if (containsState (state.getName ())) {
            throw new IllegalStateException ("State '" + state.getName () + "' already exists.");
        }
        states.put (state.getName (), state);
        state.setStateMachine (this);
        return;
    }

    public
    boolean containsState (final String name)
    {
        return getState (name) != null;
    }

    public
    State getState (final String name)
    {
        return (State) states.get (name);
    }

    public
    Iterator<String> getStates ()
    {
        return states.keySet ().iterator ();
    }
}

// EOF