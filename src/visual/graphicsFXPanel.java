/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package visual;

import climax.jpde;
import climax.universe;
import edu.uta.futureye.core.Mesh;
import jfem.*;
import geom.Point3D;
import java.awt.Cursor;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import javafx.embed.swing.JFXPanel;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.CullFace;
import javafx.scene.shape.Cylinder;
import javafx.scene.shape.DrawMode;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.Sphere;
import javafx.scene.shape.TriangleMesh;
import visual.GPDomain.DomainType;

/**
 * mainly from:
 * http://docs.oracle.com/javase/8/javafx/graphics-tutorial/sampleapp3d.htm#CJAHFAF
 * @author pchr
 */
public class graphicsFXPanel extends JFXPanel implements SDEgraphicsPanel{
    final Xform world = new Xform();
    final Xform axisGroup = new Xform();
    final Xform GridG = new Xform();
    final Xform BEMs = new Xform();
    final Xform FEMs = new Xform();
    
    final PerspectiveCamera camera = new PerspectiveCamera(true);
    final Xform cameraXform = new Xform();
    final Xform cameraXform2 = new Xform();
    final Xform cameraXform3 = new Xform();
    private static final double CAMERA_INITIAL_DISTANCE = -1000;//-2000;
    
    private static final double CAMERA_INITIAL_X_ANGLE = 70.0;
    private static final double CAMERA_INITIAL_Y_ANGLE = 320.0;
    private static final double CAMERA_NEAR_CLIP = 0.1;
    private static final double CAMERA_FAR_CLIP = 10000.0;
    
    private static double AXIS_LENGTH = 250.0;
    
    private static final double CONTROL_MULTIPLIER = 0.1;    
    private static final double SHIFT_MULTIPLIER = 10.0;    
    private static final double MOUSE_SPEED = 0.1;    
    private static final double ROTATION_SPEED = 2.0;    
    private static final double TRACK_SPEED = 0.3;
        
    double mousePosX;
    double mousePosY;
    double mouseOldX;
    double mouseOldY;
    double mouseDeltaX;
    double mouseDeltaY;
    double modifierFactor=1.0;
    
    Scene  scene ;
    
    Boolean draw_grid=true;
    
    protected static List<GPDomain> theDomains;
    private universe theUniverse;
    int margin_x=200;
    int margin_y=200;
    protected int xs,ys,zs,xe,ye,ze;
    int  width,height;
    private double min_x,min_y,min_z,max_x,max_y,max_z;
    private int gridx,gridy,gridz;
    double transX=0,transY=0,transZ=0;
    private boolean iso_scale=false;
    
    public void setIsoScale(boolean b){this.iso_scale=b;}
    
    public graphicsFXPanel(){
        this.setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
//        initFX();
    }
    
    @Override
    public void setUniverse(universe theUniverse){
        this.theUniverse=theUniverse;
        this.setGPDomains();
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
    
    private void setGPDomains(){
        if(theUniverse!=null){
            if(theDomains==null)theDomains =new ArrayList<GPDomain>();
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
        }
    }
    
    public void plot(){
//        System.out.println("in graphicsFXPanel plot()");
        initFX();
    }
    
    private void initFX() {
        // This method is invoked on the JavaFX thread
        this.setScene(createScene());
        handleKeyboard(scene, world);
        handleMouse(scene, world);
    }

    private Scene createScene() {
        scene  =  new  Scene(world, Color.BLACK);
        scene.setCamera(camera);
        buildCamera();
        width  = this.getWidth();
        height = this.getHeight();
        xs=margin_x;
        xe=width-margin_x;
        ye=margin_y;
        ys=height-margin_y;
        zs=xs; ze=xe;
        AXIS_LENGTH=Math.min(width/5, height/5);
        
        if(theUniverse!=null){
            set_size();
            System.out.println("min/max of domain");
            System.out.println(max_x+" "+max_y+" "+max_z);
            System.out.println(min_x+" "+min_y+" "+min_z);
            System.out.println("(int) min/max of domain");
            System.out.println(xTransformation(max_x)+" "+yTransformation(max_y)+" "+zTransformation(max_z));
            System.out.println(xTransformation(min_x)+" "+yTransformation(min_y)+" "+zTransformation(min_z));

        }
        
        if(draw_grid){
            //addGrid();
        }
        
        if(theUniverse!=null){
            buildBEMs();
            buildFEMs();
        }
        
        buildAxes();
        
        return (scene);
    }
    
    private void buildAxes() {
        final PhongMaterial redMaterial = new PhongMaterial();
        redMaterial.setDiffuseColor(Color.DARKRED);
        redMaterial.setSpecularColor(Color.RED);
 
        final PhongMaterial greenMaterial = new PhongMaterial();
        greenMaterial.setDiffuseColor(Color.DARKGREEN);
        greenMaterial.setSpecularColor(Color.GREEN);
 
        final PhongMaterial blueMaterial = new PhongMaterial();
        blueMaterial.setDiffuseColor(Color.DARKBLUE);
        blueMaterial.setSpecularColor(Color.BLUE);
 
        final Box xAxis = new Box(AXIS_LENGTH, 1, 1);
        final Box yAxis = new Box(1, AXIS_LENGTH, 1);
        final Box zAxis = new Box(1, 1, AXIS_LENGTH);
        
        xAxis.setMaterial(redMaterial);
        yAxis.setMaterial(greenMaterial);
        zAxis.setMaterial(blueMaterial);
 
        axisGroup.getChildren().addAll(xAxis, yAxis, zAxis);
        axisGroup.setVisible(true);
        world.getChildren().addAll(axisGroup);
    }
    
    private void buildCamera() {
        world.getChildren().add(cameraXform);
        cameraXform.getChildren().add(cameraXform2);
        cameraXform2.getChildren().add(cameraXform3);
        cameraXform3.getChildren().add(camera);
        cameraXform3.setRotateZ(180.0);
 
        camera.setNearClip(CAMERA_NEAR_CLIP);
        camera.setFarClip(CAMERA_FAR_CLIP);
        camera.setTranslateZ(CAMERA_INITIAL_DISTANCE);
        cameraXform.ry.setAngle(CAMERA_INITIAL_Y_ANGLE);
        cameraXform.rx.setAngle(CAMERA_INITIAL_X_ANGLE);
    }
    
    private void handleMouse(Scene scene, final Node root) {
 
        scene.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override public void handle(MouseEvent me) {
                mousePosX = me.getSceneX();
                mousePosY = me.getSceneY();
                mouseOldX = me.getSceneX();
                mouseOldY = me.getSceneY();
            }
        });
        scene.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override public void handle(MouseEvent me) {
                mouseOldX = mousePosX;
                mouseOldY = mousePosY;
                mousePosX = me.getSceneX();
                mousePosY = me.getSceneY();
                mouseDeltaX = (mousePosX - mouseOldX); 
                mouseDeltaY = (mousePosY - mouseOldY);

               double modifier = 1.0;

               if (me.isControlDown()) {
                    modifier = CONTROL_MULTIPLIER;
                } 
                if (me.isShiftDown()) {
                    modifier = SHIFT_MULTIPLIER;
                }     
                if (me.isPrimaryButtonDown()) {
                    cameraXform.ry.setAngle(cameraXform.ry.getAngle() -
                       mouseDeltaX*modifierFactor*modifier*ROTATION_SPEED);  // 
                   cameraXform.rx.setAngle(cameraXform.rx.getAngle() +
                       mouseDeltaY*modifierFactor*modifier*ROTATION_SPEED);  // -
                }
                else if (me.isSecondaryButtonDown()) {
                    double z = camera.getTranslateZ();
                    double newZ = z + mouseDeltaX*MOUSE_SPEED*modifier;
                    camera.setTranslateZ(newZ);
                }
                else if (me.isMiddleButtonDown()) {
                   cameraXform2.t.setX(cameraXform2.t.getX() + 
                      mouseDeltaX*MOUSE_SPEED*modifier*TRACK_SPEED);  // -
                   cameraXform2.t.setY(cameraXform2.t.getY() + 
                      mouseDeltaY*MOUSE_SPEED*modifier*TRACK_SPEED);  // -
                }
           }
       }); // setOnMouseDragged
   } //handleMouse
    
    private void handleKeyboard(Scene scene, final Node root) {

        scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
               switch (event.getCode()) {
                   case Z:
                       cameraXform2.t.setX(0.0);
                       cameraXform2.t.setY(0.0);
                       cameraXform.ry.setAngle(CAMERA_INITIAL_Y_ANGLE);
                       cameraXform.rx.setAngle(CAMERA_INITIAL_X_ANGLE);
                       break;
                   case X:
                        axisGroup.setVisible(!axisGroup.isVisible());
                        break;
                   case G:
                        GridG.setVisible(!GridG.isVisible());
                        break;
                    case F:
                       FEMs.setVisible(!FEMs.isVisible());
                       break;
                    case B:
                       BEMs.setVisible(!BEMs.isVisible());
                       break;
               } // switch
            } // handle()
        });  // setOnKeyPressed
    }  //  handleKeyboard()
    
    public void addGrid() {
        gridx=4;
        gridy=gridx; gridz=gridx;
        
        final PhongMaterial GridMaterial = new PhongMaterial();
        GridMaterial.setSpecularColor(Color.LIGHTGREY);
        Box gridline;
        int imax_x=this.xTransformation(max_x);
        int imax_y=this.yTransformation(max_y);
        int imax_z=this.zTransformation(max_z);
        int imin_x=this.xTransformation(min_x);
        int imin_y=this.yTransformation(min_y);
        int imin_z=this.zTransformation(min_z);
        
        for(int i=0;i<=gridy;i++){
            for(int j=0;j<=gridz;j++){
                gridline =new Box(imax_x-imin_x, 1, 1);
                gridline.setTranslateY(imin_y+i*(imax_y-imin_y)/gridy);
                gridline.setTranslateZ(imin_z+j*(imax_z-imin_z)/gridz);
                gridline.setTranslateX(imin_x);
                gridline.setMaterial(GridMaterial);
                gridline.setDrawMode(DrawMode.FILL);
                GridG.getChildren().add(gridline);
            }
        }
        
        for(int i=0;i<=gridx;i++){
            for(int j=0;j<=gridz;j++){
                gridline =new Box(1, imax_y-imin_y, 1);
                gridline.setTranslateX(imin_x+i*(imax_x-imin_x)/gridx);
                gridline.setTranslateZ(imin_z+j*(imax_z-imin_z)/gridz);
                gridline.setTranslateY(imin_y);
                gridline.setMaterial(GridMaterial);
                gridline.setDrawMode(DrawMode.FILL);
                GridG.getChildren().add(gridline);
            }
        }
//        
        for(int i=0;i<=gridx;i++){
            for(int j=0;j<=gridy;j++){
                gridline =new Box(1, 1, imax_z-imin_z);
                gridline.setTranslateX(imin_x+i*(imax_x-imin_x)/gridx);
                gridline.setTranslateY(imin_y+j*(imax_y-imin_y)/gridy);
//                gridline.setTranslateZ(imin_z);
                gridline.setMaterial(GridMaterial);
                gridline.setDrawMode(DrawMode.FILL);
                GridG.getChildren().add(gridline);
            }
        }
        
        GridG.setVisible(true);
        world.getChildren().addAll(GridG);
    }
    
    public void set_size(double xmin, double xmax, double ymin, double ymax, double zmin, double zmax){
        this.min_x=xmin;
        this.min_y=ymin;
        this.min_z=zmin;
        this.max_x=xmax;
        this.max_y=ymax;
        this.max_z=zmax;
    }
    
    public void set_size(){
        this.setGPDomains();
        max_x=0.0;
        max_y=0.0;
        max_z=0.0;
        min_x=0.0;
        min_y=0.0;
        min_z=0.0;
        if(theUniverse!=null){
            for(Iterator<jbem.Domain> it=theUniverse.getBEMDomains().values().iterator(); it.hasNext();){
                jbem.Domain theDomain = it.next();
                if(this.getGPDomain(theDomain.getID(), DomainType.BEM).getPlotit()){
                    if(theDomain.maximum_X_coordinate()>max_x)max_x=theDomain.maximum_X_coordinate();
                    if(theDomain.maximum_Y_coordinate()>max_y)max_y=theDomain.maximum_Y_coordinate();
                    if(theDomain.maximum_Z_coordinate()>max_z)max_z=theDomain.maximum_Z_coordinate();
                    if(theDomain.minimum_X_coordinate()<min_x)min_x=theDomain.minimum_X_coordinate();
                    if(theDomain.minimum_Y_coordinate()<min_y)min_y=theDomain.minimum_Y_coordinate();
                    if(theDomain.minimum_Z_coordinate()<min_z)min_z=theDomain.minimum_Z_coordinate();
                }
            } 
            for(Iterator<jfem.Domain> it=theUniverse.getFEMDomains().values().iterator(); it.hasNext();){
                jfem.Domain theDomain = it.next();
                if(this.getGPDomain(theDomain.getID(), DomainType.FEM).getPlotit()){
                    if(theDomain.maximum_X_coordinate()>max_x)max_x=theDomain.maximum_X_coordinate();
                    if(theDomain.maximum_Y_coordinate()>max_y)max_y=theDomain.maximum_Y_coordinate();
                    if(theDomain.maximum_Z_coordinate()>max_z)max_z=theDomain.maximum_Z_coordinate();
                    if(theDomain.minimum_X_coordinate()<min_x)min_x=theDomain.minimum_X_coordinate();
                    if(theDomain.minimum_Y_coordinate()<min_y)min_y=theDomain.minimum_Y_coordinate();
                    if(theDomain.minimum_Z_coordinate()<min_z)min_z=theDomain.minimum_Z_coordinate();
                }
            }
            for (Map.Entry<Integer,jpde> entry : theUniverse.getPDEDomains().entrySet()) {
                int key = entry.getKey();
                Mesh theDomain = entry.getValue().mesh;
                if(this.getGPDomain(key, DomainType.PDE).getPlotit()){
                    for(edu.uta.futureye.core.Node theNode : theDomain.getNodeList()){
                        if(theNode.coords()[0]>max_x)max_x=theNode.coords()[0];
                        if(theNode.coords()[0]<min_x)min_x=theNode.coords()[0];
                        if(theNode.coords()[1]>max_y)max_y=theNode.coords()[1];
                        if(theNode.coords()[1]<min_y)min_y=theNode.coords()[1];
                        if(theNode.coords()[2]>max_z)max_z=theNode.coords()[2];
                        if(theNode.coords()[2]<min_z)min_z=theNode.coords()[2];
                    }
                }
            }
            
            for(Iterator<gendomain.Domain> it=theUniverse.getGENDomains().values().iterator(); it.hasNext();){
                gendomain.Domain theDomain = it.next();
                if(this.getGPDomain(theDomain.getID(), DomainType.GEN).getPlotit()){
                    if(theDomain.maximum_X_coordinate()>max_x)max_x=theDomain.maximum_X_coordinate();
                    if(theDomain.maximum_Y_coordinate()>max_y)max_y=theDomain.maximum_Y_coordinate();
                    if(theDomain.maximum_Z_coordinate()>max_z)max_z=theDomain.maximum_Z_coordinate();
                    if(theDomain.minimum_X_coordinate()<min_x)min_x=theDomain.minimum_X_coordinate();
                    if(theDomain.minimum_Y_coordinate()<min_y)min_y=theDomain.minimum_Y_coordinate();
                    if(theDomain.minimum_Z_coordinate()<min_z)min_z=theDomain.minimum_Z_coordinate();
                }
            }
            if(Math.abs(max_x-min_x)<jmat.MachinePrecision.getMachinePrecision()*10.0){max_x+=0.5;min_x-=0.5;}
            if(Math.abs(max_y-min_y)<jmat.MachinePrecision.getMachinePrecision()*10.0){max_y+=0.5;min_y-=0.5;}
            if(Math.abs(max_z-min_z)<jmat.MachinePrecision.getMachinePrecision()*10.0){max_z+=0.5;min_z-=0.5;}
            max_x+=transX;
            min_x+=transX;
            max_y+=transY;
            min_y+=transY;
            max_z+=transZ;
            min_z+=transZ;
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
                if((max_x-min_x)>(max_z-min_z)){
                    max_y=min_y+(max_x-min_x);
                    max_z=min_z+(max_x-min_x);
                }else{
                    max_y=min_y+(max_z-min_z);
                    max_x=min_x+(max_z-min_z);
                    
                }
            }else{
                if((max_y-min_y)>(max_z-min_z)){
                    max_x=min_x+(max_y-min_y);
                    max_z=min_z+(max_y-min_y);
                }else{
                    max_y=min_y+(max_z-min_z);
                    max_x=min_x+(max_z-min_z);
                }
            }
        }
    }
    
    public GPDomain getGPDomain(int id, DomainType theType){
        GPDomain aGPDomain=null;
        for (int i = 0; i < theDomains.size(); i++) {
            if(theDomains.get(i).getID()==id && theDomains.get(i).getType()==theType)aGPDomain=theDomains.get(i);
        }
        return aGPDomain;
    }
    
    void buildBEMs(){
        for(Iterator<jbem.Domain> it=theUniverse.getBEMDomains().values().iterator(); it.hasNext();){
                jbem.Domain theDomain = it.next();
                if(this.getGPDomain(theDomain.getID(), DomainType.BEM).getPlotit())buildBEMdomain(theDomain);
        }
        
        BEMs.setVisible(true);
        world.getChildren().addAll(BEMs);
    }
    
    void buildBEMdomain(jbem.Domain theDomain){
        /*TriangleMesh mesh = new TriangleMesh();
        Map<Integer,Integer> mapIDs = new TreeMap<Integer,Integer>();
        int count=0;
        for (jbem.Node theNode : theDomain.getNodes().values()) {
            double[] coord=theNode.getCoordinates();
            mesh.getPoints().addAll(xTransformation(coord[0]),yTransformation(coord[1]),zTransformation(coord[2]));
            mapIDs.put(theNode.getID(), count++);
        }
        mesh.getTexCoords().addAll(0,0);
        for (jbem.Element theElement : theDomain.getElements().values()) {
            switch(theElement.getNumNodes()){
                case 4:
                    mesh.getFaces().addAll(
                            mapIDs.get(theElement.getNodeHier(1).getID()),0,mapIDs.get(theElement.getNodeHier(2).getID()),0,mapIDs.get(theElement.getNodeHier(3).getID()),0,
                            mapIDs.get(theElement.getNodeHier(1).getID()),0,mapIDs.get(theElement.getNodeHier(3).getID()),0,mapIDs.get(theElement.getNodeHier(4).getID()),0
                    );
                    break;
                default:
                    
            }
        }
        MeshView meshView = new MeshView(mesh);
        meshView.setCullFace(CullFace.BACK); //Removing culling to show back lines
        final PhongMaterial BEMsMaterial = new PhongMaterial();
        java.awt.Color awtColor = getGPDomain(theDomain.getID(), DomainType.BEM).getColor();
        int r = awtColor.getRed();
        int g = awtColor.getGreen();
        int b = awtColor.getBlue();
        int a = awtColor.getAlpha();
        double opacity = a / 255.0 ;
        javafx.scene.paint.Color fxColor = javafx.scene.paint.Color.rgb(r, g, b, opacity);
        BEMsMaterial.setDiffuseColor(fxColor);
        BEMsMaterial.setSpecularColor(fxColor);
        meshView.setMaterial(BEMsMaterial);
        meshView.setDrawMode(DrawMode.FILL);
        BEMs.getChildren().add(meshView);*/
        
        
        ArrayList<Point3D> points = null;
        PolyLine3D polyLine3D;
        for (jbem.Element theElement : theDomain.getElements().values()) {
            switch(theElement.getNumNodes()){
                case 4:
                    for(int i=1;i<=4;i++){
                        int start=i; int end=i+1;
                        if(i==4){start=i; end=1;}
                        points = new ArrayList<>();
                        points.add(new Point3D(
                            (float) xTransformation(theElement.getNodeHier(start).X()),
                            (float) yTransformation(theElement.getNodeHier(start).Y()),
                            (float) zTransformation(theElement.getNodeHier(start).Z())));

                        points.add(new Point3D(
                            (float) xTransformation(theElement.getNodeHier(end).X()),
                            (float) yTransformation(theElement.getNodeHier(end).Y()),
                            (float) zTransformation(theElement.getNodeHier(end).Z())));
                        polyLine3D = new PolyLine3D(points,3,javafx.scene.paint.Color.BLUE);
                        BEMs.getChildren().add(polyLine3D.meshView);
                    }
                    break;
                default:
                    
            }
        }
    }
    
    void buildFEMs(){
        
        for(Iterator<jfem.Domain> it=theUniverse.getFEMDomains().values().iterator(); it.hasNext();){
                jfem.Domain theDomain = it.next();
                if(this.getGPDomain(theDomain.getID(), DomainType.FEM).getPlotit())buildFEMdomain(theDomain);
        }
        
        FEMs.setVisible(true);
        world.getChildren().addAll(FEMs);
    }
    
    void buildFEMdomain(jfem.Domain theDomain){
        TriangleMesh mesh = new TriangleMesh();
        Map<Integer,Integer> mapIDs = new TreeMap<Integer,Integer>();
        java.awt.Color awtColor = getGPDomain(theDomain.getID(), DomainType.FEM).getColor();
        int r = awtColor.getRed();
        int g = awtColor.getGreen();
        int b = awtColor.getBlue();
        int a = awtColor.getAlpha();
        double opacity = a / 255.0 ;
        MeshView meshView = null;
        ArrayList<Point3D> points = null;
        PolyLine3D polyLine3D;
                
        int count=0;
        for(Iterator<jfem.Node> it=theDomain.getNodes().values().iterator(); it.hasNext();){
            jfem.Node theNode = it.next();
            double[] coord=theNode.getCoords();
            mesh.getPoints().addAll(xTransformation(coord[0]),yTransformation(coord[1]),zTransformation(coord[2]));
            mapIDs.put(theNode.getID(), count++);
        }
        mesh.getTexCoords().addAll(0,0);
        for(Iterator<jfem.Element> it=theDomain.getElements().values().iterator(); it.hasNext();){
            jfem.Element theElement = it.next();
            switch(theElement.getNumNodes()){
                case 2:
                    points = new ArrayList<>();
                    points.add(new Point3D(
                        (float) xTransformation(theElement.getNodeHierarchy(1).getCoords()[0]),
                        (float) yTransformation(theElement.getNodeHierarchy(1).getCoords()[1]),
                        (float) zTransformation(theElement.getNodeHierarchy(1).getCoords()[2])));
                    
                    points.add(new Point3D(
                        (float) xTransformation(theElement.getNodeHierarchy(2).getCoords()[0]),
                        (float) yTransformation(theElement.getNodeHierarchy(2).getCoords()[1]),
                        (float) zTransformation(theElement.getNodeHierarchy(2).getCoords()[2])));
                    polyLine3D = new PolyLine3D(points,3,javafx.scene.paint.Color.rgb(r, g, b, opacity));
                    FEMs.getChildren().add(polyLine3D.meshView);
                    
                    double x_s=xTransformation(theElement.getNodeHierarchy(1).getCoords()[0]);
                    double y_s=yTransformation(theElement.getNodeHierarchy(1).getCoords()[1]);
                    double z_s=zTransformation(theElement.getNodeHierarchy(1).getCoords()[2]);
                    
                    double x_e=xTransformation(theElement.getNodeHierarchy(2).getCoords()[0]);
                    double y_e=yTransformation(theElement.getNodeHierarchy(2).getCoords()[1]);
                    double z_e=zTransformation(theElement.getNodeHierarchy(2).getCoords()[2]);
                    
                    double x_m=(x_e+x_s)/2;
                    double y_m=(y_e+y_s)/2;
                    double z_m=(z_e+z_s)/2;
                    
                    double h=Math.sqrt((x_m-x_e)*(x_m-x_e)+(y_m-y_e)*(y_m-y_e)+(z_m-z_e)*(z_m-z_e));
                    double rd=zTransformation(Math.sqrt(theElement.getCrossSection().getA()/Math.PI))/10.0;
                    
                    Cylinder cylinder = new Cylinder(); 
                    cylinder.setHeight(h); 
                    cylinder.setRadius(rd); 
                    cylinder.setTranslateX(x_m);
                    cylinder.setTranslateY(y_m);
                    cylinder.setTranslateZ(z_m);
                    FEMs.getChildren().add(cylinder);
                    break;
                case 4:
                    /*mesh.getFaces().addAll(
                            mapIDs.get(theElement.getNodeHierarchy(1).getID()),0,
                            mapIDs.get(theElement.getNodeHierarchy(2).getID()),0,
                            mapIDs.get(theElement.getNodeHierarchy(3).getID()),0,
                            mapIDs.get(theElement.getNodeHierarchy(1).getID()),0,
                            mapIDs.get(theElement.getNodeHierarchy(3).getID()),0,
                            mapIDs.get(theElement.getNodeHierarchy(4).getID()),0
                    );
                    meshView = new MeshView(mesh);
                    meshView.setCullFace(CullFace.NONE); //Removing culling to show back lines
                    final PhongMaterial BEMsMaterial = new PhongMaterial();
                    javafx.scene.paint.Color fxColor = javafx.scene.paint.Color.rgb(r, g, b, opacity);
                    BEMsMaterial.setDiffuseColor(fxColor);
                    BEMsMaterial.setSpecularColor(fxColor);
                    meshView.setMaterial(BEMsMaterial);
                    meshView.setDrawMode(DrawMode.FILL);
                    FEMs.getChildren().add(meshView);*/
                    
                    for(int i=1;i<=4;i++){
                        int start=i; int end=i+1;
                        if(i==4){start=i; end=1;}
                        points = new ArrayList<>();
                        points.add(new Point3D(
                            (float) xTransformation(theElement.getNodeHierarchy(start).getCoords()[0]),
                            (float) yTransformation(theElement.getNodeHierarchy(start).getCoords()[1]),
                            (float) zTransformation(theElement.getNodeHierarchy(start).getCoords()[2])));

                        points.add(new Point3D(
                            (float) xTransformation(theElement.getNodeHierarchy(end).getCoords()[0]),
                            (float) yTransformation(theElement.getNodeHierarchy(end).getCoords()[1]),
                            (float) zTransformation(theElement.getNodeHierarchy(end).getCoords()[2])));
                        polyLine3D = new PolyLine3D(points,3,javafx.scene.paint.Color.BLUE);
                        FEMs.getChildren().add(polyLine3D.meshView);
                    }
                    break;
                default:
            }
        }
        
        double rad=Math.sqrt( (xTransformation(max_x)-xTransformation(min_x))*(xTransformation(max_x)-xTransformation(min_x))+
                (yTransformation(max_y)-yTransformation(min_y))*(yTransformation(max_y)-yTransformation(min_y))+
                (zTransformation(max_z)-zTransformation(min_z))*(zTransformation(max_z)-zTransformation(min_z)));
        for(Iterator<jfem.Node> it=theDomain.getNodes().values().iterator(); it.hasNext();){
            jfem.Node theNode = it.next();
            Sphere sphere = new Sphere();
            
            sphere.setRadius(rad/400);
            sphere.setTranslateX(xTransformation(theNode.X()));
            sphere.setTranslateY(yTransformation(theNode.Y()));
            sphere.setTranslateZ(zTransformation(theNode.Z()));
            FEMs.getChildren().add(sphere);
        }
    }
    
    protected int xTransformation(double x){
        int xd;
        double xmax=this.max_x;
        double xmin=this.min_x;
//        xd=(int) ((int) this.zoomFactor*((-xmin * xe + x * (xe - xs) + xmax * xs) / (xmax - xmin)));
        xd=(int) ((int) ((-xmin * xe + x * (xe - xs) + xmax * xs) / (xmax - xmin)));
        return xd;
    }
    
    protected int yTransformation(double y){
        int yd;
        double ymax=this.max_y;
        double ymin=this.min_y;
//        xd=(int) ((int) this.zoomFactor*((-xmin * xe + x * (xe - xs) + xmax * xs) / (xmax - xmin)));
        yd=(int) ((int) ((-ymin * ye + y * (ye - ys) + ymax * ys) / (ymax - ymin)));
        return yd;
    }
    
    protected int zTransformation(double z){
        int zd;
        double zmax=this.max_z;
        double zmin=this.min_z;
//        xd=(int) ((int) this.zoomFactor*((-xmin * xe + x * (xe - xs) + xmax * xs) / (xmax - xmin)));
        zd=(int) ((int) ((-zmin * ze + z * (ze - zs) + zmax * zs) / (zmax - zmin)));
        return zd;
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
    
}
