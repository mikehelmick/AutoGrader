/*
 * Created on Sep 24, 2005
 */
package edu.muohio.csa.autograder.classpath;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * Total hack to dynamically modify the classpath
 * code copied from here....
 * http://forum.java.sun.com/thread.jspa?threadID=300557&messageID=2931139
 * 
 * project: AutoGrader
 * package: edu.muohio.csa.autograder.classpath
 * 
 * @author mhelmick
 * @version $Id$
 */
public class DynamicLoader {

	private static final Class[] parameters = new Class[]{URL.class};
	
    public static void addFile(String s) throws IOException {
            File f = new File(s);
            addFile(f);
    }//end method

    public static void addFile(File f) throws IOException {
            addURL(f.toURL());
    }//end method

    public static void addURL(URL u) throws IOException {
            URLClassLoader sysloader = (URLClassLoader)ClassLoader.getSystemClassLoader();
            Class sysclass = URLClassLoader.class;
            try {
                    Method method = sysclass.getDeclaredMethod("addURL",parameters);
                    method.setAccessible(true);
                    method.invoke(sysloader,new Object[]{ u });
            } catch (Throwable t) {
                    t.printStackTrace();
                    throw new IOException("Error, could not add URL to system classloader");
            }//end try catch
    }//end method
	
}
