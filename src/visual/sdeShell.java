//used to have both:
// /home/pchr/netbeans-8.1/groovy/modules/ext/groovy-all.jar
// and
// /home/pchr/Documents/FOR_JAVA/groovy-2.4.6/lib/groovy-2.4.6.jar

// https://code.google.com/archive/p/groovy-matrix/
// http://pvs.ifi.uni-heidelberg.de/home/
// https://github.com/ssadedin/graxxia

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package visual;
import climax.soundplayer;
import climax.universe;
import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import java.io.PrintStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import org.codehaus.groovy.control.CompilationFailedException;

/**
 *
 * @author pchr
 */
public class sdeShell{
    private  GroovyShell groovyshell=null;
//    private GroovyClassLoader gcl;
//    private Interpreter javashell=null;
    private INTERPRETER currentInterpreter = INTERPRETER.None;
    private String basicGlobalImports;
    private PrintStream communicate;
    private PrintStream commandHistory;
    private PrintStream outp;
    private int linenum=0;
    private Binding sharedData;
    private universe theUniverse;
//    private graphicsPanel theGP;
    private SDEgraphicsPanel theGP;
    private soundplayer theSP;
    private PlotFrame thePlot;
    private boolean time=false;
    private static ArrayList<String> str_jars=null;
    private static sdeFrame sde;
//    private JConsole console;
    
    public sdeShell(PrintStream com, PrintStream console, PrintStream outp){
        this.communicate=com;
        this.commandHistory=console;
        this.outp=outp;
        this.theSP = new soundplayer();
        this.thePlot = new PlotFrame();
        basicGlobalImports="\n"
//            +"import jbem.*;\n"
//            +"import jfem.*;\n"
                +"import jmat.*;\n"
                +"import visual.GPDomain.DomainType;\n"
                +"import visual.plotfunction;\n"
                +"import visual.PlotFrame;\n"
                +"import java.awt.Color;\n"
                +"import JamaSparseMatrix.*;\n"
                +"import jmat.AbstractMatrix as Matrix;\n"
                +"import static java.lang.Math.*;\n"
                +"import static mathman.Matlike.*;\n"
                +"import mathman.DoubleFunction;\n"
                //+"import mathman.Complex;\n"
                +"import org.apache.commons.math3.complex.*;\n"
                +"import java.text.NumberFormat;\n"
                +"import static courses.examples.*;\n"
                +"import static jmat.AbstractMatrix.Matrix;\n"
                //+"import mathman.FFT;\n"
                +"import org.apache.commons.math3.transform.*;\n"
                //+"import static mathman.Matlike.pad;\n"
                +"import jmat.AbstractEigenvalueDecomposition as EigenDescomposition;\n"
                +"import static jmat.AbstractEigenvalueDecomposition.EigenDescomposition;\n"
            ;
    }

    public enum INTERPRETER {
        Groovy, Java, None
    }
    
    public void setGroovyInterpreter(){
        currentInterpreter = INTERPRETER.Groovy;
        if(groovyshell==null){
            sharedData = new Binding();
            sharedData.setVariable("basicGlobalImports", basicGlobalImports);
            sharedData.setProperty("out", outp);
            groovyshell =new GroovyShell(sharedData);
            groovyshell.setProperty("interpreterMode", false);
            //javashell=null;
            eval("Complex.metaClass.plus = { Complex c -> add(c) }\n" +
                        "Complex.metaClass.minus = { Complex c -> subtract(c) }\n" +
                        "Complex.metaClass.div = { double d -> divide(d) }\n"+
                        "Complex.metaClass.div = { Complex c -> divide(c) }",false);
            eval("Complex.metaClass.re = {getReal()}\n" +
                        "Complex.metaClass.im = { getImaginary() }",false);
            eval("Complex.metaClass.conj = {conjugate()}",false);
            eval("conj = {Complex c ->return c.conjugate()}",false);
            eval("Complex.metaClass.angle = {atan(getImaginary()/getReal())}",false);
            //eval("Integer.metaClass.plus = {edu.uta.futureye.function.operator.FAdd x -> x.plus(delegate)}",false);
            //eval("Integer.metaClass.multiply = {edu.uta.futureye.function.operator.FAdd x -> x.multiply(delegate)}",false);
             
            /*
            // possible bug
            String thessss="df_x = {double x, double y -> 8.0*x*Math.exp(4.0*x*x-(2.0*y-1.0)*(2.0*y-1.0))*Math.sin(4.0*x*(2.0*y-1.0))+4.0*(2*y-1)*Math.exp(4.0*x*x-(2.0*y-1.0)*(2.0*y-1.0))*Math.cos(4.0*x*(2.0*y-1.0))}\n";
            this.eval(thessss);
            thessss="println df_x(1.0,1.0)";
            this.eval(thessss);
            
            thessss="df_x = {double x, double y -> 8.0*x*Math.exp(4.0*x*x-(2.0*y-1.0)*(2.0*y-1.0))*Math.sin(4.0*x*(2.0*y-1.0))"
                    + "+4.0*(2*y-1)*Math.exp(4.0*x*x-(2.0*y-1.0)*(2.0*y-1.0))*Math.cos(4.0*x*(2.0*y-1.0))}\n";
            this.eval(thessss);
            thessss="println df_x(1.0,1.0)";
            this.eval(thessss);
            
            thessss="df_x = {double x, double y -> 8.0*x*Math.exp(4.0*x*x-(2.0*y-1.0)*(2.0*y-1.0))*Math.sin(4.0*x*(2.0*y-1.0))\n" +
"+4.0*(2*y-1)*Math.exp(4.0*x*x-(2.0*y-1.0)*(2.0*y-1.0))*Math.cos(4.0*x*(2.0*y-1.0))}";
            this.eval(thessss);
            thessss="println df_x(1.0,1.0)";
            this.eval(thessss);
            */
        }
        setInterpreterUniverse();
        setInterpreterGraphicsPanel();
        setInterpreterSoundPlayer();
        setInterpreterPlotPanel();
        setInterpreterSDE();
    }
    
    public void closeInterpreter(){
        currentInterpreter = INTERPRETER.None;
        //javashell=null;
        groovyshell=null;
    }
    
    public String whichInterpreter(){
        return currentInterpreter.name();
    }
    
    public int eval(String command){return eval(command,true);}
    
    public int eval(String command, boolean p){
        Date str = new Date(); Date end = str;
        if(time)str=this.getDateTime();
        String[] lines = command.split("\\r?\\n"); 
        String recommand="";
        for(int i=0;i<lines.length;i++){
//            if(!"".equals(lines[i]))if(lines[i].trim().charAt(0)!='#'){
                if(p)commandHistory.println("SDEconsole["+(linenum++)+ "]--> "+lines[i]);
                recommand+=lines[i]+"\n";
//            }
        }
//        commandHistory.println("SDEconsole > "+command)
//        communicate.println(recommand)
        switch(currentInterpreter){
            case Groovy:
            {
                try {
                    recommand=basicGlobalImports+recommand;
                    groovyshell.evaluate(recommand.trim());
                    
    //                Script script=groovyshell.parse(recommand.trim());
    //                script.run();
                } catch (CompilationFailedException cfe) {
                   System.err.println("Syntax not correct " + cfe);
                }
            }
                break;
        }
        if(time){end=this.getDateTime();
        long seconds = (end.getTime()-str.getTime())/1000;
        communicate.println(seconds+" sec.");}
        return 0;
    }
    
    private void setInterpreterUniverse(){
        switch(currentInterpreter){
            case Groovy:
                groovyshell.setVariable("theUniverse", theUniverse);
//                communicate.println("theUniverse in groovy shell");
                break;
        }
    }
    
    private void setInterpreterGraphicsPanel(){
        switch(currentInterpreter){
            case Groovy:
                groovyshell.setVariable("theGP", theGP);
//                communicate.println("theUniverse in groovy shell");
                break;
        }
    }
    
    private void setInterpreterSDE(){
        switch(currentInterpreter){
            case Groovy:
                groovyshell.setVariable("sde", sde);
//                communicate.println("theUniverse in groovy shell");
                break;
        }
    }
    
    public void setInterpreterSoundPlayer(){
        switch(currentInterpreter){
            case Groovy:
                groovyshell.setVariable("theSP", theSP);
//                communicate.println("theUniverse in groovy shell");
                break;
        }
    }
    
    public void setInterpreterPlotPanel(){
        switch(currentInterpreter){
            case Groovy:
                groovyshell.setVariable("thePlot", thePlot);
//                communicate.println("theUniverse in groovy shell");
                break;
        }
    }
    
    public void printVariables(){
        switch(currentInterpreter){
            case Groovy:
                for(Iterator<Object> it=sharedData.getVariables().values().iterator(); it.hasNext();){
                    communicate.println(it.next());
                } 
                
                break;
        }
    }
    
    public void setUniverse(universe theUniverse){
        this.theUniverse=theUniverse;
        setInterpreterUniverse();
    }
    
    public void setGraphicsPanel(SDEgraphicsPanel theGP){
        this.theGP=theGP;
        setInterpreterGraphicsPanel();
    }
    
    public void setSDE(sdeFrame theSDE){
        this.sde=theSDE;
        setInterpreterSDE();
    }
    
    public void cls(){
        switch(currentInterpreter){
            case Groovy:
                sharedData = new Binding();
                sharedData.setVariable("basicGlobalImports", basicGlobalImports);
                groovyshell =new GroovyShell(sharedData);
                if(str_jars!=null)for(int i=0;i<str_jars.size();i++){
                    String path2jar=str_jars.get(i);
                    String command="this.getClass().classLoader.addURL(new File(\""+path2jar+"\").toURL())";
                    this.eval(command, false);
                }
                //javashell=null;
                break;
        }
    }
    
    public void setTimer(boolean b){this.time=true;}

    protected Date getDateTime() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        communicate.println(dateFormat.format(date));
        return date;
    }
    
    public void addJAR(String path2jar){
        if(sdeShell.str_jars==null){str_jars=new ArrayList<String>();}
        str_jars.add(path2jar);
        String command="this.getClass().classLoader.addURL(new File(\""+path2jar+"\").toURL())";
        this.eval(command, false);
    }
    
    public void passArray(String name, String theArray){
        if(name!=null){
            double[][] DArray = null;
            communicate.print("Array in variable name: "+name);
            if(theArray!=null){
                String[] parts = theArray.split("\n");
                int rows=parts.length;
                int columns = parts[0].split(" ").length;
                DArray= new double[columns][rows];
                for(int m = 0; m < rows; m++){
                    for(int n = 0; n < columns; n++) {
                        String[] row=parts[m].split(" ");
                        //String[] row=parts[m].split(" |\\\t");
                        double val=0.0;
                        try {
                            val = Double.parseDouble(row[n]);
                        } catch (NumberFormatException e) {}
                        DArray[n][m] = val;
                    }
                }
                communicate.println(" ("+columns+"x"+rows+")");
            }else{
                communicate.println("null");
            }
            switch(currentInterpreter){
                case Groovy:
                    groovyshell.setVariable(name, DArray);
                    break;
            }
        }
    }
}
