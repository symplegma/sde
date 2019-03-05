/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package visual;

import java.awt.Color;
import java.math.BigDecimal;
import java.util.ArrayList;
import mathman.DoubleFunction;

/**
 *
 * @author pchr
 */
public class plotfunction {
    double[] x;
    double[] y;
    Color theColor=Color.BLACK;
    int MarkerStyle=0;
    int LineStyle=1;
    boolean Marker=false;
    boolean MarkerFill=false;
    String Name=null;
    float linewidth=1;
    
    public plotfunction(){}
    
    public plotfunction(double[] y){
        this.y=new double[y.length]; 
        for(int i=0;i<y.length;i++)this.y[i]=y[i];
        
        this.x=new double[y.length]; 
        for(int i=0;i<x.length;i++)this.x[i]=(double)i;
        
    }
    
    public plotfunction(double[] x, double[] y){
        this.y=new double[y.length]; 
        for(int i=0;i<y.length;i++)this.y[i]=y[i];
        
        this.x=new double[x.length]; 
        for(int i=0;i<x.length;i++)this.x[i]=x[i];
    }
    
    public plotfunction(ArrayList x, double[] y){
        this.y=new double[y.length]; 
        for(int i=0;i<this.y.length;i++)this.y[i]=y[i];
        
        this.x=new double[x.size()]; 
        for(int i=0;i<this.x.length;i++)this.x[i]=(double)x.get(i);
    }
    
    
    public plotfunction(double[] x, ArrayList y){
        this.y=new double[y.size()]; 
        for(int i=0;i<this.y.length;i++)this.y[i]=(double)y.get(i);
        
        this.x=new double[x.length]; 
        for(int i=0;i<this.x.length;i++)this.x[i]=x[i];
    }
    
    public plotfunction(ArrayList x, ArrayList y){
        this.y=new double[y.size()]; 
        for(int i=0;i<this.y.length;i++)this.y[i]=(double)y.get(i);
        
        this.x=new double[x.size()]; 
        for(int i=0;i<this.x.length;i++)this.x[i]=(double)x.get(i);
    }
    
    public plotfunction(int[] x, double[] y){
        this.y=new double[y.length]; 
        for(int i=0;i<y.length;i++)this.y[i]=y[i];
        
        this.x=new double[x.length]; 
        for(int i=0;i<x.length;i++)this.x[i]=(double)x[i];
    }
    
    public plotfunction(double[] x, DoubleFunction fy){
        this.y=new double[x.length];        
        this.x=new double[x.length]; 
        
        for(int i=0;i<x.length;i++)this.x[i]=x[i]; 
        for(int i=0;i<this.y.length;i++){
            this.y[i]=fy.run(x[i]);
        }
    }
    
    public plotfunction(int[] x, DoubleFunction fy){
        this.y=new double[x.length];        
        this.x=new double[x.length]; 
        
        for(int i=0;i<x.length;i++)this.x[i]=(double)x[i]; 
        for(int i=0;i<this.y.length;i++){
            this.y[i]=fy.run(x[i]);
        }
    }
    
    public plotfunction(double dt, double[] y){
        this.y=new double[y.length]; 
        for(int i=0;i<y.length;i++)this.y[i]=y[i];
        
        this.x=new double[y.length]; 
        for(int i=0;i<x.length;i++)this.x[i]=dt*i;
    }
    
    public plotfunction(double dt, ArrayList y){
        this.y=new double[y.size()]; 
        for(int i=0;i<this.y.length;i++)this.y[i]=(double)y.get(i);
        
        this.x=new double[this.y.length]; 
        for(int i=0;i<x.length;i++)this.x[i]=dt*i;
    }
    
    public plotfunction(ArrayList y){
        double dt=1.0;
        this.y=new double[y.size()]; 
        for(int i=0;i<this.y.length;i++){
            try{
                this.y[i]=(double)y.get(i);
            }catch (java.lang.ClassCastException e) {
                this.y[i]=((BigDecimal)y.get(i)).doubleValue();
            }
        }
        
        this.x=new double[this.y.length]; 
        for(int i=0;i<x.length;i++)this.x[i]=dt*i;
    }
    
    public double[] getXarray(){return this.x;}
    
    public double[] getYarray(){return this.y;}
    
    public double getMinX(){
        double m=Double.POSITIVE_INFINITY;
        for(int i = 0;i<x.length;i++){
            if(x[i]<m)m=x[i];
        }
        return m;
    }
    
    public double getMinY(){
        double m=Double.POSITIVE_INFINITY;
        for(int i = 0;i<y.length;i++){
            if(y[i]<m)m=y[i];
        }
        return m;
    }
    
    public double getMaxX(){
        double m=Double.NEGATIVE_INFINITY;
        for(int i = 0;i<x.length;i++){
            if(x[i]>m)m=x[i];
        }
        return m;
    }
    
    public double getMaxY(){
        double m=Double.NEGATIVE_INFINITY;
        for(int i = 0;i<y.length;i++){
            if(y[i]>m)m=y[i];
        }
        return m;
    }
    
    public void setColor(Color cl){
        this.theColor=cl;
    }
    
    public Color getColor(){return this.theColor;}
    
    public void setMarker(boolean b){this.Marker=b;}
    
    public void setMarkerFill(boolean b){this.MarkerFill=b;}
    
    public boolean getMarker(){return this.Marker;}
    
    public boolean getMarkerFill(){return this.MarkerFill;}
    
    public void setMarkerStyle(int s){this.MarkerStyle=s;}
    
    public void setLineStyle(int s){this.LineStyle=s;}
    
    public int getMarkerStyle(){return this.MarkerStyle;}
    
    public String getMarkerStyleString(){
        String mss="q";
        if(this.MarkerStyle==1)mss="c";
        if(this.MarkerStyle==2)mss="t";
        return mss;
    }
    
    public int getLineStyle(){return this.LineStyle;}
    
    public void setName(String t){this.Name=t;}
    
    public String getName(){return this.Name;}
    
    public float getLineWidth(){return this.linewidth;}
    
    public void setLineWidth(float f){this.linewidth=f;}
}