/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package visual;

import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Box;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;

/**
 *
 * @author pchr
 */
public class PlotFrame extends javax.swing.JFrame {
    public List<plotfunction> thePlotFunctions;
    protected final JFileChooser jFile = new JFileChooser();
    double ylimmin,ylimmax,xlimmin,xlimmax;
    boolean ylmin,ylmax,xlmin,xlmax;
    boolean AutoColor=false;
    boolean legend=false;
    double lxrelpos=0.8;
    double lyrelpos=0.8;
//    PlotPanel plotPanel1;

    /**
     * Creates new form PlotFrame
     */
    public PlotFrame() {
        setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("plot.png")));
        thePlotFunctions = new ArrayList<>();
        ylmin=false;
        ylmax=false;
        xlmin=false;
        xlmax=false;
        this.setTitle("SDE Figure");
        initComponents();
        this.setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
//        this.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        this.plotPanel1.setcoordLabel(xy_jLabel);
        this.plotPanel1.setScaleLabel(jLabel2);
        
        plotPanel1.setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
        plotPanel1.setParentFrame(this);
        
        FilePlot_m.setMnemonic(KeyEvent.VK_F);
        EditPlot_m.setMnemonic(KeyEvent.VK_E);
        
        // Set accelerators
        Action theAction = new AbstractAction("Save") {
 
            @Override
            public void actionPerformed(ActionEvent e) {
                //com.println("Saving...");
            }
        };
        theAction.putValue(Action.ACCELERATOR_KEY,
            KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_DOWN_MASK));
        SavePlot_mi.setAction(theAction);
        
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent event) {
                exitProcedure();
            }
        });
        
        this.pack();
        this.setLocationRelativeTo(null);
    }
    
    public PlotPanel getPlotPanel(){return this.plotPanel1;}
    
    public void setMarker(boolean w){
        for (int i = 0; i < thePlotFunctions.size(); i++) {
                thePlotFunctions.get(i).setMarker(w);
        }
    }
    
    public void setMarkerFill(boolean w){
        for (int i = 0; i < thePlotFunctions.size(); i++) {
                thePlotFunctions.get(i).setMarkerFill(w);
        }
    }
    
    public void setTex(boolean b){this.plotPanel1.setTex(b);}
        
    public void exitProcedure() {
        this.dispose();
        this.plotPanel1.setAssociatedFile(null);
        //System.exit(0);
    }
    
    public void addFunction(plotfunction f){
        thePlotFunctions.add(f);
    }
    
    public void addFunction(plotfunction f, String name){
        f.setName(name);
        thePlotFunctions.add(f);
    }
    
    public void vline(double x, java.awt.Color c){
        this.plotPanel1.vline(x,c);
    }
    
    public void vline(double x){
        vline(x, null);
    }
    
    public void hline(double y, java.awt.Color c){
        this.plotPanel1.hline(y,c);
    }
    
    public void hline(double y){
        hline(y, null);
    }
    
    public void incline(double k, double x, double y, java.awt.Color c){
        this.plotPanel1.incline(k,x, y,c);
    }
    
    public void incline(double k, double x, double y){
        incline(k,x, y, null);
    }
    
    public void text(double x, double y, String text, java.awt.Color c){
        this.plotPanel1.text(x, y, text, c);
    }
    
    public void text(double x, double y, String text){
        this.plotPanel1.text(x, y, text);
    }
    
    public void line(double xs, double ys, double xe, double ye, java.awt.Color c){
        this.plotPanel1.line(xs, ys, xe, ye, c);
    }
    
    public void line(double xs, double ys, double xe, double ye){
        this.plotPanel1.line(xs, ys, xe, ye);
    }
    
    public int NumOfFunctions(){return thePlotFunctions.size();}
    
    public List<plotfunction> getPlotFunctions(){return this.thePlotFunctions;}
    
    public void Title(String t){plotPanel1.setTitle(t);}
    
    public void xLabel(String t){plotPanel1.set_xLabel(t);}
    
    public void yLabel(String t){plotPanel1.set_yLabel(t);}
    
    public int getMargin_x(){return this.plotPanel1.getMargin_x();}
    
    public int getMargin_y(){return this.plotPanel1.getMargin_y();}
    
    public void setMargin_x(int m){this.plotPanel1.setMargin_x(m);}
    
    public void setMargin_y(int m){this.plotPanel1.setMargin_y(m);}
    
    public void setylimit_min(double v){this.ylimmin=v;}
    
    public void setxlimit_min(double v){this.xlimmin=v;}
    
    public void setylimit_max(double v){this.ylimmax=v;}
    
    public void setxlimit_max(double v){this.xlimmax=v;}
    
    public void setlimits(double vx_min, double vx_max, double vy_min, double vy_max){
        this.xlimmin=vx_min;
        this.xlimmax=vx_max;
        this.ylimmin=vy_min;
        this.ylimmax=vy_max;
    }
    
    public void activatelimits(boolean vx_min, boolean vx_max, boolean vy_min, boolean vy_max){
        xlmin=vx_min;
        xlmax=vx_max;
        ylmin=vy_min;
        ylmax=vy_max;
    }
    
    public void activate_xlimit_min(boolean b){xlmin=b;}
    public void activate_ylimit_min(boolean b){ylmin=b;}
    public void activate_xlimit_max(boolean b){xlmax=b;}
    public void activate_ylimit_max(boolean b){ylmax=b;}
    
    public double getylimit_min(){return this.ylimmin;}
    
    public double getxlimit_min(){return this.xlimmin;}
    
    public double getylimit_max(){return this.ylimmax;}
    
    public double getxlimit_max(){return this.xlimmax;}
    
    public boolean active_xlim_min(){return this.xlmin;}
    
    public boolean active_xlim_max(){return this.xlmax;}
    
    public boolean active_ylim_min(){return this.ylmin;}
    
    public boolean active_ylim_max(){return this.ylmax;}
    
    public void clear(){
        thePlotFunctions = new ArrayList<plotfunction>();
        ylmin=false;
        ylmax=false;
        xlmin=false;
        xlmax=false;
        plotPanel1.adjust();
        plotPanel1.clear();
        plotPanel1.setTitle(null);
        //plotPanel1.
    }
    
    public boolean isNumeric(String str){
        try
        {
            double d = Double.parseDouble(str);
        }catch(NumberFormatException nfe){
            System.err.println("String: ("+str+"), not a numerical value!");
            return false;
        }
        return true;
    }
    
    
    public void setFormatXAxis(NumberFormat theDF){
        plotPanel1.setFormatXAxis(theDF);
    }
    
    public void setFormatYAxis(NumberFormat theDF){
        this.plotPanel1.setFormatYAxis(theDF);
    }
    
    public void setFormatAxis(NumberFormat theDF){
        this.plotPanel1.setFormatAxis(theDF);
    }
    
    public void setAutoColor(boolean b){this.AutoColor=b;}
    
    public boolean getAutoColor(){return this.AutoColor;}
    
    public void makeLegend(boolean t){
        this.legend=t;
    }
    
    public void makeLegend(){
        this.legend=true;
    }
    
    public void makeLegend(double xrelpos, double yrelpos){
        this.legend=true;
        this.lxrelpos=xrelpos;
        this.lyrelpos=yrelpos;
    }
    
    public void setTexfontsize(int n){this.plotPanel1.setTexfontsize(n);}

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        SetLimitsDialog = new javax.swing.JDialog();
        xmin_cb = new javax.swing.JCheckBox();
        xmax_cb = new javax.swing.JCheckBox();
        ymin_cb = new javax.swing.JCheckBox();
        ymax_cb = new javax.swing.JCheckBox();
        x_min_tf = new javax.swing.JTextField();
        x_max_tf = new javax.swing.JTextField();
        y_min_tf = new javax.swing.JTextField();
        y_max_tf = new javax.swing.JTextField();
        setlimi_OK_but = new javax.swing.JButton();
        setlimi_Cancel_but = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        plotPanel1 = new visual.PlotPanel();
        xy_jLabel = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jMenuBar1 = new javax.swing.JMenuBar();
        FilePlot_m = new javax.swing.JMenu();
        SavePlot_mi = new javax.swing.JMenuItem();
        jMenuItem2 = new javax.swing.JMenuItem();
        jMenuItem3 = new javax.swing.JMenuItem();
        EditPlot_m = new javax.swing.JMenu();
        Plt_grid_mi = new javax.swing.JMenuItem();
        Plt_AutoScale_mi = new javax.swing.JMenuItem();
        Plt_limits_mi = new javax.swing.JMenuItem();
        pmargins_mi = new javax.swing.JMenuItem();
        jMenuItem1 = new javax.swing.JMenuItem();

        SetLimitsDialog.setTitle("Set plot's limits");
        SetLimitsDialog.setAlwaysOnTop(true);
        SetLimitsDialog.setMinimumSize(new java.awt.Dimension(319, 180));
        SetLimitsDialog.setResizable(false);
        SetLimitsDialog.getContentPane().setLayout(new java.awt.GridBagLayout());

        xmin_cb.setText("x_min");
        xmin_cb.setMaximumSize(new java.awt.Dimension(69, 23));
        xmin_cb.setMinimumSize(new java.awt.Dimension(69, 23));
        xmin_cb.setPreferredSize(new java.awt.Dimension(69, 23));
        xmin_cb.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                xmin_cbStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(15, 12, 0, 0);
        SetLimitsDialog.getContentPane().add(xmin_cb, gridBagConstraints);

        xmax_cb.setText("x_max");
        xmax_cb.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                xmax_cbStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(9, 12, 0, 0);
        SetLimitsDialog.getContentPane().add(xmax_cb, gridBagConstraints);

        ymin_cb.setText("y_min");
        ymin_cb.setMaximumSize(new java.awt.Dimension(69, 23));
        ymin_cb.setMinimumSize(new java.awt.Dimension(69, 23));
        ymin_cb.setPreferredSize(new java.awt.Dimension(69, 23));
        ymin_cb.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                ymin_cbStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(9, 12, 0, 0);
        SetLimitsDialog.getContentPane().add(ymin_cb, gridBagConstraints);

        ymax_cb.setText("y_max");
        ymax_cb.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                ymax_cbStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(9, 12, 0, 0);
        SetLimitsDialog.getContentPane().add(ymax_cb, gridBagConstraints);

        x_min_tf.setText("jTextField1");
        x_min_tf.setEnabled(false);
        x_min_tf.setMaximumSize(new java.awt.Dimension(84, 28));
        x_min_tf.setMinimumSize(new java.awt.Dimension(84, 28));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(12, 12, 0, 0);
        SetLimitsDialog.getContentPane().add(x_min_tf, gridBagConstraints);

        x_max_tf.setText("jTextField2");
        x_max_tf.setEnabled(false);
        x_max_tf.setMaximumSize(new java.awt.Dimension(84, 28));
        x_max_tf.setMinimumSize(new java.awt.Dimension(84, 28));
        x_max_tf.setPreferredSize(new java.awt.Dimension(84, 28));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 12, 0, 0);
        SetLimitsDialog.getContentPane().add(x_max_tf, gridBagConstraints);

        y_min_tf.setText("jTextField3");
        y_min_tf.setEnabled(false);
        y_min_tf.setMaximumSize(new java.awt.Dimension(84, 28));
        y_min_tf.setMinimumSize(new java.awt.Dimension(84, 28));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 12, 0, 0);
        SetLimitsDialog.getContentPane().add(y_min_tf, gridBagConstraints);

        y_max_tf.setText("jTextField4");
        y_max_tf.setEnabled(false);
        y_max_tf.setMaximumSize(new java.awt.Dimension(84, 28));
        y_max_tf.setMinimumSize(new java.awt.Dimension(84, 28));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 12, 27, 0);
        SetLimitsDialog.getContentPane().add(y_max_tf, gridBagConstraints);

        setlimi_OK_but.setText("OK");
        setlimi_OK_but.setMaximumSize(new java.awt.Dimension(56, 28));
        setlimi_OK_but.setMinimumSize(new java.awt.Dimension(56, 28));
        setlimi_OK_but.setPreferredSize(new java.awt.Dimension(56, 28));
        setlimi_OK_but.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                setlimi_OK_butActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 12, 27, 0);
        SetLimitsDialog.getContentPane().add(setlimi_OK_but, gridBagConstraints);

        setlimi_Cancel_but.setText("Cancel");
        setlimi_Cancel_but.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                setlimi_Cancel_butActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 27, 12);
        SetLimitsDialog.getContentPane().add(setlimi_Cancel_but, gridBagConstraints);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 431, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 28, Short.MAX_VALUE)
        );

        plotPanel1.setLayout(new java.awt.BorderLayout());

        xy_jLabel.setText("[x,y]");
        xy_jLabel.setMaximumSize(new java.awt.Dimension(50, 17));
        xy_jLabel.setMinimumSize(new java.awt.Dimension(50, 17));

        jLabel2.setText("scale: 1.0");

        FilePlot_m.setText("File");

        SavePlot_mi.setText("Save");
        SavePlot_mi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SavePlot_miActionPerformed(evt);
            }
        });
        FilePlot_m.add(SavePlot_mi);

        jMenuItem2.setText("Save As");
        jMenuItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem2ActionPerformed(evt);
            }
        });
        FilePlot_m.add(jMenuItem2);

        jMenuItem3.setText("Close");
        FilePlot_m.add(jMenuItem3);

        jMenuBar1.add(FilePlot_m);

        EditPlot_m.setText("Edit");
        EditPlot_m.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                EditPlot_mActionPerformed(evt);
            }
        });

        Plt_grid_mi.setText("Grid");
        Plt_grid_mi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Plt_grid_miActionPerformed(evt);
            }
        });
        EditPlot_m.add(Plt_grid_mi);

        Plt_AutoScale_mi.setText("Autoscale");
        Plt_AutoScale_mi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Plt_AutoScale_miActionPerformed(evt);
            }
        });
        EditPlot_m.add(Plt_AutoScale_mi);

        Plt_limits_mi.setText("Limits");
        Plt_limits_mi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Plt_limits_miActionPerformed(evt);
            }
        });
        EditPlot_m.add(Plt_limits_mi);

        pmargins_mi.setText("Set Margins ...");
        pmargins_mi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pmargins_miActionPerformed(evt);
            }
        });
        EditPlot_m.add(pmargins_mi);

        jMenuItem1.setText("Set Legend ...");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        EditPlot_m.add(jMenuItem1);

        jMenuBar1.add(EditPlot_m);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(plotPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(xy_jLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(plotPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 276, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(xy_jLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel2))))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jMenuItem2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem2ActionPerformed
        // TODO add your handling code here:
        BufferedImage image = new BufferedImage(this.getWidth(), this.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics2D = image.createGraphics();
        
        this.paint(graphics2D);
        jFile.showSaveDialog(this);
        Path pth = null;
        if(jFile.getSelectedFile()==null) {
        } else {
            pth = jFile.getSelectedFile().toPath();
            JOptionPane.showMessageDialog(null, pth.toString());
            try {
                ImageIO.write(image, "png", new File(pth.toString()));
            } catch (IOException ox) {
                // TODO: handle exception
                ox.printStackTrace();
            }
            this.plotPanel1.setAssociatedFile(jFile.getSelectedFile());
        }
        
    }//GEN-LAST:event_jMenuItem2ActionPerformed

    private void Plt_grid_miActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Plt_grid_miActionPerformed
        // TODO add your handling code here:
        this.plotPanel1.setDraw_grid();
    }//GEN-LAST:event_Plt_grid_miActionPerformed

    private void Plt_AutoScale_miActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Plt_AutoScale_miActionPerformed
        // TODO add your handling code here:
        this.plotPanel1.adjust();
    }//GEN-LAST:event_Plt_AutoScale_miActionPerformed

    private void SavePlot_miActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SavePlot_miActionPerformed
        // TODO add your handling code here:
        if(plotPanel1.getAssociatedFile()==null){
            BufferedImage image = new BufferedImage(this.getWidth(), this.getHeight(), BufferedImage.TYPE_INT_RGB);
            Graphics2D graphics2D = image.createGraphics();
            
            this.paint(graphics2D);
            jFile.showSaveDialog(this);
            Path pth = null;
            if(jFile.getSelectedFile()==null) {
            } else {
                pth = jFile.getSelectedFile().toPath();
                JOptionPane.showMessageDialog(null, pth.toString());
                try {
                    ImageIO.write(image, "png", new File(pth.toString()));
                } catch (IOException ox) {
                    // TODO: handle exception
                    ox.printStackTrace();
                }
                this.plotPanel1.setAssociatedFile(jFile.getSelectedFile());
            }
        }else{
            BufferedImage image = new BufferedImage(this.getWidth(), this.getHeight(), BufferedImage.TYPE_INT_RGB);
            Graphics2D graphics2D = image.createGraphics();

            this.paint(graphics2D);
            
            Path pth = null;
            
            pth = plotPanel1.getAssociatedFile().toPath();
            JOptionPane.showMessageDialog(null, pth.toString());
            try {
                ImageIO.write(image, "png", new File(pth.toString()));
            } catch (IOException ox) {
                // TODO: handle exception
                ox.printStackTrace();
            }
        }
    }//GEN-LAST:event_SavePlot_miActionPerformed

    private void setlimi_Cancel_butActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_setlimi_Cancel_butActionPerformed
        // TODO add your handling code here:
        SetLimitsDialog.setVisible(false);
    }//GEN-LAST:event_setlimi_Cancel_butActionPerformed

    private void EditPlot_mActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_EditPlot_mActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_EditPlot_mActionPerformed

    private void Plt_limits_miActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Plt_limits_miActionPerformed
        // TODO add your handling code here:
        x_min_tf.setText(String.valueOf(plotPanel1.getXmin()));
        y_min_tf.setText(String.valueOf(plotPanel1.getYmin()));
        x_max_tf.setText(String.valueOf(plotPanel1.getXmax()));
        y_max_tf.setText(String.valueOf(plotPanel1.getYmax()));
        SetLimitsDialog.setVisible(true);
    }//GEN-LAST:event_Plt_limits_miActionPerformed

    private void xmin_cbStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_xmin_cbStateChanged
        // TODO add your handling code here:
        x_min_tf.setEnabled(xmin_cb.isSelected());
    }//GEN-LAST:event_xmin_cbStateChanged

    private void xmax_cbStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_xmax_cbStateChanged
        // TODO add your handling code here:
        x_max_tf.setEnabled(xmax_cb.isSelected());
    }//GEN-LAST:event_xmax_cbStateChanged

    private void ymin_cbStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_ymin_cbStateChanged
        // TODO add your handling code here:
        y_min_tf.setEnabled(ymin_cb.isSelected());
    }//GEN-LAST:event_ymin_cbStateChanged

    private void ymax_cbStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_ymax_cbStateChanged
        // TODO add your handling code here:
        y_max_tf.setEnabled(ymax_cb.isSelected());
    }//GEN-LAST:event_ymax_cbStateChanged

    private void setlimi_OK_butActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_setlimi_OK_butActionPerformed
        // TODO add your handling code here:
        if(xmin_cb.isSelected()&&isNumeric(x_min_tf.getText())){setxlimit_min(Double.parseDouble(x_min_tf.getText()));activate_xlimit_min(true);}else{activate_xlimit_min(false);}
        if(xmax_cb.isSelected()&&isNumeric(x_max_tf.getText())){setxlimit_max(Double.parseDouble(x_max_tf.getText()));activate_xlimit_max(true);}else{activate_xlimit_max(false);}
        if(ymin_cb.isSelected()&&isNumeric(y_min_tf.getText())){setylimit_min(Double.parseDouble(y_min_tf.getText()));activate_ylimit_min(true);}else{activate_ylimit_min(false);}
        if(ymax_cb.isSelected()&&isNumeric(y_max_tf.getText())){setylimit_max(Double.parseDouble(y_max_tf.getText()));activate_ylimit_max(true);}else{activate_ylimit_max(false);}
        SetLimitsDialog.setVisible(false);
        this.plotPanel1.repaint();
    }//GEN-LAST:event_setlimi_OK_butActionPerformed

    private void pmargins_miActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pmargins_miActionPerformed
        // TODO add your handling code here:
        int mx=this.getMargin_x();
        int my=this.getMargin_y();
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
            this.setMargin_x(Integer.parseInt(xField.getText()));
            this.setMargin_y(Integer.parseInt(yField.getText()));
            this.plotPanel1.plot();
        }
    }//GEN-LAST:event_pmargins_miActionPerformed

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        // TODO add your handling code here:
        double lx=this.lxrelpos;
        double ly=this.lyrelpos;
        JTextField xField = new JTextField(5); xField.setText(String.valueOf(lx));
        JTextField yField = new JTextField(5); yField.setText(String.valueOf(ly));
        
        
        JPanel myPanel = new JPanel();
        myPanel.add(new JLabel("x relative position:"));
        myPanel.add(xField);
        myPanel.add(Box.createHorizontalStrut(15)); // a spacer
        myPanel.add(new JLabel("y relative position:"));
        myPanel.add(yField);
        myPanel.add(Box.createHorizontalStrut(15));

        int result = JOptionPane.showConfirmDialog(this, myPanel, 
               "Please enter x and y margins' integer values", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            this.lxrelpos=Double.parseDouble(xField.getText());
            this.lyrelpos=Double.parseDouble(yField.getText());
            this.plotPanel1.plot();
        }
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenu EditPlot_m;
    private javax.swing.JMenu FilePlot_m;
    private javax.swing.JMenuItem Plt_AutoScale_mi;
    private javax.swing.JMenuItem Plt_grid_mi;
    private javax.swing.JMenuItem Plt_limits_mi;
    private javax.swing.JMenuItem SavePlot_mi;
    private javax.swing.JDialog SetLimitsDialog;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JMenuItem jMenuItem3;
    private javax.swing.JPanel jPanel1;
    private visual.PlotPanel plotPanel1;
    private javax.swing.JMenuItem pmargins_mi;
    private javax.swing.JButton setlimi_Cancel_but;
    private javax.swing.JButton setlimi_OK_but;
    private javax.swing.JTextField x_max_tf;
    private javax.swing.JTextField x_min_tf;
    private javax.swing.JCheckBox xmax_cb;
    private javax.swing.JCheckBox xmin_cb;
    private javax.swing.JLabel xy_jLabel;
    private javax.swing.JTextField y_max_tf;
    private javax.swing.JTextField y_min_tf;
    private javax.swing.JCheckBox ymax_cb;
    private javax.swing.JCheckBox ymin_cb;
    // End of variables declaration//GEN-END:variables
}
