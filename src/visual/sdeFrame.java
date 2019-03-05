/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package visual;

import climax.universe;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Box;
import javax.swing.JColorChooser;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.filechooser.FileNameExtensionFilter;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rtextarea.RTextScrollPane;
import org.fife.ui.autocomplete.*;
import org.fife.ui.rtextarea.SearchContext;
import org.fife.ui.rtextarea.SearchEngine;
////
/**
 *
 * @author pchr
 */
public class sdeFrame extends javax.swing.JFrame implements WindowListener{
    private sdeShell theInterpreter;
    outputTextArea OutputTab;
    outputTextArea ErrorTab;
    outputTextArea CommentsTab;
    outputTextArea HistoryTab;
    sdeConsole consoleTextArea;
//    JConsole consoleTextArea;
    JTabbedPane inTAreasTabbed;
    JTabbedPane outTAreasTabbed;
    inputTextArea inputTextArea1;
    static int numUnits=1;
    BufferedReader br;
    BufferedWriter bw;
    JFileChooser fc = new JFileChooser();
    private PrintStream com;
    private PrintStream command_hist;
    private PrintStream out;
    private RTextScrollPane theRSP;
    JScrollPane theJSP;
    
    ArrayList<String> words = new ArrayList<>();
    private AutoCompletion ac;
    
    private universe theUniverse;
    
    private static RecentFileMenu recentMenu;
    
    private final graphicsFXPanel theGraphicsFXPanel = new graphicsFXPanel();
    private boolean showFXPanel=false;
    
    private SDEgraphicsPanel theGP;
    
    public void setCursorExecution(){
        this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
    }
    
    public void setCursorDefault(){
        this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    }
    // Java3D vs JOGL vs LWJGL ... JAVAFX: 
    // https://www.youtube.com/watch?v=z0S5Ded2IJY
    //see ? http://www.numfocus.org/ ?
    
    // after: http://doraprojects.net/blog/?p=1060
    // keytool -genkeypair -dname "cn=http://dynasoft.civil.auth.gr/" -alias my_key_app  -keypass agginara76 -keystore /home/pchr/NetBeansProjects/CDE/pchr_key -storepass agginara76 -validity 357
    /**
     * Creates new form SDEframe
     */
    public sdeFrame() {
        initComponents();
        Dimension d =new Dimension(340, 220);
        this.FindReplaceDialog.setSize(d);
        seticon();
        theGP = this.theGraphicsPanel;
//        SaveAs_mi.setEnabled(false);
        SaveAll_mi.setEnabled(false);
        triangles_cbmi.setState(false);
        
        // set mnemonics
        File_m.setMnemonic(KeyEvent.VK_F);
        Edit_m.setMnemonic(KeyEvent.VK_E);
        Load_m.setMnemonic(KeyEvent.VK_L);
        Help_m.setMnemonic(KeyEvent.VK_H);
        View_m.setMnemonic(KeyEvent.VK_V);
        Run_m.setMnemonic(KeyEvent.VK_R);
        
        // Set accelerators
        Action theAction = new AbstractAction("Save") {
 
            @Override
            public void actionPerformed(ActionEvent e) {
                //com.println("Saving...");
            }
        };
        theAction.putValue(Action.ACCELERATOR_KEY,
            KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_DOWN_MASK));
        Save_mi.setAction(theAction);
        
        theAction = new AbstractAction("Open...") {
            @Override
            public void actionPerformed(ActionEvent e) {
                //com.println("Opening...");
            }
 
        };
        theAction.putValue(Action.ACCELERATOR_KEY,
            KeyStroke.getKeyStroke(KeyEvent.VK_O, KeyEvent.CTRL_DOWN_MASK));
        Open_mi.setAction(theAction);
        
        theAction = new AbstractAction("Open URL...") {
            @Override
            public void actionPerformed(ActionEvent e) {
                //com.println("Opening...");
            }
 
        };
        theAction.putValue(Action.ACCELERATOR_KEY,
            KeyStroke.getKeyStroke(KeyEvent.VK_I, KeyEvent.CTRL_DOWN_MASK));
        OpenURL_mi.setAction(theAction);
        
        theAction = new AbstractAction("New") {
            @Override
            public void actionPerformed(ActionEvent e) {
                //com.println("Opening...");
            }
 
        };
        theAction.putValue(Action.ACCELERATOR_KEY,
            KeyStroke.getKeyStroke(KeyEvent.VK_N, KeyEvent.CTRL_DOWN_MASK));
        New_mi.setAction(theAction);
        
        theAction = new AbstractAction("Add jar") {
            @Override
            public void actionPerformed(ActionEvent e) {
                //com.println("Opening...");
            }
 
        };
        theAction.putValue(Action.ACCELERATOR_KEY,
            KeyStroke.getKeyStroke(KeyEvent.VK_L, KeyEvent.CTRL_DOWN_MASK));
        Addjar_mi.setAction(theAction);
        
        theAction = new AbstractAction("FindReplace") {
 
            @Override
            public void actionPerformed(ActionEvent e) {
                //com.println("Saving...");
            }
        };
        theAction.putValue(Action.ACCELERATOR_KEY,
            KeyStroke.getKeyStroke(KeyEvent.VK_F, KeyEvent.CTRL_DOWN_MASK));
        FindReplace_mi.setAction(theAction);
        
        fc = new JFileChooser();
        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);

        OutputTab = new outputTextArea();
        OutputTab.setBackground(Color.BLACK);
        OutputTab.setForeground(Color.LIGHT_GRAY);
        OutputTab.setEditable(false);
        OutputTab.set_index_in_tabs(0);
        OutputTab.setSDEframe(this);
        
        ErrorTab  = new outputTextArea();
        ErrorTab.setBackground(Color.BLACK);
        ErrorTab.setForeground(Color.ORANGE);
        ErrorTab.setEditable(false);
        ErrorTab.set_index_in_tabs(1);
        ErrorTab.setSDEframe(this);
        
        CommentsTab  = new outputTextArea();
        CommentsTab.setBackground(Color.BLACK);
        CommentsTab.setForeground(Color.RED);
        CommentsTab.setEditable(false);
        CommentsTab.set_index_in_tabs(2);
        CommentsTab.setSDEframe(this);
        
        consoleTextArea  = new sdeConsole();
//        consoleTextArea  = new JConsole();
        
        HistoryTab= new outputTextArea();
        HistoryTab.setBackground(Color.BLACK);
        HistoryTab.setForeground(Color.GREEN);
        HistoryTab.setEditable(false);
        HistoryTab.set_index_in_tabs(3);
        HistoryTab.setSDEframe(this);

        //redirect to OutputTab   ByteArrayOutputStream
        out = new PrintStream( new TextAreaOutputStream( OutputTab ) );
        String t="This is the output control area."+'\n'
        +"----------------------------------------------------------------";
        OutputTab.setTitle(t);
        // redirect standard output stream to the TextAreaOutputStream
        System.setOut( out );
        System.out.println(t);

        // redirect standard error stream to the TextAreaOutputStream
        out = new PrintStream( new TextAreaOutputStream( ErrorTab ) );
        t="This is the errors control area."+'\n'
        +"----------------------------------------------------------------";
        ErrorTab.setTitle(t);
        System.setErr( out );
        System.err.println(t);
        
        com = new PrintStream( new TextAreaOutputStream( CommentsTab ) );
        t="This is the comments control area."+'\n'
        +"----------------------------------------------------------------";
        CommentsTab.setTitle(t);
        com.println(t);
        
        t="Symlegma, version X.X.X\n" +
        "Copyright (C) 2016 Christos G. Panagiotopoulos and others.\n" +
        "This is free software; see the source code for copying conditions.\n" +
        "There is ABSOLUTELY NO WARRANTY; not even for MERCHANTABILITY or\n" +
        "FITNESS FOR A PARTICULAR PURPOSE.  For details, type 'warranty'.\n" +
        "\n" +
        "Symplegma was configured for \"x86_64-pc-linux-gnu\".\n" +
        "\n" +
        "Additional information about Symplegma is available at http://www.symplegma.org.\n" +
        "\n" +
        "Please contribute if you find this software useful.\n" +
        "For more information, visit http://www.symplegma.org/get-involved.html\n" +
        "\n" +
        "Read http://www.symplegma.org/bugs.html to learn how to submit bug reports.\n" 
        +"----------------------------------------------------------------"+'\n';
        //+">>";
        consoleTextArea.setText(t);
//        consoleTextArea.print(t);
        
        command_hist = new PrintStream( new TextAreaOutputStream( HistoryTab ) );
        t="This is the command history area."+'\n'
        +"----------------------------------------------------------------";
        HistoryTab.setTitle(t);
        command_hist.println(t);
        
        outTAreasTabbed = new JTabbedPane();
        outTAreasTabbed.add("Output", OutputTab);
        outTAreasTabbed.add("Errors", ErrorTab);
        outTAreasTabbed.add("Comments", CommentsTab);
        //outTAreasTabbed.add("Console",consoleTextArea);
        outTAreasTabbed.add("History", HistoryTab);
        //outTAreasTabbed.setSelectedIndex(3);
//        outTAreasTabbed.setEnabledAt(3, false);
        consoleTextArea.setCaretPosition(consoleTextArea.getText().length());
        consoleTextArea.getCaret().setVisible(true);
        consoleTextArea.setLineWrap(true);
        consoleTextArea.requestInput ();
        consoleTextArea.setEnabled(true);
        
        
        inputTextArea1 = new inputTextArea();
        inputTextArea1.setCDEframe(this);
        inputTextArea1.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_GROOVY);
        inputTextArea1.append("// Input area where climax code is expected"+"\n");
        CompletionProvider provider = createCompletionProvider();
        ac = new AutoCompletion(provider);
        ac.install(inputTextArea1);
        ac.setAutoCompleteEnabled(AutoSuggestionCBMI.isSelected());

        inTAreasTabbed = new JTabbedPane();
        inTAreasTabbed.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent me) {
                    if(SwingUtilities.isMiddleMouseButton(me)){
                        java.awt.event.ActionEvent evt = null;
                        jMenuItem6ActionPerformed(evt);
                    }
            }
        });
        inTAreasTabbed.add("unit_1", inputTextArea1);
        inputTextArea1.set_index_in_tabs(inTAreasTabbed.indexOfComponent(inputTextArea1));
        
        console_cbmi.setSelected(false);
        theRSP = new RTextScrollPane(inTAreasTabbed);
        theRSP.setViewportView(inTAreasTabbed);
        theRSP.getVerticalScrollBar().setUnitIncrement(20);
        theRSP.setLineNumbersEnabled(LineNumbersCBMI.isSelected());
        theRSP.getGutter().setLineNumberingStartIndex(-1);
        jSplitPane2.setLeftComponent(theRSP);
        theJSP = new JScrollPane();
        theJSP.setViewportView(outTAreasTabbed);
        theJSP.getVerticalScrollBar().setUnitIncrement(20);
        jSplitPane2.setRightComponent(theJSP);
        
        theInterpreter = new sdeShell(com,command_hist,System.out);
        theGraphicsPanel.setcomPrintStream(com);
        inputTextArea.setInterpreter(theInterpreter);
        outputTextArea.setInterpreter(theInterpreter);
        
        if(interpreter_groovy_rbmi.isSelected()){
            theInterpreter.setGroovyInterpreter();
            com.println("Initial interrpeter: Groovy");
        }else if(interpreter_java_rbmi.isSelected()){
            //theInterpreter.setJavaInterpreter();
            com.println("Initial interpreter: Java not exists");
        }else{
            com.println("Initial interpreter: None");
        }
        
        ListenedButtonGroup InterpreterGroup = new ListenedButtonGroup();
        InterpreterGroup.add(interpreter_groovy_rbmi);
        InterpreterGroup.add(interpreter_java_rbmi);
        InterpreterGroup.add(interpreter_none_rbmi);
        
        InterpreterGroup.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                com.print("Interpreter have been modified, from "+theInterpreter.whichInterpreter());
                if(interpreter_none_rbmi.isSelected()){
                    RunSelected_mi.setEnabled(false);
                    inputTextArea1.setRunSelected(false);
                    theInterpreter.closeInterpreter();
                }else{
                    RunSelected_mi.setEnabled(true);
                    inputTextArea1.setRunSelected(true);
                    if(interpreter_groovy_rbmi.isSelected()){
                        theInterpreter.setGroovyInterpreter();
                    }else{
                        //theInterpreter.setJavaInterpreter();
                    }
                }
                com.println(" to "+theInterpreter.whichInterpreter());
                
            }
        });
        
        consoleTextArea.setInterpreter(theInterpreter);
        
        ListenedButtonGroup SyntaxHighlightGroup = new ListenedButtonGroup();
        SyntaxHighlightGroup.add(java_sh_rbmi);
        SyntaxHighlightGroup.add(groovy_sh_rbmi);
        SyntaxHighlightGroup.add(climax_sh_rbmi);
        SyntaxHighlightGroup.add(none_sh_rbmi);
        SyntaxHighlightGroup.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if(java_sh_rbmi.isSelected()){
                    inputTextArea1.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVA);
                    inputTextArea1.repaint();
                }else if(groovy_sh_rbmi.isSelected()){
                    inputTextArea1.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_GROOVY);
                    inputTextArea1.repaint();
                }else if(climax_sh_rbmi.isSelected()){
                    inputTextArea1.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_NONE);
                    inputTextArea1.repaint();
                }else{
                    inputTextArea1.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_NONE);
                    inputTextArea1.repaint();
                }
            }
        });

        ListenedButtonGroup ComplierGroup = new ListenedButtonGroup();
        ComplierGroup.add(compiler_java_rbmi);
        ComplierGroup.add(compiler_groovy_rbmi);

        theGraphicsPanel.setBackground(Color.WHITE);
        
        theUniverse =new universe();
        theInterpreter.setUniverse(theUniverse);
        theInterpreter.setSDE(this);
        theGraphicsPanel.setUniverse(theUniverse);
        theGraphicsFXPanel.setUniverse(theUniverse);
        theInterpreter.setGraphicsPanel(theGP);

        jSplitPane1.setDividerLocation(500);
        jSplitPane2.setDividerLocation(420);
        
        addWindowListener(this);
        
        recentMenu=new RecentFileMenu("RecentFileMenu_Test",10){
        	public void onSelectFile(String filePath){
                    inputTextArea1 = new inputTextArea();
                    ac.install(inputTextArea1);
                    File theRecentFile = new File(filePath);
                    try {
                        br = new BufferedReader(new FileReader(theRecentFile));
                    } catch (FileNotFoundException ex) {
                        Logger.getLogger(sdeFrame.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    inputTextArea1.setAssociatedFile(theRecentFile);
                    inTAreasTabbed.add(theRecentFile.getName(), inputTextArea1);
                    inTAreasTabbed.setSelectedComponent(inputTextArea1);
                    try {
                        String line = br.readLine();
                        while (line != null) {
                            inputTextArea1.append(line+'\n');
                            line = br.readLine();
                        }
                        br.close();
                    } catch (IOException ex) {
                        Logger.getLogger(sdeFrame.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
        };
        File_m.add(recentMenu);
        
//        com.println("jSplitPane1.getDividerLocation "+jSplitPane1.getDividerLocation());
//        com.println("jSplitPane2.getDividerLocation "+jSplitPane2.getDividerLocation());
//        jSplitPane1.addPropertyChangeListener(JSplitPane.DIVIDER_LOCATION_PROPERTY, 
//                new PropertyChangeListener() {
//                    @Override
//                    public void propertyChange(PropertyChangeEvent pce) {
//                        com.println("jSplitPane1.getDividerLocation "+jSplitPane1.getDividerLocation());
//                    }
//                }
//        );
//        
//        jSplitPane2.addPropertyChangeListener(JSplitPane.DIVIDER_LOCATION_PROPERTY, 
//                new PropertyChangeListener() {
//                    @Override
//                    public void propertyChange(PropertyChangeEvent pce) {
//                        com.println("jSplitPane2.getDividerLocation "+jSplitPane2.getDividerLocation());
//                    }
//                }
//        );
    }
    
    public void repaintGraphicsPanel(){this.theGraphicsPanel.plot();}
    
    private CompletionProvider createCompletionProvider() {

      // A DefaultCompletionProvider is the simplest concrete implementation
      // of CompletionProvider. This provider has no understanding of
      // language semantics. It simply checks the text entered up to the
      // caret position for a match against known completions. This is all
      // that is needed in the majority of cases.
      DefaultCompletionProvider provider = new DefaultCompletionProvider();

//      // Add completions for all Java keywords. A BasicCompletion is just
//      // a straightforward word completion.
//      provider.addCompletion(new BasicCompletion(provider, "abstract"));
//      provider.addCompletion(new BasicCompletion(provider, "assert"));
//      provider.addCompletion(new BasicCompletion(provider, "break"));
//      provider.addCompletion(new BasicCompletion(provider, "case"));
//      // ... etc ...
//      provider.addCompletion(new BasicCompletion(provider, "transient"));
//      provider.addCompletion(new BasicCompletion(provider, "try"));
//      provider.addCompletion(new BasicCompletion(provider, "void"));
//      provider.addCompletion(new BasicCompletion(provider, "volatile"));
//      provider.addCompletion(new BasicCompletion(provider, "while"));

      // Add a couple of "shorthand" completions. These completions don't
      // require the input text to be the same thing as the replacement text.
      this.LoadWordsFromPackage("jfem", "JFEM.jar");
      for (int i = 0; i < this.words.size(); i++) {
          words.get(i);
          provider.addCompletion(new ShorthandCompletion(provider, words.get(i),
                  words.get(i), words.get(i)));
      }
      
      this.LoadWordsFromPackage("jmat", "Algebra.jar");
      for (int i = 0; i < this.words.size(); i++) {
          words.get(i);
          provider.addCompletion(new ShorthandCompletion(provider, words.get(i),
                  words.get(i), words.get(i)));
      }
      
      
//      provider.addCompletion(new ShorthandCompletion(provider, "Domain/Java",
//            "Domain someDomain = new Domain();", "Domain someDomain = new Domain();"));
//      provider.addCompletion(new ShorthandCompletion(provider, "Domain/Groovy",
//            "someDomain = new Domain", "someNode = new Domain"));
//      provider.addCompletion(new ShorthandCompletion(provider, "Domain/Climax",
//            "Domain someDomain", "Domain someDomain"));
//      
//      provider.addCompletion(new ShorthandCompletion(provider, "Node/Java",
//            "Node someNode = new Node(int id, double[] coords);", "Node someNode = new Node(int id, double[] coords);"));
//      provider.addCompletion(new ShorthandCompletion(provider, "Node/Groovy",
//            "someNode = new Node(int id, double[] coords)", "someNode = new Node(int id, double[] coords)"));
//      provider.addCompletion(new ShorthandCompletion(provider, "Node/Climax",
//            "Node (int id) at (double x),(double/opt y),(double/opt z)", "Node (int id) at (double x),(double/opt y),(double/opt z)"));

      return provider;

   }
    
    private void LoadWordsFromPackage(String PackName, String JarName){
        //e.g., PackName="jfem";
        //e.g., JarName="JFEM.jar";
        String currPath="";
        try {
            currPath=new java.io.File( "." ).getCanonicalPath();
//            System.out.println(currPath);
        } catch (IOException ex) {
            Logger.getLogger(sdeFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
        words =  PackageUtils.getClasseNamesInPackage(currPath+"/dist/lib/"+JarName, PackName);
        com.print("Keywords produced by classes of "+JarName+": ");
        com.println(words);
    }
    
    public JTabbedPane get_outTAreasTabbed(){return outTAreasTabbed;}

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        FindReplaceDialog = new javax.swing.JDialog();
        FindReplacePanel = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        FindTextField = new javax.swing.JTextField();
        ReplaceTextField = new javax.swing.JTextField();
        MatchCaseRB = new javax.swing.JRadioButton();
        FindB = new javax.swing.JButton();
        CloseB = new javax.swing.JButton();
        ReplaceAllB = new javax.swing.JButton();
        ReplaceB = new javax.swing.JButton();
        RegularExpRB = new javax.swing.JRadioButton();
        jSplitPane1 = new javax.swing.JSplitPane();
        jSplitPane2 = new javax.swing.JSplitPane();
        theGraphicsPanel = new visual.graphicsPanel();
        jMenuBar1 = new javax.swing.JMenuBar();
        File_m = new javax.swing.JMenu();
        New_mi = new javax.swing.JMenuItem();
        Open_mi = new javax.swing.JMenuItem();
        OpenURL_mi = new javax.swing.JMenuItem();
        jMenuItem6 = new javax.swing.JMenuItem();
        Save_mi = new javax.swing.JMenuItem();
        SaveAs_mi = new javax.swing.JMenuItem();
        SaveAll_mi = new javax.swing.JMenuItem();
        Edit_m = new javax.swing.JMenu();
        variables_mi = new javax.swing.JMenuItem();
        cls_variables_mi = new javax.swing.JMenuItem();
        Domains_mi = new javax.swing.JMenuItem();
        cls_domains_mi = new javax.swing.JMenuItem();
        Timer_cbmi = new javax.swing.JCheckBoxMenuItem();
        FindReplace_mi = new javax.swing.JMenuItem();
        FontSizeMenu = new javax.swing.JMenu();
        IncreaseFont_mi = new javax.swing.JMenuItem();
        DecreaseFont_mi = new javax.swing.JMenuItem();
        View_m = new javax.swing.JMenu();
        AutoSuggestionCBMI = new javax.swing.JCheckBoxMenuItem();
        jMenu7 = new javax.swing.JMenu();
        java_sh_rbmi = new javax.swing.JRadioButtonMenuItem();
        groovy_sh_rbmi = new javax.swing.JRadioButtonMenuItem();
        climax_sh_rbmi = new javax.swing.JRadioButtonMenuItem();
        none_sh_rbmi = new javax.swing.JRadioButtonMenuItem();
        LineNumbersCBMI = new javax.swing.JCheckBoxMenuItem();
        jMenu8 = new javax.swing.JMenu();
        ChooseColor_mi = new javax.swing.JMenuItem();
        DrawAxes_cbmi = new javax.swing.JCheckBoxMenuItem();
        DrawGrid_cbmi = new javax.swing.JCheckBoxMenuItem();
        DrawPerimeter_cbmi = new javax.swing.JCheckBoxMenuItem();
        IsoScale_cbmi = new javax.swing.JCheckBoxMenuItem();
        SetMargins_mi = new javax.swing.JMenuItem();
        Repaint_mi = new javax.swing.JMenuItem();
        View2_3D_mi = new javax.swing.JMenuItem();
        PlotValues_mi = new javax.swing.JMenuItem();
        Nodes_m = new javax.swing.JMenu();
        DrawNodes_cbmi = new javax.swing.JCheckBoxMenuItem();
        DrawNodesIDs_cbmi = new javax.swing.JCheckBoxMenuItem();
        resolution_mi = new javax.swing.JMenuItem();
        triangles_cbmi = new javax.swing.JCheckBoxMenuItem();
        adjust_mi = new javax.swing.JMenuItem();
        saveimage_mi = new javax.swing.JMenuItem();
        console_cbmi = new javax.swing.JCheckBoxMenuItem();
        Run_m = new javax.swing.JMenu();
        RunProject_mi = new javax.swing.JMenuItem();
        RunSelected_mi = new javax.swing.JMenuItem();
        BuildProject_mi = new javax.swing.JMenuItem();
        theInterpreterMenu = new javax.swing.JMenu();
        interpreter_java_rbmi = new javax.swing.JRadioButtonMenuItem();
        interpreter_groovy_rbmi = new javax.swing.JRadioButtonMenuItem();
        interpreter_none_rbmi = new javax.swing.JRadioButtonMenuItem();
        jMenu5 = new javax.swing.JMenu();
        compiler_java_rbmi = new javax.swing.JRadioButtonMenuItem();
        compiler_groovy_rbmi = new javax.swing.JRadioButtonMenuItem();
        Load_m = new javax.swing.JMenu();
        Addjar_mi = new javax.swing.JMenuItem();
        Help_m = new javax.swing.JMenu();

        FindReplaceDialog.setTitle("Find & Replace");

        FindReplacePanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel2.setText("Replace");
        FindReplacePanel.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 40, -1, -1));

        jLabel1.setText("Find");
        FindReplacePanel.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 10, -1, -1));
        FindReplacePanel.add(FindTextField, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 10, 210, -1));
        FindReplacePanel.add(ReplaceTextField, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 40, 210, -1));

        MatchCaseRB.setText("Match Case");
        FindReplacePanel.add(MatchCaseRB, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 90, -1, -1));

        FindB.setText("Find");
        FindB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                FindBActionPerformed(evt);
            }
        });
        FindReplacePanel.add(FindB, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 140, -1, -1));

        CloseB.setText("Close");
        CloseB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CloseBActionPerformed(evt);
            }
        });
        FindReplacePanel.add(CloseB, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 140, -1, -1));

        ReplaceAllB.setText("Replace All");
        ReplaceAllB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ReplaceAllBActionPerformed(evt);
            }
        });
        FindReplacePanel.add(ReplaceAllB, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 140, -1, -1));

        ReplaceB.setText("Replace");
        ReplaceB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ReplaceBActionPerformed(evt);
            }
        });
        FindReplacePanel.add(ReplaceB, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 140, -1, -1));

        RegularExpRB.setText("Regular expression");
        FindReplacePanel.add(RegularExpRB, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 90, -1, -1));

        FindReplaceDialog.getContentPane().add(FindReplacePanel, java.awt.BorderLayout.CENTER);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Symplegma Development Environment");
        setSize(new java.awt.Dimension(800, 800));

        jSplitPane2.setDividerLocation(100);
        jSplitPane2.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        jSplitPane1.setLeftComponent(jSplitPane2);
        jSplitPane1.setRightComponent(theGraphicsPanel);

        getContentPane().add(jSplitPane1, java.awt.BorderLayout.CENTER);

        File_m.setText("File");

        New_mi.setText("New");
        New_mi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                New_miActionPerformed(evt);
            }
        });
        File_m.add(New_mi);

        Open_mi.setText("Open...");
        Open_mi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Open_miActionPerformed(evt);
            }
        });
        File_m.add(Open_mi);

        OpenURL_mi.setText("Open url");
        OpenURL_mi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                OpenURL_miActionPerformed(evt);
            }
        });
        File_m.add(OpenURL_mi);

        jMenuItem6.setText("Close");
        jMenuItem6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem6ActionPerformed(evt);
            }
        });
        File_m.add(jMenuItem6);

        Save_mi.setText("Save...");
        Save_mi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Save_miActionPerformed(evt);
            }
        });
        File_m.add(Save_mi);

        SaveAs_mi.setText("Save As...");
        SaveAs_mi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SaveAs_miActionPerformed(evt);
            }
        });
        File_m.add(SaveAs_mi);

        SaveAll_mi.setText("Save All");
        SaveAll_mi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SaveAll_miActionPerformed(evt);
            }
        });
        File_m.add(SaveAll_mi);

        jMenuBar1.add(File_m);

        Edit_m.setText("Edit");
        Edit_m.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Edit_mActionPerformed(evt);
            }
        });

        variables_mi.setText("Report Variables");
        variables_mi.setToolTipText("printed in \"Comments\" tab");
        variables_mi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                variables_miActionPerformed(evt);
            }
        });
        Edit_m.add(variables_mi);

        cls_variables_mi.setText("Clear Variables");
        cls_variables_mi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cls_variables_miActionPerformed(evt);
            }
        });
        Edit_m.add(cls_variables_mi);

        Domains_mi.setText("Report Domains");
        Domains_mi.setToolTipText("print in \"Comments\" tab numbers of domains defined");
        Domains_mi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Domains_miActionPerformed(evt);
            }
        });
        Edit_m.add(Domains_mi);

        cls_domains_mi.setText("Clear Domains");
        cls_domains_mi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cls_domains_miActionPerformed(evt);
            }
        });
        Edit_m.add(cls_domains_mi);

        Timer_cbmi.setText("Time");
        Timer_cbmi.setToolTipText("use a \"clock\" for interpreter's procedures and report it in \"Comments\" tab");
        Timer_cbmi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Timer_cbmiActionPerformed(evt);
            }
        });
        Edit_m.add(Timer_cbmi);

        FindReplace_mi.setText("Find & Replace");
        FindReplace_mi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                FindReplace_miActionPerformed(evt);
            }
        });
        Edit_m.add(FindReplace_mi);

        FontSizeMenu.setText("Font size");

        IncreaseFont_mi.setText("Increase");
        IncreaseFont_mi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                IncreaseFont_miActionPerformed(evt);
            }
        });
        FontSizeMenu.add(IncreaseFont_mi);

        DecreaseFont_mi.setText("Decrease");
        DecreaseFont_mi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                DecreaseFont_miActionPerformed(evt);
            }
        });
        FontSizeMenu.add(DecreaseFont_mi);

        Edit_m.add(FontSizeMenu);

        jMenuBar1.add(Edit_m);

        View_m.setText("View");
        View_m.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                View_mActionPerformed(evt);
            }
        });

        AutoSuggestionCBMI.setSelected(true);
        AutoSuggestionCBMI.setText("Auto-suggestion");
        AutoSuggestionCBMI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AutoSuggestionCBMIActionPerformed(evt);
            }
        });
        View_m.add(AutoSuggestionCBMI);

        jMenu7.setText("Syntax Highlight");

        java_sh_rbmi.setSelected(true);
        java_sh_rbmi.setText("Java");
        jMenu7.add(java_sh_rbmi);

        groovy_sh_rbmi.setSelected(true);
        groovy_sh_rbmi.setText("Groovy");
        jMenu7.add(groovy_sh_rbmi);

        climax_sh_rbmi.setSelected(true);
        climax_sh_rbmi.setText("Climax");
        jMenu7.add(climax_sh_rbmi);

        none_sh_rbmi.setSelected(true);
        none_sh_rbmi.setText("None");
        jMenu7.add(none_sh_rbmi);

        View_m.add(jMenu7);

        LineNumbersCBMI.setSelected(true);
        LineNumbersCBMI.setText("Line Numbers");
        LineNumbersCBMI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                LineNumbersCBMIActionPerformed(evt);
            }
        });
        View_m.add(LineNumbersCBMI);

        jMenu8.setText("Graphics Panel");

        ChooseColor_mi.setText("Choose Background Color");
        ChooseColor_mi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ChooseColor_miActionPerformed(evt);
            }
        });
        jMenu8.add(ChooseColor_mi);

        DrawAxes_cbmi.setSelected(true);
        DrawAxes_cbmi.setText("Draw Axes");
        DrawAxes_cbmi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                DrawAxes_cbmiActionPerformed(evt);
            }
        });
        jMenu8.add(DrawAxes_cbmi);

        DrawGrid_cbmi.setSelected(true);
        DrawGrid_cbmi.setText("Draw Grid");
        DrawGrid_cbmi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                DrawGrid_cbmiActionPerformed(evt);
            }
        });
        jMenu8.add(DrawGrid_cbmi);

        DrawPerimeter_cbmi.setText("Draw Perimeter");
        DrawPerimeter_cbmi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                DrawPerimeter_cbmiActionPerformed(evt);
            }
        });
        jMenu8.add(DrawPerimeter_cbmi);

        IsoScale_cbmi.setText("Iso-Scale");
        IsoScale_cbmi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                IsoScale_cbmiActionPerformed(evt);
            }
        });
        jMenu8.add(IsoScale_cbmi);

        SetMargins_mi.setText("Set Margins...");
        SetMargins_mi.setToolTipText("");
        SetMargins_mi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SetMargins_miActionPerformed(evt);
            }
        });
        jMenu8.add(SetMargins_mi);

        Repaint_mi.setText("Repaint");
        Repaint_mi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Repaint_miActionPerformed(evt);
            }
        });
        jMenu8.add(Repaint_mi);

        View2_3D_mi.setText("2D/3D View");
        View2_3D_mi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                View2_3D_miActionPerformed(evt);
            }
        });
        jMenu8.add(View2_3D_mi);

        PlotValues_mi.setText("Plot Values");
        PlotValues_mi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                PlotValues_miActionPerformed(evt);
            }
        });
        jMenu8.add(PlotValues_mi);

        Nodes_m.setText("Nodes");

        DrawNodes_cbmi.setSelected(true);
        DrawNodes_cbmi.setText("Draw Nodes");
        DrawNodes_cbmi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                DrawNodes_cbmiActionPerformed(evt);
            }
        });
        Nodes_m.add(DrawNodes_cbmi);

        DrawNodesIDs_cbmi.setSelected(true);
        DrawNodesIDs_cbmi.setText("Draw Nodes' id");
        DrawNodesIDs_cbmi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                DrawNodesIDs_cbmiActionPerformed(evt);
            }
        });
        Nodes_m.add(DrawNodesIDs_cbmi);

        jMenu8.add(Nodes_m);

        resolution_mi.setText("Resolution");
        resolution_mi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                resolution_miActionPerformed(evt);
            }
        });
        jMenu8.add(resolution_mi);

        triangles_cbmi.setSelected(true);
        triangles_cbmi.setText("triangulation");
        triangles_cbmi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                triangles_cbmiActionPerformed(evt);
            }
        });
        jMenu8.add(triangles_cbmi);

        adjust_mi.setText("Adjust");
        adjust_mi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                adjust_miActionPerformed(evt);
            }
        });
        jMenu8.add(adjust_mi);

        saveimage_mi.setText("Save image");
        saveimage_mi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveimage_miActionPerformed(evt);
            }
        });
        jMenu8.add(saveimage_mi);

        View_m.add(jMenu8);

        console_cbmi.setSelected(true);
        console_cbmi.setText("Console");
        console_cbmi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                console_cbmiActionPerformed(evt);
            }
        });
        View_m.add(console_cbmi);

        jMenuBar1.add(View_m);

        Run_m.setText("Run");
        Run_m.setEnabled(false);

        RunProject_mi.setText("Run Project");
        RunProject_mi.setEnabled(false);
        Run_m.add(RunProject_mi);

        RunSelected_mi.setText("Run Selected");
        RunSelected_mi.setEnabled(false);
        Run_m.add(RunSelected_mi);

        BuildProject_mi.setText("Build Project");
        BuildProject_mi.setEnabled(false);
        Run_m.add(BuildProject_mi);

        theInterpreterMenu.setText("Interpreter");
        theInterpreterMenu.setToolTipText("If you select to transit over interpreters, all variables (apart form theUniverse) will be lost from cache.");

        interpreter_java_rbmi.setSelected(true);
        interpreter_java_rbmi.setText("Java (BeanShell)");
        interpreter_java_rbmi.setToolTipText("If you select to transit over interpreters, all variables (apart form theUniverse) will be lost from cache.");
        theInterpreterMenu.add(interpreter_java_rbmi);

        interpreter_groovy_rbmi.setSelected(true);
        interpreter_groovy_rbmi.setText("GroovyShell ");
        interpreter_groovy_rbmi.setToolTipText("If you select to transit over interpreters, all variables (apart form theUniverse) will be lost from cache.");
        theInterpreterMenu.add(interpreter_groovy_rbmi);

        interpreter_none_rbmi.setSelected(true);
        interpreter_none_rbmi.setText("None");
        interpreter_none_rbmi.setToolTipText("If you select to transit over interpreters, all variables (apart form theUniverse) will be lost from cache.");
        theInterpreterMenu.add(interpreter_none_rbmi);

        Run_m.add(theInterpreterMenu);

        jMenu5.setText("Complier");
        jMenu5.setEnabled(false);

        compiler_java_rbmi.setSelected(true);
        compiler_java_rbmi.setText("Java");
        jMenu5.add(compiler_java_rbmi);

        compiler_groovy_rbmi.setSelected(true);
        compiler_groovy_rbmi.setText("Groovy");
        jMenu5.add(compiler_groovy_rbmi);

        Run_m.add(jMenu5);

        jMenuBar1.add(Run_m);

        Load_m.setText("Load");

        Addjar_mi.setText("Add jar");
        Addjar_mi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Addjar_miActionPerformed(evt);
            }
        });
        Load_m.add(Addjar_mi);

        jMenuBar1.add(Load_m);

        Help_m.setText("Help");
        jMenuBar1.add(Help_m);

        setJMenuBar(jMenuBar1);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void LineNumbersCBMIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_LineNumbersCBMIActionPerformed
        // TODO add your handling code here:
        theRSP.setLineNumbersEnabled(LineNumbersCBMI.isSelected());
        theRSP.repaint();
    }//GEN-LAST:event_LineNumbersCBMIActionPerformed

    private void AutoSuggestionCBMIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AutoSuggestionCBMIActionPerformed
        // TODO add your handling code here:
        //        ac.setAutoActivationEnabled(AutoSuggestionCBMI.isSelected());
        ac.setAutoCompleteEnabled(AutoSuggestionCBMI.isSelected());
    }//GEN-LAST:event_AutoSuggestionCBMIActionPerformed

    private void ChooseColor_miActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ChooseColor_miActionPerformed
        // TODO add your handling code here:
        Color aColor=JColorChooser.showDialog(this, "Background color for graphical area", theGraphicsPanel.getBackground());
        if(aColor!=null){
            theGraphicsPanel.setBackground(aColor);
            theGraphicsPanel.setBColor(aColor);
            theGraphicsPanel.repaint();
        }
    }//GEN-LAST:event_ChooseColor_miActionPerformed

    private void DrawAxes_cbmiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_DrawAxes_cbmiActionPerformed
        // TODO add your handling code here:
        this.theGraphicsPanel.setDrawAxes(DrawAxes_cbmi.isSelected());
        theGraphicsPanel.repaint();
    }//GEN-LAST:event_DrawAxes_cbmiActionPerformed

    private void DrawGrid_cbmiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_DrawGrid_cbmiActionPerformed
        // TODO add your handling code here:
        this.theGraphicsPanel.setDrawGrid(DrawGrid_cbmi.isSelected());
        theGraphicsPanel.repaint();
    }//GEN-LAST:event_DrawGrid_cbmiActionPerformed

    private void IsoScale_cbmiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_IsoScale_cbmiActionPerformed
        // TODO add your handling code here:
        this.theGraphicsPanel.setIsoScale(IsoScale_cbmi.isSelected());
        theGraphicsPanel.repaint();
        
    }//GEN-LAST:event_IsoScale_cbmiActionPerformed

    private void Repaint_miActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Repaint_miActionPerformed
        // TODO add your handling code here:
        theGraphicsPanel.plot();
    }//GEN-LAST:event_Repaint_miActionPerformed

    private void View_mActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_View_mActionPerformed
        // TODO add your handling code here:
        
    }//GEN-LAST:event_View_mActionPerformed

    private void SetMargins_miActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SetMargins_miActionPerformed
        // TODO add your handling code here:
        int mx=this.theGraphicsPanel.getMargin_x();
        int my=this.theGraphicsPanel.getMargin_y();
        JTextField xField = new JTextField(5); xField.setText(String.valueOf(mx));
        JTextField yField = new JTextField(5); yField.setText(String.valueOf(my));
        xField.setToolTipText("default: 30");
        yField.setToolTipText("default: 20");

        JPanel myPanel = new JPanel();
        myPanel.add(new JLabel("x margin:"));
        myPanel.add(xField);
        myPanel.add(Box.createHorizontalStrut(15)); // a spacer
        myPanel.add(new JLabel("y margin:"));
        myPanel.add(yField);

        int result = JOptionPane.showConfirmDialog(this, myPanel, 
               "Please enter x and y margins' integer values", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            theGraphicsPanel.setMargin_x(Integer.parseInt(xField.getText()));
            theGraphicsPanel.setMargin_y(Integer.parseInt(yField.getText()));
            theGraphicsPanel.plot();
        }
    }//GEN-LAST:event_SetMargins_miActionPerformed

    private void DrawNodesIDs_cbmiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_DrawNodesIDs_cbmiActionPerformed
        // TODO add your handling code here:
        this.theGraphicsPanel.setDrawNodesID(DrawNodesIDs_cbmi.isSelected());
        theGraphicsPanel.plot();
    }//GEN-LAST:event_DrawNodesIDs_cbmiActionPerformed

    private void DrawNodes_cbmiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_DrawNodes_cbmiActionPerformed
        // TODO add your handling code here:
        this.theGraphicsPanel.setDrawNodes(DrawNodes_cbmi.isSelected());
        theGraphicsPanel.plot();
    }//GEN-LAST:event_DrawNodes_cbmiActionPerformed

    private void View2_3D_miActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_View2_3D_miActionPerformed
        // TODO add your handling code here:
        if(showFXPanel){
            System.out.println("in sdeFRame View2_3D_miActionPerformed-> showFXPanel=true");
            theGP=theGraphicsPanel;
            showFXPanel=false;
            int loc=jSplitPane1.getDividerLocation();
            jSplitPane1.setRightComponent(theGraphicsPanel);
            jSplitPane1.setDividerLocation(loc);
            theGraphicsPanel.plot();
        }else{
            System.out.println("in sdeFRame View2_3D_miActionPerformed-> showFXPanel=false");
            showFXPanel=true;
            theGP=theGraphicsFXPanel;
            int loc=jSplitPane1.getDividerLocation();
            jSplitPane1.setRightComponent(theGraphicsFXPanel);
            jSplitPane1.setDividerLocation(loc);
                    
            //theGraphicsFXPanel.plot();
            // trying to follow:
            // https://docs.oracle.com/javase/8/javafx/interoperability-tutorial/swing-fx-interoperability.htm
            Platform.runLater(new Runnable() { 
                @Override
                public void run() {
                    theGraphicsFXPanel.plot();
                }
            });
            
        }
//        this.theGraphicsPanel.set3D();
//        theGraphicsPanel.plot();
    }//GEN-LAST:event_View2_3D_miActionPerformed

    private void PlotValues_miActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_PlotValues_miActionPerformed
        // TODO add your handling code here:
        theGraphicsPanel.switchDiagramValues();
        theGraphicsPanel.plot();
    }//GEN-LAST:event_PlotValues_miActionPerformed

    private void SaveAs_miActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SaveAs_miActionPerformed
        // TODO add your handling code here:
        inputTextArea1 = (inputTextArea) inTAreasTabbed.getSelectedComponent();
        fc.setSelectedFile(new File(inTAreasTabbed.getTitleAt(inputTextArea1.get_index_in_tabs())));
        int returnVal = fc.showSaveDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            try {
                bw = new BufferedWriter(new FileWriter(fc.getSelectedFile()));
                //recentMenu.addEntry(fc.getSelectedFile().getPath());
                bw.append(inputTextArea1.getText());
                bw.close();
                inputTextArea1.setChanged(false);
            } catch (IOException ex) {
                Logger.getLogger(sdeFrame.class.getName()).log(Level.SEVERE, null, ex);
            }
            inputTextArea1.setAssociatedFile(fc.getSelectedFile());
            inTAreasTabbed.getSelectedComponent().setName(fc.getSelectedFile().getName());
            inTAreasTabbed.setTitleAt(inTAreasTabbed.getSelectedIndex(), fc.getSelectedFile().getName());

        }
    }//GEN-LAST:event_SaveAs_miActionPerformed

    private void Save_miActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Save_miActionPerformed
        // TODO add your handling code here:
        inputTextArea1 = (inputTextArea) inTAreasTabbed.getSelectedComponent();
        if(inputTextArea1.getAssociatedFile()==null){
            int returnVal = fc.showSaveDialog(this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                try {
                    bw = new BufferedWriter(new FileWriter(fc.getSelectedFile()));
                    //recentMenu.addEntry(fc.getSelectedFile().getPath());
                    bw.append(inputTextArea1.getText());
                    bw.close();
                    inputTextArea1.setChanged(false);
                } catch (IOException ex) {
                    Logger.getLogger(sdeFrame.class.getName()).log(Level.SEVERE, null, ex);
                }
                inputTextArea1.setAssociatedFile(fc.getSelectedFile());
                inTAreasTabbed.getSelectedComponent().setName(fc.getSelectedFile().getName());
                inTAreasTabbed.setTitleAt(inTAreasTabbed.getSelectedIndex(), fc.getSelectedFile().getName());
            }
        }else{
            try {
                bw = new BufferedWriter(new FileWriter(inputTextArea1.getAssociatedFile()));
                //recentMenu.addEntry(inputTextArea1.getAssociatedFile().getPath());
                bw.append(inputTextArea1.getText());
                bw.close();
                inputTextArea1.setChanged(false);
//                inTAreasTabbed.setTitleAt(inTAreasTabbed.getSelectedIndex(), fc.getSelectedFile().getName());
                String fname=inputTextArea1.getAssociatedFile().getName();
                if(fname.substring(0, 0)=="*")fname=fname.substring(1);
                inTAreasTabbed.setTitleAt(inTAreasTabbed.getSelectedIndex(), fname);
            } catch (IOException ex) {
                Logger.getLogger(sdeFrame.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_Save_miActionPerformed

    private void jMenuItem6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem6ActionPerformed
        // TODO add your handling code here:
         inputTextArea1 = (inputTextArea) inTAreasTabbed.getSelectedComponent();
        if(!inputTextArea1.getChanged()){
            inTAreasTabbed.remove(inputTextArea1);
        }else{
            if(inputTextArea1.getAssociatedFile()!=null){
                String filename=inputTextArea1.getAssociatedFile().getName();
                Object[] options = {"Close without Saving",
                    "Cancel",
                    "Save"};
                //If you don't save, changes will be permanently lost.
                int n = JOptionPane.showOptionDialog(this,
                    "If you don't save, changes will be permanently lost.",
                    "Save file?",
                    JOptionPane.YES_NO_CANCEL_OPTION,
                    JOptionPane.WARNING_MESSAGE,
                    null,
                    options,
                    options[2]);
                //        System.out.println("selection is:"+n);
                switch (n) {
                    case 2:
                    if(inputTextArea1.getAssociatedFile()==null){
                        fc.setSelectedFile(new File(filename));
                        int returnVal = fc.showSaveDialog(this);
                        if (returnVal == JFileChooser.APPROVE_OPTION) {
                            try {
                                bw = new BufferedWriter(new FileWriter(fc.getSelectedFile()));
                                bw.append(inputTextArea1.getText());
                                bw.close();
                                inTAreasTabbed.remove(inputTextArea1);
                            } catch (IOException ex) {
                                Logger.getLogger(sdeFrame.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                    }else{
                        try {
                            bw = new BufferedWriter(new FileWriter(inputTextArea1.getAssociatedFile()));
                            bw.append(inputTextArea1.getText());
                            bw.close();
                            inTAreasTabbed.remove(inputTextArea1);
                        } catch (IOException ex) {
                            Logger.getLogger(sdeFrame.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                    break;
                    case 0:
                    inTAreasTabbed.remove(inputTextArea1);
                    break;
                    default:
                    break;
                }
            }else{
                SaveAs_miActionPerformed(evt);
            }
        }
    }//GEN-LAST:event_jMenuItem6ActionPerformed

    private void Open_miActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Open_miActionPerformed
        try {
            // TODO add your handling code here:
            //            fc.setAcceptAllFileFilterUsed(false);
            File dir=fc.getCurrentDirectory();
            fc = new JFileChooser();
            fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
            fc.setCurrentDirectory(dir);
            FileNameExtensionFilter filter = new FileNameExtensionFilter("Climax File","climax");
            fc.addChoosableFileFilter(filter);
            filter = new FileNameExtensionFilter("Java File","java");
            fc.addChoosableFileFilter(filter);
            filter = new FileNameExtensionFilter("Groovy File","groovy");
            fc.addChoosableFileFilter(filter);
            filter = new FileNameExtensionFilter("Compatible Files","climax", "java", "groovy");
            fc.addChoosableFileFilter(filter);
            
            int returnVal = fc.showOpenDialog(this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                br = new BufferedReader(new FileReader(fc.getSelectedFile()));
                recentMenu.addEntry(fc.getSelectedFile().getPath());
                inputTextArea1 = new inputTextArea();
                ac.install(inputTextArea1);
                inputTextArea1.setAssociatedFile(fc.getSelectedFile());
                //                autoSuggestor = new AutoSuggestor(inputTextArea1, this, words, Color.WHITE.brighter(), Color.BLUE, Color.RED, 0.75f);
                //                autoSuggestor.setActive(AutoSuggestionCBMI.isSelected());
                //                inputTextArea1.setAutoSuggestor(autoSuggestor);
                inTAreasTabbed.add(fc.getSelectedFile().getName(), inputTextArea1);
                inTAreasTabbed.setSelectedComponent(inputTextArea1);
                inputTextArea1.set_index_in_tabs(inTAreasTabbed.indexOfComponent(inputTextArea1));
                try {
                    String line = br.readLine();
                    while (line != null) {
                        inputTextArea1.append(line+'\n');
                        line = br.readLine();
                    }
                    br.close();
                } catch (IOException ex) {
                    Logger.getLogger(sdeFrame.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(sdeFrame.class.getName()).log(Level.SEVERE, null, ex);
        }

    }//GEN-LAST:event_Open_miActionPerformed

    private void New_miActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_New_miActionPerformed
        // TODO add your handling code here:
        inputTextArea1 = new inputTextArea();
        inputTextArea1.append("// Input area where climax code is expected"+"\n");
        ac.install(inputTextArea1);
        numUnits++;
        String strI = "unit_" + numUnits;
        inTAreasTabbed.add(strI, inputTextArea1);
        inTAreasTabbed.setSelectedComponent(inputTextArea1);
        inputTextArea1.set_index_in_tabs(inTAreasTabbed.indexOfComponent(inputTextArea1));
    }//GEN-LAST:event_New_miActionPerformed

    private void SaveAll_miActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SaveAll_miActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_SaveAll_miActionPerformed

    private void console_cbmiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_console_cbmiActionPerformed
        // TODO add your handling code here:
        if(console_cbmi.isSelected()){
            JSplitPane jSplitPane3 = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
            theJSP.setViewportView(outTAreasTabbed);
            theJSP.getVerticalScrollBar().setUnitIncrement(20);
            jSplitPane3.setRightComponent(theJSP);
            
            JScrollPane theJSP2 = new JScrollPane();
            theJSP2.setViewportView(consoleTextArea);
            theJSP2.getVerticalScrollBar().setUnitIncrement(20);
            jSplitPane3.setLeftComponent(theJSP2);
//            jSplitPane3.
            jSplitPane2.setRightComponent(jSplitPane3);
        }else{
            theJSP.setViewportView(outTAreasTabbed);
            theJSP.getVerticalScrollBar().setUnitIncrement(20);
            jSplitPane2.setRightComponent(theJSP);
        }
    }//GEN-LAST:event_console_cbmiActionPerformed

    private void resolution_miActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resolution_miActionPerformed
        // TODO add your handling code here:
        int m=this.theGraphicsPanel.getContourResolution();
        JTextField xField = new JTextField(5); xField.setText(String.valueOf(m));
        xField.setToolTipText("default: 10");

        JPanel myPanel = new JPanel();
        myPanel.add(new JLabel("resolution:"));
        myPanel.add(xField);
        myPanel.add(Box.createHorizontalStrut(15)); // a spacer

        int result = JOptionPane.showConfirmDialog(this, myPanel, 
               "Give resolution for contour plots", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            m=Integer.parseInt(xField.getText());
            theGraphicsPanel.setContourResolution(m);
            theGraphicsPanel.plot();
        }
    }//GEN-LAST:event_resolution_miActionPerformed

    private void triangles_cbmiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_triangles_cbmiActionPerformed
        // TODO add your handling code here:
        this.theGraphicsPanel.setDrawTriagles(triangles_cbmi.isSelected());
        theGraphicsPanel.repaint();
    }//GEN-LAST:event_triangles_cbmiActionPerformed

    private void adjust_miActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_adjust_miActionPerformed
        // TODO add your handling code here:
        ((graphicsPanel)theGraphicsPanel).adjust();
    }//GEN-LAST:event_adjust_miActionPerformed

    private void saveimage_miActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveimage_miActionPerformed
        // TODO add your handling code here:        
        ((graphicsPanel)theGraphicsPanel).saveImageSelection();
    }//GEN-LAST:event_saveimage_miActionPerformed

    private void CloseBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CloseBActionPerformed
        // TODO add your handling code here:
        FindReplaceDialog.setVisible(false);
    }//GEN-LAST:event_CloseBActionPerformed

    private void DrawPerimeter_cbmiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_DrawPerimeter_cbmiActionPerformed
        // TODO add your handling code here:
        this.theGraphicsPanel.setDrawPerimeter(DrawPerimeter_cbmi.isSelected());
        theGraphicsPanel.repaint();
    }//GEN-LAST:event_DrawPerimeter_cbmiActionPerformed

    private void OpenURL_miActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_OpenURL_miActionPerformed
        String urlStr = JOptionPane.showInputDialog(this, "url of climax file: ", "Read file form url", JOptionPane.INFORMATION_MESSAGE);
        inputTextArea1 = new inputTextArea();
        ac.install(inputTextArea1);
        numUnits++;
        String strI = "unit_" + numUnits;
        inTAreasTabbed.add(strI, inputTextArea1);
        inTAreasTabbed.setSelectedComponent(inputTextArea1);
        inputTextArea1.set_index_in_tabs(inTAreasTabbed.indexOfComponent(inputTextArea1));
        try {
            // TODO add your handling code here:
            URL url = new URL(urlStr);
            
            // read text returned by server
            BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));

            String line;
            while ((line = in.readLine()) != null) {
                //System.out.println(line);
                inputTextArea1.append(line+"\n");
            }
            in.close();
        } catch (MalformedURLException ex) {
            Logger.getLogger(sdeFrame.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException e) {
            System.err.println("I/O Error: " + e.getMessage());
        }
    }//GEN-LAST:event_OpenURL_miActionPerformed

    private void Addjar_miActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Addjar_miActionPerformed
        File dir=fc.getCurrentDirectory();
        fc = new JFileChooser();
        fc.setDialogTitle("Find and choose jar file to load");
        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fc.setCurrentDirectory(dir);
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Java ARchive","jar");
        fc.addChoosableFileFilter(filter);

        int returnVal = fc.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            com.println(fc.getSelectedFile().getPath());
            theInterpreter.addJAR(fc.getSelectedFile().getPath());
        }
        
    }//GEN-LAST:event_Addjar_miActionPerformed

    private void Edit_mActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Edit_mActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_Edit_mActionPerformed

    private void FindReplace_miActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_FindReplace_miActionPerformed
        // TODO add your handling code here:
        FindReplaceDialog.setVisible(true);
    }//GEN-LAST:event_FindReplace_miActionPerformed

    private void Timer_cbmiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Timer_cbmiActionPerformed
        // TODO add your handling code here:
        this.theInterpreter.setTimer(Timer_cbmi.isSelected());
    }//GEN-LAST:event_Timer_cbmiActionPerformed

    private void cls_domains_miActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cls_domains_miActionPerformed
        // TODO add your handling code here:
        //        theInterpreter.cls();
        theUniverse.cls();
        theInterpreter.setUniverse(theUniverse);
        //        theInterpreter.setGraphicsPanel(theGraphicsPanel);
        //        theInterpreter.setInterpreterPlotPanel();
        //        theInterpreter.setInterpreterSoundPlayer();
        theGraphicsPanel.stop();
        this.theGraphicsPanel.plot();
    }//GEN-LAST:event_cls_domains_miActionPerformed

    private void Domains_miActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Domains_miActionPerformed
        // TODO add your handling code here:
        com.println("<< theUniverse features >> ");
        int num;
        int[] ids;
        num=theUniverse.getFEMDomainsNum();
        com.print("jFEM domains: "+num+" ids=[");
        ids=theUniverse.getFEMDomainsIDs();
        for(int i=0;i<ids.length;i++){
            if(i==ids.length-1){
                com.print(ids[i]);
            }else{
                com.print(ids[i]+", ");
            }
        }
        com.println("]");
        num=theUniverse.getBEMDomainsNum();
        com.print("jBEM domains: "+num+" ids=[");
        ids=theUniverse.getBEMDomainsIDs();
        for(int i=0;i<ids.length;i++){
            if(i==ids.length-1){
                com.print(ids[i]);
            }else{
                com.print(ids[i]+", ");
            }
        }
        com.println("]");
        num=theUniverse.getPDEDomainsNum();
        com.print("jPDE domains: "+num+" ids=[");
        ids=theUniverse.getPDEDomainsIDs();
        for(int i=0;i<ids.length;i++){
            if(i==ids.length-1){
                com.print(ids[i]);
            }else{
                com.print(ids[i]+", ");
            }
        }
        com.println("]");
        num=theUniverse.getGENDomainsNum();
        com.print("jGEN: "+num+" ids=[");
        ids=theUniverse.getGENDomainsIDs();
        for(int i=0;i<ids.length;i++){
            if(i==ids.length-1){
                com.print(ids[i]);
            }else{
                com.print(ids[i]+", ");
            }
        }
        com.println("]");
        num=theUniverse.getDEMDomainsNum();
        com.print("jDEM domains: "+num+" ids=[");
        ids=theUniverse.getDEMDomainsIDs();
        for(int i=0;i<ids.length;i++){
            if(i==ids.length-1){
                com.print(ids[i]);
            }else{
                com.print(ids[i]+", ");
            }
        }
        com.println("]");
    }//GEN-LAST:event_Domains_miActionPerformed

    private void cls_variables_miActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cls_variables_miActionPerformed
        // TODO add your handling code here:
        theInterpreter.cls();
        theUniverse.cls();
        theInterpreter.setUniverse(theUniverse);
        theInterpreter.setSDE(this);
        theInterpreter.setGraphicsPanel(theGraphicsPanel);
        theInterpreter.setInterpreterPlotPanel();
        theInterpreter.setInterpreterSoundPlayer();
        theGraphicsPanel.stop();
        this.theGraphicsPanel.plot();
        sympmesher.Area2D.initglobalIndex();
    }//GEN-LAST:event_cls_variables_miActionPerformed

    private void variables_miActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_variables_miActionPerformed
        // TODO add your handling code here:
        this.theInterpreter.printVariables();
    }//GEN-LAST:event_variables_miActionPerformed

    private void IncreaseFont_miActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_IncreaseFont_miActionPerformed
        // TODO add your handling code here:
        Font font = inputTextArea1.getFont();
        float size = font.getSize() + 1.0f;
        inputTextArea1.setFont( font.deriveFont(size) );
    }//GEN-LAST:event_IncreaseFont_miActionPerformed

    private void DecreaseFont_miActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_DecreaseFont_miActionPerformed
        // TODO add your handling code here:
        Font font = inputTextArea1.getFont();
        float size = font.getSize() - 1.0f;
        inputTextArea1.setFont( font.deriveFont(size) );
    }//GEN-LAST:event_DecreaseFont_miActionPerformed

    private void FindBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_FindBActionPerformed
        // TODO add your handling code here:
        SearchContext context = new SearchContext();
        String text = FindTextField.getText();
        if (text.length() == 0) {
           return;
        }
        context.setSearchFor(text);
        context.setMatchCase(MatchCaseRB.isSelected());
        context.setRegularExpression(RegularExpRB.isSelected());
        context.setSearchForward(true);
        context.setWholeWord(false);

        boolean found = SearchEngine.find(this.inputTextArea1, context).wasFound();
        if (!found) {
           JOptionPane.showMessageDialog(this, "Text not found");
        }
    }//GEN-LAST:event_FindBActionPerformed

    private void ReplaceBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ReplaceBActionPerformed
        // TODO add your handling code here:
        SearchContext context = new SearchContext();
        String text = FindTextField.getText();
        String text2 = ReplaceTextField.getText();
        if (text.length() == 0) {
           return;
        }
        context.setSearchFor(text);
        context.setMatchCase(MatchCaseRB.isSelected());
        context.setRegularExpression(RegularExpRB.isSelected());
        context.setSearchForward(true);
        context.setWholeWord(false);
        context.setReplaceWith(text2);
        
        SearchEngine.replace(inputTextArea1, context);
    }//GEN-LAST:event_ReplaceBActionPerformed

    private void ReplaceAllBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ReplaceAllBActionPerformed
        // TODO add your handling code here:
        SearchContext context = new SearchContext();
        String text = FindTextField.getText();
        String text2 = ReplaceTextField.getText();
        if (text.length() == 0) {
           return;
        }
        context.setSearchFor(text);
        context.setMatchCase(MatchCaseRB.isSelected());
        context.setRegularExpression(RegularExpRB.isSelected());
        context.setSearchForward(true);
        context.setWholeWord(false);
        context.setReplaceWith(text2);
        
        
        boolean found = SearchEngine.find(this.inputTextArea1, context).wasFound();
        if (!found) {
           JOptionPane.showMessageDialog(this, "Text not found");
        }else{
            JOptionPane.showMessageDialog(this, "Total number of replacements: "+SearchEngine.replaceAll(inputTextArea1, context).getCount());
        }
    }//GEN-LAST:event_ReplaceAllBActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        try {
            /* Set the Nimbus look and feel */
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(sdeFrame.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            Logger.getLogger(sdeFrame.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(sdeFrame.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedLookAndFeelException ex) {
            Logger.getLogger(sdeFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(sdeFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(sdeFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(sdeFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(sdeFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        final sdeFrame myGUI = new sdeFrame();
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                myGUI.setVisible(true);
                myGUI.setExtendedState(JFrame.MAXIMIZED_BOTH);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem Addjar_mi;
    private javax.swing.JCheckBoxMenuItem AutoSuggestionCBMI;
    private javax.swing.JMenuItem BuildProject_mi;
    private javax.swing.JMenuItem ChooseColor_mi;
    private javax.swing.JButton CloseB;
    private javax.swing.JMenuItem DecreaseFont_mi;
    private javax.swing.JMenuItem Domains_mi;
    private javax.swing.JCheckBoxMenuItem DrawAxes_cbmi;
    private javax.swing.JCheckBoxMenuItem DrawGrid_cbmi;
    private javax.swing.JCheckBoxMenuItem DrawNodesIDs_cbmi;
    private javax.swing.JCheckBoxMenuItem DrawNodes_cbmi;
    private javax.swing.JCheckBoxMenuItem DrawPerimeter_cbmi;
    private javax.swing.JMenu Edit_m;
    private javax.swing.JMenu File_m;
    private javax.swing.JButton FindB;
    private javax.swing.JDialog FindReplaceDialog;
    private javax.swing.JPanel FindReplacePanel;
    private javax.swing.JMenuItem FindReplace_mi;
    private javax.swing.JTextField FindTextField;
    private javax.swing.JMenu FontSizeMenu;
    private javax.swing.JMenu Help_m;
    private javax.swing.JMenuItem IncreaseFont_mi;
    private javax.swing.JCheckBoxMenuItem IsoScale_cbmi;
    private javax.swing.JCheckBoxMenuItem LineNumbersCBMI;
    private javax.swing.JMenu Load_m;
    private javax.swing.JRadioButton MatchCaseRB;
    private javax.swing.JMenuItem New_mi;
    private javax.swing.JMenu Nodes_m;
    private javax.swing.JMenuItem OpenURL_mi;
    private javax.swing.JMenuItem Open_mi;
    private javax.swing.JMenuItem PlotValues_mi;
    private javax.swing.JRadioButton RegularExpRB;
    private javax.swing.JMenuItem Repaint_mi;
    private javax.swing.JButton ReplaceAllB;
    private javax.swing.JButton ReplaceB;
    private javax.swing.JTextField ReplaceTextField;
    private javax.swing.JMenuItem RunProject_mi;
    private javax.swing.JMenuItem RunSelected_mi;
    private javax.swing.JMenu Run_m;
    private javax.swing.JMenuItem SaveAll_mi;
    private javax.swing.JMenuItem SaveAs_mi;
    private javax.swing.JMenuItem Save_mi;
    private javax.swing.JMenuItem SetMargins_mi;
    private javax.swing.JCheckBoxMenuItem Timer_cbmi;
    private javax.swing.JMenuItem View2_3D_mi;
    private javax.swing.JMenu View_m;
    private javax.swing.JMenuItem adjust_mi;
    private javax.swing.JRadioButtonMenuItem climax_sh_rbmi;
    private javax.swing.JMenuItem cls_domains_mi;
    private javax.swing.JMenuItem cls_variables_mi;
    private javax.swing.JRadioButtonMenuItem compiler_groovy_rbmi;
    private javax.swing.JRadioButtonMenuItem compiler_java_rbmi;
    private javax.swing.JCheckBoxMenuItem console_cbmi;
    private javax.swing.JRadioButtonMenuItem groovy_sh_rbmi;
    private javax.swing.JRadioButtonMenuItem interpreter_groovy_rbmi;
    private javax.swing.JRadioButtonMenuItem interpreter_java_rbmi;
    private javax.swing.JRadioButtonMenuItem interpreter_none_rbmi;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JMenu jMenu5;
    private javax.swing.JMenu jMenu7;
    private javax.swing.JMenu jMenu8;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem6;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JSplitPane jSplitPane2;
    private javax.swing.JRadioButtonMenuItem java_sh_rbmi;
    private javax.swing.JRadioButtonMenuItem none_sh_rbmi;
    private javax.swing.JMenuItem resolution_mi;
    private javax.swing.JMenuItem saveimage_mi;
    private visual.graphicsPanel theGraphicsPanel;
    private javax.swing.JMenu theInterpreterMenu;
    private javax.swing.JCheckBoxMenuItem triangles_cbmi;
    private javax.swing.JMenuItem variables_mi;
    // End of variables declaration//GEN-END:variables

    private void seticon() {
        setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("symp2.png")));
    }

    @Override
    public void windowOpened(WindowEvent e) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void windowClosing(WindowEvent e) {
        boolean unsavedfile=false;
        Component[] comps = new Component[inTAreasTabbed.getComponents().length];
        for(int i = 0; i < comps.length; i++){
            comps[i]=inTAreasTabbed.getComponents()[i];
            if(((inputTextArea)comps[i]).getChanged())unsavedfile=true;
        }
        if(!unsavedfile){
        } else {
            int reply = JOptionPane.showConfirmDialog(null, "There exist unsaved files. Save files?", "Unsaved files", JOptionPane.YES_NO_OPTION);
            if (reply == JOptionPane.YES_OPTION) {
                java.awt.event.ActionEvent evt = null;
                for(int i = 0; i < comps.length; i++){
                    inTAreasTabbed.setSelectedComponent(comps[i]);
                    //            System.out.println("i: "+i);
                    evt = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "SELECTED");
                    jMenuItem6ActionPerformed(evt);
                }
            }
            else {
                //           JOptionPane.showMessageDialog(null, "GOODBYE");
                //           System.exit(0);
            }
        }
    }

    @Override
    public void windowClosed(WindowEvent e) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void windowIconified(WindowEvent e) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void windowDeiconified(WindowEvent e) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void windowActivated(WindowEvent e) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void windowDeactivated(WindowEvent e) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
