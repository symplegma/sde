/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package visual;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import javax.swing.JFileChooser;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;

/**
 *
 * @author pchr
 */
public class outputTextArea extends javax.swing.JTextArea{
    public JPopupMenu popup;
    TextTransfer theTextTr = new TextTransfer();
    StringBuffer theSB;
    String title="";
    private boolean changed=false;
    private static sdeFrame theSDEframe;
    private int index_in_tabs;
    private static sdeShell theInterpreter;
    
    public outputTextArea(){
        theSB = new StringBuffer();
        popup = new JPopupMenu();
        final String theName1="copy";
        final String theName2="select all";
        final String theName3="save";
        final String theName4="clear";
        final String theName5="array copy";
        ActionListener menuListener = new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                if(event.getActionCommand().equals(theName1)){
                    copySelection();
                }else if(event.getActionCommand().equals(theName2)){
                    selAll();
                }else if(event.getActionCommand().equals(theName3)){
                    save2file();
                }else if(event.getActionCommand().equals(theName4)){
                    clear();
                }else if(event.getActionCommand().equals(theName5)){
                    popup.setVisible(false);
                    arrayCopy();
                }
            }
        };

        JMenuItem item;
        popup.add(item = new JMenuItem(theName1));
        item.addActionListener(menuListener);
        addMouseListener(new outputTextArea.MousePopupListener());
        
        popup.add(item = new JMenuItem(theName2));
        item.addActionListener(menuListener);
        addMouseListener(new outputTextArea.MousePopupListener());

        popup.add(item = new JMenuItem(theName3));
        item.addActionListener(menuListener);
        addMouseListener(new outputTextArea.MousePopupListener());

        popup.add(item = new JMenuItem(theName5));
        item.addActionListener(menuListener);
        addMouseListener(new outputTextArea.MousePopupListener());

        popup.add(item = new JMenuItem(theName4));
        item.addActionListener(menuListener);
        addMouseListener(new outputTextArea.MousePopupListener());
        
    }
    
    public static void setInterpreter(sdeShell ip){
        outputTextArea.theInterpreter=ip;
    }
    
    public boolean getChanged(){return this.changed;}
    
    public void setChanged(boolean b){this.changed=b;}
    
    public void setSDEframe(sdeFrame aSDEframe){theSDEframe=aSDEframe;}
    
    public void set_index_in_tabs(int ind){this.index_in_tabs=ind;}
    
    public int get_index_in_tabs(){return this.index_in_tabs;}
    
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
    
    private void clear(){
        this.setText(null);
        this.append(title+'\n');
    }
    
    private void arrayCopy(){
        String test1= JOptionPane.showInputDialog("Variable name for the array: ");
        outputTextArea.theInterpreter.passArray(test1, this.getSelectedText());
    }

    private void copySelection(){
        String selected=this.getSelectedText();
        theTextTr.setClipboardContents(selected);
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
                popup.show(outputTextArea.this, e.getX(), e.getY());
            }
        }
    }
        
        public void setTitle(String title){this.title=title;}
}
