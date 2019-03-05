/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package visual;

import java.awt.Color;

/**
 *
 * @author pchr
 */
public class GPDomain {
    
    public enum DomainType {
        BEM,FEM,PDE,GEN,DEM,CNT
    }
    
    private int id;
    private boolean plotit;
    private DomainType theType;
    private Color domainColor;
        
    public GPDomain(int id, DomainType theType){
        this.id=id;
        this.theType=theType;
        this.plotit=true;
        domainColor=Color.RED;
    }

    public int getID(){return this.id;}
    public DomainType getType(){return this.theType;}
    public boolean getPlotit(){return this.plotit;}
    public void setPlotit(boolean what){this.plotit=what;}
    public void setColor(Color cl){this.domainColor=cl;}
    public Color getColor(){return this.domainColor;}
}
