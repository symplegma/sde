/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package visual;
import java.util.jar.*;
import java.util.*;
import java.io.*;
/**
 *
 * @author antonia
 */
public class PackageUtils {
    private static boolean debug = false;

    private PackageUtils() {}

    public static ArrayList getClasseNamesInPackage
        (String jarName, String packageName){
      ArrayList classes = new ArrayList ();

      packageName = packageName.replaceAll("\\." , "/");
      if (debug) System.out.println
           ("Jar " + jarName + " looking for " + packageName);
      try{
        JarInputStream jarFile = new JarInputStream
           (new FileInputStream (jarName));
        JarEntry jarEntry;

        while(true) {
          jarEntry=jarFile.getNextJarEntry ();
          if(jarEntry == null){
            break;
          }
          if((jarEntry.getName ().startsWith (packageName)) &&
               (jarEntry.getName ().endsWith (".class")) ) {
            if (debug) System.out.println
              ("Found " + jarEntry.getName().replaceAll("/", "\\."));
            classes.add ( jarEntry.getName().replaceAll("/", "\\.").replaceFirst(".class", "").replaceFirst(packageName+".", ""));
          }
        }
      }
      catch( Exception e){
          
      }
      return classes;
      }
}
