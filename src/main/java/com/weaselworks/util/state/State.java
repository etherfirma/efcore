package com.weaselworks.util.state;

import java.util.*;

/**
 *  Represents a distinct state in a {@link StateMachine}. Each {@link State} has a set
 *  of allowable {@link Transition Transitions} through which the state may be changed. </p>
 *
 *  A {@link State} can have a set of {@link StateEntryListener StateEntryListeners}
 *   and/or {@link StateExitListener StateExitListeners} that are notified whenever the State is entered or exited.
 *
 *  @see StateMachine
 *  @see Transition
 */

public class State
{
    public
    State (final String name, final StateMachine fsm)
    {
        setName (name);
        setStateMachine (fsm); 
        return;
    }

    protected String name;

    public
    String getName ()
    {
        return this.name;
    }

    private
    void setName (final String name)
    {
        assert name != null;
        assert this.name == null;

        this.name = name;
        return;
    }

    protected StateMachine stateMachine;

    public
    StateMachine getStateMachine ()
    {
        return this.stateMachine;
    }

    public
    void setStateMachine (final StateMachine stateMachine)
    {
        assert stateMachine != null;
        assert this.stateMachine == null;

        this.stateMachine = stateMachine;
        return;
    }

    protected StateEntryListenerSupport entryListeners;

    public
    void addEntryListeners (final StateEntryListener... lers)
    {
        for (final StateEntryListener ler : lers) {
            addEntryListener (ler);
        }
        return;
    }

    /**
     *
     * @param ler
     */

    public
    void addEntryListener (final StateEntryListener ler)
    {
        assert ler != null;

        if (entryListeners == null) {
            entryListeners = new StateEntryListenerSupport ();
        }
        entryListeners.addListener (ler);
        return;
    }

    public
    void removeEntryListener (final StateEntryListener... lers)
    {
        for (final StateEntryListener ler: lers) {
            removeEntryListener (ler);
        }
        return;
    }

    /**
     *
     * @param ler
     */

    public
    void removeEntryListener (final StateEntryListener ler)
    {
        assert ler != null;

        if (entryListeners != null) {
            entryListeners.removeListener (ler);
        }
        return;
    }

    /**
     *
     * @param tevt
     */

    protected
    void notifyEntryListeners (final TransitionEvent tevt)
    {
        assert tevt != null;

        if (entryListeners != null) {
            entryListeners.perform (tevt);
        }
        return;
    }

    protected StateExitListenerSupport exitListeners;

    public
    void addExitListeners (final StateExitListener... lers)
    {
        for (final StateExitListener ler : lers) {
            addExitListener (ler);
        }
        return;
    }

    /**
     *
     * @param ler
     */

    public
    void addExitListener (final StateExitListener ler)
    {
        assert ler != null;

        if (exitListeners == null) {
            exitListeners = new StateExitListenerSupport ();
        }
        exitListeners.addListener (ler);
        return;
    }

    public
    void removeExitListeners (final StateExitListener... lers)
    {
        for (final StateExitListener ler : lers) {
            removeExitListener (ler);
        }
        return;
    }

    /**
     *
     * @param ler
     */

    public
    void removeExitListener (final StateExitListener ler)
    {
        assert ler != null;

        if (exitListeners != null) {
            exitListeners.removeListener (ler);
        }
        return;
    }

    /**
     *
     * @param tevt
     */

    protected
    void notifyExitListeners (final TransitionEvent tevt)
    {
        assert tevt != null;

        if (exitListeners != null) {
            exitListeners.perform (tevt);
        }
        return;
    }

    protected Map<String, Transition> transitions = new HashMap<String, Transition> ();

    public
    List<Transition> addTransitions (final State... states)
    {
        final List<Transition> transitions = new ArrayList<Transition>();
        for (final State state : states) {
            transitions.add (addTransition (state));
        }
        return transitions;
    }

    /**
     *
     * @param targetState
     */

    public
    Transition addTransition (final State targetState)
    {
        assert targetState != null;

        // Make sure we don't already have one

        Transition trans = getTransition (targetState.getName ());

        if (trans != null) {
            throw new IllegalArgumentException ("Transition to state '" + targetState + "' exists.");
        }

        // Make sure the target state is in our state machine

        if (targetState.getStateMachine () != getStateMachine ()) {
            throw new IllegalArgumentException ("Target state not in this state machine.");
        }

        // Create a new transition object

        trans = new Transition (this, targetState);
        transitions.put (targetState.getName (), trans);
        return trans;
    }

    /**
     *
     * @param targetState
     * @return
     */

    public
    Transition getTransition (final String targetState)
    {
        assert targetState != null;
        final Transition trans = (Transition) transitions.get (targetState);
        return trans;
    }

    /**
     *
     * @return
     */

    public
    Map<String, Transition> getTransitions ()
    {
        return transitions; 
    }

    /**
     *
     * @param tevt
     */

    public
    void entering (final TransitionEvent tevt)
    {
        assert tevt != null;
        notifyEntryListeners (tevt);
        return;
    }

    /**
     *
     * @param tevt
     */

    public
    void exiting (final TransitionEvent tevt)
    {
        assert tevt != null;
        notifyExitListeners (tevt);
        return;
    }

    /**
     *  Convenience method for transitioning from this state to a new state. Throws an
     *  IllegalStateException if this state is not the current state. Equivalent to
     *  calling:
     *
     *  <blockquote><code>
     *  getStateMachine ().transition (state);
     *  </code></blockquote>
     *
     *  @param state the new state to transition to
     */

    public
    void transition (final State state)
    {
        assert state != null;

        final StateMachine sm = getStateMachine ();
        if (sm.getCurrentState () != this) {
            throw new IllegalStateException ("State not current.");
        }
        sm.transition (state);
        return;
    }

    /**
     *  Convenience method for transitioning from this state to a new state. Throws
     *  an IllegalArgumentException if this state is not the current state. Equivalent to
     *  calling:
     *
     *  <blockquote><code>
     *  getStateMachine ().transition (name);
     *  </code></blockquote>
     *
     *  @param name the name of the new state to transition to
     */

    public
    void transition (final String name)
    {
        assert name != null;

        final StateMachine sm = getStateMachine ();
        if (sm.getCurrentState () != this) {
            throw new IllegalStateException ("State not current.");
        }
        sm.transition (name);
        return;
    }

    /**
     *  @see Object#toString
     */

    public
    String toString ()
    {
        String s = getClass ().getName () + "[";
        s += "name=" + getName();
        s += ",transitions={";
        final Collection<Transition> transitions =getTransitions ().values ();
        boolean first = true; 
        for (final Transition transition : transitions) { 
        	if (first) { 
        		first = false; 
        	} else { 
        		 s += ","; 
        	}
        	s += transition.getEndState ().getName (); 
        }
        s += "}";
        return s + "]";
    }
}

// EOF