/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package visual;

import org.scilab.forge.jlatexmath.TeXFormula;
import org.scilab.forge.jlatexmath.TeXIcon;
import org.scilab.forge.jlatexmath.TeXConstants;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.ListIterator;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 *
 * @author pchr
 */
public class PlotPanel extends JPanel{
    private int xs,ys,xe,ye,xm,ym;
    private String title=null;
    private String xLabel=null;
    private String yLabel=null;
    private double min_x=0.0, min_y=0.0, max_x=1.0,max_y=1.0;
    private boolean draw_grid=true;
    private JLabel CoordLabel;
    private JLabel ScaleLabel;
    private JLabel TitleLabel;
    private JLabel LegendLabel;
    private JLabel YLabel;
    private JLabel XLabel;
    private PlotFrame theParentFrame;
    int width;
    int height;
    int margin_x=30;
    int margin_y=20;
    double transX=0,transY=0;
    double Click_x=0.0,Click_y=0.0;
    private double zoomFactor=1.;
    private double Roll_x=0,Roll_y=0;
    private File AssociatedFile;
    NumberFormat dfX,dfY;
    private boolean tex;
    private int xSize,ySize,TitleSize,LegendSize;
    private ArrayList<Double> vlinesPosition = new ArrayList<Double>();
    private ArrayList<Color> vlinesColor = new ArrayList<Color>();
    
    private ArrayList<Double> hlinesPosition = new ArrayList<Double>();
    private ArrayList<Color> hlinesColor = new ArrayList<Color>();
    
    private ArrayList<Double> inclinesX = new ArrayList<Double>();
    private ArrayList<Double> inclinesY = new ArrayList<Double>();
    private ArrayList<Double> inclinesSlope = new ArrayList<Double>();
    private ArrayList<Color> inclinesColor = new ArrayList<Color>();
    
    private ArrayList<Double> textxPositions = new ArrayList<Double>();
    private ArrayList<Double> textyPositions = new ArrayList<Double>();
    private ArrayList<String> textString = new ArrayList<String>();
    private ArrayList<Color> textColors = new ArrayList<Color>();
    
    private ArrayList<Double> linesxsPosition = new ArrayList<Double>();
    private ArrayList<Double> linesysPosition = new ArrayList<Double>();
    private ArrayList<Double> linesxePosition = new ArrayList<Double>();
    private ArrayList<Double> linesyePosition = new ArrayList<Double>();
    private ArrayList<Color> linesColors = new ArrayList<Color>();
    
    Color[] colorArray;
    
    public PlotPanel(){
        TitleSize=14;
        xSize=14;
        ySize=14;
        LegendSize=14;
        tex=false;
        dfX = new DecimalFormat("##.00#");
        dfY = new DecimalFormat("##.00#");
        TitleLabel = new JLabel();
        LegendLabel= new JLabel();
        YLabel= new JLabel();
        XLabel= new JLabel();
        addMouseMotionListener(new MyMouseML());
        addMouseWheelListener(new MyMouseWL());
        addMouseListener(new MA(this));
    }
    
    public void plot(){
        repaint();
    }
    
    public int getMargin_x(){return this.margin_x;}
    
    public int getMargin_y(){return this.margin_y;}
    
    public void setMargin_x(int m){this.margin_x=m;}
    
    public void setMargin_y(int m){this.margin_y=m;}
    
    public void setParentFrame(PlotFrame theParentFrame){this.theParentFrame=theParentFrame;}
    
    public void setcoordLabel(JLabel jLabel1){this.CoordLabel=jLabel1;}
    
    public void setScaleLabel(JLabel jLabel1){this.ScaleLabel=jLabel1;}
    
    @Override
    public void paintComponent(Graphics g) {
        // Define graphics
        Graphics2D g2 = (Graphics2D) g;
        // Get dimensions
	//g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        width  = this.getWidth();
	height = this.getHeight();
	// Clear window
	g2.setBackground(Color.WHITE);
	g2.clearRect(0, 0, width, height);
        
        xm=width/2;
        xs=width/20;
        
        xs=margin_x;
        xe=width-margin_x;
        ye=margin_y;
        ys=height-margin_y;
        
        g2.drawLine(xs, ys, xe, ys);
        g2.drawLine(xe, ys, xe, ye);
        g2.drawLine(xe, ye, xs, ye);
        g2.drawLine(xs, ye, xs, ys);
        
        if(theParentFrame!=null){
            if(theParentFrame.NumOfFunctions()>0){
                min_x=Double.POSITIVE_INFINITY; 
                min_y=Double.POSITIVE_INFINITY; 
                max_x=Double.NEGATIVE_INFINITY;
                max_y=Double.NEGATIVE_INFINITY;
            }

            for (ListIterator<plotfunction> it = this.theParentFrame.getPlotFunctions().listIterator(theParentFrame.NumOfFunctions()); it.hasPrevious(); ) {
                plotfunction pf = it.previous();
                double val;
                val=pf.getMinX();
                if(min_x>val)min_x=val;
                val=pf.getMinY();
                if(min_y>val)min_y=val;
                val=pf.getMaxX();
                if(max_x<val)max_x=val;
                val=pf.getMaxY();
                if(max_y<val)max_y=val;
            }

            if(theParentFrame.active_xlim_max())max_x=theParentFrame.getxlimit_max();
            if(theParentFrame.active_xlim_min())min_x=theParentFrame.getxlimit_min();
            if(theParentFrame.active_ylim_max())max_y=theParentFrame.getylimit_max();
            if(theParentFrame.active_ylim_min())min_y=theParentFrame.getylimit_min();

            max_x+=transX;
            min_x+=transX;
            max_y+=transY;
            min_y+=transY;

            colorArray = new Color[theParentFrame.NumOfFunctions()];
            setupColors();

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

            int numx=10;
            for(int i=0;i<=numx;i++){
                g2.setColor(Color.BLACK);
                double d=min_x;
                if(i!=0){
                    d=min_x+(max_x-min_x)*i/numx;
                    g2.drawLine( (xs+(xe-xs)*(i-1)/numx+ xs+(xe-xs)*i/numx)/2, ys, (xs+(xe-xs)*(i-1)/numx+ xs+(xe-xs)*i/numx)/2, ys-4);
                    g2.drawLine( (xs+(xe-xs)*(i-1)/numx+ xs+(xe-xs)*i/numx)/2, ye, (xs+(xe-xs)*(i-1)/numx+ xs+(xe-xs)*i/numx)/2, ye+4);
                }
                g2.drawLine(xs+(xe-xs)*i/numx, ys, xs+(xe-xs)*i/numx, ys-8);
                g2.drawLine(xs+(xe-xs)*i/numx, ye, xs+(xe-xs)*i/numx, ye+8);
                Font currentFont = g2.getFont();
                if(this.xSize>0){
                    Font newFont = currentFont.deriveFont(Font.PLAIN, this.xSize);
                    g2.setFont(newFont);
                }
                g2.drawString(dfX.format(d), xs+(xe-xs)*i/numx+3, height-this.margin_y/2);
                g2.setFont(currentFont);
            }

            int numy=10;
            for(int i=0;i<=numy;i++){
                g2.setColor(Color.BLACK);
                double d=min_y;
                if(i!=0){
                    d=min_y+(max_y-min_y)*i/numy;
                    g2.drawLine(xs, (ys+(ye-ys)*(i-1)/numy+ys+(ye-ys)*i/numy)/2, xs-4, (ys+(ye-ys)*(i-1)/numy+ys+(ye-ys)*i/numy)/2);
                    g2.drawLine(xe, (ys+(ye-ys)*(i-1)/numy+ys+(ye-ys)*i/numy)/2, xe+4, (ys+(ye-ys)*(i-1)/numy+ys+(ye-ys)*i/numy)/2);
                }
                g2.drawLine(xs, ys+(ye-ys)*i/numy, xs-8, ys+(ye-ys)*i/numy);
                g2.drawLine(xe, ys+(ye-ys)*i/numy, xe+8, ys+(ye-ys)*i/numy);
                Font currentFont = g2.getFont();
                //if(this.ySize>0){
                    Font newFont = currentFont.deriveFont(Font.PLAIN, this.ySize);
                    g2.setFont(newFont);
                //}
                g2.drawString(dfY.format(d), this.margin_x/2, ys+(ye-ys)*i/numy-3);
                g2.setFont(currentFont);
            }

            if(draw_grid){
                int num=numx;
                for(int i=1;i<num;i++){
                    g2.setColor(Color.DARK_GRAY);
                    if(i==0 || i==num)g2.setColor(Color.BLACK);
                    //g2.drawLine(xs+(xe-xs)*i/num, ys, xs+(xe-xs)*i/num, ye);
                    Color cc=g2.getColor(); g2.setColor(Color.GRAY);
                    this.drawDashedLine(g2, xs+(xe-xs)*i/num, ys, xs+(xe-xs)*i/num, ye);
                    g2.setColor(cc);
                }

                num=numy;
                for(int i=1;i<num;i++){
                    g2.setColor(Color.DARK_GRAY);
                    if(i==0 || i==num)g2.setColor(Color.BLACK);
                    //g2.drawLine(xs, ys+(ye-ys)*i/num, xe, ys+(ye-ys)*i/num);
                    Color cc=g2.getColor(); g2.setColor(Color.GRAY);
                    this.drawDashedLine(g2, xs, ys+(ye-ys)*i/num, xe, ys+(ye-ys)*i/num);
                    g2.setColor(cc);
                }
            }


            for(int i=0;i<this.vlinesPosition.size();i++){
                int x=this.xTransformation(vlinesPosition.get(i));
                Color tempC=g2.getColor();
                if(this.vlinesColor.get(i)!=null)g2.setColor(vlinesColor.get(i));
                g2.drawLine( x, ys, x, ye);
                g2.setColor(tempC);
            }

            Color tempC=g2.getColor();
            for(int i=0;i<this.hlinesPosition.size();i++){
                int y=this.yTransformation(hlinesPosition.get(i));
                if(this.hlinesColor.get(i)!=null)g2.setColor(hlinesColor.get(i));
                g2.drawLine( xs, y, xe, y);
            }
            g2.setColor(tempC);

            tempC=g2.getColor();
            double xmin = Double.POSITIVE_INFINITY;
            double xmax = Double.NEGATIVE_INFINITY;
            for (int j = 0; j < theParentFrame.thePlotFunctions.size(); j++) {
                    double maxX=theParentFrame.thePlotFunctions.get(j).getMaxX();
                    double minX=theParentFrame.thePlotFunctions.get(j).getMinX();
                    if(xmin>minX)xmin=minX; if(xmax<maxX)xmax=maxX;
            }
            for(int i=0;i<this.inclinesX.size();i++){
                double k=inclinesSlope.get(i);
                double x1=inclinesX.get(i);
                double y1=inclinesY.get(i);
                      
                if(this.inclinesColor.get(i)!=null)g2.setColor(inclinesColor.get(i));
                g2.drawLine( this.xTransformation(xmin), this.yTransformation(k*xmin+y1-k*x1), 
                        this.xTransformation(xmax), this.yTransformation(k*xmax+y1-k*x1));
            }
            g2.setColor(tempC);
            
            tempC=g2.getColor();
            for(int i=0;i<this.linesxePosition.size();i++){
                int x0=this.xTransformation(linesxsPosition.get(i));
                int y0=this.yTransformation(linesysPosition.get(i));
                int x1=this.xTransformation(linesxePosition.get(i));
                int y1=this.yTransformation(linesyePosition.get(i));
                g2.setColor(this.linesColors.get(i));
                g2.drawLine( x0, y0, x1, y1);
            }
            g2.setColor(tempC);

            
            int actualTextWidth,actualTextHeight;
            if(this.title!=null){
                if(tex){
                    TeXFormula mform = new TeXFormula(title);
                    TeXIcon ti = mform.createTeXIcon(TeXConstants.STYLE_TEXT, TitleSize);
                    BufferedImage b =new BufferedImage(ti.getIconWidth(),ti.getIconHeight(),BufferedImage.TYPE_4BYTE_ABGR);
                    ti.paintIcon(TitleLabel, b.getGraphics(), xs,ys);
                    TitleLabel.setIcon(ti);
                    this.add(TitleLabel);
                    TitleLabel.setLocation(xm-ti.getIconWidth()/2, margin_y/2-ti.getIconHeight()/2);
                    TitleLabel.setVisible(true);
                    this.setLayout(new FlowLayout(FlowLayout.LEFT));
                }else{
                    TitleLabel.setText("");
                    Font currentFont = g2.getFont();
                    if(this.TitleSize>0){
                        Font newFont = currentFont.deriveFont(Font.PLAIN, this.TitleSize);
                        g2.setFont(newFont);
                    }
                    FontMetrics m = getFontMetrics(getFont());
                    actualTextWidth = m.stringWidth(title);
                    g2.drawString(title, xm-actualTextWidth/2, margin_y/2);  
                    g2.setFont(currentFont);
                }
            }

            if(this.xLabel!=null){
                if(tex){
                    TeXFormula mform = new TeXFormula(xLabel);
                    TeXIcon ti = mform.createTeXIcon(TeXConstants.STYLE_TEXT, xSize);
                    BufferedImage b =new BufferedImage(ti.getIconWidth(),ti.getIconHeight(),BufferedImage.TYPE_4BYTE_ABGR);
                    ti.paintIcon(XLabel, b.getGraphics(), xs,ys);
                    XLabel.setIcon(ti);
                    this.add(XLabel);
        //            XLabel.setLocation(xm-ti.getIconWidth()/2, margin_y/2-ti.getIconHeight()/2);
                    XLabel.setLocation(xm-ti.getIconWidth()/2, height-margin_y/3);
                    XLabel.setVisible(true);
                    this.setLayout(new FlowLayout(FlowLayout.LEFT));
                }else{
                    XLabel.setText("");
                    Font currentFont = g2.getFont();
                    if(this.xSize>0){
                        Font newFont = currentFont.deriveFont(Font.PLAIN, this.xSize);
                        g2.setFont(newFont);
                    }
                    FontMetrics m = getFontMetrics(getFont());
                    actualTextWidth = m.stringWidth(xLabel);
                    g2.drawString(xLabel, xm-actualTextWidth/2, height-margin_y/3);
                    g2.setFont(currentFont);
                }
            }

            if(this.yLabel!=null){
                if(tex){
                    TeXFormula mform = new TeXFormula(yLabel);
                    TeXIcon ti = mform.createTeXIcon(TeXConstants.STYLE_TEXT, ySize);
                    BufferedImage b =new BufferedImage(ti.getIconWidth(),ti.getIconHeight(),BufferedImage.TYPE_4BYTE_ABGR);
                    Graphics2D gB = (Graphics2D)b.getGraphics();
                    gB.rotate(-Math.PI / 2);
                    ti.paintIcon(YLabel, gB, 0, 0);
                    YLabel.setIcon(ti);
                    this.add(YLabel);
                    YLabel.setLocation(margin_x/4, height/2);
                    YLabel.setVisible(true);
                    this.setLayout(new FlowLayout(FlowLayout.LEFT));
                }else{
                    YLabel.setText("");
                    AffineTransform orig = g2.getTransform();
                    AffineTransform at = new AffineTransform();
                    at.rotate(-Math.PI / 2, margin_x/4, height/2);
                    g2.setTransform(at);
                    Font currentFont = g2.getFont();
                    if(this.ySize>0){
                        Font newFont = currentFont.deriveFont(Font.PLAIN, this.ySize);
                        g2.setFont(newFont);
                    }
                    g2.drawString(yLabel, margin_x/4, height/2);
                    g2.setFont(currentFont);
                    g2.setTransform(orig);
                }
            }
            
            tempC=g2.getColor();
            for(int i=0;i<this.textString.size();i++){
                int x=this.xTransformation(this.textxPositions.get(i));
                int y=this.yTransformation(this.textyPositions.get(i));
                String str=this.textString.get(i);
                g2.setColor(this.textColors.get(i));
                if(tex){
                    TeXFormula mform = new TeXFormula(str);
                    TeXIcon ti = mform.createTeXIcon(TeXConstants.STYLE_TEXT, TitleSize);
                    BufferedImage b =new BufferedImage(ti.getIconWidth(),ti.getIconHeight(),BufferedImage.TYPE_4BYTE_ABGR);
                    JLabel aJLabel=new JLabel();
                    ti.paintIcon(aJLabel, b.getGraphics(), 0,0);
                    aJLabel.setIcon(ti);
                    aJLabel.setForeground(g2.getColor());
                    this.add(aJLabel);
                    aJLabel.setLocation(x,y);
                    aJLabel.setVisible(true);
                    this.setLayout(new FlowLayout(FlowLayout.LEFT));
                }
                else{
                    Font currentFont = g2.getFont();
                    if(this.TitleSize>0){
                        Font newFont = currentFont.deriveFont(Font.PLAIN, this.TitleSize);
                        g2.setFont(newFont);
                    }
                    g2.drawString(str, x, y);
                    g2.setFont(currentFont);
                }
            }
            g2.setColor(tempC);

            int count=0;
            for (ListIterator<plotfunction> it = this.theParentFrame.getPlotFunctions().listIterator(theParentFrame.NumOfFunctions()); it.hasPrevious(); ) {
                plotfunction pf = it.previous();
                count++;
                if(this.theParentFrame.getAutoColor()){
                    this.dotheplot(g2,pf,count);
                }else{
                    this.dotheplot(g2,pf,0);
                }
            }

            if(theParentFrame.legend){
                if(tex){
                    actualTextHeight=0;
                    String smath="";
                    count=0;
                    int npf=this.theParentFrame.getPlotFunctions().size();
                    actualTextWidth=0;
                    for (ListIterator<plotfunction> it = this.theParentFrame.getPlotFunctions().listIterator(theParentFrame.NumOfFunctions()); it.hasPrevious(); ) {
                        plotfunction pf = it.previous();
                        count++;
                        if(this.theParentFrame.getAutoColor()){
                            g2.setColor(colorArray[count-1]);
                        }else{
                            g2.setColor(pf.getColor());
                        }

    //                    String red = Integer.toString(colorArray[count-1].getRed());
    //                    String green = Integer.toString(colorArray[count-1].getGreen());
    //                    String blue = Integer.toString(colorArray[count-1].getBlue());

                        smath+=pf.getName()+"\\\\";
                        FontMetrics m = getFontMetrics(getFont());
                        actualTextWidth=Math.max(actualTextWidth, m.stringWidth(pf.getName()));
                    }
                    actualTextWidth+=2;

                    TeXFormula mform = new TeXFormula(smath);
                    TeXIcon ti = mform.createTeXIcon(TeXConstants.STYLE_TEXT, this.LegendSize);
                    BufferedImage b =new BufferedImage(ti.getIconWidth(),ti.getIconHeight(),BufferedImage.TYPE_4BYTE_ABGR);
                    ti.paintIcon(LegendLabel, b.getGraphics(), xs,ys);
                    actualTextHeight=ti.getIconHeight();
                    actualTextWidth=ti.getIconWidth()+2;
                    g2.setColor(Color.WHITE);
                    g2.fillRect((int) (width*theParentFrame.lxrelpos), 
                            (int) (height*(1.0-theParentFrame.lyrelpos)), 
                            (int) (actualTextWidth), 
                            (int) (actualTextHeight));
                    g2.setColor(Color.BLACK);
                    g2.drawRect((int) (width*theParentFrame.lxrelpos), 
                            (int) (height*(1.0-theParentFrame.lyrelpos)), 
                            (int) (actualTextWidth), 
                            (int) (actualTextHeight));

                    LegendLabel.setIcon(ti);
                    this.add(LegendLabel);
                    LegendLabel.setLocation(1+(int) (width*theParentFrame.lxrelpos), (int) (height*(1.0-theParentFrame.lyrelpos)) );
                    LegendLabel.setVisible(true);
                    this.setLayout(new FlowLayout(FlowLayout.LEFT));
                }else{
                    LegendLabel.setText("");
                    count=0;
                    Font currentFont = g2.getFont();
                    if(this.LegendSize>0){
                        Font newFont = currentFont.deriveFont(Font.PLAIN, this.LegendSize);
                        g2.setFont(newFont);
                    }
                    int npf=this.theParentFrame.getPlotFunctions().size();
                    actualTextWidth=0;actualTextHeight=0;
                    for (ListIterator<plotfunction> it = this.theParentFrame.getPlotFunctions().listIterator(theParentFrame.NumOfFunctions()); it.hasPrevious(); ) {
                        plotfunction pf = it.previous();
                        FontMetrics m = getFontMetrics(getFont());
                        actualTextWidth=Math.max(actualTextWidth, m.stringWidth(pf.getName()));
                        actualTextHeight+=m.getHeight();
                    }
                    actualTextWidth+=2;
                    g2.setColor(Color.WHITE);
                    g2.fillRect((int) (width*theParentFrame.lxrelpos), 
                            (int) (height*(1.0-theParentFrame.lyrelpos)), 
                            (int) (actualTextWidth), 
                            (int) (actualTextHeight));
                    g2.setColor(Color.BLACK);
                    g2.drawRect((int) (width*theParentFrame.lxrelpos), 
                            (int) (height*(1.0-theParentFrame.lyrelpos)), 
                            (int) (actualTextWidth), 
                            (int) (actualTextHeight));

                    count=0;
                    npf=this.theParentFrame.getPlotFunctions().size();
                    for (ListIterator<plotfunction> it = this.theParentFrame.getPlotFunctions().listIterator(theParentFrame.NumOfFunctions()); it.hasPrevious(); ) {
                        plotfunction pf = it.previous();
                        count++;
                        if(this.theParentFrame.getAutoColor()){
                            g2.setColor(colorArray[count-1]);
                        }else{
                            g2.setColor(pf.getColor());
                        }
                        g2.drawString(pf.getName(), 1+(int) (width*theParentFrame.lxrelpos), (int) (height*(1.0-theParentFrame.lyrelpos)+count*actualTextHeight/npf-1));
                    }
                    g2.setFont(currentFont);
                }
            }
        }
    }
    
    private void dotheplot(Graphics g, plotfunction pf, int jcolor){
        int sz=Math.min(pf.getXarray().length, pf.getYarray().length);
        Color curCol = g.getColor();
        if(jcolor>0){
            g.setColor(colorArray[jcolor-1]);
        }else{
            g.setColor(pf.getColor());
        }
        if(sz==1){
            int x1=xTransformation(pf.getXarray()[0]);
            int y1=yTransformation(pf.getYarray()[0]);
            if(pf.getMarker()){
                int s=pf.getMarkerStyle();
                if(s>=0){
                    if(pf.getMarkerFill()){
                        switch(s){
                            case 1: g.fillOval(x1-3, y1-3, 6, 6); break;
                            case 2: int[] xPoints = new int[3];
                            int[] yPoints = new int[3];
                            xPoints[0]=x1-3; yPoints[0]=y1+3;
                            xPoints[1]=x1+3; yPoints[1]=y1+3;
                            xPoints[2]=x1; yPoints[2]=y1-3;
                            g.fillPolygon(xPoints, yPoints, 3); break;
                            default: g.fillRect(x1-3, y1-3, 6, 6); break;
                        }
                    }else{
                        switch(s){
                            case 1: g.drawOval(x1-3, y1-3, 6, 6); break;
                            case 2: int[] xPoints = new int[3];
                            int[] yPoints = new int[3];
                            xPoints[0]=x1-3; yPoints[0]=y1+3;
                            xPoints[1]=x1+3; yPoints[1]=y1+3;
                            xPoints[2]=x1; yPoints[2]=y1-3;
                            g.drawPolygon(xPoints, yPoints, 3); break;
                            default: g.drawRect(x1-3, y1-3, 6, 6); break;
                        }
                    }
                }else{
                    g.drawString(Integer.toString(Math.abs(s)), x1-3, y1-3);
                }
            }
        }else{
            for(int i=0;i<sz-1;i++){
                int x1=xTransformation(pf.getXarray()[i]);
                int y1=yTransformation(pf.getYarray()[i]);

                int x2=xTransformation(pf.getXarray()[i+1]);
                int y2=yTransformation(pf.getYarray()[i+1]);
                if(pf.getMarker()){
                    int s=pf.getMarkerStyle();
                    if(s>=0){
                        if(pf.getMarkerFill()){
                            if(i==0)switch(s){
                                case 1: g.fillOval(x1-3, y1-3, 6, 6); break;
                                case 2: int[] xPoints = new int[3];
                                int[] yPoints = new int[3];
                                xPoints[0]=x1-3; yPoints[0]=y1+3;
                                xPoints[1]=x1+3; yPoints[1]=y1+3;
                                xPoints[2]=x1; yPoints[2]=y1-3;
                                g.fillPolygon(xPoints, yPoints, 3); break;
                                default: g.fillRect(x1-3, y1-3, 6, 6); break;
                            }
                            switch(s){
                                case 1: g.fillOval(x2-3, y2-3, 6, 6); break;
                                case 2: int[] xPoints = new int[3];
                                int[] yPoints = new int[3];
                                xPoints[0]=x2-3; yPoints[0]=y2+3;
                                xPoints[1]=x2+3; yPoints[1]=y2+3;
                                xPoints[2]=x2; yPoints[2]=y2-3;
                                g.fillPolygon(xPoints, yPoints, 3); break;
                                default: g.fillRect(x2-3, y2-3, 6, 6); break;
                            }
                        }else{
                            if(i==0)switch(s){
                                case 1: g.drawOval(x1-3, y1-3, 6, 6); break;
                                case 2: int[] xPoints = new int[3];
                                int[] yPoints = new int[3];
                                xPoints[0]=x1-3; yPoints[0]=y1+3;
                                xPoints[1]=x1+3; yPoints[1]=y1+3;
                                xPoints[2]=x1; yPoints[2]=y1-3;
                                g.drawPolygon(xPoints, yPoints, 3); break;
                                default: g.drawRect(x1-3, y1-3, 6, 6); break;
                            }
                            switch(s){
                                case 1: g.drawOval(x2-3, y2-3, 6, 6); break;
                                case 2: int[] xPoints = new int[3];
                                int[] yPoints = new int[3];
                                xPoints[0]=x2-3; yPoints[0]=y2+3;
                                xPoints[1]=x2+3; yPoints[1]=y2+3;
                                xPoints[2]=x2; yPoints[2]=y2-3;
                                g.drawPolygon(xPoints, yPoints, 3); break;
                                default: g.drawRect(x2-3, y2-3, 6, 6); break;
                            }
                        }
                    }else{
                        g.drawString(Integer.toString(Math.abs(s)), x2-3, y2-3);
                    }
                }
                Graphics2D g2 = (Graphics2D) g;
                g2.setStroke(new BasicStroke(pf.getLineWidth()));
                switch(pf.getLineStyle()){
                    case 0: break;
                    case 2: drawDashedLine(g2, x1, y1, x2, y2); break;
                    case 3: drawDottedLine(g2, x1, y1, x2, y2); break;
                    default: g2.drawLine(x1, y1, x2, y2);
                }
                g2.setStroke(new BasicStroke(1));
            }
        }
        g.setColor(curCol);
    }
    
    class MyMouseWL implements MouseWheelListener{
        
      public void mouseWheelMoved(MouseWheelEvent e)  {
            Roll_x = xTransformationInv(e.getX());
            Roll_y = xTransformationInv(e.getY());
            //Zoom in
            if(e.getWheelRotation()<0){
                zoomFactor=1.1*zoomFactor;
                repaint();
            }
            //Zoom out
            if(e.getWheelRotation()>0){
                // bug: for zoomFactor>=0.0001 everything freeze
                if(zoomFactor>=0.0001)zoomFactor=zoomFactor/1.1;
                repaint();
            }
            ScaleLabel.setText("scale: "+1./zoomFactor);
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
            CoordLabel.setText("["+dfX.format(xTransformationInv(e.getX()))+","+dfY.format(yTransformationInv(e.getY()))+"]");
        }
    }
    
    // For both MIA and MA classes it seems we have the same result
    
//    private class MIA extends MouseInputAdapter {
//        @Override
//        public void mousePressed(MouseEvent e) {
//            Click_x = xTransformationInv(e.getX());
//            Click_y = yTransformationInv(e.getY());
//        }
//    }
    
    private class MA extends MouseAdapter {
        PlotPanel parentPanel;
        public MA(PlotPanel parentPanel){this.parentPanel=parentPanel;}
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
    
    public void drawDashedLine(Graphics g, int x1, int y1, int x2, int y2){

        //creates a copy of the Graphics instance
        Graphics2D g2d = (Graphics2D) g.create();

        //set the stroke of the copy, not the original 
        Stroke dashed = new BasicStroke(0.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{9}, 0);
        g2d.setStroke(dashed);
        //g2d.setColor(Color.GRAY);
        g2d.drawLine(x1, y1, x2, y2);

        //gets rid of the copy
        g2d.dispose();
    }
    
    public void drawDottedLine(Graphics g, int x1, int y1, int x2, int y2){

        //creates a copy of the Graphics instance
        Graphics2D g2d = (Graphics2D) g.create();

        //set the stroke of the copy, not the original 
        Stroke dashed = new BasicStroke(0.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{1,2}, 0);
        g2d.setStroke(dashed);
        //g2d.setColor(Color.GRAY);
        g2d.drawLine(x1, y1, x2, y2);

        //gets rid of the copy
        g2d.dispose();
    }
    
    public void setTitle(String title){this.title=title;}
    
    public void set_xLabel(String title){this.xLabel=title;}
    
    public void set_yLabel(String title){this.yLabel=title;}
    
    protected int xTransformation(double x){
        int xd=0;
        double xmax=this.max_x;
        double xmin=this.min_x;
//        xd=(int) ((int) this.zoomFactor*((-xmin * xe + x * (xe - xs) + xmax * xs) / (xmax - xmin)));
        xd=(int) ((int) ((-xmin * xe + x * (xe - xs) + xmax * xs) / (xmax - xmin)));
        return xd;
    }
    
    protected int yTransformation(double y){
        int yd=0;
        double ymax=this.max_y;
        double ymin=this.min_y;
//        yd=(int) ((int) this.zoomFactor*((-ymin * ye + y * (ye - ys) + ymax * ys) / (ymax - ymin)));
        yd=(int) ((int) ((-ymin * ye + y * (ye - ys) + ymax * ys) / (ymax - ymin)));
        return yd;
    }
    
    protected double xTransformationInv(int xd){
        double x=0;
        double xmax=this.max_x;
        double xmin=this.min_x;
        x=(xd*(xmax-xmin)+xe*xmin-xs*xmax)/(xe-xs);
        return x;
    }
    
    protected double yTransformationInv(int yd){
        double y=0;
        double ymax=this.max_y;
        double ymin=this.min_y;
        y=(yd*(ymax-ymin)+ye*ymin-ys*ymax)/(ye-ys);
        return y;
    }
    
    public void setDraw_grid(){
        draw_grid=!draw_grid; 
        this.repaint();
    }
    
    public void adjust(){
        transX=0; transY=0; zoomFactor=1.0;
        Roll_x=0;Roll_y=0;
        ScaleLabel.setText("scale: "+1./zoomFactor);
        this.repaint();
    }
    public void setAssociatedFile(File af){this.AssociatedFile=af;}
    
    public File getAssociatedFile(){return this.AssociatedFile;}
    
    public double getXmin(){return this.min_x;}
    
    public double getYmin(){return this.min_y;}
    
    public double getXmax(){return this.max_x;}
    
    public double getYmax(){return this.max_y;}
    
    public void setFormatXAxis(NumberFormat theDF){
        this.dfX=theDF;
    }
    
    public void setFormatYAxis(NumberFormat theDF){
        this.dfY=theDF;
    }
    
    public void setFormatAxis(NumberFormat theDF){
        this.dfX=theDF;
        this.dfY=theDF;
    }
    
    private void setupColors(){
        //
        //  Setting up colors based upon hue....
        //
        int colorCount = this.theParentFrame.NumOfFunctions();
        int i;
        float hinc = (float)(1.0/colorCount);
        float h = hinc;
        float saturation = (float)(0.8);
        float intensity  = (float)(0.9);
        for(i = 0; i < colorCount; i++){
            int color = Color.HSBtoRGB(h, saturation, intensity);
            h +=  hinc;
            colorArray[i] = new Color(color);
        }
    }
    
    public void setTex(boolean b){this.tex=b;}
    
    public void vline(double x, Color c){
        this.vlinesPosition.add(x);
        this.vlinesColor.add(c);
    }
    
    public void hline(double y, Color c){
        this.hlinesPosition.add(y);
        this.hlinesColor.add(c);
    }
    
    public void incline(double k, double x, double y, Color c){
        this.inclinesX.add(x);
        this.inclinesY.add(y);
        this.inclinesSlope.add(k);
        this.inclinesColor.add(c);
    }
    
    public void vline(double x){
        vline(x, Color.black);
    }
    
    public void hline(double y){
        hline(y, Color.black);
    }
    
    public void incline(double k, double x, double y){
        incline(k, x, y, Color.black);
    }
    
    public void clear(){
        hlinesPosition.clear();
        hlinesColor.clear();
        vlinesPosition.clear();
        vlinesColor.clear();
        inclinesColor.clear();
        inclinesX.clear();
        inclinesY.clear();
        inclinesSlope.clear();
        this.textxPositions.clear();
        this.textyPositions.clear();
        this.textString.clear();
        this.textColors.clear();
    }
    
    public void setTexfontsize(int n){
        TitleSize=n;
        xSize=n;
        ySize=n;
        LegendSize=n;
    }
    
    public void text(double x, double y, String text, Color c){
        this.textxPositions.add(x);
        this.textyPositions.add(y);
        this.textString.add(text);
        this.textColors.add(c);
    }
    
    public void text(double x, double y, String text){
        text(x, y, text, Color.black);
    }
    
    public void line(double xs, double ys, double xe, double ye, Color c){
        this.linesxsPosition.add(xs);
        this.linesysPosition.add(ys);
        this.linesxePosition.add(xe);
        this.linesyePosition.add(ye);
        this.linesColors.add(c);
    }
    
    public void line(double xs, double ys, double xe, double ye){
        line(xs, ys, xe, ye, Color.BLACK);
    }
}
