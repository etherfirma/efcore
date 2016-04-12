package com.weaselworks.util.interp;

import java.io.*;
import java.util.*;

/**
 *  An interpreter class for implementing command-line interpreter (CLI) 
 *  driven applications.  
 *
 *  Example: 
 *
 *  <pre>
 *    public class MyApp
 *    {
 *      public static
 *      void main (final String [] args)
 *      {
 *        final Interpreter i = new MyIntepreter (); 
 *        i.interpret (new InputStreamReader (System.in), new OutputStreamWriter (System.out));  
 *        return;
 *      }
 *    }
 *
 *    protected static class MyInterpreter 
 *      extends Interpreter
 *    {
 *      public
 *      MyInterpreter ()
 *      {
 *        addCommand (new MyFirstCommand ()); 
 *        addCommand (new MySecondCommand ()); 
 *        return;
 *    }
 *  </pre>
 *
 *  @see Command
 *  @see CommandSupport
 */ 

public class Interpreter
{
    public
    Interpreter ()
    {
        addCommand (new HelpCommand ());
        addCommand (new EchoCommand ());
        addCommand (new TimeCommand ());
        addCommand (new RepeatCommand ());
        addCommand (new AddCommand ());
        addCommand (new RemoveCommand ());
        addCommand (new DumpStateCommand ()); 
        return;
    }
    
    protected Map<String, Object> state = new HashMap<String, Object> (); 
    public Map<String, Object> getState () { return this.state; } 
    public <T> T getState (final String key) { return (T) this.state.get (key); }
    public void setState (final String key, final String value) { this.state.put (key, value); return; } 
        
    protected Map<String, Command> commands = new HashMap<String, Command> (); 
    public Map<String, Command> getCommands () { return this.commands; } 
    public boolean hasCommand (final String name) { return commands.containsKey (name); } 
    
    public
    void addCommand (final Command cmd)
    {
    	addCommand (cmd.getName (), cmd);
    	return; 
    }

    public
    void addCommands (final Command... cmds) {
        for (final Command cmd : cmds) {
            addCommand (cmd);
        }
        return;
    }

    public
    void addCommand (final String name, final Command cmd)
    {
    	commands.put (name, cmd); 
        if (cmd instanceof CommandSupport) {
            final CommandSupport cs = (CommandSupport) cmd;
            cs.setInterpreter (this);
        }
    	return; 
    }
        
    public
    void addCommands (final List<Command> cmds) 
    {
    	for (final Command cmd : cmds) { 
    		addCommand (cmd); 
    	}
    	return; 
    }
    
    protected
    void removeCommand (final String command)
    {
    	if (commands.containsKey (command)) { 
    		commands.remove (command); 
    	}
        return;
    }

    protected
    void removeCommand (final Command cmd)
    {
        commands.remove (cmd);
        return;
    }

    public static final String DEFAULT_PROMPT = "> ";
    protected String prompt = DEFAULT_PROMPT;
     
    public 
    void setPrompt (final String prompt)
    {
        this.prompt = prompt; 
        return;
    }
    
    public
    String getPrompt ()
    {
        return this.prompt;
    }
    
    protected boolean quit; 
    public boolean getQuit () { return this.quit; } 
    public void setQuit (final boolean quit) { this.quit = quit; return; } 

    public
    void interpret ()
    {
        interpret (new InputStreamReader (System.in), new OutputStreamWriter (System.out));
        return;
    }

    public 
    void interpret (final Reader r, final Writer w) 
    {
        final BufferedReader br = new BufferedReader (r); 
        final PrintWriter pw = new PrintWriter (w); 
        
        // Let them know that we're here and listening      
        
        setQuit (false); 
        
        try {         
            while (! getQuit ()) { 
                pw.print (getPrompt ()); 
                pw.flush (); 

                final String s = br.readLine (); 
                if (s == null) { 
                    return;                    
                }

                invoke (s, pw);
            }
        }
        catch (final IOException io_e) { 
            // IGNORED
        }
        
        return;           
    }

    /**
     * 
     * @param cline
     * @return
     */
    
    public
    String invoke (final String cline)
    {
    	final List<String> args = parse (cline); 
        return invoke (args);
    }

    /**
     * 
     * @param args
     * @return
     */
    
    public
    String invoke (final List<String> args)
    {
        final StringWriter sw = new StringWriter ();
        invoke (args, new PrintWriter (sw));
        return sw.toString ();
    }

    /**
     * 
     * @param args
     * @param out
     */
    
    public
    void invoke (final String args, final PrintWriter out)
    {
        invoke (parse (args), out);
        return;
    }

    /**
     *  @see Command#invoke
     */

    public
    void invoke (final List<String> args, final PrintWriter out)
    {
        if (args.size () > 0) {
//            final Command cmd = lookupCommand (args.get (0));
        	final Command cmd = commands.get (args.get (0)); 

            if (cmd == null) {
                out.println ("ERROR: Command '" + args.get (0) + "' not found.");
            } else {
                try {
                    cmd.invoke (args, out);
                }
                catch (final Exception e) {
                    out.println ("EXCEPTION: " + e.getMessage ());
                    e.printStackTrace ();
                }
            }
        }

        return;
    }

    /**
     * 
     * @param str
     * @return
     */
    
    // package
    Command lookupCommand (final String str)
    {
    	return commands.get (str); 
    }

    public static
    void main (final String[] args)
        throws IOException
    {
        final InputStreamReader isr = new InputStreamReader (System.in); 
        final BufferedReader br = new BufferedReader (isr); 
        
        System.out.println ("CLI Parser Tester"); 
        
        while (true) {
            System.out.print ("> ");
            final String line = br.readLine ();
            final List<String> cli = parse (line);

            int cnt = 0; 
            for (final String arg : cli) { 
                System.out.println ("" + (++cnt) + ": '" + arg + "'");
            }
        }

        // NOT REACHED
    }

    /**
     * 
     * @param str
     * @return
     */
    
    public static
    List<String> parse (final String str)
    {
        final List<String> args = new ArrayList<String> (); 
        StringBuffer sb = null;
        int i = 0;

        while (i < str.length ()) {
            while (i < str.length () && Character.isSpaceChar (str.charAt (i))) {
                i ++;
            }
            if (i >= str.length ()) {
                break;
            }
            char c = str.charAt (i ++);
            sb = new StringBuffer ();
            if (c == '#') {
                break;
            }
            if (c == '\'' || c == '\"') {
                final char delim = c;
                while (i < str.length () && ((c = str.charAt (i)) != delim)) {
                    sb.append (c);
                    i ++;
                }
                i ++;
            } else {
                sb.append (c);
                while (i < str.length () && ! Character.isSpaceChar (c = str.charAt (i))) {
                    sb.append (c);
                    i ++;
                }
            }
            args.add (sb.toString ());
        }

        return args; 
    }
}

// EOF