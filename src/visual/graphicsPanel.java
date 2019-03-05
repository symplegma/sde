/*******************************************************************************
* Symplegma Development Environment - SDE.                                                                      *
* Copyright (C) 2009-2017 C.G. Panagiotopoulos [http://www.symplegma.org]      *
*                                                                              *
* This program is free software; you can redistribute it and/or modify         *
* it under the terms of the GNU General Public License version 3, as           *
* published by the Free Software Foundation.                                   *
*                                                                              *
* This program is distributed in the hope that it will be useful,              *
* but WITHOUT ANY WARRANTY; without even the implied warranty of               *
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the                *
* GNU General Public License for more details.                                 *
*                                                                              *
* You should have received a copy of the GNU General Public License            *
* along with this program.  If not, see < http://www.gnu.org/licenses/>.       *
*******************************************************************************/

// *****************************************************************************
// $LastChangedDate$
// $LastChangedRevision$
// $LastChangedBy$
// $HeadURL$
// Author(s): C.G. Panagiotopoulos (pchr76@gmail.com)
// *****************************************************************************
package visual;

import climax.contraption;
import climax.jpde;
import climax.universe;
import edu.uta.futureye.core.Mesh;
import geom.Circle;
import geom.Point;
import geom.Rectangle;
import geom.Shape;
import geom.Triangle;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import jbem.ResultPoint;
import jdem.DEMdomain;
import jdem.Particle;
import jmat.AbstractMatrix;
import visual.GPDomain.DomainType;
//import jbem.ResultPoint;
//import jfem.Node;

/**
 *
 * @author pchr
 */
public class graphicsPanel extends javax.swing.JPanel implements Runnable,SDEgraphicsPanel{
    static int counting=0;
    int resolution=10;  // determines the resolution of the canvas

    int xDataCount;
    int yDataCount;

    double [] contourData;
    double dataRangeMin;
    double dataRangeMax;
    boolean explicitMinMax;

    int colorCount;
    Color[] colorArray;
    
    boolean trace=false;
    int nodeScale=200;
    boolean implicitSize=false;
    boolean repeat=true;
    int colormode=2;
    boolean colorinv=false;
    
    String title="Here is the graphics' area where geometric objects are displayed:";
    NumberFormat gridnumfx, gridnumfy;
    int FontSize;
    Color fontClr=Color.WHITE;
    Color gridClr=Color.DARK_GRAY;
    
    protected static List<GPDomain> theDomains;
//    protected VisualObject someVO;
    protected int xs,ys,xe,ye;
    private PrintStream com=System.out;
    private Color bcolor;
    public JPopupMenu popup;
    protected final JFileChooser FileChooser = new JFileChooser();
    double transX=0,transY=0;
    double Click_x=0.0,Click_y=0.0;
    private double zoomFactor=1.;
    private double Roll_x=0,Roll_y=0;
    // domain size
    private double min_x,min_y,max_x,max_y;
    // domain units
    private int numx,numy;
    private boolean draw_grid;
    private boolean draw_axes;
    private boolean draw_perimeter;
    private boolean iso_scale;
    private boolean drawtriangles;
    private universe theUniverse;
    int margin_x=30;
    int margin_y=20;
    int width;
    int height;
    private boolean drawNodesID=true;
    private boolean drawNodes=true;
    // 3D stuff
    private boolean threeD=false;
    // repsonse stuff ... to be modified
    private boolean resp=false;
    private int LC;
    private int step;
    private double defscale=1.0;
    public Thread runner;
    //public long Dt=100;
    public long Dts=100;
    private int step_start; 
    private int step_end; 
    private int step_per=1;
    private int theMQN=-1; // 1-Axial, 2-Shear, 3-Momment
    private double diagramscale=1.0;
    private boolean animate=false;
    public boolean diagramvalues=true;
    public int theTraction=-1; // 1-tx, 2-ty, 3-tz (?)
    public int theStress=-1;    // theStress  2D: 0 σxx, 1 σyy, 2 τxy
                                //          3D: 0 σxx, 1 σyy, 2 σzz, 3 τxy, 4 τyz, 5 τxz
                                // 6: VonMisses
    public int theDisp=-1; // 1-ux, 2-uy, 3-uz (?)
    public boolean GlobalMinMax=true;
    
    private boolean contour=false;
    private boolean Defcontour=false;
    private boolean contourMisses=false;
    private boolean contourDefMisses=false;
    private boolean discrdomain=true;
    
    private boolean showcolorbar=false;
    private boolean nodalAveraging=false;
    int colorbarlevels=10;
    NumberFormat cbarnumf;
    boolean horizontalcolorbar=false;
    boolean titlestep=true;
    
    public graphicsPanel(){
        super();
        this.setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
        theMQN=-1;
        theTraction=-1;
        theDisp=-1;
        bcolor=Color.BLACK;
        popup = new JPopupMenu();
        zoomFactor=1.;
        draw_axes=true;
        draw_grid=true;
        iso_scale=false;
        min_x=0.0; min_y=0.0; max_x=1.0;max_y=1.0;
        this.cbarnumf=new DecimalFormat("0.#####E0");
        this.gridnumfx=new DecimalFormat("##.00#");
        this.gridnumfy=new DecimalFormat("##.00#");
        final String theName1="adjust";
        final String theName2="save picture";
        ActionListener menuListener = new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                if(event.getActionCommand().equals(theName1)){
                    adjust();
                }
                if(event.getActionCommand().equals(theName2)){
                    saveImageSelection();
                }
            }
        };
        
        JMenuItem item;
        popup.add(item = new JMenuItem(theName1));
        item.addActionListener(menuListener);
        popup.add(item = new JMenuItem(theName2));
        item.addActionListener(menuListener);
        
        addMouseListener(new MousePopupListener());
        
        addMouseWheelListener(new MyMouseWL());
//        addMouseListener(new MouseListen());
        addMouseMotionListener(new MyMouseML());
        addMouseListener(new MA(this));
    }
    
    private void setGPDomains(){
//        List<GPDomain> tempDomain=new ArrayList<GPDomain>();
        if(theUniverse!=null){
            if(theDomains==null)theDomains =new ArrayList<GPDomain>();
            for(Iterator<DEMdomain> it=theUniverse.getDEMDomains().values().iterator(); it.hasNext();){
                DEMdomain theDomain = it.next();
                if(!this.existGPDomain(theDomain.getID(), DomainType.DEM)){
                    GPDomain aGPDomain= new GPDomain(theDomain.getID(),DomainType.DEM);
                    theDomains.add(aGPDomain);
                }
            }
            for(Iterator<jbem.Domain> it=theUniverse.getBEMDomains().values().iterator(); it.hasNext();){
                jbem.Domain theDomain = it.next();
                if(!this.existGPDomain(theDomain.getID(), DomainType.BEM)){
                    GPDomain aGPDomain= new GPDomain(theDomain.getID(),DomainType.BEM);
                    theDomains.add(aGPDomain);
                }
            } 
            for(Iterator<jfem.Domain> it=theUniverse.getFEMDomains().values().iterator(); it.hasNext();){
                jfem.Domain theDomain = it.next();
                if(!this.existGPDomain(theDomain.getID(), DomainType.FEM)){
                    GPDomain aGPDomain= new GPDomain(theDomain.getID(),DomainType.FEM);
                    theDomains.add(aGPDomain);
                }
            } 
            int count=0;
            for (Map.Entry<Integer,jpde> entry : theUniverse.getPDEDomains().entrySet()) {
                count++;
                int key = entry.getKey();
                jpde theDomain = entry.getValue();
                if(!this.existGPDomain(key, DomainType.PDE)){
                    GPDomain aGPDomain= new GPDomain(count,DomainType.PDE);
                    theDomains.add(aGPDomain);
                }
            }
            for(Iterator<gendomain.Domain> it=theUniverse.getGENDomains().values().iterator(); it.hasNext();){
                gendomain.Domain theDomain = it.next();
                if(!this.existGPDomain(theDomain.getID(), DomainType.GEN)){
                    GPDomain aGPDomain= new GPDomain(theDomain.getID(),DomainType.GEN);
                    theDomains.add(aGPDomain);
                }
            }
            for(Iterator<contraption> it=theUniverse.getContraptions().values().iterator(); it.hasNext();){
                contraption theDomain = it.next();
                if(!this.existGPDomain(theDomain.getID(), DomainType.CNT)){
                    GPDomain aGPDomain= new GPDomain(theDomain.getID(),DomainType.CNT);
                    theDomains.add(aGPDomain);
                }
            }
        }
    }
    
    public void setAnimationSpeed(int dta){this.Dts = dta;}
    public void IncreaseAnimationSpeed(double coef){this.Dts -= (int) coef*Dts;}
    public void DecreaseAnimationSpeed(double coef){this.Dts += (int) coef*Dts;}
    
    @Override
    public void setUniverse(universe theUniverse){
        this.theUniverse=theUniverse;
        this.setGPDomains();
    }
    
    public void set_size(double xmin, double xmax, double ymin, double ymax){
        implicitSize=true;
        this.min_x=xmin;
        this.min_y=ymin;
        this.max_x=xmax;
        this.max_y=ymax;
    }
    
    public void set_size(){
        implicitSize=false;
        this.setGPDomains();
        max_x=0.0;
        max_y=0.0;
        min_x=0.0;
        min_y=0.0;
        if(theUniverse!=null){
            for(Iterator<DEMdomain> it=theUniverse.getDEMDomains().values().iterator(); it.hasNext();){
                DEMdomain theDomain = it.next();
                if(this.getGPDomain(theDomain.getID(), DomainType.DEM).getPlotit()){
                    if(theDomain.maximum_X_coordinate()>max_x)max_x=theDomain.maximum_X_coordinate();
                    if(theDomain.maximum_Y_coordinate()>max_y)max_y=theDomain.maximum_Y_coordinate();
                    if(theDomain.minimum_X_coordinate()<min_x)min_x=theDomain.minimum_X_coordinate();
                    if(theDomain.minimum_Y_coordinate()<min_y)min_y=theDomain.minimum_Y_coordinate();
                }
            } 
            for(Iterator<jbem.Domain> it=theUniverse.getBEMDomains().values().iterator(); it.hasNext();){
                jbem.Domain theDomain = it.next();
                if(this.getGPDomain(theDomain.getID(), DomainType.BEM).getPlotit()){
                    if(theDomain.maximum_X_coordinate()>max_x)max_x=theDomain.maximum_X_coordinate();
                    if(theDomain.maximum_Y_coordinate()>max_y)max_y=theDomain.maximum_Y_coordinate();
                    if(theDomain.minimum_X_coordinate()<min_x)min_x=theDomain.minimum_X_coordinate();
                    if(theDomain.minimum_Y_coordinate()<min_y)min_y=theDomain.minimum_Y_coordinate();
                }
            } 
            for(Iterator<jfem.Domain> it=theUniverse.getFEMDomains().values().iterator(); it.hasNext();){
                jfem.Domain theDomain = it.next();
                if(this.getGPDomain(theDomain.getID(), DomainType.FEM).getPlotit()){
                    if(theDomain.maximum_X_coordinate()>max_x)max_x=theDomain.maximum_X_coordinate();
                    if(theDomain.maximum_Y_coordinate()>max_y)max_y=theDomain.maximum_Y_coordinate();
                    if(theDomain.minimum_X_coordinate()<min_x)min_x=theDomain.minimum_X_coordinate();
                    if(theDomain.minimum_Y_coordinate()<min_y)min_y=theDomain.minimum_Y_coordinate();
                }
            }
            for (Map.Entry<Integer,jpde> entry : theUniverse.getPDEDomains().entrySet()) {
                int key = entry.getKey();
                jpde theDomain =entry.getValue();
                if(this.getGPDomain(key, DomainType.PDE).getPlotit()){
                    for(edu.uta.futureye.core.Node theNode : theDomain.mesh.getNodeList()){
                        if(theNode.coords()[0]>max_x)max_x=theNode.coords()[0];
                        if(theNode.coords()[0]<min_x)min_x=theNode.coords()[0];
                        if(theNode.coords()[1]>max_y)max_y=theNode.coords()[1];
                        if(theNode.coords()[1]<min_y)min_y=theNode.coords()[1];
                    }
                }
            }
            for(Iterator<gendomain.Domain> it=theUniverse.getGENDomains().values().iterator(); it.hasNext();){
                gendomain.Domain theDomain = it.next();
                if(this.getGPDomain(theDomain.getID(), DomainType.GEN).getPlotit()){
                    if(theDomain.maximum_X_coordinate()>max_x)max_x=theDomain.maximum_X_coordinate();
                    if(theDomain.maximum_Y_coordinate()>max_y)max_y=theDomain.maximum_Y_coordinate();
                    if(theDomain.minimum_X_coordinate()<min_x)min_x=theDomain.minimum_X_coordinate();
                    if(theDomain.minimum_Y_coordinate()<min_y)min_y=theDomain.minimum_Y_coordinate();
                }
            }
            for(Iterator<contraption> it=theUniverse.getContraptions().values().iterator(); it.hasNext();){
                contraption theDomain = it.next();
                if(this.getGPDomain(theDomain.getID(), DomainType.CNT).getPlotit()){
                    if(theDomain.maximum_X_coordinate()>max_x)max_x=theDomain.maximum_X_coordinate();
                    if(theDomain.maximum_Y_coordinate()>max_y)max_y=theDomain.maximum_Y_coordinate();
                    if(theDomain.minimum_X_coordinate()<min_x)min_x=theDomain.minimum_X_coordinate();
                    if(theDomain.minimum_Y_coordinate()<min_y)min_y=theDomain.minimum_Y_coordinate();
                }
            }
            if(Math.abs(max_x-min_x)<jmat.MachinePrecision.getMachinePrecision()*10.0){max_x+=0.5;min_x-=0.5;}
            if(Math.abs(max_y-min_y)<jmat.MachinePrecision.getMachinePrecision()*10.0){max_y+=0.5;min_y-=0.5;}
            max_x+=transX;
            min_x+=transX;
            max_y+=transY;
            min_y+=transY;
        }
        double plmargin=0.05;
        max_x+=plmargin*(max_x-min_x);
        max_y+=plmargin*(max_y-min_y);
        min_x-=plmargin*(max_x-min_x);
        min_y-=plmargin*(max_y-min_y);
        double t;
        if(max_x<min_x){t=max_x;max_x=min_x;min_x=t;}
        if(max_y<min_y){t=max_y;max_y=min_y;min_y=t;}
        if(iso_scale){
            if((max_x-min_x)>(max_y-min_y)){
                max_y=min_y+(max_x-min_x);
            }else{
                max_x=min_x+(max_y-min_y);
            }
        }
    }
    
    class MyMouseWL implements MouseWheelListener{
      public void mouseWheelMoved(MouseWheelEvent e)  {
          implicitSize=false;
            Roll_x = xTransformationInv(e.getX());
            Roll_y = xTransformationInv(e.getY());
            //Zoom in
            if(e.getWheelRotation()>0){
                zoomFactor=1.1*zoomFactor;
                repaint();
            }
            //Zoom out
            if(e.getWheelRotation()<0){
                // bug: for zoomFactor>=0.0001 everything freeze
                //if(zoomFactor>=0.0001)
                zoomFactor=zoomFactor/1.1;
                repaint();
            }
        }
    }
    
    class MyMouseML implements MouseMotionListener{
        @Override
        public void mouseDragged(MouseEvent e) {
            transX-=xTransformationInv(e.getX())-Click_x;//
            transY-=yTransformationInv(e.getY())-Click_y;//
            repaint();
        }

        @Override
        public void mouseMoved(MouseEvent e) {
//            DecimalFormat df = new DecimalFormat("##.00#");
//            CoordLabel.setText("["+df.format(xTransformationInv(e.getX()))+","+df.format(yTransformationInv(e.getY()))+"]");
        }
      
    }
    
    private class MA extends MouseAdapter {
        graphicsPanel parentPanel;
        public MA(graphicsPanel parentPanel){this.parentPanel=parentPanel;}
        @Override
        public void mousePressed(MouseEvent e) {
            parentPanel.setCursor(new Cursor(Cursor.MOVE_CURSOR));
            //System.out.println("click...");
            Click_x = xTransformationInv(e.getX());
            Click_y = yTransformationInv(e.getY());
        }
        
        @Override
        public void mouseReleased(MouseEvent e) {
            parentPanel.setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
        }
    }
    
    public void setcomPrintStream(PrintStream com){this.com=com;}
    
    public int xTransformation(double x){
        int xd;
        double xmax=this.max_x;
        double xmin=this.min_x;
//        xd=(int) ((int) this.zoomFactor*((-xmin * xe + x * (xe - xs) + xmax * xs) / (xmax - xmin)));
        xd=(int) ((int) ((-xmin * xe + x * (xe - xs) + xmax * xs) / (xmax - xmin)));
        return xd;
    }
    
    public int yTransformation(double y){
        int yd;
        double ymax=this.max_y;
        double ymin=this.min_y;
//        yd=(int) ((int) this.zoomFactor*((-ymin * ye + y * (ye - ys) + ymax * ys) / (ymax - ymin)));
        yd=(int) ((int) ((-ymin * ye + y * (ye - ys) + ymax * ys) / (ymax - ymin)));
        return yd;
    }
    
    public double xTransformationInv(int xd){
        double x;
        double xmax=this.max_x;
        double xmin=this.min_x;
        x=(xd*(xmax-xmin)+xe*xmin-xs*xmax)/(xe-xs);
        return x;
    }
    
    public double yTransformationInv(int yd){
        double y;
        double ymax=this.max_y;
        double ymin=this.min_y;
        y=(yd*(ymax-ymin)+ye*ymin-ys*ymax)/(ye-ys);
        return y;
    }
    
    public void plot(){
        repaint();
    }
    
    @Override
    public void paintComponent(Graphics g) {
        setGPDomains();
        if(!implicitSize) set_size();
        setBackground(bcolor);
        //System.out.println("paintComponent");
	// Define graphics
        Graphics2D g2 = (Graphics2D) g;

        // Get dimensions
        width  = this.getWidth();
        height = this.getHeight();
//        com.println("[width,height] = ["+width+","+height+"]");
        // Clear window
        g2.setBackground(bcolor);
        g2.clearRect(0, 0, width, height);
        xs=margin_x;
        xe=width-margin_x;
        ye=margin_y;
        ys=height-margin_y;
        
        AffineTransform trans;
        Point2D trp;
        
        trans = new AffineTransform();
        trans.setToTranslation(-Roll_x,-Roll_y);
        trp = new Point2D.Double(max_x,max_y);
        max_x=trp.getX(); max_y=trp.getY();
        trp = new Point2D.Double(min_x,min_y);
        trans.transform(trp, trp);
        min_x=trp.getX(); min_y=trp.getY();
        
        trans = new AffineTransform();
        trans.setToScale(zoomFactor,zoomFactor);
        trp = new Point2D.Double(max_x,max_y);
        trans.transform(trp, trp);
        max_x=trp.getX(); max_y=trp.getY();
        trp = new Point2D.Double(min_x,min_y);
        trans.transform(trp, trp);
        min_x=trp.getX(); min_y=trp.getY();
        
        trans = new AffineTransform();
        trans.setToTranslation(Roll_x,Roll_y);
        trp = new Point2D.Double(max_x,max_y);
        max_x=trp.getX(); max_y=trp.getY();
        trp = new Point2D.Double(min_x,min_y);
        trans.transform(trp, trp);
        min_x=trp.getX(); min_y=trp.getY();
        
        Font currentFont = g2.getFont();
        if(FontSize>0){
            Font newFont = currentFont.deriveFont(Font.PLAIN, this.FontSize);
            g2.setFont(newFont);
        }
        if(draw_grid){
            g2.setColor(this.fontClr);
            numx=10;
            for(int i=0;i<=numx;i++){
                double d=min_x;
                if(i!=0){
                    d=min_x+(max_x-min_x)*i/numx;d/=zoomFactor;
                    g2.drawLine( (xs+(xe-xs)*(i-1)/numx+ xs+(xe-xs)*i/numx)/2, ys, (xs+(xe-xs)*(i-1)/numx+ xs+(xe-xs)*i/numx)/2, ys+4);
                    g2.drawLine( (xs+(xe-xs)*(i-1)/numx+ xs+(xe-xs)*i/numx)/2, ye, (xs+(xe-xs)*(i-1)/numx+ xs+(xe-xs)*i/numx)/2, ye-4);
                }
                g2.drawLine(xs+(xe-xs)*i/numx, ys, xs+(xe-xs)*i/numx, ys+8);
                g2.drawLine(xs+(xe-xs)*i/numx, ye, xs+(xe-xs)*i/numx, ye-8);
//                g2.drawString(df.format(d), xs+(xe-xs)*i/numx+3, height);
                g2.drawString(gridnumfx.format(d), xs+(xe-xs)*i/numx+3, ys+g2.getFontMetrics().getHeight());
            }

            numy=10;
            for(int i=0;i<=numy;i++){
                double d=min_y;
                if(i!=0){
                    d=min_y+(max_y-min_y)*i/numy;d/=zoomFactor;
                    g2.drawLine(xs, (ys+(ye-ys)*(i-1)/numy+ys+(ye-ys)*i/numy)/2, xs-4, (ys+(ye-ys)*(i-1)/numy+ys+(ye-ys)*i/numy)/2);
                    g2.drawLine(xe, (ys+(ye-ys)*(i-1)/numy+ys+(ye-ys)*i/numy)/2, xe+4, (ys+(ye-ys)*(i-1)/numy+ys+(ye-ys)*i/numy)/2);
                }
                g2.drawLine(xs, ys+(ye-ys)*i/numy, xs-8, ys+(ye-ys)*i/numy);
                g2.drawLine(xe, ys+(ye-ys)*i/numy, xe+8, ys+(ye-ys)*i/numy);
                g2.drawString(gridnumfy.format(d), xs-g2.getFontMetrics().stringWidth(gridnumfy.format(d))-3, ys+(ye-ys)*i/numy-3);
            }
            
            int num=width/5;
            for(int i=0;i<=num;i++){
                g2.setColor(this.gridClr);
                if(i==0 || i==num)g2.setColor(this.fontClr);
                g2.drawLine(xs+(xe-xs)*i/num, ys, xs+(xe-xs)*i/num, ye);
            }

            num=height/5;
            for(int i=0;i<=num;i++){
                g2.setColor(this.gridClr);
                if(i==0 || i==num)g2.setColor(this.fontClr);
                g2.drawLine(xs, ys+(ye-ys)*i/num, xe, ys+(ye-ys)*i/num);
            }
        }
        
        g2.setColor(this.getBackground());
        String ttitle="  "+title;
        if(this.titlestep){
            ttitle="  "+title+" ["+String.format("%03d", step)+"]  ";
        }
        g2.fillRect(xs+2, ye-10, g2.getFontMetrics().stringWidth(ttitle), 20);
        g2.setColor(this.fontClr);
        g2.drawString(ttitle, xs+3, ye+3);
        g2.setFont(currentFont);
        
        // draw something 
        if(theUniverse!=null){
            g2.setColor(Color.BLUE);
            for(Iterator<DEMdomain> it=theUniverse.getDEMDomains().values().iterator(); it.hasNext();){
                DEMdomain theDomain = it.next();
                if(this.getGPDomain(theDomain.getID(), DomainType.DEM).getPlotit()){
                    this.drawDEMDomain(g2, theDomain);
                    if(contour)drawContourDEMDomain(g2, theDomain);
                    if(animate){
                        if(step!=this.step_end-1){
                            step+=step_per;
                            if(step>=this.step_end)step=this.step_start;
                        }else{
                            step=this.step_start;
                        }
                    }
                    if(this.resp)this.drawDeformedDEMDomain(g2, theDomain);
                }
            } 
            g2.setColor(Color.BLUE);
            for(Iterator<jbem.Domain> it=theUniverse.getBEMDomains().values().iterator(); it.hasNext();){
                jbem.Domain theDomain = it.next();
                if(this.getGPDomain(theDomain.getID(), DomainType.BEM).getPlotit()){
                    if(contour)this.drawContour_u_BEMDomain(g2, theDomain);
                    if(contourMisses)drawContourStressBEMDomain(g2, theDomain);
                    this.drawBEMDomain(g2, theDomain);
                    if(animate){
                        if(step!=this.step_end-1){
                            step+=step_per;
                            if(step>=this.step_end)step=this.step_start;
                        }else{
                            step=this.step_start;
                        }
                    }
                    if(this.resp)this.drawDeformedBEMDomain(g2, theDomain);
                    if(this.theTraction==1||this.theTraction==2||this.theTraction==3)this.drawTractionsBEMDomain(g2, theDomain);
                    if(this.theDisp==1||this.theDisp==2||this.theDisp==3)this.drawDisplacementBEMDomain(g2, theDomain);
                }
            } 
            g2.setColor(Color.BLUE);
            for(Iterator<jfem.Domain> it=theUniverse.getFEMDomains().values().iterator(); it.hasNext();){
                jfem.Domain theDomain = it.next();
                if(this.getGPDomain(theDomain.getID(), DomainType.FEM).getPlotit()){
                    if(discrdomain)drawFEMDomain(g2, theDomain);
                    if(contour)drawContourFEMDomain(g2, theDomain);
                    if(Defcontour)drawContourDeformedFEMDomain(g2, theDomain);
                    if(contourMisses)drawContourStressFEMDomain(g2, theDomain);
                    if(contourDefMisses)drawContourDefStressFEMDomain(g2, theDomain);
                
                    if(animate){
                         if(step!=this.step_end-1){
                             step+=step_per;
                             if(step>=this.step_end)step=this.step_start;
                         }else{
                             step=this.step_start;
                         }
                     }
                    if(this.resp)this.drawDeformedFEMDomain(g2, theDomain);
                    if(this.theMQN==1||this.theMQN==2||this.theMQN==3)this.drawMQNFEMDomain(g2, theDomain);
                    if(this.theDisp==1||this.theDisp==2||this.theDisp==3 || this.theDisp==4||this.theDisp==5||this.theDisp==6)this.drawDisplacementFEMDomain(g2, theDomain);
                }
            } 
            g2.setColor(Color.BLUE);
            for (Map.Entry<Integer,jpde> entry : theUniverse.getPDEDomains().entrySet()) {
                int key = entry.getKey();
                jpde theDomain = entry.getValue();
                if(this.getGPDomain(key, DomainType.PDE).getPlotit()){
                    if(contour)drawContourPDEDomain(g2, theDomain);
                    if(discrdomain)this.drawPDEDomain(g2, theDomain);
                    if(animate){
                        if(step!=this.step_end-1){
                            step+=step_per;
                            if(step>=this.step_end)step=this.step_start;
                        }else{
                            step=this.step_start;
                        }
                    }
                }
            }
            g2.setColor(Color.BLUE);
            for(Iterator<gendomain.Domain> it=theUniverse.getGENDomains().values().iterator(); it.hasNext();){
                gendomain.Domain theDomain = it.next();
                if(this.getGPDomain(theDomain.getID(), DomainType.GEN).getPlotit()){
                    if(contour)drawContourGENDomain(g2, theDomain);
                    this.drawGENDomain(g2, theDomain);
                    if(this.draw_perimeter){
                        GeneralPath gp = new GeneralPath();
                        boolean start=true;
                        for(Iterator<gendomain.Node> nd=theDomain.getNodes().values().iterator();nd.hasNext();){
                            gendomain.Node theNode = nd.next();
                            if(start && theNode.getBoundary()){
                                gp.moveTo(xTransformation(theNode.X()),this.yTransformation(theNode.Y()));
                                start=false;
                            }else if(theNode.getBoundary()){
                                gp.lineTo(xTransformation(theNode.X()),yTransformation(theNode.Y()));
                            }
                        }
                        gp.closePath();
                        g2.draw(gp);
                    }
                    controlAnimation();
                    if(this.resp)this.drawDeformedGENDomain(g2, theDomain);
                    if(this.theDisp==1||this.theDisp==2||this.theDisp==3)this.drawDisplacementGENDomain(g2, theDomain);
                }
            }
            for(Iterator<contraption> it=theUniverse.getContraptions().values().iterator(); it.hasNext();){
                contraption theDomain = it.next();
                if(this.getGPDomain(theDomain.getID(), DomainType.CNT).getPlotit()){
                    theDomain.SelfPortrait(g2,min_y,max_y,ye,ys,min_x,max_x,xe,xs);
                    controlAnimation();
                    if(resp || contour){
                        theDomain.Motion(g2,min_y,max_y,ye,ys,min_x,max_x,xe,xs,step,defscale);
                        if(this.showcolorbar)this.drawCLBar(g2);
                    }
                }
            }
        }
        
        // draw axes of origin
        if(this.draw_axes){
            int xo=xTransformation(0.0);
            int yo=yTransformation(0.0);
//            xo=xs;yo=ys;
            int arr=Math.min(width, height);
            int alength=Math.min((xe-xs)/numx, (ys-ye)/numy);
//            g2.setColor(Color.WHITE);
//            g2.fillOval(xo-alength/10, yo-alength/10, alength/5, alength/5);
            g2.setColor(Color.RED);
            g2.drawLine(xo, yo, xo+alength, yo);
            g2.drawLine(xo+alength, yo, xo+alength-arr/100, yo+arr/100);
            g2.drawLine(xo+alength, yo, xo+alength-arr/100, yo-arr/100);
            g2.setColor(this.fontClr);
            g2.drawString("X", xo+alength, yo+(ye-ys)/(10*numy));
            g2.setColor(Color.GREEN);
            g2.drawLine(xo, yo, xo, yo-alength);
            g2.drawLine(xo, yo-alength, xo-arr/100, yo-alength+arr/100);
            g2.drawLine(xo, yo-alength, xo+arr/100, yo-alength+arr/100);
            g2.setColor(this.fontClr);
            g2.drawString("Y", xo, yo-alength+(ye-ys)/(10*numy));
        }
    }
    
    public void drawBEMDomain(Graphics2D g2,jbem.Domain theDomain){
        for(Iterator<jbem.Element> eit=theDomain.getElements().values().iterator(); eit.hasNext();){
            jbem.Element theElement= eit.next();
            GeneralPath gp = new GeneralPath();
            gp.moveTo(xTransformation(theElement.getNodeHier(1).getCoordinates()[0]),this.yTransformation(theElement.getNodeHier(1).getCoordinates()[1]));
            for(int j=2;j<=theElement.getNumNodes();j++){
                gp.lineTo(xTransformation(theElement.getNodeHier(j).getCoordinates()[0]),yTransformation(theElement.getNodeHier(j).getCoordinates()[1]));
            }
            gp.closePath();
            g2.draw(gp);
        }
        if(drawNodes||drawNodesID)for(Iterator<jbem.Node> eit=theDomain.getNodes().values().iterator(); eit.hasNext();){
            jbem.Node theNode= eit.next();
            g2.setColor(Color.ORANGE);
            int di=Math.min(xe-xs,Math.abs(ye-ys))/nodeScale;
            if(drawNodes)g2.fillOval(this.xTransformation(theNode.getCoordinates()[0])-di/2, this.yTransformation(theNode.getCoordinates()[1])-di/2, di, di);
            g2.setColor(Color.LIGHT_GRAY);
            if(drawNodesID)g2.drawString(Integer.toString(theNode.getID()), this.xTransformation(theNode.getCoordinates()[0])+di/2, this.yTransformation(theNode.getCoordinates()[1])-di/2);
        }
        
        if(drawNodes||drawNodesID)for(Iterator<jbem.ResultPoint> eit=theDomain.getResultPoints().values().iterator(); eit.hasNext();){
            jbem.ResultPoint theNode= eit.next();
            g2.setColor(Color.ORANGE);
            int di=Math.min(xe-xs,Math.abs(ye-ys))/nodeScale;
            if(drawNodes)g2.fillOval(this.xTransformation(theNode.getCoordinates()[0])-di/2, this.yTransformation(theNode.getCoordinates()[1])-di/2, di, di);
            g2.setColor(Color.LIGHT_GRAY);
            if(drawNodesID)g2.drawString(Integer.toString(theNode.getID()), this.xTransformation(theNode.getCoordinates()[0])+di/2, this.yTransformation(theNode.getCoordinates()[1])-di/2);
        }
        
        if(theDomain.isTriangulized()&&drawtriangles)for(Iterator<Shape> eit=theDomain.getShapes().values().iterator(); eit.hasNext();){
            geom.Triangle theTriangle= (geom.Triangle) eit.next();
//            System.out.print("Triangle with id= "+theTriangle.getID()+"/"+theDomain.getShapes().size());
            try{
//            System.out.println(", n1 ["+theTriangle.getPoints().get(1).getID()+"], n2["+theTriangle.getPoints().get(2).getID()+"], n3["+theTriangle.getPoints().get(3).getID()+"].");
            g2.drawLine(this.xTransformation(theTriangle.getPoints().get(1).getCoordinates()[0]), this.yTransformation(theTriangle.getPoints().get(1).getCoordinates()[1]), 
                    this.xTransformation(theTriangle.getPoints().get(2).getCoordinates()[0]), this.yTransformation(theTriangle.getPoints().get(2).getCoordinates()[1]));
            g2.drawLine(this.xTransformation(theTriangle.getPoints().get(2).getCoordinates()[0]), this.yTransformation(theTriangle.getPoints().get(2).getCoordinates()[1]), 
                    this.xTransformation(theTriangle.getPoints().get(3).getCoordinates()[0]), this.yTransformation(theTriangle.getPoints().get(3).getCoordinates()[1]));
            g2.drawLine(this.xTransformation(theTriangle.getPoints().get(1).getCoordinates()[0]), this.yTransformation(theTriangle.getPoints().get(1).getCoordinates()[1]), 
                    this.xTransformation(theTriangle.getPoints().get(3).getCoordinates()[0]), this.yTransformation(theTriangle.getPoints().get(3).getCoordinates()[1]));
            }catch(java.lang.NullPointerException e){
//                System.out.println(", "+e.getMessage());
                this.com.println("Triangle with id= "+theTriangle.getID()+"/"+theDomain.getShapes().size()+" has some null node!");
            }
        }
    }
    
    public void drawDEMDomain(Graphics2D g2,DEMdomain theDomain){
        for(Iterator<Particle> eit=theDomain.getParticles().values().iterator(); eit.hasNext();){
            Particle theParticle= eit.next();
            if(theParticle.getShape().getClass().toString().equalsIgnoreCase(geom.Circle.class.toString())){
                Circle aCircle=(Circle) theParticle.getShape();
//                g2.drawOval(xTransformation(theParticle.getCentroid().X()-aCircle.getR()), 
//                        yTransformation(theParticle.getCentroid().Y()+aCircle.getR()), 
//                        xTransformation(2*aCircle.getR()), yTransformation(2*aCircle.getR()));
                double r=aCircle.getR();
                double xc=theParticle.getCentroid().X();
                double yc=theParticle.getCentroid().Y();
                int nseg=20;
                GeneralPath gp = new GeneralPath();
                gp.moveTo(xTransformation(xc+r*Math.cos(0*2*Math.PI/nseg)),this.yTransformation(yc+r*Math.sin(0*2*Math.PI/nseg)));
                for(int j=1;j<=nseg;j++){
                    gp.lineTo(xTransformation(xc+r*Math.cos(j*2*Math.PI/nseg)),this.yTransformation(yc+r*Math.sin(j*2*Math.PI/nseg)));
                }
                gp.closePath();
//                g2.SelfPortrait(gp);
                g2.fill(gp);
            }
        }
        
        if(drawNodes||drawNodesID)for(Iterator<gendomain.Node > eit=theDomain.getNodes().values().iterator(); eit.hasNext();){
            gendomain.Node theNode= eit.next();
            g2.setColor(Color.ORANGE);
            int di=Math.min(xe-xs,Math.abs(ye-ys))/nodeScale;
            if(drawNodes)g2.fillOval(this.xTransformation(theNode.X())-di/2, this.yTransformation(theNode.Y())-di/2, di, di);
            g2.setColor(Color.LIGHT_GRAY);
            if(drawNodesID)g2.drawString(Integer.toString(theNode.getID()), this.xTransformation(theNode.getCoordinates()[0])+di/2, this.yTransformation(theNode.getCoordinates()[1])-di/2);
        }
    }
    
    public void drawFEMDomain(Graphics2D g2,jfem.Domain theDomain){
        int x1,x2,y1,y2;
        g2.setColor(theDomain.ColorElemsUND);
        for(Iterator<jfem.Element> eit=theDomain.getElements().values().iterator(); eit.hasNext();){
            jfem.Element theElement= eit.next();
            GeneralPath gp = new GeneralPath();
            gp.moveTo(xTransformation(theElement.getNodeHierarchy(1).getCoords()[0]),this.yTransformation(theElement.getNodeHierarchy(1).getCoords()[1]));
            for(int j=2;j<=theElement.getNumNodes();j++){
                gp.lineTo(xTransformation(theElement.getNodeHierarchy(j).getCoords()[0]),yTransformation(theElement.getNodeHierarchy(j).getCoords()[1]));
            }
            gp.closePath();
            g2.draw(gp);
        }
        if(drawNodes||drawNodesID)for(Iterator<jfem.Node> eit=theDomain.getNodes().values().iterator(); eit.hasNext();){
            jfem.Node theNode= eit.next();
            g2.setColor(theDomain.ColorNodesUND);
            int di=Math.min(xe-xs,Math.abs(ye-ys))/nodeScale;
            if(drawNodes)g2.fillOval(this.xTransformation(theNode.getCoords()[0])-di/2, this.yTransformation(theNode.getCoords()[1])-di/2, di, di);
            g2.setColor(theDomain.ColorNodesID);
            if(drawNodesID)g2.drawString(Integer.toString(theNode.getID()), this.xTransformation(theNode.getCoords()[0])+di/2, this.yTransformation(theNode.getCoords()[1])-di/2);
        }
    }
    
    public void drawPDEDomain(Graphics2D g2,jpde theDomain){
        int x1,x2,y1,y2;
        for (edu.uta.futureye.core.Element theElement : theDomain.mesh.getElementList()){
            GeneralPath gp = new GeneralPath();
            gp.moveTo(xTransformation(theElement.nodes.at(1).coords()[0]),this.yTransformation(theElement.nodes.at(1).coords()[1]));
            for(int j=2;j<=theElement.nodes.size();j++){
                gp.lineTo(xTransformation(theElement.nodes.at(j).coords()[0]),yTransformation(theElement.nodes.at(j).coords()[1]));
            }
            gp.closePath();
            g2.draw(gp);
        }
        if(drawNodes||drawNodesID)for(edu.uta.futureye.core.Node theNode : theDomain.mesh.getNodeList()){
            g2.setColor(Color.ORANGE);
            int di=Math.min(xe-xs,Math.abs(ye-ys))/nodeScale;
            if(drawNodes)g2.fillOval(this.xTransformation(theNode.coords()[0])-di/2, this.yTransformation(theNode.coords()[1])-di/2, di, di);
            g2.setColor(Color.LIGHT_GRAY);
            if(drawNodesID)g2.drawString(Integer.toString(theNode.getIndex()), this.xTransformation(theNode.coords()[0])+di/2, this.yTransformation(theNode.coords()[1])-di/2);
        }
    }
    
    public void drawGENDomain(Graphics2D g2,gendomain.Domain theDomain){
        Color iCl=g2.getColor();
        if(drawNodes||drawNodesID)for(Iterator<gendomain.Node> eit=theDomain.getNodes().values().iterator(); eit.hasNext();){
            gendomain.Node theNode= eit.next();
            g2.setColor(theDomain.ColorNodesUND);
            int di=Math.min(xe-xs,Math.abs(ye-ys))/nodeScale;
            if(drawNodes)g2.fillOval(this.xTransformation(theNode.getCoords()[0])-di/2, this.yTransformation(theNode.getCoords()[1])-di/2, di, di);
            g2.setColor(Color.LIGHT_GRAY);
            if(drawNodesID)g2.drawString(Integer.toString(theNode.getID()), this.xTransformation(theNode.getCoords()[0])+di/2, this.yTransformation(theNode.getCoords()[1])-di/2);
        }
        
        if(theDomain.isTriangulized()&&drawtriangles)for(Iterator<Shape> eit=theDomain.getShapes().values().iterator(); eit.hasNext();){
            geom.Triangle theTriangle= (geom.Triangle) eit.next();
//            System.out.print("Triangle with id= "+theTriangle.getID()+"/"+theDomain.getShapes().size());
            try{
//            System.out.println(", n1 ["+theTriangle.getPoints().get(1).getID()+"], n2["+theTriangle.getPoints().get(2).getID()+"], n3["+theTriangle.getPoints().get(3).getID()+"].");
            g2.drawLine(this.xTransformation(theTriangle.getPoints().get(1).getCoordinates()[0]), this.yTransformation(theTriangle.getPoints().get(1).getCoordinates()[1]), 
                    this.xTransformation(theTriangle.getPoints().get(2).getCoordinates()[0]), this.yTransformation(theTriangle.getPoints().get(2).getCoordinates()[1]));
            g2.drawLine(this.xTransformation(theTriangle.getPoints().get(2).getCoordinates()[0]), this.yTransformation(theTriangle.getPoints().get(2).getCoordinates()[1]), 
                    this.xTransformation(theTriangle.getPoints().get(3).getCoordinates()[0]), this.yTransformation(theTriangle.getPoints().get(3).getCoordinates()[1]));
            g2.drawLine(this.xTransformation(theTriangle.getPoints().get(1).getCoordinates()[0]), this.yTransformation(theTriangle.getPoints().get(1).getCoordinates()[1]), 
                    this.xTransformation(theTriangle.getPoints().get(3).getCoordinates()[0]), this.yTransformation(theTriangle.getPoints().get(3).getCoordinates()[1]));
            }catch(java.lang.NullPointerException e){
//                System.out.println(", "+e.getMessage());
                this.com.println("Triangle with id= "+theTriangle.getID()+"/"+theDomain.getShapes().size()+" has some null node!");
            }
        }
        g2.setColor(iCl);
    }
    
     public void drawDeformedDEMDomain(Graphics2D g2,DEMdomain theDomain){
         g2.setColor(Color.RED);
         double omega=0.0,xd,yd;
         for(Iterator<Particle> eit=theDomain.getParticles().values().iterator(); eit.hasNext();){
            Particle theParticle= eit.next();
            if(theParticle.getCentroid().getuEFTable()[5]!=0)omega=theParticle.getCentroid().getDisps(this.step,5);
            xd=theParticle.getCentroid().getDisps(this.step,0);
            yd=theParticle.getCentroid().getDisps(this.step,1);
            if(theParticle.getShape().getClass().toString().equalsIgnoreCase(geom.Circle.class.toString())){
                Circle aCircle=(Circle) theParticle.getShape();
                double r=aCircle.getR();
                double xc=theParticle.getCentroid().X()+defscale*xd;
                double yc=theParticle.getCentroid().Y()+defscale*yd;
                int nseg=20;
                GeneralPath gp = new GeneralPath();
                gp.moveTo(xTransformation(xc+r*Math.cos(0*2*Math.PI/nseg)),this.yTransformation(yc+r*Math.sin(0*2*Math.PI/nseg)));
                for(int j=1;j<=nseg;j++){
                    gp.lineTo(xTransformation(xc+r*Math.cos(j*2*Math.PI/nseg)),this.yTransformation(yc+r*Math.sin(j*2*Math.PI/nseg)));
                }
                gp.closePath();
                g2.fill(gp);
                Color thc=g2.getColor();
                g2.setColor(Color.GREEN);
                g2.draw(gp);
                g2.drawLine(xTransformation(xc), yTransformation(yc)
                        , xTransformation(xc+r*Math.cos(omega)), yTransformation(yc+r*Math.sin(omega)));
                g2.setColor(thc);
            }
        }
        
        if(drawNodes)for(Iterator<gendomain.Node > eit=theDomain.getNodes().values().iterator(); eit.hasNext();){
            gendomain.Node theNode= eit.next();
            g2.setColor(Color.ORANGE);
            int di=Math.min(xe-xs,Math.abs(ye-ys))/nodeScale;
            xd=theNode.getDisps(this.step,0);
            yd=theNode.getDisps(this.step,1);
            if(drawNodes)g2.fillOval(this.xTransformation(theNode.X()+defscale*xd)-di/2, this.yTransformation(theNode.Y()+defscale*yd)-di/2, di, di);
        }
     }
    
    public void drawDeformedBEMDomain(Graphics2D g2,jbem.Domain theDomain){
        double ux,uy;
        Color cl = g2.getColor();
        g2.setColor(Color.RED);
        for(Iterator<jbem.Element> eit=theDomain.getElements().values().iterator(); eit.hasNext();){
            jbem.Element theElement= eit.next();
            ux=theElement.getNodeHier(1).getu()[0][step][LC];
            uy=theElement.getNodeHier(1).getu()[1][step][LC];
            GeneralPath gp = new GeneralPath();
            gp.moveTo(xTransformation(theElement.getNodeHier(1).getCoordinates()[0]+ux*defscale),this.yTransformation(theElement.getNodeHier(1).getCoordinates()[1]+uy*defscale));
            for(int j=2;j<=theElement.getNumNodes();j++){
                ux=theElement.getNodeHier(j).getu()[0][step][LC];
                uy=theElement.getNodeHier(j).getu()[1][step][LC];
                gp.lineTo(xTransformation(theElement.getNodeHier(j).getCoordinates()[0]+ux*defscale),yTransformation(theElement.getNodeHier(j).getCoordinates()[1]+uy*defscale));
            }
            gp.closePath();
            g2.draw(gp);
        }
        if(drawNodes)for(Iterator<jbem.Node> eit=theDomain.getNodes().values().iterator(); eit.hasNext();){
            jbem.Node theNode= eit.next();
            g2.setColor(Color.GREEN);
            int di=Math.min(xe-xs,Math.abs(ye-ys))/nodeScale;
            ux=theNode.getu()[0][step][LC];
            uy=theNode.getu()[1][step][LC];
            g2.fillOval(this.xTransformation(theNode.getCoordinates()[0]+ux*defscale)-di/2, this.yTransformation(theNode.getCoordinates()[1]+uy*defscale)-di/2, di, di);
        }
        
        if(drawNodes)for(Iterator<jbem.ResultPoint> eit=theDomain.getResultPoints().values().iterator(); eit.hasNext();){
            jbem.ResultPoint theNode= eit.next();
            g2.setColor(Color.GREEN);
            int di=Math.min(xe-xs,Math.abs(ye-ys))/nodeScale;
            ux=theNode.getu(0, step, LC);
            uy=theNode.getu(1, step, LC);
            if(drawNodes)g2.fillOval(this.xTransformation(theNode.getCoordinates()[0]+ux*defscale)-di/2, this.yTransformation(theNode.getCoordinates()[1]+uy*defscale)-di/2, di, di);
        }
        g2.setColor(cl);
    }
    
    public void drawDeformedFEMDomain(Graphics2D g2,jfem.Domain theDomain){
        double ux,uy;
        Color cl = g2.getColor();
        g2.setColor(theDomain.ColorElemsDEF);
        for(Iterator<jfem.Element> eit=theDomain.getElements().values().iterator(); eit.hasNext();){
            jfem.Element theElement= eit.next();
            GeneralPath gp = new GeneralPath();
            ux=theElement.getNodeHierarchy(1).getLoadCaseDisps(LC,step)[0];
            uy=theElement.getNodeHierarchy(1).getLoadCaseDisps(LC,step)[1];
            gp.moveTo(xTransformation(theElement.getNodeHierarchy(1).getCoords()[0]+ux*defscale),this.yTransformation(theElement.getNodeHierarchy(1).getCoords()[1]+uy*defscale));
            for(int j=2;j<=theElement.getNumNodes();j++){
                ux=theElement.getNodeHierarchy(j).getLoadCaseDisps(LC,step)[0];
                uy=theElement.getNodeHierarchy(j).getLoadCaseDisps(LC,step)[1];
                gp.lineTo(xTransformation(theElement.getNodeHierarchy(j).getCoords()[0]+ux*defscale),yTransformation(theElement.getNodeHierarchy(j).getCoords()[1]+uy*defscale));
            }
            gp.closePath();
            g2.draw(gp);
        }
        if(drawNodes)for(Iterator<jfem.Node> eit=theDomain.getNodes().values().iterator(); eit.hasNext();){
           jfem.Node theNode= eit.next();
            g2.setColor(theDomain.ColorNodesDEF);
            int di=Math.min(xe-xs,Math.abs(ye-ys))/nodeScale;
            ux=theNode.getLoadCaseDisps(LC,step)[0];
            uy=theNode.getLoadCaseDisps(LC,step)[1];
            g2.fillOval(this.xTransformation(theNode.getCoords()[0]+ux*this.defscale)-di/2, this.yTransformation(theNode.getCoords()[1]+uy*this.defscale)-di/2, di, di);
        }
        g2.setColor(cl);
    }
    
    public void drawContour_u_BEMDomain(Graphics2D g2,jbem.Domain theDomain){
        colorCount  = 64;
        colorArray = new Color[colorCount];
        setupColors();
        double dataValue;
        for(Iterator<ResultPoint> nit=theDomain.getResultPoints().values().iterator(); nit.hasNext();){
            ResultPoint theNode= nit.next();
            dataValue=theDomain.getInternalPointDispNorm(theNode.getID(), step, LC);
            if(dataValue>dataRangeMax)dataRangeMax=dataValue;
            if(dataValue<dataRangeMin)dataRangeMin=dataValue;
        }
        //if(Math.abs(dataRangeMax- dataRangeMin) < 1.0e-10) return;
        if(this.showcolorbar)this.drawCLBar(g2);
        
        for(Iterator<Shape> eit=theDomain.getShapes().values().iterator(); eit.hasNext();){
            Shape theElement= eit.next();
            int ShapeDimension=2;
            switch(ShapeDimension){
                case 2:
                    int[] xPoints=new int[3];
                    int[] yPoints=new int[3];
                    int res=0;
                    for(int i=0;i<resolution;i++){
                        for(int j=0;j<resolution-res;j++){
                            double xsi1=-i/((double)resolution)+(resolution-j)/((double)resolution);
                            double eta1=j/((double)resolution);
                            double xsi2=-i/((double)resolution)+(resolution-j-1)/((double)resolution);
                            double eta2=(j+1)/((double)resolution);
                            double xsi3=-i/((double)resolution)+(resolution-j-1)/((double)resolution);
                            double eta3=j/((double)resolution);
                            double[] coords1 = new double[2]; coords1[0]=0.0; coords1[1]=0.0;
                            double[] coords2 = new double[2]; coords2[0]=0.0; coords2[1]=0.0;
                            double[] coords3 = new double[2]; coords3[0]=0.0; coords3[1]=0.0;
                            double[] disps = new double[2]; disps[0]=0.0; disps[1]=0.0;
                            for(int node=1; node<=theElement.getNumNodes();node++){
                                coords1[0]+=theElement.ShapeFunction(node, xsi1, eta1)*theElement.getPoints().get(node).getCoordinates()[0];
                                coords1[1]+=theElement.ShapeFunction(node, xsi1, eta1)*theElement.getPoints().get(node).getCoordinates()[1];
                                coords2[0]+=theElement.ShapeFunction(node, xsi2, eta2)*theElement.getPoints().get(node).getCoordinates()[0];
                                coords2[1]+=theElement.ShapeFunction(node, xsi2, eta2)*theElement.getPoints().get(node).getCoordinates()[1];
                                coords3[0]+=theElement.ShapeFunction(node, xsi3, eta3)*theElement.getPoints().get(node).getCoordinates()[0];
                                coords3[1]+=theElement.ShapeFunction(node, xsi3, eta3)*theElement.getPoints().get(node).getCoordinates()[1];
                                disps[0]+=theElement.ShapeFunction(node, (xsi1+xsi2+xsi3)/3.0, (eta1+eta2+eta3)/3.0)*theDomain.getInternalPointDispNorm(theElement.getPoints().get(node).getID(), step, LC);
                            }
                            dataValue=disps[0];
    //                            int w=Math.abs(xTransformation(coords1[0])-xTransformation(coords_[0]));
    //                            int h=Math.abs(yTransformation(coords1[1])-yTransformation(coords_[1]));
                            xPoints[0]=this.xTransformation(coords1[0]);
                            yPoints[0]=this.yTransformation(coords1[1]);
                            xPoints[1]=this.xTransformation(coords2[0]);
                            yPoints[1]=this.yTransformation(coords2[1]);
                            xPoints[2]=this.xTransformation(coords3[0]);
                            yPoints[2]=this.yTransformation(coords3[1]);
                            double colorLocation =((colorCount-1.0)*(dataValue - dataRangeMin))/(dataRangeMax - dataRangeMin);
                            int colorIndex = (int)colorLocation;
                            if(colorIndex >= colorCount) colorIndex = colorCount-1;
                            if(colorIndex < 0) colorIndex = 0;
                            g2.setColor(colorArray[colorIndex]);
                            g2.fillPolygon(xPoints, yPoints, 3);
                        }
                        for(int j=1;j<resolution-res;j++){
                            double xsi1=-i/((double)resolution)+(resolution-j)/((double)resolution);
                            double eta1=j/((double)resolution);
                            double xsi2=-i/((double)resolution)+(resolution-j-1)/((double)resolution);
                            double eta2=(j)/((double)resolution);
                            double xsi3=-i/((double)resolution)+(resolution-j)/((double)resolution);
                            double eta3=(j-1)/((double)resolution);
                            double[] coords1 = new double[2]; coords1[0]=0.0; coords1[1]=0.0;
                            double[] coords2 = new double[2]; coords2[0]=0.0; coords2[1]=0.0;
                            double[] coords3 = new double[2]; coords3[0]=0.0; coords3[1]=0.0;
                            double[] disps = new double[2]; disps[0]=0.0; disps[1]=0.0;
                            for(int node=1; node<=theElement.getNumNodes();node++){
                                coords1[0]+=theElement.ShapeFunction(node, xsi1, eta1)*theElement.getPoints().get(node).getCoordinates()[0];
                                coords1[1]+=theElement.ShapeFunction(node, xsi1, eta1)*theElement.getPoints().get(node).getCoordinates()[1];
                                coords2[0]+=theElement.ShapeFunction(node, xsi2, eta2)*theElement.getPoints().get(node).getCoordinates()[0];
                                coords2[1]+=theElement.ShapeFunction(node, xsi2, eta2)*theElement.getPoints().get(node).getCoordinates()[1];
                                coords3[0]+=theElement.ShapeFunction(node, xsi3, eta3)*theElement.getPoints().get(node).getCoordinates()[0];
                                coords3[1]+=theElement.ShapeFunction(node, xsi3, eta3)*theElement.getPoints().get(node).getCoordinates()[1];
                                disps[0]+=theElement.ShapeFunction(node, (xsi1+xsi2+xsi3)/3.0, (eta1+eta2+eta3)/3.0)*theDomain.getInternalPointDispNorm(theElement.getPoints().get(node).getID(), step, LC);
                            }
                            dataValue=disps[0];
    //                            int w=Math.abs(xTransformation(coords1[0])-xTransformation(coords_[0]));
    //                            int h=Math.abs(yTransformation(coords1[1])-yTransformation(coords_[1]));
                            xPoints[0]=this.xTransformation(coords1[0]);
                            yPoints[0]=this.yTransformation(coords1[1]);
                            xPoints[1]=this.xTransformation(coords2[0]);
                            yPoints[1]=this.yTransformation(coords2[1]);
                            xPoints[2]=this.xTransformation(coords3[0]);
                            yPoints[2]=this.yTransformation(coords3[1]);
                            double colorLocation =((colorCount-1.0)*(dataValue - dataRangeMin))/(dataRangeMax - dataRangeMin);
                            int colorIndex = (int)colorLocation;
                            if(colorIndex >= colorCount) colorIndex = colorCount-1;
                            if(colorIndex < 0) colorIndex = 0;
                            g2.setColor(colorArray[colorIndex]);
                            g2.fillPolygon(xPoints, yPoints, 3);
                        }
                        res+=1;
                    }
                    break;
                default:
                    com.append("not recognised element's dimension: "+ShapeDimension+" for element id: "+theElement.getID());
            }
//            break;
        }
    }
    
    public void drawContourStressBEMDomain(Graphics2D g2,jbem.Domain theDomain){
        // yet no
        colorCount  = 64;
        colorArray = new Color[colorCount];
        setupColors();
        double dataValue;
        if(this.GlobalMinMax){
            int n = theDomain.getNodes().values().iterator().next().getu()[0][0].length;
            for(int ct=1;ct<n;ct++){
                for(Iterator<ResultPoint> nit=theDomain.getResultPoints().values().iterator(); nit.hasNext();){
                    ResultPoint theNode= nit.next();
                    dataValue=theDomain.getInternalPointStress(theNode.getID(), this.theStress, step, LC);
                    if(dataValue>dataRangeMax)dataRangeMax=dataValue;
                    if(dataValue<dataRangeMin)dataRangeMin=dataValue;
                }
            }
        }else{
            for(Iterator<ResultPoint> nit=theDomain.getResultPoints().values().iterator(); nit.hasNext();){
                ResultPoint theNode= nit.next();
                dataValue=theDomain.getInternalPointStress(theNode.getID(), this.theStress, step, LC);
                if(dataValue>dataRangeMax)dataRangeMax=dataValue;
                if(dataValue<dataRangeMin)dataRangeMin=dataValue;
            }
        }
        
        //if(Math.abs(dataRangeMax- dataRangeMin) < 1.0e-10) return;
        if(this.showcolorbar)this.drawCLBar(g2);
        
        for(Iterator<Shape> eit=theDomain.getShapes().values().iterator(); eit.hasNext();){
            Shape theElement= eit.next();
            int ShapeDimension=2;
            switch(ShapeDimension){
                case 2:
                    int[] xPoints=new int[3];
                    int[] yPoints=new int[3];
                    int res=0;
                    for(int i=0;i<resolution;i++){
                        for(int j=0;j<resolution-res;j++){
                            double xsi1=-i/((double)resolution)+(resolution-j)/((double)resolution);
                            double eta1=j/((double)resolution);
                            double xsi2=-i/((double)resolution)+(resolution-j-1)/((double)resolution);
                            double eta2=(j+1)/((double)resolution);
                            double xsi3=-i/((double)resolution)+(resolution-j-1)/((double)resolution);
                            double eta3=j/((double)resolution);
                            double[] coords1 = new double[2]; coords1[0]=0.0; coords1[1]=0.0;
                            double[] coords2 = new double[2]; coords2[0]=0.0; coords2[1]=0.0;
                            double[] coords3 = new double[2]; coords3[0]=0.0; coords3[1]=0.0;
                            double[] disps = new double[2]; disps[0]=0.0; disps[1]=0.0;
                            for(int node=1; node<=theElement.getNumNodes();node++){
                                coords1[0]+=theElement.ShapeFunction(node, xsi1, eta1)*theElement.getPoints().get(node).getCoordinates()[0];
                                coords1[1]+=theElement.ShapeFunction(node, xsi1, eta1)*theElement.getPoints().get(node).getCoordinates()[1];
                                coords2[0]+=theElement.ShapeFunction(node, xsi2, eta2)*theElement.getPoints().get(node).getCoordinates()[0];
                                coords2[1]+=theElement.ShapeFunction(node, xsi2, eta2)*theElement.getPoints().get(node).getCoordinates()[1];
                                coords3[0]+=theElement.ShapeFunction(node, xsi3, eta3)*theElement.getPoints().get(node).getCoordinates()[0];
                                coords3[1]+=theElement.ShapeFunction(node, xsi3, eta3)*theElement.getPoints().get(node).getCoordinates()[1];
                                disps[0]+=theElement.ShapeFunction(node, (xsi1+xsi2+xsi3)/3.0, (eta1+eta2+eta3)/3.0)*theDomain.getInternalPointStress(theElement.getPoints().get(node).getID(), this.theStress, step, LC);
                            }
                            dataValue=disps[0];
                            System.out.println(1+": "+dataValue);
    //                            int w=Math.abs(xTransformation(coords1[0])-xTransformation(coords_[0]));
    //                            int h=Math.abs(yTransformation(coords1[1])-yTransformation(coords_[1]));
                            xPoints[0]=this.xTransformation(coords1[0]);
                            yPoints[0]=this.yTransformation(coords1[1]);
                            xPoints[1]=this.xTransformation(coords2[0]);
                            yPoints[1]=this.yTransformation(coords2[1]);
                            xPoints[2]=this.xTransformation(coords3[0]);
                            yPoints[2]=this.yTransformation(coords3[1]);
                            double colorLocation =((colorCount-1.0)*(dataValue - dataRangeMin))/(dataRangeMax - dataRangeMin);
                            int colorIndex = (int)colorLocation;
                            if(colorIndex >= colorCount) colorIndex = colorCount-1;
                            if(colorIndex < 0) colorIndex = 0;
                            g2.setColor(colorArray[colorIndex]);
                            g2.fillPolygon(xPoints, yPoints, 3);
                        }
                        for(int j=1;j<resolution-res;j++){
                            double xsi1=-i/((double)resolution)+(resolution-j)/((double)resolution);
                            double eta1=j/((double)resolution);
                            double xsi2=-i/((double)resolution)+(resolution-j-1)/((double)resolution);
                            double eta2=(j)/((double)resolution);
                            double xsi3=-i/((double)resolution)+(resolution-j)/((double)resolution);
                            double eta3=(j-1)/((double)resolution);
                            double[] coords1 = new double[2]; coords1[0]=0.0; coords1[1]=0.0;
                            double[] coords2 = new double[2]; coords2[0]=0.0; coords2[1]=0.0;
                            double[] coords3 = new double[2]; coords3[0]=0.0; coords3[1]=0.0;
                            double[] disps = new double[2]; disps[0]=0.0; disps[1]=0.0;
                            for(int node=1; node<=theElement.getNumNodes();node++){
                                coords1[0]+=theElement.ShapeFunction(node, xsi1, eta1)*theElement.getPoints().get(node).getCoordinates()[0];
                                coords1[1]+=theElement.ShapeFunction(node, xsi1, eta1)*theElement.getPoints().get(node).getCoordinates()[1];
                                coords2[0]+=theElement.ShapeFunction(node, xsi2, eta2)*theElement.getPoints().get(node).getCoordinates()[0];
                                coords2[1]+=theElement.ShapeFunction(node, xsi2, eta2)*theElement.getPoints().get(node).getCoordinates()[1];
                                coords3[0]+=theElement.ShapeFunction(node, xsi3, eta3)*theElement.getPoints().get(node).getCoordinates()[0];
                                coords3[1]+=theElement.ShapeFunction(node, xsi3, eta3)*theElement.getPoints().get(node).getCoordinates()[1];
                                disps[0]+=theElement.ShapeFunction(node, (xsi1+xsi2+xsi3)/3.0, (eta1+eta2+eta3)/3.0)*theDomain.getInternalPointStress(theElement.getPoints().get(node).getID(), this.theStress, step, LC);
                            }
                            dataValue=disps[0];
                            System.out.println(2+": "+dataValue);
    //                            int w=Math.abs(xTransformation(coords1[0])-xTransformation(coords_[0]));
    //                            int h=Math.abs(yTransformation(coords1[1])-yTransformation(coords_[1]));
                            xPoints[0]=this.xTransformation(coords1[0]);
                            yPoints[0]=this.yTransformation(coords1[1]);
                            xPoints[1]=this.xTransformation(coords2[0]);
                            yPoints[1]=this.yTransformation(coords2[1]);
                            xPoints[2]=this.xTransformation(coords3[0]);
                            yPoints[2]=this.yTransformation(coords3[1]);
                            double colorLocation =((colorCount-1.0)*(dataValue - dataRangeMin))/(dataRangeMax - dataRangeMin);
                            int colorIndex = (int)colorLocation;
                            if(colorIndex >= colorCount) colorIndex = colorCount-1;
                            if(colorIndex < 0) colorIndex = 0;
                            g2.setColor(colorArray[colorIndex]);
                            g2.fillPolygon(xPoints, yPoints, 3);
                        }
                        res+=1;
                    }
                    break;
                default:
                    com.append("not recognised element's dimension: "+ShapeDimension+" for element id: "+theElement.getID());
            }
//            break;
        }
    }
    
    public void setMinMaxContour(gendomain.Domain theDomain){
        double dataValue;
        dataRangeMin = Double.POSITIVE_INFINITY;
        dataRangeMax = Double.NEGATIVE_INFINITY;
        for(Iterator<gendomain.Node> nit=theDomain.getNodes().values().iterator(); nit.hasNext();){
            gendomain.Node theNode= nit.next();
            if(this.GlobalMinMax){
                int nd=theNode.getNumSteps();
                for(int ct=0;ct<nd;ct++){
                    dataValue=theNode.getDisps(ct,LC);
                    if(dataValue>dataRangeMax)dataRangeMax=dataValue;
                    if(dataValue<dataRangeMin)dataRangeMin=dataValue;
                }
            }else{
                dataValue=theNode.getDisps(step,LC);
                if(dataValue>dataRangeMax)dataRangeMax=dataValue;
                if(dataValue<dataRangeMin)dataRangeMin=dataValue;
            }
        }
    }
    
    public void drawContourGENDomain(Graphics2D g2,gendomain.Domain theDomain){
        colorCount  = 64;
        colorArray = new Color[colorCount];
        setupColors();
        double dataValue;
        if(!this.explicitMinMax)setMinMaxContour(theDomain);
        if(this.showcolorbar)this.drawCLBar(g2);
        
        for(Iterator<Shape> eit=theDomain.getShapes().values().iterator(); eit.hasNext();){
            Shape theElement= eit.next();
            int ShapeDimension=2;
            switch(ShapeDimension){
                case 2:
                    int[] xPoints=new int[3];
                    int[] yPoints=new int[3];
                    int res=0;
                    for(int i=0;i<resolution;i++){
                        for(int j=0;j<resolution-res;j++){
                            double xsi1=-i/((double)resolution)+(resolution-j)/((double)resolution);
                            double eta1=j/((double)resolution);
                            double xsi2=-i/((double)resolution)+(resolution-j-1)/((double)resolution);
                            double eta2=(j+1)/((double)resolution);
                            double xsi3=-i/((double)resolution)+(resolution-j-1)/((double)resolution);
                            double eta3=j/((double)resolution);
                            double[] coords1 = new double[2]; coords1[0]=0.0; coords1[1]=0.0;
                            double[] coords2 = new double[2]; coords2[0]=0.0; coords2[1]=0.0;
                            double[] coords3 = new double[2]; coords3[0]=0.0; coords3[1]=0.0;
                            double[] disps = new double[2]; disps[0]=0.0; disps[1]=0.0;
                            for(int node=1; node<=theElement.getNumNodes();node++){
                                coords1[0]+=theElement.ShapeFunction(node, xsi1, eta1)*theElement.getPoints().get(node).getCoordinates()[0];
                                coords1[1]+=theElement.ShapeFunction(node, xsi1, eta1)*theElement.getPoints().get(node).getCoordinates()[1];
                                coords2[0]+=theElement.ShapeFunction(node, xsi2, eta2)*theElement.getPoints().get(node).getCoordinates()[0];
                                coords2[1]+=theElement.ShapeFunction(node, xsi2, eta2)*theElement.getPoints().get(node).getCoordinates()[1];
                                coords3[0]+=theElement.ShapeFunction(node, xsi3, eta3)*theElement.getPoints().get(node).getCoordinates()[0];
                                coords3[1]+=theElement.ShapeFunction(node, xsi3, eta3)*theElement.getPoints().get(node).getCoordinates()[1];
                                disps[0]+=theElement.ShapeFunction(node, (xsi1+xsi2+xsi3)/3.0, (eta1+eta2+eta3)/3.0)*((gendomain.Node)theElement.getPoints().get(node)).getDisps(step, LC);
                            }
                            dataValue=disps[0];
    //                            int w=Math.abs(xTransformation(coords1[0])-xTransformation(coords_[0]));
    //                            int h=Math.abs(yTransformation(coords1[1])-yTransformation(coords_[1]));
                            xPoints[0]=this.xTransformation(coords1[0]);
                            yPoints[0]=this.yTransformation(coords1[1]);
                            xPoints[1]=this.xTransformation(coords2[0]);
                            yPoints[1]=this.yTransformation(coords2[1]);
                            xPoints[2]=this.xTransformation(coords3[0]);
                            yPoints[2]=this.yTransformation(coords3[1]);
                            double colorLocation =((colorCount-1.0)*(dataValue - dataRangeMin))/(dataRangeMax - dataRangeMin);
                            int colorIndex = (int)colorLocation;
                            if(colorIndex >= colorCount) colorIndex = colorCount-1;
                            if(colorIndex < 0) colorIndex = 0;
                            g2.setColor(colorArray[colorIndex]);
                            g2.fillPolygon(xPoints, yPoints, 3);
                        }
                        for(int j=1;j<resolution-res;j++){
                            double xsi1=-i/((double)resolution)+(resolution-j)/((double)resolution);
                            double eta1=j/((double)resolution);
                            double xsi2=-i/((double)resolution)+(resolution-j-1)/((double)resolution);
                            double eta2=(j)/((double)resolution);
                            double xsi3=-i/((double)resolution)+(resolution-j)/((double)resolution);
                            double eta3=(j-1)/((double)resolution);
                            double[] coords1 = new double[2]; coords1[0]=0.0; coords1[1]=0.0;
                            double[] coords2 = new double[2]; coords2[0]=0.0; coords2[1]=0.0;
                            double[] coords3 = new double[2]; coords3[0]=0.0; coords3[1]=0.0;
                            double[] disps = new double[2]; disps[0]=0.0; disps[1]=0.0;
                            for(int node=1; node<=theElement.getNumNodes();node++){
                                coords1[0]+=theElement.ShapeFunction(node, xsi1, eta1)*theElement.getPoints().get(node).getCoordinates()[0];
                                coords1[1]+=theElement.ShapeFunction(node, xsi1, eta1)*theElement.getPoints().get(node).getCoordinates()[1];
                                coords2[0]+=theElement.ShapeFunction(node, xsi2, eta2)*theElement.getPoints().get(node).getCoordinates()[0];
                                coords2[1]+=theElement.ShapeFunction(node, xsi2, eta2)*theElement.getPoints().get(node).getCoordinates()[1];
                                coords3[0]+=theElement.ShapeFunction(node, xsi3, eta3)*theElement.getPoints().get(node).getCoordinates()[0];
                                coords3[1]+=theElement.ShapeFunction(node, xsi3, eta3)*theElement.getPoints().get(node).getCoordinates()[1];
                                disps[0]+=theElement.ShapeFunction(node, (xsi1+xsi2+xsi3)/3.0, (eta1+eta2+eta3)/3.0)*((gendomain.Node)theElement.getPoints().get(node)).getDisps(step, LC);
                            }
                            dataValue=disps[0];
    //                            int w=Math.abs(xTransformation(coords1[0])-xTransformation(coords_[0]));
    //                            int h=Math.abs(yTransformation(coords1[1])-yTransformation(coords_[1]));
                            xPoints[0]=this.xTransformation(coords1[0]);
                            yPoints[0]=this.yTransformation(coords1[1]);
                            xPoints[1]=this.xTransformation(coords2[0]);
                            yPoints[1]=this.yTransformation(coords2[1]);
                            xPoints[2]=this.xTransformation(coords3[0]);
                            yPoints[2]=this.yTransformation(coords3[1]);
                            double colorLocation =((colorCount-1.0)*(dataValue - dataRangeMin))/(dataRangeMax - dataRangeMin);
                            int colorIndex = (int)colorLocation;
                            if(colorIndex >= colorCount) colorIndex = colorCount-1;
                            if(colorIndex < 0) colorIndex = 0;
                            g2.setColor(colorArray[colorIndex]);
                            g2.fillPolygon(xPoints, yPoints, 3);
                        }
                        res+=1;
                    }
                    break;
                default:
                    com.append("not recognised element's dimension: "+ShapeDimension+" for element id: "+theElement.getID());
            }
//            break;
        }
    }
    
    public void drawContourDEMDomain(Graphics2D g2,jdem.DEMdomain theDomain){
        colorCount  = 64;
        colorArray = new Color[colorCount];
        setupColors();
        double dataValue;
        dataRangeMin = Double.POSITIVE_INFINITY;
        dataRangeMax = Double.NEGATIVE_INFINITY;
        double omega=0.0,xd,yd;
        for(Iterator<Particle> eit=theDomain.getParticles().values().iterator(); eit.hasNext();){
            Particle theParticle= eit.next();
            if(theParticle.getCentroid().getuEFTable()[5]!=0)omega=theParticle.getCentroid().getDisps(this.step,5);
            xd=theParticle.getCentroid().getDisps(this.step,0);
            yd=theParticle.getCentroid().getDisps(this.step,1);
            dataValue=Math.sqrt(xd*xd+yd*yd);
            if(dataValue>dataRangeMax)dataRangeMax=dataValue;
            if(dataValue<dataRangeMin)dataRangeMin=dataValue;
        }
        //if(Math.abs(dataRangeMax- dataRangeMin) < 1.0e-10) return;
        if(this.showcolorbar)this.drawCLBar(g2);
        
        for(Iterator<Particle> eit=theDomain.getParticles().values().iterator(); eit.hasNext();){
            Particle theParticle= eit.next();
            if(theParticle.getCentroid().getuEFTable()[5]!=0)omega=theParticle.getCentroid().getDisps(this.step,5);
            xd=theParticle.getCentroid().getDisps(this.step,0);
            yd=theParticle.getCentroid().getDisps(this.step,1);
            dataValue=Math.sqrt(xd*xd+yd*yd);
            if(theParticle.getShape().getClass().toString().equalsIgnoreCase(geom.Circle.class.toString())){
                Circle aCircle=(Circle) theParticle.getShape();
                double r=aCircle.getR();
                double xc=theParticle.getCentroid().X();//+defscale*xd;
                double yc=theParticle.getCentroid().Y();//+defscale*yd;
                int nseg=20;
                GeneralPath gp = new GeneralPath();
                gp.moveTo(xTransformation(xc+r*Math.cos(0*2*Math.PI/nseg)),this.yTransformation(yc+r*Math.sin(0*2*Math.PI/nseg)));
                for(int j=1;j<=nseg;j++){
                    gp.lineTo(xTransformation(xc+r*Math.cos(j*2*Math.PI/nseg)),this.yTransformation(yc+r*Math.sin(j*2*Math.PI/nseg)));
                }
                gp.closePath();
                Color thc=g2.getColor();
                double colorLocation =((colorCount-1.0)*(dataValue - dataRangeMin))/(dataRangeMax - dataRangeMin);
                int colorIndex = (int)colorLocation;
                if(colorIndex >= colorCount) colorIndex = colorCount-1;
                if(colorIndex < 0) colorIndex = 0;
                g2.setColor(colorArray[colorIndex]);
                g2.fill(gp);
                g2.drawLine(xTransformation(xc), yTransformation(yc)
                        , xTransformation(xc+r*Math.cos(omega)), yTransformation(yc+r*Math.sin(omega)));
                g2.setColor(thc);
            }
        }
    }
    
    public void drawContourPDEDomain(Graphics2D g2, jpde theDomain){
        colorCount  = 64;
        colorArray = new Color[colorCount];
        setupColors();
        double dataValue=0.0;
        dataRangeMin = Double.POSITIVE_INFINITY;
        dataRangeMax = Double.NEGATIVE_INFINITY;
//        for(Iterator<jfem.Node> nit=theDomain.getNodes().values().iterator(); nit.hasNext();){
        for(int eit=1;eit<=theDomain.mesh.getElementList().size(); eit++){
            edu.uta.futureye.core.Element theElement= theDomain.mesh.getElementList().at(eit);
            if(this.GlobalMinMax){
                for(int ct=0;ct<theDomain.getNumSteps();ct++){
                    dataValue=0.0;
                    for(int nit=1;nit<=theElement.nodes.size();nit++){
                        //if(theDomain.getSol(ct)!=null)dataValue=theDomain.getSol(ct).get(theElement.getNodeDOFList(nit).at(1).getGlobalIndex());
                        if(theDomain.getSol(ct)!=null)dataValue=theDomain.getSol(ct).get(theElement.nodes.at(nit).globalIndex);
                        if(dataValue>dataRangeMax)dataRangeMax=dataValue;
                        if(dataValue<dataRangeMin)dataRangeMin=dataValue;
                    }
                }
            }else{
                for(int nit=1;nit<=theElement.nodes.size();nit++){
                    dataValue=0.0;
                    //if(theDomain.getSol(step)!=null)dataValue=theDomain.getSol(step).get(theElement.getNodeDOFList(nit).at(1).getGlobalIndex());
                    if(theDomain.getSol(step)!=null)dataValue=theDomain.getSol(step).get(theElement.nodes.at(nit).globalIndex);
                    if(dataValue>dataRangeMax)dataRangeMax=dataValue;
                    if(dataValue<dataRangeMin)dataRangeMin=dataValue;
                }
            }
        }
        
        //if(Math.abs(dataRangeMax- dataRangeMin) < 1.0e-10) return;
        if(this.showcolorbar)this.drawCLBar(g2);
        for(int eit=1;eit<=theDomain.mesh.getElementList().size(); eit++){
            edu.uta.futureye.core.Element theElement= theDomain.mesh.getElementList().at(eit);
            switch(theElement.dim()){
                case 2:
                    switch(theElement.nodes.size()){
                        case 4:
                            Point[] thePoints = new Point[4];
                            thePoints[0]=new Point(theElement.nodes.at(1).coord(1),theElement.nodes.at(1).coord(2));    
                            thePoints[1]=new Point(theElement.nodes.at(2).coord(1),theElement.nodes.at(2).coord(2));
                            thePoints[2]=new Point(theElement.nodes.at(3).coord(1),theElement.nodes.at(3).coord(2));
                            thePoints[3]=new Point(theElement.nodes.at(4).coord(1),theElement.nodes.at(4).coord(2));
                            Rectangle theRectangle = new Rectangle(0,thePoints);
                            
                            int[] xPoints=new int[4];
                            int[] yPoints=new int[4];
                            for(int i=0;i<resolution;i++){
                                for(int j=0;j<resolution;j++){
                                    double xsi1=-1.0+i*2.0/((double)resolution);
                                    double eta1=-1.0+j*2.0/((double)resolution);
                                    double xsi2=-1.0+(i+1)*2.0/((double)resolution);
                                    double eta2=-1.0+j*2.0/((double)resolution);
                                    double xsi3=-1.0+(i+1)*2.0/((double)resolution);
                                    double eta3=-1.0+(j+1)*2.0/((double)resolution);
                                    double xsi4=-1.0+i*2.0/((double)resolution);
                                    double eta4=-1.0+(j+1)*2.0/((double)resolution);
                                    double[] coords1 = new double[2]; coords1[0]=0.0; coords1[1]=0.0;
                                    double[] coords2 = new double[2]; coords2[0]=0.0; coords2[1]=0.0;
                                    double[] coords3 = new double[2]; coords3[0]=0.0; coords3[1]=0.0;
                                    double[] coords4 = new double[2]; coords4[0]=0.0; coords4[1]=0.0;
                                    double[] disps = new double[2]; disps[0]=0.0; disps[1]=0.0;
                                    for(int node=1; node<=theRectangle.getNumNodes();node++){
                                        coords1[0]+=theRectangle.ShapeFunction(node, xsi1, eta1)*theRectangle.getPoints().get(node).getCoordinates()[0];
                                        coords1[1]+=theRectangle.ShapeFunction(node, xsi1, eta1)*theRectangle.getPoints().get(node).getCoordinates()[1];
                                        coords2[0]+=theRectangle.ShapeFunction(node, xsi2, eta2)*theRectangle.getPoints().get(node).getCoordinates()[0];
                                        coords2[1]+=theRectangle.ShapeFunction(node, xsi2, eta2)*theRectangle.getPoints().get(node).getCoordinates()[1];
                                        coords3[0]+=theRectangle.ShapeFunction(node, xsi3, eta3)*theRectangle.getPoints().get(node).getCoordinates()[0];
                                        coords3[1]+=theRectangle.ShapeFunction(node, xsi3, eta3)*theRectangle.getPoints().get(node).getCoordinates()[1];
                                        coords4[0]+=theRectangle.ShapeFunction(node, xsi4, eta4)*theRectangle.getPoints().get(node).getCoordinates()[0];
                                        coords4[1]+=theRectangle.ShapeFunction(node, xsi4, eta4)*theRectangle.getPoints().get(node).getCoordinates()[1];
                                        //if(theDomain.getSol(step)!=null)disps[0]+=theRectangle.ShapeFunction(node, (xsi1+xsi2+xsi3+xsi4)/4.0, (eta1+eta2+eta3+eta4)/4.0)*theDomain.getSol(step).get(theElement.getNodeDOFList(node).at(1).getGlobalIndex());
                                        if(theDomain.getSol(step)!=null)disps[0]+=theRectangle.ShapeFunction(node, (xsi1+xsi2+xsi3+xsi4)/4.0, (eta1+eta2+eta3+eta4)/4.0)*theDomain.getSol(step).get(theElement.nodes.at(node).globalIndex);
                                    }
                                    dataValue=disps[0];
            //                            int w=Math.abs(xTransformation(coords1[0])-xTransformation(coords_[0]));
            //                            int h=Math.abs(yTransformation(coords1[1])-yTransformation(coords_[1]));
                                    xPoints[0]=this.xTransformation(coords1[0]);
                                    yPoints[0]=this.yTransformation(coords1[1]);
                                    xPoints[1]=this.xTransformation(coords2[0]);
                                    yPoints[1]=this.yTransformation(coords2[1]);
                                    xPoints[2]=this.xTransformation(coords3[0]);
                                    yPoints[2]=this.yTransformation(coords3[1]);
                                    xPoints[3]=this.xTransformation(coords4[0]);
                                    yPoints[3]=this.yTransformation(coords4[1]);
                                    double colorLocation =((colorCount-1.0)*(dataValue - dataRangeMin))/(dataRangeMax - dataRangeMin);
                                    int colorIndex = (int)colorLocation;
                                    if(colorIndex >= colorCount) colorIndex = colorCount-1;
                                    if(colorIndex < 0) colorIndex = 0;
                                    g2.setColor(colorArray[colorIndex]);
                                    g2.fillPolygon(xPoints, yPoints, 4);
                                }
                            }
                            break;
                        case 3:
                            thePoints = new Point[3];
                            thePoints[0]=new Point(theElement.nodes.at(1).coord(1),theElement.nodes.at(1).coord(2));    
                            thePoints[1]=new Point(theElement.nodes.at(2).coord(1),theElement.nodes.at(2).coord(2));
                            thePoints[2]=new Point(theElement.nodes.at(3).coord(1),theElement.nodes.at(3).coord(2)); 
                            Triangle theTriangle = new Triangle(0,thePoints);
                            
                            xPoints=new int[3];
                            yPoints=new int[3];
                            int res=0;
                            for(int i=0;i<resolution;i++){
                                for(int j=0;j<resolution-res;j++){
                                    double xsi1=-i/((double)resolution)+(resolution-j)/((double)resolution);
                                    double eta1=j/((double)resolution);
                                    double xsi2=-i/((double)resolution)+(resolution-j-1)/((double)resolution);
                                    double eta2=(j+1)/((double)resolution);
                                    double xsi3=-i/((double)resolution)+(resolution-j-1)/((double)resolution);
                                    double eta3=j/((double)resolution);
                                    double[] coords1 = new double[2]; coords1[0]=0.0; coords1[1]=0.0;
                                    double[] coords2 = new double[2]; coords2[0]=0.0; coords2[1]=0.0;
                                    double[] coords3 = new double[2]; coords3[0]=0.0; coords3[1]=0.0;
                                    double[] disps = new double[2]; disps[0]=0.0; disps[1]=0.0;
                                    for(int node=1; node<=theTriangle.getNumNodes();node++){
                                        coords1[0]+=theTriangle.ShapeFunction(node, xsi1, eta1)*theTriangle.getPoints().get(node).getCoordinates()[0];
                                        coords1[1]+=theTriangle.ShapeFunction(node, xsi1, eta1)*theTriangle.getPoints().get(node).getCoordinates()[1];
                                        coords2[0]+=theTriangle.ShapeFunction(node, xsi2, eta2)*theTriangle.getPoints().get(node).getCoordinates()[0];
                                        coords2[1]+=theTriangle.ShapeFunction(node, xsi2, eta2)*theTriangle.getPoints().get(node).getCoordinates()[1];
                                        coords3[0]+=theTriangle.ShapeFunction(node, xsi3, eta3)*theTriangle.getPoints().get(node).getCoordinates()[0];
                                        coords3[1]+=theTriangle.ShapeFunction(node, xsi3, eta3)*theTriangle.getPoints().get(node).getCoordinates()[1];
                                        //if(theDomain.getSol(step)!=null)disps[0]+=theTriangle.ShapeFunction(node, (xsi1+xsi2+xsi3)/3.0, (eta1+eta2+eta3)/3.0)*theDomain.getSol(step).get(theElement.getNodeDOFList(node).at(1).getGlobalIndex());
                                        if(theDomain.getSol(step)!=null)disps[0]+=theTriangle.ShapeFunction(node, (xsi1+xsi2+xsi3)/3.0, (eta1+eta2+eta3)/3.0)*theDomain.getSol(step).get(theElement.nodes.at(node).globalIndex);
                                    }
                                    dataValue=disps[0];
            //                            int w=Math.abs(xTransformation(coords1[0])-xTransformation(coords_[0]));
            //                            int h=Math.abs(yTransformation(coords1[1])-yTransformation(coords_[1]));
                                    xPoints[0]=this.xTransformation(coords1[0]);
                                    yPoints[0]=this.yTransformation(coords1[1]);
                                    xPoints[1]=this.xTransformation(coords2[0]);
                                    yPoints[1]=this.yTransformation(coords2[1]);
                                    xPoints[2]=this.xTransformation(coords3[0]);
                                    yPoints[2]=this.yTransformation(coords3[1]);
                                    double colorLocation =((colorCount-1.0)*(dataValue - dataRangeMin))/(dataRangeMax - dataRangeMin);
                                    int colorIndex = (int)colorLocation;
                                    if(colorIndex >= colorCount) colorIndex = colorCount-1;
                                    if(colorIndex < 0) colorIndex = 0;
                                    g2.setColor(colorArray[colorIndex]);
                                    g2.fillPolygon(xPoints, yPoints, 3);
                                }
                                for(int j=1;j<resolution-res;j++){
                                    double xsi1=-i/((double)resolution)+(resolution-j)/((double)resolution);
                                    double eta1=j/((double)resolution);
                                    double xsi2=-i/((double)resolution)+(resolution-j-1)/((double)resolution);
                                    double eta2=(j)/((double)resolution);
                                    double xsi3=-i/((double)resolution)+(resolution-j)/((double)resolution);
                                    double eta3=(j-1)/((double)resolution);
                                    double[] coords1 = new double[2]; coords1[0]=0.0; coords1[1]=0.0;
                                    double[] coords2 = new double[2]; coords2[0]=0.0; coords2[1]=0.0;
                                    double[] coords3 = new double[2]; coords3[0]=0.0; coords3[1]=0.0;
                                    double[] disps = new double[2]; disps[0]=0.0; disps[1]=0.0;
                                    for(int node=1; node<=theTriangle.getNumNodes();node++){
                                        coords1[0]+=theTriangle.ShapeFunction(node, xsi1, eta1)*theTriangle.getPoints().get(node).getCoordinates()[0];
                                        coords1[1]+=theTriangle.ShapeFunction(node, xsi1, eta1)*theTriangle.getPoints().get(node).getCoordinates()[1];
                                        coords2[0]+=theTriangle.ShapeFunction(node, xsi2, eta2)*theTriangle.getPoints().get(node).getCoordinates()[0];
                                        coords2[1]+=theTriangle.ShapeFunction(node, xsi2, eta2)*theTriangle.getPoints().get(node).getCoordinates()[1];
                                        coords3[0]+=theTriangle.ShapeFunction(node, xsi3, eta3)*theTriangle.getPoints().get(node).getCoordinates()[0];
                                        coords3[1]+=theTriangle.ShapeFunction(node, xsi3, eta3)*theTriangle.getPoints().get(node).getCoordinates()[1];
                                        //if(theDomain.getSol(step)!=null)disps[0]+=theTriangle.ShapeFunction(node, (xsi1+xsi2+xsi3)/3.0, (eta1+eta2+eta3)/3.0)*theDomain.getSol(step).get(theElement.getNodeDOFList(node).at(1).getGlobalIndex());
                                        if(theDomain.getSol(step)!=null)disps[0]+=theTriangle.ShapeFunction(node, (xsi1+xsi2+xsi3)/3.0, (eta1+eta2+eta3)/3.0)*theDomain.getSol(step).get(theElement.nodes.at(node).globalIndex);
                                    }
                                    dataValue=disps[0];
                                    xPoints[0]=this.xTransformation(coords1[0]);
                                    yPoints[0]=this.yTransformation(coords1[1]);
                                    xPoints[1]=this.xTransformation(coords2[0]);
                                    yPoints[1]=this.yTransformation(coords2[1]);
                                    xPoints[2]=this.xTransformation(coords3[0]);
                                    yPoints[2]=this.yTransformation(coords3[1]);
                                    double colorLocation =((colorCount-1.0)*(dataValue - dataRangeMin))/(dataRangeMax - dataRangeMin);
                                    int colorIndex = (int)colorLocation;
                                    if(colorIndex >= colorCount) colorIndex = colorCount-1;
                                    if(colorIndex < 0) colorIndex = 0;
                                    g2.setColor(colorArray[colorIndex]);
                                    g2.fillPolygon(xPoints, yPoints, 3);
                                }
                                res+=1;
                            }
                            break;
                        default:
                            com.append("yet not able to make contour for pde element of: "+theElement.nodes.size()+" nodes, in "+theElement.dim()+" dimensions.");    
                    }
                   
                    break;
                default:
                    com.append("not recognised element's dimension: "+theElement.dim()+" for element id: "+theElement.globalIndex);
            }
        }
    }
    
    public void drawContourFEMDomain(Graphics2D g2,jfem.Domain theDomain){
        colorCount  = 64;
        colorArray = new Color[colorCount];
        setupColors();
        double p,ux,uy,dataValue = 0;
        dataRangeMin = Double.POSITIVE_INFINITY;
        dataRangeMax = Double.NEGATIVE_INFINITY;
        for(Iterator<jfem.Node> nit=theDomain.getNodes().values().iterator(); nit.hasNext();){
            jfem.Node theNode= nit.next();
            if(this.GlobalMinMax){
                for(int ct=0;ct<=theDomain.getLoadCase(LC).getNumOfIncrements();ct++){
                    ux=theNode.getLoadCaseDisps(LC,ct)[0];
                    uy=theNode.getLoadCaseDisps(LC,ct)[1];
                    p=theNode.getLoadCaseDisps(LC,ct)[6];
                    dataValue=Math.sqrt(ux*ux+uy*uy+p*p);
                    if(dataValue>dataRangeMax)dataRangeMax=dataValue;
                    if(dataValue<dataRangeMin)dataRangeMin=dataValue;
                }
            }else{
                ux=theNode.getLoadCaseDisps(LC,step)[0];
                uy=theNode.getLoadCaseDisps(LC,step)[1];
                p=theNode.getLoadCaseDisps(LC,step)[6];
                dataValue=Math.sqrt(ux*ux+uy*uy+p*p);
                if(dataValue>dataRangeMax)dataRangeMax=dataValue;
                if(dataValue<dataRangeMin)dataRangeMin=dataValue;
            }
        }
        
        //if(Math.abs(dataRangeMax- dataRangeMin) < 1.0e-10) return;
        if(this.showcolorbar)this.drawCLBar(g2);
        
        for(Iterator<jfem.Element> eit=theDomain.getElements().values().iterator(); eit.hasNext();){
            jfem.Element theElement= eit.next();
            switch(theElement.getDimension()){
                case 1:
                    for(int i=0;i<resolution;i++){
                        double xsi=-1.+i*2.0/((double)resolution);
                        double xsi_=-1.+(i+1)*2.0/((double)resolution);
                        double[] coords = new double[2]; coords[0]=0.0; coords[1]=0.0;
                        double[] coords_ = new double[2]; coords_[0]=0.0; coords_[1]=0.0;
                        double[] disps = new double[2]; disps[0]=0.0; disps[1]=0.0; p=0.0;
                        for(int node=1; node<=theElement.getNumNodes();node++){
                            coords[0]+=theElement.ShapeFunction(node, xsi)*theElement.getNodeHierarchy(node).getCoords()[0];
                            coords[1]+=theElement.ShapeFunction(node, xsi)*theElement.getNodeHierarchy(node).getCoords()[1];
                            coords_[0]+=theElement.ShapeFunction(node, xsi_)*theElement.getNodeHierarchy(node).getCoords()[0];
                            coords_[1]+=theElement.ShapeFunction(node, xsi_)*theElement.getNodeHierarchy(node).getCoords()[1];
                            disps[0]+=theElement.ShapeFunction(node, xsi)*theElement.getNodeHierarchy(node).getLoadCaseDisps(LC,step)[0];
                            disps[1]+=theElement.ShapeFunction(node, xsi)*theElement.getNodeHierarchy(node).getLoadCaseDisps(LC,step)[1];
                            p+=theElement.ShapeFunction(node, xsi)*theElement.getNodeHierarchy(node).getLoadCaseDisps(LC,step)[6];
                        }
                        dataValue=Math.sqrt(disps[0]*disps[0]+disps[1]*disps[1]+p*p);
                        double colorLocation =((colorCount-1.0)*(dataValue - dataRangeMin))/(dataRangeMax - dataRangeMin);
                        int colorIndex = (int)colorLocation;
                        if(colorIndex >= colorCount) colorIndex = colorCount-1;
                        if(colorIndex < 0) colorIndex = 0;
                        g2.setColor(colorArray[colorIndex]);
                        g2.drawLine(xTransformation(coords[0]), yTransformation(coords[1]), xTransformation(coords_[0]), yTransformation(coords_[1]));
                    }
                    break;
                case 2:
                    for(int j=1;j<=resolution;j++){
                        double eta1=-1.+(j-1)*2.0/((double)resolution);
                        double eta2=-1.+(j)*2.0/((double)resolution);
                        for(int i=1;i<=resolution;i++){
                            double xsi1=-1.+(i-1)*2.0/((double)resolution);
                            double xsi2=-1.+(i)*2.0/((double)resolution);
                            
                            double[] coordx = new double[theElement.getNumNodes()];
                            double[] coordy = new double[theElement.getNumNodes()];
                            double[] dispx =  new double[theElement.getNumNodes()];
                            double[] dispy =  new double[theElement.getNumNodes()];
                            double[] press =  new double[theElement.getNumNodes()];
                            
                            coordx[0]=0.0;coordx[1]=0.0;coordx[2]=0.0;coordx[3]=0.0;
                            coordy[0]=0.0;coordy[1]=0.0;coordy[2]=0.0;coordy[3]=0.0;
                            dispx[0]=0.0;dispx[1]=0.0;dispx[2]=0.0;dispx[3]=0.0;
                            dispy[0]=0.0;dispy[1]=0.0;dispy[2]=0.0;dispy[3]=0.0;
                            press[0]=0.0;press[1]=0.0;press[2]=0.0;press[3]=0.0;
                            for(int sf=1;sf<=theElement.getNumNodes();sf++){
                                coordx[0]+=theElement.ShapeFunction(sf, xsi1, eta1)*theElement.getNodeHierarchy(sf).getCoords()[0];
                                coordx[1]+=theElement.ShapeFunction(sf, xsi2, eta1)*theElement.getNodeHierarchy(sf).getCoords()[0];
                                coordx[2]+=theElement.ShapeFunction(sf, xsi2, eta2)*theElement.getNodeHierarchy(sf).getCoords()[0];
                                coordx[3]+=theElement.ShapeFunction(sf, xsi1, eta2)*theElement.getNodeHierarchy(sf).getCoords()[0];
                                
                                
                                coordy[0]+=theElement.ShapeFunction(sf, xsi1, eta1)*theElement.getNodeHierarchy(sf).getCoords()[1];
                                coordy[1]+=theElement.ShapeFunction(sf, xsi2, eta1)*theElement.getNodeHierarchy(sf).getCoords()[1];
                                coordy[2]+=theElement.ShapeFunction(sf, xsi2, eta2)*theElement.getNodeHierarchy(sf).getCoords()[1];
                                coordy[3]+=theElement.ShapeFunction(sf, xsi1, eta2)*theElement.getNodeHierarchy(sf).getCoords()[1];
                                
                                dispx[0]+=theElement.ShapeFunction(sf, xsi1, xsi1)*theElement.getNodeHierarchy(sf).getLoadCaseDisps(LC,step)[0];
                                dispx[1]+=theElement.ShapeFunction(sf, xsi2, eta1)*theElement.getNodeHierarchy(sf).getLoadCaseDisps(LC,step)[0];
                                dispx[2]+=theElement.ShapeFunction(sf, xsi2, eta2)*theElement.getNodeHierarchy(sf).getLoadCaseDisps(LC,step)[0];
                                dispx[3]+=theElement.ShapeFunction(sf, xsi1, eta2)*theElement.getNodeHierarchy(sf).getLoadCaseDisps(LC,step)[0];
                                
                                dispy[0]+=theElement.ShapeFunction(sf, xsi1, xsi1)*theElement.getNodeHierarchy(sf).getLoadCaseDisps(LC,step)[1];
                                dispy[1]+=theElement.ShapeFunction(sf, xsi2, eta1)*theElement.getNodeHierarchy(sf).getLoadCaseDisps(LC,step)[1];
                                dispy[2]+=theElement.ShapeFunction(sf, xsi2, eta2)*theElement.getNodeHierarchy(sf).getLoadCaseDisps(LC,step)[1];
                                dispy[3]+=theElement.ShapeFunction(sf, xsi1, eta2)*theElement.getNodeHierarchy(sf).getLoadCaseDisps(LC,step)[1];
                                
                                press[0]+=theElement.ShapeFunction(sf, xsi1, xsi1)*theElement.getNodeHierarchy(sf).getLoadCaseDisps(LC,step)[6];
                                press[1]+=theElement.ShapeFunction(sf, xsi2, eta1)*theElement.getNodeHierarchy(sf).getLoadCaseDisps(LC,step)[6];
                                press[2]+=theElement.ShapeFunction(sf, xsi2, eta2)*theElement.getNodeHierarchy(sf).getLoadCaseDisps(LC,step)[6];
                                press[3]+=theElement.ShapeFunction(sf, xsi1, eta2)*theElement.getNodeHierarchy(sf).getLoadCaseDisps(LC,step)[6];
                                
                            }
                            dataValue=Math.sqrt(dispx[0]*dispx[0]+dispy[0]*dispy[0]+press[0]*press[0])+Math.sqrt(dispx[1]*dispx[1]+dispy[1]*dispy[1]+press[1]*press[1])+
                                    Math.sqrt(dispx[2]*dispx[2]+dispy[2]*dispy[2]+press[2]*press[2])+Math.sqrt(dispx[3]*dispx[3]+dispy[3]*dispy[3]+press[3]*press[3]);
                            
                            dataValue/=4.0;
                            double colorLocation =((colorCount-1.0)*(dataValue - dataRangeMin))/(dataRangeMax - dataRangeMin);
                            int colorIndex = (int)colorLocation;
                            if(colorIndex >= colorCount) colorIndex = colorCount-1;
                            if(colorIndex < 0) colorIndex = 0;
                            g2.setColor(colorArray[colorIndex]);
                            ///////////////
                            GeneralPath polyline = 
                                    new GeneralPath(GeneralPath.WIND_EVEN_ODD, coordx.length);

                            polyline.moveTo (xTransformation(coordx[0]), yTransformation(coordy[0]));

                            for (int index = 1; index < coordx.length; index++) {
                                     polyline.lineTo(xTransformation(coordx[index]), yTransformation(coordy[index]));
                            }
                            polyline.closePath();
                            g2.fill(polyline);
                            ///////////////
                            //g2.fillRect(xTransformation(coords1[0]), yTransformation(coords1[1]), w, h);
                        }
                    }
                    break;
                default:
                    com.append("not recognised element's dimension: "+theElement.getDimension()+" for element id: "+theElement.getID());
            }
        }
    }
    
    public void drawContourStressFEMDomain(Graphics2D g2,jfem.Domain theDomain){
        counting++;
        //System.out.println("theStress: "+this.theStress);
        colorCount  = 64;
        colorArray = new Color[colorCount];
        setupColors();
        double dataValue;
        dataRangeMin = Double.POSITIVE_INFINITY;
        dataRangeMax = Double.NEGATIVE_INFINITY;
        for(Iterator<jfem.Element> eit=theDomain.getElements().values().iterator(); eit.hasNext();){
            jfem.Element theElement= eit.next();
            switch(theElement.getDimension()){
                case 2:
                    double[] coords = new double[2]; 
                    coords[0]=-1.0; coords[1]=-1.0;
                    if(theStress==6){
                        dataValue=theElement.getStressVonMisses(coords, LC, step);
                    }else{
                        dataValue=theElement.getStress(coords, LC, step)[theStress];
                    }
                    if(dataValue>dataRangeMax)dataRangeMax=dataValue;
                    if(dataValue<dataRangeMin)dataRangeMin=dataValue;
                    coords[0]=1.0; coords[1]=-1.0;
                    if(theStress==6){
                        dataValue=theElement.getStressVonMisses(coords, LC, step);
                    }else{
                        dataValue=theElement.getStress(coords, LC, step)[theStress];
                    }
                    if(dataValue>dataRangeMax)dataRangeMax=dataValue;
                    if(dataValue<dataRangeMin)dataRangeMin=dataValue;
                    coords[0]=1.0; coords[1]=1.0;
                    if(theStress==6){
                        dataValue=theElement.getStressVonMisses(coords, LC, step);
                    }else{
                        dataValue=theElement.getStress(coords, LC, step)[theStress];
                    }
                    if(dataValue>dataRangeMax)dataRangeMax=dataValue;
                    if(dataValue<dataRangeMin)dataRangeMin=dataValue;
                    coords[0]=-1.0; coords[1]=1.0;
                    if(theStress==6){
                        dataValue=theElement.getStressVonMisses(coords, LC, step);
                    }else{
                        dataValue=theElement.getStress(coords, LC, step)[theStress];
                    }
                    if(dataValue>dataRangeMax)dataRangeMax=dataValue;
                    if(dataValue<dataRangeMin)dataRangeMin=dataValue;
                    break;
                default:
                    com.append("not recognised element's dimension: "+theElement.getDimension()+" for element id: "+theElement.getID());
            }
        }
        
        //if(Math.abs(dataRangeMax- dataRangeMin) < 1.0e-10) return;
        if(this.showcolorbar)this.drawCLBar(g2);
        
        for(Iterator<jfem.Element> eit=theDomain.getElements().values().iterator(); eit.hasNext();){
            jfem.Element theElement= eit.next();
            switch(theElement.getDimension()){
                case 2:
                    for(int j=1;j<=resolution;j++){
                        double eta1=-1.+(j-1)*2.0/((double)resolution);
                        double eta2=-1.+(j)*2.0/((double)resolution);
                        for(int i=1;i<=resolution;i++){
                            double xsi1=-1.+(i-1)*2.0/((double)resolution);
                            double xsi2=-1.+(i)*2.0/((double)resolution);
                            
                            double[] coordx = new double[4];
                            double[] coordy = new double[4];
                            
                            coordx[0]=0.0;coordx[1]=0.0;coordx[2]=0.0;coordx[3]=0.0;
                            coordy[0]=0.0;coordy[1]=0.0;coordy[2]=0.0;coordy[3]=0.0;
                            for(int sf=1;sf<=theElement.getNumNodes();sf++){
                                coordx[0]+=theElement.ShapeFunction(sf, xsi1, eta1)*theElement.getNodeHierarchy(sf).getCoords()[0];
                                coordx[1]+=theElement.ShapeFunction(sf, xsi2, eta1)*theElement.getNodeHierarchy(sf).getCoords()[0];
                                coordx[2]+=theElement.ShapeFunction(sf, xsi2, eta2)*theElement.getNodeHierarchy(sf).getCoords()[0];
                                coordx[3]+=theElement.ShapeFunction(sf, xsi1, eta2)*theElement.getNodeHierarchy(sf).getCoords()[0];
                                
                                coordy[0]+=theElement.ShapeFunction(sf, xsi1, eta1)*theElement.getNodeHierarchy(sf).getCoords()[1];
                                coordy[1]+=theElement.ShapeFunction(sf, xsi2, eta1)*theElement.getNodeHierarchy(sf).getCoords()[1];
                                coordy[2]+=theElement.ShapeFunction(sf, xsi2, eta2)*theElement.getNodeHierarchy(sf).getCoords()[1];
                                coordy[3]+=theElement.ShapeFunction(sf, xsi1, eta2)*theElement.getNodeHierarchy(sf).getCoords()[1];
                                
                            }
                            double[] stress =  new double[4];
                            stress[0]=0.0;stress[1]=0.0;stress[2]=0.0;stress[3]=0.0;
                            double[] at = new double[2]; 
                            at[0]=xsi1; at[1]=eta1;
                            if(theStress==6){stress[0]=theElement.getStressVonMisses(at, LC, step);}else{
                            stress[0]=theElement.getStress(at, LC, step)[theStress];} 
                            at[0]=xsi2; at[1]=eta1;
                            if(theStress==6){stress[1]=theElement.getStressVonMisses(at, LC, step);}else{
                            stress[1]=theElement.getStress(at, LC, step)[theStress];} 
                            at[0]=xsi2; at[1]=eta2;
                            if(theStress==6){stress[2]=theElement.getStressVonMisses(at, LC, step);}else{
                            stress[2]=theElement.getStress(at, LC, step)[theStress];} 
                            at[0]=xsi1; at[1]=eta2;
                            if(theStress==6){stress[3]=theElement.getStressVonMisses(at, LC, step);}else{
                            stress[3]=theElement.getStress(at, LC, step)[theStress];}
                            dataValue=stress[0]+stress[1]+stress[2]+stress[3];
                            dataValue/=4.0;
                            //System.out.println("stress data value = "+dataValue);
                            double colorLocation =((colorCount-1.0)*(dataValue - dataRangeMin))/(dataRangeMax - dataRangeMin);
                            int colorIndex = (int)colorLocation;
                            if(colorIndex >= colorCount) colorIndex = colorCount-1;
                            if(colorIndex < 0) colorIndex = 0;
                            g2.setColor(colorArray[colorIndex]);
                            ///////////////
                            GeneralPath polyline = 
                                    new GeneralPath(GeneralPath.WIND_EVEN_ODD, coordx.length);

                            polyline.moveTo (xTransformation(coordx[0]), yTransformation(coordy[0]));

                            for (int index = 1; index < coordx.length; index++) {
                                     polyline.lineTo(xTransformation(coordx[index]), yTransformation(coordy[index]));
                            }
                            polyline.closePath();
                            g2.fill(polyline);
                            ///////////////
                            //g2.fillRect(xTransformation(coords1[0]), yTransformation(coords1[1]), w, h);
                        }
                    }
                    break;
                default:
                    com.append("not recognised element's dimension: "+theElement.getDimension()+" for element id: "+theElement.getID());
            }
        }
    }
    
    public void drawContourDefStressFEMDomain(Graphics2D g2,jfem.Domain theDomain){
        colorCount  = 64;
        colorArray = new Color[colorCount];
        setupColors();
        double dataValue;
        dataRangeMin = Double.POSITIVE_INFINITY;
        dataRangeMax = Double.NEGATIVE_INFINITY;
        for(Iterator<jfem.Element> eit=theDomain.getElements().values().iterator(); eit.hasNext();){
            jfem.Element theElement= eit.next();
            switch(theElement.getDimension()){
                case 2:
                    double[] coords = new double[2]; 
                    coords[0]=-1.0; coords[1]=-1.0;
                    if(theStress==6){
                        dataValue=theElement.getStressVonMisses(coords, LC, step);
                    }else{
                        dataValue=theElement.getStress(coords, LC, step)[theStress];
                    }
                    if(dataValue>dataRangeMax)dataRangeMax=dataValue;
                    if(dataValue<dataRangeMin)dataRangeMin=dataValue;
                    coords[0]=1.0; coords[1]=-1.0;
                    if(theStress==6){
                        dataValue=theElement.getStressVonMisses(coords, LC, step);
                    }else{
                        dataValue=theElement.getStress(coords, LC, step)[theStress];
                    }
                    if(dataValue>dataRangeMax)dataRangeMax=dataValue;
                    if(dataValue<dataRangeMin)dataRangeMin=dataValue;
                    coords[0]=1.0; coords[1]=1.0;
                    if(theStress==6){
                        dataValue=theElement.getStressVonMisses(coords, LC, step);
                    }else{
                        dataValue=theElement.getStress(coords, LC, step)[theStress];
                    }
                    if(dataValue>dataRangeMax)dataRangeMax=dataValue;
                    if(dataValue<dataRangeMin)dataRangeMin=dataValue;
                    coords[0]=-1.0; coords[1]=1.0;
                    if(theStress==6){
                        dataValue=theElement.getStressVonMisses(coords, LC, step);
                    }else{
                        dataValue=theElement.getStress(coords, LC, step)[theStress];
                    }
                    if(dataValue>dataRangeMax)dataRangeMax=dataValue;
                    if(dataValue<dataRangeMin)dataRangeMin=dataValue;
                    break;
                default:
                    com.append("not recognised element's dimension: "+theElement.getDimension()+" for element id: "+theElement.getID());
            }
        }
        
        //if(Math.abs(dataRangeMax- dataRangeMin) < 1.0e-10) return;
        if(this.showcolorbar)this.drawCLBar(g2);
        
        for(Iterator<jfem.Element> eit=theDomain.getElements().values().iterator(); eit.hasNext();){
            jfem.Element theElement= eit.next();
            switch(theElement.getDimension()){
                case 2:
                    for(int j=1;j<=resolution;j++){
                        double eta1=-1.+(j-1)*2.0/((double)resolution);
                        double eta2=-1.+(j)*2.0/((double)resolution);
                        for(int i=1;i<=resolution;i++){
                            double xsi1=-1.+(i-1)*2.0/((double)resolution);
                            double xsi2=-1.+(i)*2.0/((double)resolution);
                            
                            double[] coordx = new double[4];
                            double[] coordy = new double[4];
                            double[] dispx =  new double[4];
                            double[] dispy =  new double[4];
                            
                            coordx[0]=0.0;coordx[1]=0.0;coordx[2]=0.0;coordx[3]=0.0;
                            coordy[0]=0.0;coordy[1]=0.0;coordy[2]=0.0;coordy[3]=0.0;
                            dispx[0]=0.0;dispx[1]=0.0;dispx[2]=0.0;dispx[3]=0.0;
                            dispy[0]=0.0;dispy[1]=0.0;dispy[2]=0.0;dispy[3]=0.0;
                            for(int sf=1;sf<=theElement.getNumNodes();sf++){
                                coordx[0]+=theElement.ShapeFunction(sf, xsi1, eta1)*theElement.getNodeHierarchy(sf).getCoords()[0];
                                coordx[1]+=theElement.ShapeFunction(sf, xsi2, eta1)*theElement.getNodeHierarchy(sf).getCoords()[0];
                                coordx[2]+=theElement.ShapeFunction(sf, xsi2, eta2)*theElement.getNodeHierarchy(sf).getCoords()[0];
                                coordx[3]+=theElement.ShapeFunction(sf, xsi1, eta2)*theElement.getNodeHierarchy(sf).getCoords()[0];
                                
                                
                                coordy[0]+=theElement.ShapeFunction(sf, xsi1, eta1)*theElement.getNodeHierarchy(sf).getCoords()[1];
                                coordy[1]+=theElement.ShapeFunction(sf, xsi2, eta1)*theElement.getNodeHierarchy(sf).getCoords()[1];
                                coordy[2]+=theElement.ShapeFunction(sf, xsi2, eta2)*theElement.getNodeHierarchy(sf).getCoords()[1];
                                coordy[3]+=theElement.ShapeFunction(sf, xsi1, eta2)*theElement.getNodeHierarchy(sf).getCoords()[1];
                                
                                dispx[0]+=theElement.ShapeFunction(sf, xsi1, xsi1)*theElement.getNodeHierarchy(sf).getLoadCaseDisps(LC,step)[0];
                                dispx[1]+=theElement.ShapeFunction(sf, xsi2, eta1)*theElement.getNodeHierarchy(sf).getLoadCaseDisps(LC,step)[0];
                                dispx[2]+=theElement.ShapeFunction(sf, xsi2, eta2)*theElement.getNodeHierarchy(sf).getLoadCaseDisps(LC,step)[0];
                                dispx[3]+=theElement.ShapeFunction(sf, xsi1, eta2)*theElement.getNodeHierarchy(sf).getLoadCaseDisps(LC,step)[0];
                                
                                dispy[0]+=theElement.ShapeFunction(sf, xsi1, xsi1)*theElement.getNodeHierarchy(sf).getLoadCaseDisps(LC,step)[1];
                                dispy[1]+=theElement.ShapeFunction(sf, xsi2, eta1)*theElement.getNodeHierarchy(sf).getLoadCaseDisps(LC,step)[1];
                                dispy[2]+=theElement.ShapeFunction(sf, xsi2, eta2)*theElement.getNodeHierarchy(sf).getLoadCaseDisps(LC,step)[1];
                                dispy[3]+=theElement.ShapeFunction(sf, xsi1, eta2)*theElement.getNodeHierarchy(sf).getLoadCaseDisps(LC,step)[1];
                                
                            }
                            double[] stress =  new double[4];
                            stress[0]=0.0;stress[1]=0.0;stress[2]=0.0;stress[3]=0.0;
                            double[] at = new double[2]; 
                            at[0]=xsi1; at[1]=eta1;
                            if(theStress==6){stress[0]=theElement.getStressVonMisses(at, LC, step);}else{
                            stress[0]=theElement.getStress(at, LC, step)[theStress];} 
                            at[0]=xsi2; at[1]=eta1;
                            if(theStress==6){stress[1]=theElement.getStressVonMisses(at, LC, step);}else{
                            stress[1]=theElement.getStress(at, LC, step)[theStress];} 
                            at[0]=xsi2; at[1]=eta2;
                            if(theStress==6){stress[2]=theElement.getStressVonMisses(at, LC, step);}else{
                            stress[2]=theElement.getStress(at, LC, step)[theStress];} 
                            at[0]=xsi1; at[1]=eta2;
                            if(theStress==6){stress[3]=theElement.getStressVonMisses(at, LC, step);}else{
                            stress[3]=theElement.getStress(at, LC, step)[theStress];}	
                            dataValue=stress[0]+stress[1]+stress[2]+stress[3];
                            dataValue/=4.0;
                            double colorLocation =((colorCount-1.0)*(dataValue - dataRangeMin))/(dataRangeMax - dataRangeMin);
                            int colorIndex = (int)colorLocation;
                            if(colorIndex >= colorCount) colorIndex = colorCount-1;
                            if(colorIndex < 0) colorIndex = 0;
                            g2.setColor(colorArray[colorIndex]);
                            ///////////////
                            GeneralPath polyline = 
                                    new GeneralPath(GeneralPath.WIND_EVEN_ODD, coordx.length);

                            polyline.moveTo (xTransformation(coordx[0]+dispx[0]*defscale), yTransformation(coordy[0]+dispy[0]*defscale));

                            for (int index = 1; index < coordx.length; index++) {
                                     polyline.lineTo(xTransformation(coordx[index]+dispx[index]*defscale), yTransformation(coordy[index]+dispy[index]*defscale));
                            }
                            polyline.closePath();
                            g2.fill(polyline);
                            ///////////////
                            //g2.fillRect(xTransformation(coords1[0]), yTransformation(coords1[1]), w, h);
                        }
                    }
                    break;
                default:
                    com.append("not recognised element's dimension: "+theElement.getDimension()+" for element id: "+theElement.getID());
            }
        }
    }
    
    public void drawContourDeformedFEMDomain(Graphics2D g2,jfem.Domain theDomain){
        colorCount  = 64;
        colorArray = new Color[colorCount];
        setupColors();
        double ux,uy,dataValue;
        dataRangeMin = Double.POSITIVE_INFINITY;
        dataRangeMax = Double.NEGATIVE_INFINITY;
        for(Iterator<jfem.Node> nit=theDomain.getNodes().values().iterator(); nit.hasNext();){
            jfem.Node theNode= nit.next();
            if(this.GlobalMinMax){
                for(int ct=0;ct<=theDomain.getLoadCase(LC).getNumOfIncrements();ct++){
                    ux=theNode.getLoadCaseDisps(LC,ct)[0];
                    uy=theNode.getLoadCaseDisps(LC,ct)[1];
                    dataValue=Math.sqrt(ux*ux+uy*uy);
                    if(dataValue>dataRangeMax)dataRangeMax=dataValue;
                    if(dataValue<dataRangeMin)dataRangeMin=dataValue;
                }
            }else{
                ux=theNode.getLoadCaseDisps(LC,step)[0];
                uy=theNode.getLoadCaseDisps(LC,step)[1];
                dataValue=Math.sqrt(ux*ux+uy*uy);
                if(dataValue>dataRangeMax)dataRangeMax=dataValue;
                if(dataValue<dataRangeMin)dataRangeMin=dataValue;
            }
        }
        
        //if(Math.abs(dataRangeMax- dataRangeMin) < 1.0e-10) return;
        if(this.showcolorbar)this.drawCLBar(g2);
        
        for(Iterator<jfem.Element> eit=theDomain.getElements().values().iterator(); eit.hasNext();){
            jfem.Element theElement= eit.next();
            switch(theElement.getDimension()){
                case 1:
                    for(int i=0;i<resolution;i++){
                        double xsi=-1.+i*2.0/((double)resolution);
                        double xsi_=-1.+(i+1)*2.0/((double)resolution);
                        double[] coords = new double[2]; coords[0]=0.0; coords[1]=0.0;
                        double[] coords_ = new double[2]; coords_[0]=0.0; coords_[1]=0.0;
                        double[] disps = new double[2]; disps[0]=0.0; disps[1]=0.0;
                        double[] disps_ = new double[2]; disps_[0]=0.0; disps_[1]=0.0;
                        for(int node=1; node<=theElement.getNumNodes();node++){
                            coords[0]+=theElement.ShapeFunction(node, xsi)*theElement.getNodeHierarchy(node).getCoords()[0];
                            coords[1]+=theElement.ShapeFunction(node, xsi)*theElement.getNodeHierarchy(node).getCoords()[1];
                            coords_[0]+=theElement.ShapeFunction(node, xsi_)*theElement.getNodeHierarchy(node).getCoords()[0];
                            coords_[1]+=theElement.ShapeFunction(node, xsi_)*theElement.getNodeHierarchy(node).getCoords()[1];
                            disps[0]+=theElement.ShapeFunction(node, xsi)*theElement.getNodeHierarchy(node).getLoadCaseDisps(LC,step)[0];
                            disps[1]+=theElement.ShapeFunction(node, xsi)*theElement.getNodeHierarchy(node).getLoadCaseDisps(LC,step)[1];
                            disps_[0]+=theElement.ShapeFunction(node, xsi_)*theElement.getNodeHierarchy(node).getLoadCaseDisps(LC,step)[0];
                            disps_[1]+=theElement.ShapeFunction(node, xsi_)*theElement.getNodeHierarchy(node).getLoadCaseDisps(LC,step)[1];
                        }
                        coords[0]+=disps[0]*defscale;
                        coords[1]+=disps[1]*defscale;
                        coords_[0]+=disps_[0]*defscale;
                        coords_[1]+=disps_[1]*defscale;
                        dataValue=Math.sqrt(disps[0]*disps[0]+disps[1]*disps[1]);
                        double colorLocation =((colorCount-1.0)*(dataValue - dataRangeMin))/(dataRangeMax - dataRangeMin);
                        int colorIndex = (int)colorLocation;
                        if(colorIndex >= colorCount) colorIndex = colorCount-1;
                        if(colorIndex < 0) colorIndex = 0;
                        g2.setColor(colorArray[colorIndex]);
                        g2.drawLine(xTransformation(coords[0]), yTransformation(coords[1]), xTransformation(coords_[0]), yTransformation(coords_[1]));
                    }
                    break;
                case 2:
                    for(int j=1;j<=resolution;j++){
                        double eta1=-1.+(j-1)*2.0/((double)resolution);
                        double eta2=-1.+(j)*2.0/((double)resolution);
                        for(int i=1;i<=resolution;i++){
                            double xsi1=-1.+(i-1)*2.0/((double)resolution);
                            double xsi2=-1.+(i)*2.0/((double)resolution);
                            
                            double[] coordx = new double[theElement.getNumNodes()];
                            double[] coordy = new double[theElement.getNumNodes()];
                            double[] dispx =  new double[theElement.getNumNodes()];
                            double[] dispy =  new double[theElement.getNumNodes()];
                            
                            coordx[0]=0.0;coordx[1]=0.0;coordx[2]=0.0;coordx[3]=0.0;
                            coordy[0]=0.0;coordy[1]=0.0;coordy[2]=0.0;coordy[3]=0.0;
                            dispx[0]=0.0;dispx[1]=0.0;dispx[2]=0.0;dispx[3]=0.0;
                            dispy[0]=0.0;dispy[1]=0.0;dispy[2]=0.0;dispy[3]=0.0;
                            for(int sf=1;sf<=theElement.getNumNodes();sf++){
                                coordx[0]+=theElement.ShapeFunction(sf, xsi1, eta1)*theElement.getNodeHierarchy(sf).getCoords()[0];
                                coordx[1]+=theElement.ShapeFunction(sf, xsi2, eta1)*theElement.getNodeHierarchy(sf).getCoords()[0];
                                coordx[2]+=theElement.ShapeFunction(sf, xsi2, eta2)*theElement.getNodeHierarchy(sf).getCoords()[0];
                                coordx[3]+=theElement.ShapeFunction(sf, xsi1, eta2)*theElement.getNodeHierarchy(sf).getCoords()[0];
                                
                                
                                coordy[0]+=theElement.ShapeFunction(sf, xsi1, eta1)*theElement.getNodeHierarchy(sf).getCoords()[1];
                                coordy[1]+=theElement.ShapeFunction(sf, xsi2, eta1)*theElement.getNodeHierarchy(sf).getCoords()[1];
                                coordy[2]+=theElement.ShapeFunction(sf, xsi2, eta2)*theElement.getNodeHierarchy(sf).getCoords()[1];
                                coordy[3]+=theElement.ShapeFunction(sf, xsi1, eta2)*theElement.getNodeHierarchy(sf).getCoords()[1];
                                
                                dispx[0]+=theElement.ShapeFunction(sf, xsi1, xsi1)*theElement.getNodeHierarchy(sf).getLoadCaseDisps(LC,step)[0];
                                dispx[1]+=theElement.ShapeFunction(sf, xsi2, eta1)*theElement.getNodeHierarchy(sf).getLoadCaseDisps(LC,step)[0];
                                dispx[2]+=theElement.ShapeFunction(sf, xsi2, eta2)*theElement.getNodeHierarchy(sf).getLoadCaseDisps(LC,step)[0];
                                dispx[3]+=theElement.ShapeFunction(sf, xsi1, eta2)*theElement.getNodeHierarchy(sf).getLoadCaseDisps(LC,step)[0];
                                
                                dispy[0]+=theElement.ShapeFunction(sf, xsi1, xsi1)*theElement.getNodeHierarchy(sf).getLoadCaseDisps(LC,step)[1];
                                dispy[1]+=theElement.ShapeFunction(sf, xsi2, eta1)*theElement.getNodeHierarchy(sf).getLoadCaseDisps(LC,step)[1];
                                dispy[2]+=theElement.ShapeFunction(sf, xsi2, eta2)*theElement.getNodeHierarchy(sf).getLoadCaseDisps(LC,step)[1];
                                dispy[3]+=theElement.ShapeFunction(sf, xsi1, eta2)*theElement.getNodeHierarchy(sf).getLoadCaseDisps(LC,step)[1];
                                
                            }
                            dataValue=Math.sqrt(dispx[0]*dispx[0]+dispy[0]*dispy[0])+Math.sqrt(dispx[1]*dispx[1]+dispy[1]*dispy[1])+
                                    Math.sqrt(dispx[2]*dispx[2]+dispy[2]*dispy[2])+Math.sqrt(dispx[3]*dispx[3]+dispy[3]*dispy[3]);
                            
                            dataValue/=4.0;
                            double colorLocation =((colorCount-1.0)*(dataValue - dataRangeMin))/(dataRangeMax - dataRangeMin);
                            int colorIndex = (int)colorLocation;
                            if(colorIndex >= colorCount) colorIndex = colorCount-1;
                            if(colorIndex < 0) colorIndex = 0;
                            g2.setColor(colorArray[colorIndex]);
                            ///////////////
                            GeneralPath polyline = 
                                    new GeneralPath(GeneralPath.WIND_EVEN_ODD, coordx.length);

                            polyline.moveTo (xTransformation(coordx[0]+dispx[0]*defscale), yTransformation(coordy[0]+dispy[0]*defscale));

                            for (int index = 1; index < coordx.length; index++) {
                                     polyline.lineTo(xTransformation(coordx[index]+dispx[index]*defscale), yTransformation(coordy[index]+dispy[index]*defscale));
                            }
                            polyline.closePath();
                            g2.fill(polyline);
                            ///////////////
                            //g2.fillRect(xTransformation(coords1[0]), yTransformation(coords1[1]), w, h);
                        }
                    }
                    break;
                default:
                    com.append("not recognised element's dimension: "+theElement.getDimension()+" for element id: "+theElement.getID());
            }
        }
    }
    
    public void drawDeformedGENDomain(Graphics2D g2,gendomain.Domain theDomain){
        double ux,uy;
        Color tColor=g2.getColor();
        g2.setColor(theDomain.ColorNodesDEF);
        boolean init=true;
        int initSt=step;
        GeneralPath polyline=null;
        if(trace){
            polyline =  new GeneralPath(GeneralPath.WIND_NON_ZERO, theDomain.getNumNodes());
            initSt=0;
        }
        for(int time=initSt;time<=step;time++){
            for(Iterator<gendomain.Node> eit=theDomain.getNodes().values().iterator(); eit.hasNext();){
                gendomain.Node theNode= eit.next();
                int di=Math.min(xe-xs,Math.abs(ye-ys))/nodeScale;
                ux=theNode.getDisps(time,0);
                uy=0.0;if(theNode.getNumDOFS()>1)uy=theNode.getDisps(time,1);
                if(drawNodes){
                    if(theDomain.fillNodes){
                        g2.fillOval(this.xTransformation(theNode.getCoords()[0]+ux*this.defscale)-di/2, this.yTransformation(theNode.getCoords()[1]+uy*this.defscale)-di/2, di, di);
                    }else{
                        g2.drawOval(this.xTransformation(theNode.getCoords()[0]+ux*this.defscale)-di/2, this.yTransformation(theNode.getCoords()[1]+uy*this.defscale)-di/2, di, di);
                    }
                }
                if(!init && trace){
                    polyline.lineTo(xTransformation(theNode.X()+ux*defscale), yTransformation(theNode.Y()+uy*defscale));
                }
                if(init && trace){
                    polyline.moveTo (xTransformation(theNode.X()+ux*defscale), yTransformation(theNode.Y()+uy*defscale));
                    init=false;
                }
            }
        }
        
        if(trace){//polyline.closePath(); 
        g2.draw(polyline);}
        g2.setColor(tColor);
    }
    
    public void drawMQNFEMDomain(Graphics2D g2,jfem.Domain theDomain){
        double f;
        AbstractMatrix data;
        double maxv=-jmat.MachinePrecision.getMachinePrecision();
        double minv=-maxv;
        for(Iterator<jfem.Element> eit=theDomain.getElements().values().iterator(); eit.hasNext();){
            jfem.Element theElement= eit.next();
            if(GlobalMinMax){
                for(int tm=this.step_start;tm<=this.step_end;tm++){
                    data = theElement.getF(this.LC,this.step);
                    for(int j=1;j<=theElement.getNumNodes();j++){
                        double v=0.0;
                        if(theMQN!=3)
                        {
                            v=data.get(theMQN-1+(j-1)*theElement.get_dof_per_node(), 0);
                        }else{
                            if(theElement.getClass()==jfem.EBeam2d.class)v=data.get(theMQN-1+(j-1)*theElement.get_dof_per_node(), 0);
                        }
                        if(v>maxv)maxv=v;
                        if(v<minv)minv=v;
                    }
                }
            }else{
                data = theElement.getF(this.LC,this.step);
                for(int j=1;j<=theElement.getNumNodes();j++){
                    double v=0.0;
                    if(theMQN!=3)
                    {
                        v=data.get(theMQN-1+(j-1)*theElement.get_dof_per_node(), 0);
                    }else{
                        if(theElement.getClass()==jfem.EBeam2d.class)v=data.get(theMQN-1+(j-1)*theElement.get_dof_per_node(), 0);
                    }
                    if(v>maxv)maxv=v;
                    if(v<minv)minv=v;
                }
            }
        }
        double[] normal;
        for(Iterator<jfem.Element> eit=theDomain.getElements().values().iterator(); eit.hasNext();){
            g2.setColor(Color.RED);
            jfem.Element theElement= eit.next();
            GeneralPath gp = new GeneralPath();
            data = theElement.getF(this.LC,this.step);
            int xp,yp,xp_ = 0,yp_ = 0;
            for(int j=1;j<=theElement.getNumNodes();j++){
                g2.setColor(Color.RED);
                f=0.0;
                if(theMQN!=3){
                    f=data.get(theMQN-1+(j-1)*theElement.get_dof_per_node(), 0);
                }else{
                    if(theElement.getClass()==jfem.EBeam2d.class)f=data.get(theMQN-1+(j-1)*theElement.get_dof_per_node(), 0);
                }
                //f=data.get(theMQN-1+(j-1)*theElement.get_dof_per_node(), 0); // get(i, 0); i dof
                int nid=theElement.getNodeHierarchy(j).getID();
                normal=theElement.getNormal(nid);normal[0]=-normal[0];normal[1]=-normal[1];normal[2]=-normal[2];
//                double angle=Math.atan2(normal[1], normal[0])-Math.atan2(0.0, 1.0);
//                if(angle<0)angle+=2.0*Math.PI;
                int xn=xTransformation(theElement.getNodeHierarchy(j).getCoords()[0]);
                int yn=yTransformation(theElement.getNodeHierarchy(j).getCoords()[1]);
                double coef=f*diagramscale*0.1*Math.min(max_x-min_x, max_y-min_y);
                if(Math.max(Math.abs(maxv), Math.abs(minv))>jmat.MachinePrecision.getMachineZero())coef/=Math.max(Math.abs(maxv), Math.abs(minv));
                xp = xTransformation(normal[0]*coef+theElement.getNodeHierarchy(j).getCoords()[0]);
                yp = yTransformation(normal[1]*coef+theElement.getNodeHierarchy(j).getCoords()[1]);
                g2.drawLine(xn, yn, xp, yp);
                if(j>1){
                    g2.drawLine(xp_, yp_, xp, yp);
                }
                xp_=xp; yp_=yp;
                if(diagramvalues){
                    DecimalFormat df = new DecimalFormat("##.00#");
                    g2.setColor(Color.WHITE);
                    g2.drawString(df.format(f), xp, yp);
                }
            }
        }
    }
    
    public void drawDisplacementFEMDomain(Graphics2D g2, jfem.Domain theDomain){
        Color origcol=g2.getColor();
        double f;
        double maxv=-jmat.MachinePrecision.getMachinePrecision();
        double minv=-maxv;
        for(Iterator<jfem.Element> eit=theDomain.getElements().values().iterator(); eit.hasNext();){
            jfem.Element theElement= eit.next();
            if(GlobalMinMax){
                for(int tm=this.step_start;tm<=this.step_end;tm++){
                    for(int j=1;j<=theElement.getNumNodes();j++){
                        double v=theElement.getNodeHierarchy(j).getLoadCaseEvolution(theDisp-1, LC)[tm];
                        if(v>maxv)maxv=v;
                        if(v<minv)minv=v;
                    }
                }
            }else{
                for(int j=1;j<=theElement.getNumNodes();j++){
                    double v=theElement.getNodeHierarchy(j).getLoadCaseEvolution(theDisp-1, LC)[step];
                    if(v>maxv)maxv=v;
                    if(v<minv)minv=v;
                }
            }
        }
        double[] normal;
        for(Iterator<jfem.Element> eit=theDomain.getElements().values().iterator(); eit.hasNext();){
            g2.setColor(Color.RED);
            jfem.Element theElement= eit.next();
            GeneralPath gp = new GeneralPath();
            int xp,yp,xp_ = 0,yp_ = 0;
            for(int j=1;j<=theElement.getNumNodes();j++){
                g2.setColor(Color.RED);
                f=theElement.getNodeHierarchy(j).getLoadCaseEvolution(theDisp-1, LC)[step];
                int nid=theElement.getNodeHierarchy(j).getID();
                normal=theElement.getNormal(nid);normal[0]=-normal[0];normal[1]=-normal[1];normal[2]=-normal[2];
                int xn=xTransformation(theElement.getNodeHierarchy(j).getCoords()[0]);
                int yn=yTransformation(theElement.getNodeHierarchy(j).getCoords()[1]);
                double coef=f*diagramscale*0.1*Math.min(max_x-min_x, max_y-min_y);
                if(Math.max(Math.abs(maxv), Math.abs(minv))>jmat.MachinePrecision.getMachineZero())coef/=Math.max(Math.abs(maxv), Math.abs(minv));
                xp = xTransformation(normal[0]*coef+theElement.getNodeHierarchy(j).getCoords()[0]);
                yp = yTransformation(normal[1]*coef+theElement.getNodeHierarchy(j).getCoords()[1]);
                g2.drawLine(xn, yn, xp, yp);
                if(j>1){
                    g2.drawLine(xp_, yp_, xp, yp);
                }
                xp_=xp; yp_=yp;
                if(diagramvalues){
                    DecimalFormat df = new DecimalFormat("##.00#");
                    g2.setColor(Color.WHITE);
                    g2.drawString(df.format(f), xp, yp);
                }
            }
        }
        g2.setColor(origcol);
    }
    
    public void drawDisplacementGENDomain(Graphics2D g2,gendomain.Domain theDomain){
        Color origCol=g2.getColor();
        double minv=jmat.MachinePrecision.getMachinePrecision();
        double maxv=-minv;
        for(Iterator<gendomain.Node> eit=theDomain.getNodes().values().iterator(); eit.hasNext();){
            gendomain.Node theNode= eit.next();
                if(this.GlobalMinMax){
                    for(int tm=this.step_start;tm<=this.step_end;tm++){
                        double v=theNode.getDisps(tm,theDisp-1);
                        if(minv>v)minv=v;
                        if(maxv<v)maxv=v;
                    }
                }else{
                    double v=theNode.getDisps(step,theDisp-1);
                    if(minv>v)minv=v;
                    if(maxv<v)maxv=v;
                }
        }
        double[] normal=new double[2]; normal[0]=0.0; normal[1]=1.0;
        int xp,yp,xp_ = 0,yp_ = 0;
        double f; int j=1;
        for(Iterator<gendomain.Node> eit=theDomain.getNodes().values().iterator(); eit.hasNext();){
            gendomain.Node theNode= eit.next();
            g2.setColor(Color.blue);
            f=theNode.getDisps(step,theDisp-1);
            int xn=xTransformation(theNode.getCoordinates()[0]);
            int yn=yTransformation(theNode.getCoordinates()[1]);
            double coef=f*diagramscale*0.1*Math.min(max_x-min_x, max_y-min_y);
            if(Math.max(Math.abs(maxv), Math.abs(minv))>jmat.MachinePrecision.getMachineZero())coef/=Math.max(Math.abs(maxv), Math.abs(minv));
            xp = xTransformation(normal[0]*coef+theNode.getCoordinates()[0]);
            yp = yTransformation(normal[1]*coef+theNode.getCoordinates()[1]);
            g2.drawLine(xn, yn, xp, yp);
            if(j>1){
                //g2.drawLine(xp_, yp_, xp, yp);
            }
            j++;
            xp_=xp; yp_=yp;
            g2.setColor(Color.red);
            int di=Math.min(xe-xs,Math.abs(ye-ys))/nodeScale;
            if(drawNodes)g2.fillOval(this.xTransformation(normal[0]*coef+theNode.getCoords()[0])-di/2, this.yTransformation(normal[1]*coef+theNode.getCoords()[1])-di/2, di, di);
            if(diagramvalues){
                DecimalFormat df = new DecimalFormat("##.00#");
                g2.setColor(Color.WHITE);
                g2.drawString(df.format(f), xp, yp);
            }
        }
        g2.setColor(origCol);
    }
    
    public void drawTractionsBEMDomain(Graphics2D g2,jbem.Domain theDomain){
//        ux=theElement.getNodeHier(1).getu()[0][step][LC];
//            uy=theElement.getNodeHier(1).getu()[1][step][LC];

        double minv=jmat.MachinePrecision.getMachinePrecision();
        double maxv=-minv;
        for(Iterator<jbem.Element> eit=theDomain.getElements().values().iterator(); eit.hasNext();){
            jbem.Element theElement= eit.next();
            for(int j=1;j<=theElement.getNumNodes();j++){
                if(this.GlobalMinMax){
                    for(int tm=this.step_start;tm<=this.step_end;tm++){
                        double v=theElement.getNodeHier(j).getp(theElement, theDomain.getFundamentalSolution().get_p_DOFs(), LC)[theTraction-1][tm];
        //                double v=theElement.getTractionLocalonNode(j,step,LC)[theTraction-1];
                        if(minv>v)minv=v;
                        if(maxv<v)maxv=v;
                    }
                }else{
                    double v=theElement.getNodeHier(j).getp(theElement, theDomain.getFundamentalSolution().get_p_DOFs(), LC)[theTraction-1][step];
                    if(minv>v)minv=v;
                    if(maxv<v)maxv=v;
                }
            }
        }
        double[] normal;
        int xp,yp,xp_ = 0,yp_ = 0;
        double f;
        for(Iterator<jbem.Element> eit=theDomain.getElements().values().iterator(); eit.hasNext();){
            jbem.Element theElement= eit.next();
            for(int j=1;j<=theElement.getNumNodes();j++){
                normal=theElement.getNormal(theElement.getNodeHier(j).getID());
                f=theElement.getNodeHier(j).getp(theElement, theDomain.getFundamentalSolution().get_p_DOFs(), LC)[theTraction-1][step];
                int xn=xTransformation(theElement.getNodeHier(j).getCoordinates()[0]);
                int yn=yTransformation(theElement.getNodeHier(j).getCoordinates()[1]);
                double coef=f*diagramscale*0.1*Math.min(max_x-min_x, max_y-min_y);
                if(Math.max(Math.abs(maxv), Math.abs(minv))>jmat.MachinePrecision.getMachineZero())coef/=Math.max(Math.abs(maxv), Math.abs(minv));
                xp = xTransformation(normal[0]*coef+theElement.getNodeHier(j).getCoordinates()[0]);
                yp = yTransformation(normal[1]*coef+theElement.getNodeHier(j).getCoordinates()[1]);
                g2.drawLine(xn, yn, xp, yp);
                if(j>1){
                    g2.drawLine(xp_, yp_, xp, yp);
                }
                xp_=xp; yp_=yp;
                if(diagramvalues){
                    DecimalFormat df = new DecimalFormat("##.00#");
                    g2.setColor(Color.WHITE);
                    g2.drawString(df.format(f), xp, yp);
                }
            }
        }
    }
    
    public void drawDisplacementBEMDomain(Graphics2D g2,jbem.Domain theDomain){
//        ux=theElement.getNodeHier(1).getu()[0][step][LC];
//            uy=theElement.getNodeHier(1).getu()[1][step][LC];

        double minv=jmat.MachinePrecision.getMachinePrecision();
        double maxv=-minv;
        for(Iterator<jbem.Element> eit=theDomain.getElements().values().iterator(); eit.hasNext();){
            jbem.Element theElement= eit.next();
            for(int j=1;j<=theElement.getNumNodes();j++){
                if(this.GlobalMinMax){
                    for(int tm=this.step_start;tm<=this.step_end;tm++){
                        double v=theElement.getNodeHier(j).getu()[theDisp-1][tm][0];
                        if(minv>v)minv=v;
                        if(maxv<v)maxv=v;
                    }
                }else{
                    double v=theElement.getNodeHier(j).getu()[theDisp-1][step][0];
                    if(minv>v)minv=v;
                    if(maxv<v)maxv=v;
                }
            }
        }
        double[] normal;
        int xp,yp,xp_ = 0,yp_ = 0;
        double f;
        for(Iterator<jbem.Element> eit=theDomain.getElements().values().iterator(); eit.hasNext();){
            jbem.Element theElement= eit.next();
            for(int j=1;j<=theElement.getNumNodes();j++){
                normal=theElement.getNormal(theElement.getNodeHier(j).getID());
                f=theElement.getNodeHier(j).getu()[theDisp-1][step][0];
                int xn=xTransformation(theElement.getNodeHier(j).getCoordinates()[0]);
                int yn=yTransformation(theElement.getNodeHier(j).getCoordinates()[1]);
                double coef=f*diagramscale*0.1*Math.min(max_x-min_x, max_y-min_y);
                if(Math.max(Math.abs(maxv), Math.abs(minv))>jmat.MachinePrecision.getMachineZero())coef/=Math.max(Math.abs(maxv), Math.abs(minv));
                xp = xTransformation(normal[0]*coef+theElement.getNodeHier(j).getCoordinates()[0]);
                yp = yTransformation(normal[1]*coef+theElement.getNodeHier(j).getCoordinates()[1]);
                g2.drawLine(xn, yn, xp, yp);
                if(j>1){
                    g2.drawLine(xp_, yp_, xp, yp);
                }
                xp_=xp; yp_=yp;
                if(diagramvalues){
                    DecimalFormat df = new DecimalFormat("##.00#");
                    g2.setColor(Color.WHITE);
                    g2.drawString(df.format(f), xp, yp);
                }
            }
        }
    }
    
            
    @Override
    public void run() {
        while(this.animate){
            //this.seeGraph.setWhichStep(this.wstep);
            repaint();
            //this.seeGraph.repaint();
            try {
                Thread.sleep(Dts);
            } catch (InterruptedException ex) {
                //Logger.getLogger(DrawPanelInput.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    public void setBColor(Color cl){this.bcolor=cl;}
    
    public void saveImageSelection(){
//        from http://stackoverflow.com/questions/15565328/save-jpanel-into-jpg-png-image
        BufferedImage image = new BufferedImage(this.getWidth(), this.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics2D = image.createGraphics();
        
        this.paint(graphics2D);
        
        FileChooser.showSaveDialog(this);
        Path pth = FileChooser.getSelectedFile().toPath();
        JOptionPane.showMessageDialog(null, pth.toString());
        try {
            ImageIO.write(image, "png", new File(pth.toString()));
        } catch (IOException ox) {
            // TODO: handle exception
            ox.printStackTrace();
        }
    }
    
    public void saveImageSelection(String pth){
        BufferedImage image = new BufferedImage(this.getWidth(), this.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics2D = image.createGraphics();
        
        this.paint(graphics2D);
        
        try {
            ImageIO.write(image, "png", new File(pth.toString()));
        } catch (IOException ox) {
            // TODO: handle exception
            ox.printStackTrace();
        }
    }
    
    public void adjust(){
        transX=0; transY=0; zoomFactor=1.0;
        Roll_x=0;Roll_y=0;
        this.zoomFactor=1.0;
        this.repaint();
    }
    
    public void setDrawGrid(boolean b){this.draw_grid=b;}
    
    public void setDrawAxes(boolean b){this.draw_axes=b;}
    
    public void setDrawPerimeter(boolean b){this.draw_perimeter=b;}
    
    public void setIsoScale(boolean b){this.iso_scale=b;}
    
    public boolean getIsoScale(){return this.iso_scale;}
    
    public int getMargin_x(){return this.margin_x;}
    
    public int getMargin_y(){return this.margin_y;}
    
    public void setMargin_x(int m){this.margin_x=m;}
    
    public void setMargin_y(int m){this.margin_y=m;}
    
    public void set3D(){
        this.threeD = !this.threeD;
    }
    
    public void setDrawNodes(boolean b){this.drawNodes=b;}
    
    public void setDrawNodesID(boolean b){this.drawNodesID=b;}
    
    public void plotDeform(){
        plotDeform(1, 1.0, 1);
    }
    
    public void plotDeform(double scale){
        plotDeform(1, scale, 1);
    }
    
    public void plotDeform(int LC, double scale){
        plotDeform(LC, scale, 1);
    }
    
    public void plotDeform(double scale, int step){
        plotDeform(1, scale, step);
    }
    
    public void plotDeform(int LC, double scale, int step){
        // temporary method ... to be replaced
        this.resp=true;
        this.LC=LC;
        this.step=step;
        this.step_end=step;
        this.step_start=step;
        this.defscale=scale;
        this.plot();
    }
    
    public void plotDeform(double scale, int step_start, int step_end){
        plotDeform(1, scale, step_start, step_end);
    }
    
    public void plotDeform(int LC, double scale, int step_start, int step_end){
        // temporary method ... to be replaced
        runner=null;
        animate=true;
        this.resp=true;
        this.LC=LC;
        this.defscale=scale;
        this.step=step_start;
        this.step_start=step_start;
        this.step_end=step_end;
        runner = new Thread(this);
        runner.start();
        runner=null;
    }
    
    public void plotDeform(double scale, int step_start, int step_end, int step_per){
        plotDeform(1, scale, step_start, step_end, step_per);
    }
    
    public void plotDeform(int LC, double scale, int step_start, int step_end, int step_per){
        // temporary method ... to be replaced
        runner=null;
        animate=true;
        this.resp=true;
        this.LC=LC;
        this.defscale=scale;
        this.step=step_start;
        this.step_start=step_start;
        this.step_end=step_end;
        this.step_per=step_per;
        runner = new Thread(this);
        runner.start();
        runner=null;
    }
    
    ///////////////////////////////////////
    
    public void plotMQN(int MQN, int LC, double scale, int step){
        // temporary method ... to be replaced
        this.theMQN=MQN;
        diagramscale=scale;
        this.LC=LC;
        this.step=step;
        this.step_end=step;
        this.step_start=step;
        this.plot();
    }
    
    public void plotMQN(int MQN, int LC, double scale, int step_start, int step_end){
        // temporary method ... to be replaced
        runner=null;
        animate=true;
        this.theMQN=MQN;
        diagramscale=scale;
        this.LC=LC;
        this.step=step_start;
        this.step_start=step_start;
        this.step_end=step_end;
        runner = new Thread(this);
        runner.start();
        runner=null;
    }
    
    public void plotMQN(int MQN, int LC, double scale, int step_start, int step_end, int step_per){
        // temporary method ... to be replaced
        runner=null;
        animate=true;
        this.theMQN=MQN;
        diagramscale=scale;
        this.LC=LC;
        this.step=step_start;
        this.step_start=step_start;
        this.step_end=step_end;
        this.step_per=step_per;
        runner = new Thread(this);
        runner.start();
        runner=null;
    }
    
    //////////////////////////////////////////////////
    
    public void plotTractions(int Trac, double scale, int step){
        plotTractions(Trac, 0, scale, step);
    }
    
    public void plotTractions(int Trac, int LC, double scale, int step){
        // temporary method ... to be replaced
        this.theTraction=Trac;
        diagramscale=scale;
        this.LC=LC;
        this.step=step;
        this.step_end=step;
        this.step_start=step;
        this.plot();
    }
    
    public void plotTractions(int Trac, double scale){
        plotTractions(Trac, 0, scale,0);
    }
    
    public void plotTractions(int Trac, int LC, double scale, int step_start, int step_end){
        plotTractions(Trac, LC, scale, step_start, step_end, 1);
    }
    
    public void plotTractions(int Trac, double scale, int step_start, int step_end){
        plotTractions(Trac, 0, scale, step_start, step_end, 1);
    }
    
    public void plotTractions(int Trac, double scale, int step_start, int step_end, int step_per){
        plotTractions(Trac, 0, scale, step_start, step_end, step_per);
    }
    
    public void plotTractions(int Trac, int LC, double scale, int step_start, int step_end, int step_per){
        // temporary method ... to be replaced
        runner=null;
        animate=true;
        this.theTraction=Trac;
        diagramscale=scale;
        this.LC=LC;
        this.step=step_start;
        this.step_start=step_start;
        this.step_end=step_end;
        this.step_per=step_per;
        runner = new Thread(this);
        runner.start();
        runner=null;
    }
    
    //////////////////////////////////////////////////
    
    public void plotDisps(int Trac, int LC, double scale, int step){
        // temporary method ... to be replaced
        this.theDisp=Trac;
        diagramscale=scale;
        this.LC=LC;
        this.step=step;
        this.step_end=step;
        this.step_start=step;
        this.plot();
    }
    
    public void plotDisps(int Trac, double scale, int step){
        plotDisps(Trac, 0, scale, step);
    }
    
    public void plotDisps(int Trac, double scale){
        plotDisps(Trac, 0, scale,0);
    }
    
    public void plotDisps(){
        plotDisps(1, 0, 1.0,0);
    }
    
    public void plotDisps(int Trac){
        plotDisps(Trac, 0, 1.0,0);
    }
    
    public void plotDisps(double scale){
        plotDisps(1, 0, scale,0);
    }
    
    public void plotDisps(int Trac, int LC, double scale, int step_start, int step_end){
        plotDisps(Trac, LC, scale, step_start, step_end, 1);
    }
    
    public void plotDisps(int Trac, double scale, int step_start, int step_end){
        plotDisps(Trac, 0, scale, step_start, step_end, 1);
    }
    
    public void plotDisps(int Trac, double scale, int step_start, int step_end, int step_per){
        plotDisps(Trac, 0, scale, step_start, step_end, step_per);
    }
    
    public void plotDisps(int Trac, int LC, double scale, int step_start, int step_end, int step_per){
        // temporary method ... to be replaced
        runner=null;
        animate=true;
        this.theDisp=Trac;
        diagramscale=scale;
        this.LC=LC;
        this.step=step_start;
        this.step_start=step_start;
        this.step_end=step_end;
        this.step_per=step_per;
        runner = new Thread(this);
        runner.start();
        runner=null;
    }
    
    public void stop(){
        this.runner=null;
        animate=false;
        this.resp=false;
        this.contour=false;
        this.contourDefMisses=false;
        this.contourMisses=false;
        this.Defcontour=false;
        this.theMQN=0;
        this.theTraction=0;
        this.theDisp=0;
        this.theStress=0;
        this.showcolorbar=false;
        this.step=0;
        this.step_end=step;
        this.step_start=step;
        this.step_per=1;
        this.plot();
    }
    
    public void switchDiagramValues(){
        diagramvalues=!diagramvalues;
    }
    
    public void switchGlobalMinMax(){
        GlobalMinMax=!GlobalMinMax;
    }
    
    @Override
    public void setPlotDomainTrue(int id, DomainType theType){
        setPlotDomainTrue(id,theType,false);
    }
    
    @Override
    public void setPlotDomainFalse(int id, DomainType theType){
        setPlotDomainFalse(id,theType,false);
    }
    
    @Override
    public void setPlotDomainTrue(int id, DomainType theType, boolean single){
        if(single){
            for (int i = 0; i < theDomains.size(); i++) {
                if(theDomains.get(i).getID()==id && theDomains.get(i).getType()==theType){
                    theDomains.get(i).setPlotit(true);
                }else{
                    theDomains.get(i).setPlotit(false);
                }
            }
        }else{
            if(getGPDomain(id,theType)!=null)getGPDomain(id,theType).setPlotit(true);
        }
    }
    
    @Override
    public void setPlotDomainTrue(){
        for (int i = 0; i < theDomains.size(); i++) {
            theDomains.get(i).setPlotit(true);
        }
    }
    
    @Override
    public void setPlotDomainFalse(){
        for (int i = 0; i < theDomains.size(); i++) {
            theDomains.get(i).setPlotit(false);
        }
    }
    
    @Override
    public void setPlotDomainFalse(int id, DomainType theType, boolean single){
        if(single){
            for (int i = 0; i < theDomains.size(); i++) {
                if(theDomains.get(i).getID()==id || theDomains.get(i).getType()==theType){
                    theDomains.get(i).setPlotit(false);
                }else{
                    theDomains.get(i).setPlotit(true);
                }
            }
        }else{
            if(getGPDomain(id,theType)!=null)getGPDomain(id,theType).setPlotit(false);
        }
    }
    
    @Override
    public GPDomain getGPDomain(int id, DomainType theType){
        GPDomain aGPDomain=null;
        for (int i = 0; i < theDomains.size(); i++) {
            if(theDomains.get(i).getID()==id && theDomains.get(i).getType()==theType)aGPDomain=theDomains.get(i);
        }
        return aGPDomain;
    }
    
    @Override
    public boolean existGPDomain(int id, DomainType theType){
        boolean exist=false;
        for (int i = 0; i < theDomains.size(); i++) {
            if(theDomains.get(i).getID()==id && theDomains.get(i).getType()==theType){
                exist=true;
                break;
            }
        }
        return exist;
    }
    
    // An inner class to check whether mouse events are the popup trigger
    class MousePopupListener extends MouseAdapter {
        @Override
        public void mousePressed(MouseEvent e) {
            checkPopup(e);
        }
//        
        @Override
        public void mouseClicked(MouseEvent e) {
            checkPopup(e);
        }
        
        private void checkPopup(MouseEvent e) {
            if (e.isPopupTrigger()) {
                popup.show(graphicsPanel.this, e.getX(), e.getY());
            }
        }
    }
    
    public void setupColors(){
        // see also at:
        //http://stackoverflow.com/questions/340209/generate-colors-between-red-and-green-for-a-power-meter
        switch(colormode){
            case 0:
                float hinc = (float)(0.68*1.0/colorCount); // 0.68
                float h = hinc;
                float saturation = (float)(0.8);
                float intensity  = (float)(0.9);
                for(int i = 0; i < colorCount; i++){
                    int color = Color.HSBtoRGB(h, saturation, intensity);
                    h +=  hinc;
                    if(colorinv){colorArray[colorCount-i-1] = new Color(color);}else{colorArray[i] = new Color(color);}
                }
                break;
            case 1:
                // green to red
                for(int i = 0; i < colorCount; i++){
                    int R = (255 * i) / (colorCount-1);
                    int G = (255 * (colorCount-1 - i)) / (colorCount-1);
                    int B = 0;
                    if(colorinv){colorArray[colorCount-i-1] = new Color(R,G,B);}else{colorArray[i] = new Color(R,G,B);}
                }
                break;
            case 2:
                // blue to red
                for(int i = 0; i < colorCount; i++){
                    int R = (255 * i) / (colorCount-1);
                    int G = 0;
                    int B = (255 * (colorCount-1 - i)) / (colorCount-1);
                    if(colorinv){colorArray[colorCount-i-1] = new Color(R,G,B);}else{colorArray[i] = new Color(R,G,B);}
                }
                break;
            default:
                for(int i = 0; i < colorCount; i++){
                    int R = (255 * i) / (colorCount-1);
                    int G = R;
                    int B = R;
                    if(colorinv){colorArray[colorCount-i-1] = new Color(R,G,B);}else{colorArray[i] = new Color(R,G,B);}
                }
                break;
        } 
    }
    
    public void setData(double [] inputData, int xCount, int yCount){
    //
    //  This routine assumes that the incoming data is stored by ROWS
    //
        contourData = new double[xCount*yCount];
        System.arraycopy(inputData,0,contourData,0,xCount*yCount);
        xDataCount = xCount;
        yDataCount = yCount;
//        repaint();
    }

    public void setData(double [][] inputData){
        xDataCount = inputData.length;
        yDataCount = inputData[0].length;
        contourData = new double[xDataCount*yDataCount];
        int i;
        for(i = 0; i < xDataCount; i++){
            System.arraycopy(inputData[i],0,contourData,i*yDataCount,yDataCount);
        }
//        repaint();
    }
    
    public void setDataRange(double dMin, double dMax){
        dataRangeMin = dMin;
        dataRangeMax = dMax;
    }

    public void setResolution(int PanelCount){
        resolution = PanelCount;
    }
    
    public int  getResolution(){
        return resolution;
    }
    
    public void setDataRange(){
        if(contourData != null){
            dataRangeMin = contourData[0];
            dataRangeMax = contourData[0];
            double val;
            int i; int j;
            for(i =0;  i < xDataCount; i++){
                for(j = 0; j < yDataCount; j++){
                    val = contourData[j + i*yDataCount];
                    if(val < dataRangeMin) dataRangeMin = val;
                    if(val > dataRangeMax) dataRangeMax = val;
                }
            }
        }
    }
    
    public void contourDisp(){
        contourDisp(1, 1, 1, 1);
        animate=false;
    }
    
    public void contourDisp(int LC){
        contourDisp(LC, 1, 1, 1);
        animate=false;
    }
    
    public void contourDisp(int LC, int step_start){
        contourDisp(LC, step_start, step_start, 1);
        animate=false;
    }
    
    public void contourDisp(int step_start, int step_end, int step_per){
        contourDisp(1, step_start, step_end, step_per);
    }
    
    public void contourDisp(int LC, int step_start, int step_end, int step_per){
        // temporary method ... to be replaced
        if(!this.explicitMinMax){
            dataRangeMin = Double.POSITIVE_INFINITY;
            dataRangeMax = Double.NEGATIVE_INFINITY;
        }
        runner=null;
        animate=true;
        this.contour=true;
        this.LC=LC;
//        this.defscale=scale;
        this.step=step_start;
        this.step_start=step_start;
        this.step_end=step_end;
        this.step_per=step_per;
        runner = new Thread(this);
        runner.start();
        runner=null;
    }
    
    public void contourDefDisp(){
        contourDefDisp(1, 1.0, 1);
        animate=false;
    }
    
    public void contourDefDisp(double scale){
        contourDefDisp(1, scale, 1);
        animate=false;
    }
    
    public void contourDefDisp(int LC){
        contourDefDisp(LC, 1.0, 1);
        animate=false;
    }
    
    public void contourDefDisp(int LC, double scale){
        contourDefDisp(LC, scale, 1);
        animate=false;
    }
    
    public void contourDefDisp(int LC, double scale, int step_start){
        contourDefDisp(LC, scale, step_start, 1, 1);
        animate=false;
    }
    
    public void contourDefDisp(double scale, int step_start, int step_end, int step_per){
        contourDefDisp(1, scale, step_start, step_end, step_per);
    }
    
    public void contourDefDisp(int LC, double scale, int step_start, int step_end, int step_per){
        // temporary method ... to be replaced
        runner=null;
        animate=true;
        this.Defcontour=true;
        this.LC=LC;
        this.defscale=scale;
        this.step=step_start;
        this.step_start=step_start;
        this.step_end=step_end;
        this.step_per=step_per;
        runner = new Thread(this);
        runner.start();
        runner=null;
    }
    
    public int getContourResolution(){
        return resolution;
    }
    
    public void setContourResolution(int v){
        resolution=v;
    }
    
    public void contourMisses(){
        contourMisses(1, 1, 1, 1);
        animate=false;
    }
    
    public void contourMisses(int LC){
        contourMisses(LC, 1, 1, 1);
        animate=false;
    }
    
    public void contourMisses(int LC, int step_start){
        contourMisses(LC, step_start, 1, 1);
        animate=false;
    }
    
    public void contourMisses(int LC, int step_start, int step_end, int step_per){
        // temporary method ... to be replaced
        this.theStress=6;
        runner=null;
        animate=true;
        this.contourMisses=true;
        this.LC=LC;
//        this.defscale=scale;
        this.step=step_start;
        this.step_start=step_start;
        this.step_end=step_end;
        this.step_per=step_per;
        runner = new Thread(this);
        runner.start();
        runner=null;
    }
    
    public void contourDefMisses(double scale){
        contourDefMisses(1, scale, 1, 1, 1);
        animate=false;
    }
    
    public void contourDefMisses(int LC, double scale){
        contourDefMisses(LC, scale, 1, 1, 1);
        animate=false;
    }
    
    public void contourDefMisses(int LC, double scale, int step_start){
        contourDefMisses(LC, scale, step_start, 1, 1);
        animate=false;
    }
    
    public void contourDefMisses(int LC, double scale, int step_start, int step_end, int step_per){
        // temporary method ... to be replaced
        this.theStress=6;
        runner=null;
        animate=true;
        this.contourDefMisses=true;
        this.LC=LC;
        this.defscale=scale;
        this.step=step_start;
        this.step_start=step_start;
        this.step_end=step_end;
        this.step_per=step_per;
        runner = new Thread(this);
        runner.start();
        runner=null;
    }
    
    public void setDrawDomainDiscretization(boolean b){this.discrdomain=b;}
    
    public void setDrawTriagles(boolean b){this.drawtriangles=b;}
    
    public void contour(){
        contour(0);
    }
    
    public void contour(int step_start){
        contour(step_start, step_start, 1);
        animate=false;
    }
    
    public void contour(int step_start, int step_end){
        contour(step_start, step_end, 1);
    }
    
    public void contour(int step_start, int step_end, int step_per){
        // temporary method ... to be replaced
        if(!this.explicitMinMax){
            dataRangeMin = Double.POSITIVE_INFINITY;
            dataRangeMax = Double.NEGATIVE_INFINITY;
        }
        runner=null;
        animate=true;
        LC=0;
        this.contour=true;
        this.step=step_start;
        this.step_start=step_start;
        this.step_end=step_end;
        this.step_per=step_per;
        runner = new Thread(this);
        runner.start();
        runner=null;
    }
    
    public void setStress2Plot(int which){
        this.theStress=which;
    }
    
    public void showcolorbar(boolean b){this.showcolorbar=b;}
    
    public void setNumLevels4CLBar(int n){this.colorbarlevels=n;}
    
    public void setGlobalMinMax(boolean b){GlobalMinMax=b;}
    
    
    private void drawCLBar(Graphics2D g2){
        Font currentFont = g2.getFont();
        Color currentColor = g2.getColor();
        if(FontSize>0){
            Font newFont = currentFont.deriveFont(Font.PLAIN, this.FontSize);
            g2.setFont(newFont);
        }
        if(this.horizontalcolorbar){
            drawCLBarHorizontal(g2);
        }else{
            int nr=colorbarlevels;
            g2.setColor(Color.white);
            int wx=Math.min(this.margin_x/4, (xe-xs)/10); wx=Math.max(wx, 3);
            g2.drawString(cbarnumf.format(dataRangeMin), xe+wx+5, ys);
            double colorLocation;
            int colorIndex;
            for(int i=1;i<=nr;i++){
                colorLocation =((colorCount-1.0)*((dataRangeMin+i*(dataRangeMax-dataRangeMin)/(nr)) - dataRangeMin))/(dataRangeMax - dataRangeMin);
                colorIndex = (int)colorLocation;
//                if(colorIndex >= colorCount) colorIndex = colorCount-1;
//                if(colorIndex < 0) colorIndex = 0;
                g2.setColor(colorArray[colorIndex]);
                g2.fillRect(xe+4, ys+(ye-ys)*i/nr, wx, (ys-ye)/nr);
                g2.setColor(this.fontClr);
                g2.drawString(cbarnumf.format(dataRangeMin+i*(dataRangeMax-dataRangeMin)/nr), xe+wx+5, ys+(ye-ys)*i/nr);
            }
        }
        g2.setFont(currentFont);
        g2.setColor(currentColor);
    }
    
    private void drawCLBarHorizontal(Graphics2D g2){
        int nr=colorbarlevels;
        g2.setColor(Color.white);
        int wy=Math.min(this.margin_y/4, Math.abs(ye-ys)/10); wy=Math.max(wy, 3);
        g2.drawString(cbarnumf.format(dataRangeMin), xs, ye-wy-15);
        
        double colorLocation;
        int colorIndex;
        for(int i=1;i<=nr;i++){
            colorLocation =((colorCount-1.0)*((dataRangeMin+i*(dataRangeMax-dataRangeMin)/(nr)) - dataRangeMin))/(dataRangeMax - dataRangeMin);
            colorIndex = (int)colorLocation;
//            if(colorIndex >= colorCount) colorIndex = colorCount-1;
//            if(colorIndex < 0) colorIndex = 0;
            g2.setColor(colorArray[colorIndex]);
            g2.fillRect(xs+(xe-xs)*(i-1)/nr, ye-wy-10, (xe-xs)/nr, wy);
            g2.setColor(this.fontClr);
            g2.drawString(cbarnumf.format(dataRangeMin+i*(dataRangeMax-dataRangeMin)/nr), xs+(xe-xs)*i/nr, ye-wy-15);
        }
    }
    
    public void setHorizontalColorBar(boolean b){this.horizontalcolorbar=b;}
    
    public void contourStress(){
        contourStress(1, 1, 1, 1);
        animate=false;
    }
    
    public void contourStress(int LC){
        contourStress(LC, 1, 1, 1);
        animate=false;
    }
    
    public void contourStress(int LC, int step_start){
        contourStress(LC, step_start, 1, 1);
        animate=false;
    }
    
    public void contourStress(int LC, int step_start, int step_end, int step_per){
        // temporary method ... to be replaced
        dataRangeMin = Double.POSITIVE_INFINITY;
        dataRangeMax = Double.NEGATIVE_INFINITY;
        runner=null;
        animate=true;
        this.contourMisses=true;
        this.LC=LC;
//        this.defscale=scale;
        this.step=step_start;
        this.step_start=step_start;
        this.step_end=step_end;
        this.step_per=step_per;
        runner = new Thread(this);
        runner.start();
        runner=null;
    }
    
    public void contourDefStress(double scale){
        contourDefStress(1, scale, 1, 1, 1);
        animate=false;
    }
    
    public void contourDefStress(int LC, double scale){
        contourDefStress(LC, scale, 1, 1, 1);
        animate=false;
    }
    
    public void contourDefStress(int LC, double scale, int step_start){
        contourDefStress(LC, scale, step_start, 1, 1);
        animate=false;
    }
    
    public void contourDefStress(int LC, double scale, int step_start, int step_end, int step_per){
        // temporary method ... to be replaced
        dataRangeMin = Double.POSITIVE_INFINITY;
        dataRangeMax = Double.NEGATIVE_INFINITY;
        runner=null;
        animate=true;
        this.contourDefMisses=true;
        this.LC=LC;
        this.defscale=scale;
        this.step=step_start;
        this.step_start=step_start;
        this.step_end=step_end;
        this.step_per=step_per;
        runner = new Thread(this);
        runner.start();
        runner=null;
    }
    
    private void controlAnimation(){
        if(animate){
            if(step!=this.step_end){
                step+=step_per;
                if(step>=this.step_end){
                    if(this.repeat){
                        step=this.step_start;
                    }
                }
            }else{
                if(this.repeat){
                    step=this.step_start;
                }
            }
        }
    }
}
