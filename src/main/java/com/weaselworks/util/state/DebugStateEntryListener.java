package com.weaselworks.util.state;

import java.util.*;
import java.util.logging.*;

import com.weaselworks.util.*;

/**
 *  An example {@link StateEntryListener} that reports state entry events to the log.
 */

public class DebugStateEntryListener
    implements StateEntryListener
{
    protected static final Logger logger = Logger.getLogger ("tictactoe.state");

    public
    DebugStateEntryListener ()
    {
        return;
    }

    /**
     *  @see #stateEntered(TransitionEvent)
     */

    public
    void stateEntered (final TransitionEvent tevt)
    {
        final State state = tevt.getEndState ();
        final Map<String, Transition> transitions = state.getTransitions ();
        String possible = "";

        for (final Transition transition : transitions.values ()) { 
        	if (! StringUtil.isEmpty (possible)) { 
                possible += ", ";
            }
        	possible += transition.getEndState ().getName (); 
        }

        logger.log (Level.INFO, "STATE [" + tevt.getEndState().getName () + "] ENTERED. POSSIBLE: " + possible);
        return;
    }
}

// EOF