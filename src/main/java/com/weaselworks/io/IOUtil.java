package com.weaselworks.io;

import com.weaselworks.util.Pair;

import java.io.*;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 *
 * @author Lee Crawford (lee.crawford@gmail.com)
 * @copyright Copyright (c) 2009, All Rights Reserved.
 */

public class IOUtil
{
    private
    IOUtil ()
    {
        return ;
    }
    
    public static 
	void unzipFile (final String from, final String to)
		throws IOException
	{
    	unzipFile (new File (from), new File (to)); 
    	return; 
	}
	
    public static
    void unzipFile (final File from, final String to)
        	throws IOException
    {
    	unzipFile (from, new File (to)); 
    	return; 
    }
    
    public static
    void unzipFile (final String from, final File to)
        	throws IOException
    {
    	unzipFile (new File (from), to); 
    	return; 
    }
    
    public static
    void unzipFile (final File from, final File dest)
        	throws IOException
    {
		// Create the output directory
		
    	if (! dest.exists ()) { 
    		dest.mkdirs ();
    	}

		// And then unzip the file
		
		final ZipFile zipFile = new ZipFile (from);
	    final Enumeration<? extends ZipEntry> entries = zipFile.entries ();
	    
	    try { 
		    while (entries.hasMoreElements()) {
		        final ZipEntry entry = entries.nextElement();
		        if (entry.isDirectory ()) { 
		        	continue; 
		        }
		        final File entryDestination = new File (dest, entry.getName());
		        entryDestination.getParentFile().mkdirs();
		        final InputStream in = zipFile.getInputStream (entry);
		        final OutputStream out = new FileOutputStream (entryDestination);
		        IOUtil.copy (in, out);
		        IOUtil.safeClose (in);
		        IOUtil.safeClose (out);
		    }
	    }
	    finally { 
	    	zipFile.close (); 
	    }
		    
	    return; 
	}
    
    /**
     * 
     * @param os
     * @return
     */

    public static
    PrintStream getPrintStream (final OutputStream os)
    {
        if (os instanceof PrintStream) {
            return (PrintStream) os;
        } else {
            return new PrintStream (os);
        }
        // NOT REACHED
    }

    /**
     *
     * @param is
     */

    public static
    void safeClose (final InputStream is)
    {
        try {
            if (is != null) {
                is.close ();
            }
        }
        catch (final IOException io_e) {
            // IGNORED
        }
        return;
    }

    /**     *
     * @param os
     */

    public static
    void safeClose (final OutputStream os)
    {
        try {
            if (os != null) {
                os.close ();
            }
        }
        catch (final IOException io_e) {
            // IGNORED
        }
        return;
    }

    /**
     *
     * @param r
     */

    public static
    void safeClose (final Reader r)
    {
        try {
            if (r != null) {
                r.close ();
            }
        }
        catch (final IOException io_e) {
            // IGNORED
        }
        return;
    }

    /**
     *
     * @param w
     */

    public static
    void safeClose (final Writer w)
    {
        try {
            if (w != null) {
                w.close ();
            }
        }
        catch (final IOException io_e) {
            // IGNORED
        }
        return;
    }

    /**
     *
     *
     *  @param is
     *  @return
     */

    public static
    String readFully (final InputStream is)
    	throws IOException
    {
        final StringBuffer sb = new StringBuffer ();
        int c;

        while ((c = is.read ()) != -1) {
            sb.append ((char) c);
        }

        return sb.toString ();
    }


    /**
     *
     *  @param r
     *  @return
     */

    public static
    String readFully (final Reader r)
        throws IOException
    {
        final StringBuffer sb = new StringBuffer ();
        int c;

        while ((c = r.read ()) != -1) {
//        	if (sb.length () % 1000 == 0) { 
//        		System.out.println ("len : " + sb.length ()); 
//        	}
        	final char ch = (char) c; 
//        	System.out.print (ch); 
            sb.append (ch);
        }

        return sb.toString ();
    }

    /**
     *
     *  @param is the InputStream to be read from
     *  @param os the OutputStream to be written to
     */

    public static
    void copy (final InputStream is, final OutputStream os)
        throws IOException
    {
        synchronized (is) {
            synchronized (os) {
                final byte [] buf = new byte [8192];
                while (true) {
                    final int read = is.read (buf);
                    if (read == -1) {
                        break;
                    }
                    os.write (buf, 0, read);
                }
            }
        }

        return;
    }
    
    /**
     * 
     * @param r
     * @param w
     * @throws IOException
     */

    public static
    void copy (final Reader r, final Writer w)
        throws IOException
    {
        synchronized (r) {
            synchronized (w) {
                final char [] buf = new char [8192];
                while (true) {
                    final int read = r.read (buf);
                    if (read == -1) {
                        break;
                    }
                    w.write (buf, 0, read);
                }
            }
        }

        return;
    }

    /**
     * Creates a connected pair of input and output streams. 
     * 
     * @return
     * @throws IOException
     */

    public static
    Pair<InputStream, OutputStream> getPipePair ()
        throws IOException
    {
        final PipedInputStream pis = new PipedInputStream ();
        final PipedOutputStream pos = new PipedOutputStream (pis);
        return new Pair <InputStream, OutputStream> (pis, pos);
    }

    /**
     * 
     * @param file
     * @return
     * @throws IOException
     */
    
    public static
    byte [] getFileContents (final File file)
    	throws IOException
    {
        final InputStream is = new FileInputStream (file);
        try { 
	        final long length = file.length ();
	        if (length > Integer.MAX_VALUE) {
	        	throw new IOException ("File too large: " + length + " bytes"); 
	        }
	        
	        final byte[] bytes = new byte[(int)length];
	        
	        // Read in the bytes
	        int offset = 0;
	        int numRead = 0;
	        while (offset < bytes.length
	               && (numRead=is.read(bytes, offset, bytes.length-offset)) >= 0) {
	            offset += numRead;
	        }
	    
	        // Ensure all the bytes have been read in
	        if (offset < bytes.length) {
	            throw new IOException("Could not completely read file "+ file.getName());
	        }
	        return bytes;
	    }    
        finally { 
        	is.close();
        }
        
        // NOT REACHED
    }    
    
	public static
	String readFile (File file)
		throws IOException
	{
		final Reader in = new InputStreamReader(new FileInputStream (file), "UTF-8");
		final StringBuffer buf = new StringBuffer (); 
		
		final int blockSize = 4096; 
		char [] block = new char [blockSize]; 
		int read; 
		
		do { 
			read = in.read (block);
			if (read <= 0) { 
				break; 
			}
			buf.append (block, 0, read); 
		} while (true);
		 
		in.close (); 
		return buf.toString (); 
	}

    public static
    void writeFile (final File file, final String contents)
        throws IOException
    {
        final OutputStream os = new FileOutputStream (file);
        os.write (contents.getBytes());
        os.flush ();
        os.close ();
        return;
    }

	public static
	String readFile (String filename)
		throws IOException
	{
		return readFile (new File (filename)); 
	}
}

// EOF