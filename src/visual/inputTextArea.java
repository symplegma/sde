/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package visual;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import javax.swing.JFileChooser;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
/**
 *
 * @author pchr
 * https://github.com/bobbylight/RSyntaxTextArea
 * http://stackoverflow.com/questions/2020796/swing-text-editor-that-color-and-highlight
 * http://stackoverflow.com/questions/12678104/text-editor-with-syntax-highlighting-and-line-numbers
 * http://www.antonioshome.net/kitchen/netbeans/nbms-standalone.php
 * https://github.com/yannrichet/jxtextpane
 */
public class inputTextArea extends RSyntaxTextArea{
    TextTransfer theTextTr = new TextTransfer();
    private static sdeShell theInterpreter;
//    StringBuffer theSB;
    private File AssociatedFile=null;
    private static sdeFrame theSDEframe;
    private static boolean commentize=false;
    private boolean changed=false;
    private int index_in_tabs;

    
    public inputTextArea(){
//        this.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVA);
        this.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_GROOVY);
        this.setCodeFoldingEnabled(true);
        
        //CompletionProvider provider = createCompletionProvider();
        
//        theSB = new StringBuffer();
        final String theName="run";
        final String theName0="run selected";
        final String theName1="copy";
        final String theName2="select all";
        final String theName3="save";
        final String theName4="array copy";
        ActionListener menuListener = new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                if(event.getActionCommand().equals(theName)){
                    getPopupMenu().setVisible(false);
                    grun(0);
                }else if(event.getActionCommand().equals(theName0)){
                    getPopupMenu().setVisible(false);
                    grun(1);
                }else if(event.getActionCommand().equals(theName1)){
                    copySelection();
                }else if(event.getActionCommand().equals(theName2)){
                    selAll();
                }else if(event.getActionCommand().equals(theName3)){
                    save2file();
                }else if(event.getActionCommand().equals(theName4)){
                    getPopupMenu().setVisible(false);
                    arrayCopy();
                }
            }
        };
//
        JMenuItem item;
        this.getPopupMenu().addSeparator();
        this.getPopupMenu().add(item = new JMenuItem(theName));
        item.addActionListener(menuListener);
        addMouseListener(new MousePopupListener());
        
        getPopupMenu().add(item = new JMenuItem(theName0));
        item.addActionListener(menuListener);
        addMouseListener(new MousePopupListener());
        
        this.addKeyListener(new KeyAdapter()
        {
            public void keyPressed(KeyEvent ke)
            {
                if(!changed)if(!(ke.getKeyChar()==27||ke.getKeyChar()==65535))//this section will execute only when user is editing the JTextField
                {
                    //System.out.println("User is editing something in TextField of index_in_tabs= "+index_in_tabs);
                    changed=true;
//                    theSDEframe.inTAreasTabbed.getComponentAt(index_in_tabs).setName("*"+
//                            theSDEframe.inTAreasTabbed.getComponentAt(index_in_tabs).getName());
                    theSDEframe.inTAreasTabbed.setTitleAt(index_in_tabs, "*"+theSDEframe.inTAreasTabbed.getTitleAt(index_in_tabs));
                }
                
                if ((ke.getKeyCode() == KeyEvent.VK_ENTER) && ((ke.getModifiers() & KeyEvent.SHIFT_MASK) != 0)) {
                    if(getSelectedText()!=null){
                        grun(1);
                    }else{
                        grun(0);
                    }
                }
            }
        });
        
        getPopupMenu().add(item = new JMenuItem(theName4));
        item.addActionListener(menuListener);
        addMouseListener(new MousePopupListener());
        
        this.addMouseWheelListener( new MouseWheelListener(){
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                if ( e.isControlDown() ) {
                    Font font = getFont();
                    float size = font.getSize() + e.getWheelRotation();
                    setFont( font.deriveFont(size) );
                }else {
                    // pass the event on to the scroll pane
                    getParent().dispatchEvent(e);
                }
            }
	} );

//        getPopupMenu().add(item = new JMenuItem(theName1));
//        item.addActionListener(menuListener);
//        addMouseListener(new MousePopupListener());
//
//        getPopupMenu().add(item = new JMenuItem(theName2));
//        item.addActionListener(menuListener);
//        addMouseListener(new MousePopupListener());
//
//        getPopupMenu().add(item = new JMenuItem(theName3));
//        item.addActionListener(menuListener);
//        addMouseListener(new MousePopupListener());
    }
    
    public boolean getChanged(){return this.changed;}
    
    public void setChanged(boolean b){this.changed=b;}
    
    public void set_index_in_tabs(int ind){this.index_in_tabs=ind;}
    
    public int get_index_in_tabs(){return this.index_in_tabs;}
    
    public void setCommentize(boolean b){commentize=b;}
    
    public static void setInterpreter(sdeShell ip){
        inputTextArea.theInterpreter=ip;
    }
    
    class MousePopupListener extends MouseAdapter {
        @Override
        public void mousePressed(MouseEvent e) {
            checkPopup(e);
        }

        public void mouseClicked(MouseEvent e) {
            checkPopup(e);
        }

        public void mouseReleased(MouseEvent e) {
            checkPopup(e);
        }

        private void checkPopup(MouseEvent e) {
            if (e.isPopupTrigger()) {
                getPopupMenu().show(inputTextArea.this, e.getX(), e.getY());
            }
        }
    }

    private void grun(int mode){
//        theSDEframe.get_outTAreasTabbed().setSelectedIndex(0);
        String command;
        switch(mode){
            case 0:
                command = this.getText();
                break;
            default:
                command = this.getSelectedText();
                break;
        }
        theInterpreter.eval(command);
//        String[] lines = command.split("\\r?\\n");
        String[] lines = command.split("\n");
        String recommand="";
        
        for (String line : lines) {
            if(commentize){
                if (!"".equals(line)) {
                    if (line.trim().charAt(0) != '#') {
                        recommand += "# " + line+"\n";
                    } else {
                        recommand += line + "\n";
                    }
                }else{
                    recommand += " \n";
                }
            }else{
                recommand += line + "\n";
            }
        }
        switch(mode){
            case 0:
                this.setText(recommand);
                break;
            default:
                if("\n".equals(recommand.substring(recommand.length()-2, recommand.length()-1))){
                    this.replaceSelection(recommand.substring(0, recommand.length()-2));
                }else{
                    this.replaceSelection(recommand);
                }
//                this.replaceSelection(recommand.substring(0, recommand.length()-2));
                break;
        }
        theSDEframe.repaintGraphicsPanel();
    }

    private void selAll(){
        this.selectAll();
    }
    
    private void save2file(){
        String text = this.getText();

        JFileChooser chooser = new JFileChooser();
        chooser.setCurrentDirectory( new File( "./") );
        int actionDialog = chooser.showSaveDialog(this);
        if (actionDialog == JFileChooser.APPROVE_OPTION)
        {
            File fileName = new File(chooser.getSelectedFile( ) + "" );
            if(fileName == null)
                return;
            if(fileName.exists())
            {
                actionDialog = JOptionPane.showConfirmDialog(this,
                                   "Replace existing file?");
                if (actionDialog == JOptionPane.NO_OPTION)
                    return;
            }
            try
            {
                BufferedWriter out = new BufferedWriter(new FileWriter(fileName));

                    out.write(text);
                    out.close();
            }
            catch(Exception e)
            {
                 System.err.println("Error: " + e.getMessage());
            }
        }
    }

    private void copySelection(){
        String selected=this.getSelectedText();
        theTextTr.setClipboardContents(selected);
    }
    
    public void commentSelection2(){
        String command=this.getSelectedText();
        String[] lines = command.split("\n");
        String recommand="";
        
        for (String line : lines) {
            if (line.trim().charAt(0) != '#'){
                recommand +="#" + line+"\n";
            }else{
                recommand += line.replaceFirst("#", "")+"\n";
            }
            
        }
        if(recommand.substring(recommand.length()-2, recommand.length()-1)=="\n"){
            this.replaceSelection(recommand.substring(0, recommand.length()-2));
        }else{
            this.replaceSelection(recommand);
        }
    }
    
    public void setRunSelected(boolean bvar){
        this.getPopupMenu().getComponent(1).setEnabled(bvar);
    }
    
    public void setAssociatedFile(File af){this.AssociatedFile=af;}
    
    public File getAssociatedFile(){return this.AssociatedFile;}
    
    public void setCDEframe(sdeFrame aCDEframe){theSDEframe=aCDEframe;}
    
    private void arrayCopy(){
        String test1= JOptionPane.showInputDialog("Variable name for the array: ");
        inputTextArea.theInterpreter.passArray(test1, this.getSelectedText());
    }
}