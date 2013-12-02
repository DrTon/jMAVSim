package me.drton.jmavsim;

import com.sun.j3d.loaders.Scene;
import com.sun.j3d.loaders.objectfile.ObjectFile;
import com.sun.j3d.utils.geometry.Sphere;
import com.sun.j3d.utils.image.TextureLoader;
import com.sun.j3d.utils.universe.SimpleUniverse;
import me.drton.jmavsim.vehicle.Vehicle;

import javax.media.j3d.*;
import javax.vecmath.*;
import java.io.FileNotFoundException;

/**
 * User: ton Date: 28.11.13 Time: 23:15
 */
public class Visualizer {
    private static Color3f white = new Color3f(1.0f, 1.0f, 1.0f);
    private SimpleUniverse universe;
    private Environment environment;
    private Vehicle vehicle;
    private Transform3D vehicleTransform;
    private TransformGroup vehicle3D;
    private BoundingSphere sceneBounds = new BoundingSphere(new Point3d(0, 0, 0), 100000.0);
    private Vector3d viewerPos = new Vector3d(-7.0, 0.0, -1.7);
    private Transform3D viewerTransform = new Transform3D();

    public Visualizer(Environment environment) {
        this.environment = environment;
        universe = new SimpleUniverse();
        universe.getViewer().getView().setBackClipDistance(100000.0);
        createEnvironment();
    }

    public void setVehicle(Vehicle vehicle, String fileName) throws FileNotFoundException {
        this.vehicle = vehicle;
        if (vehicle3D == null) {
            createVehicle(fileName);
            updateViewer();
        }
    }

    private void createEnvironment() {
        BranchGroup group = new BranchGroup();
        // Sky
        BoundingSphere bounds = new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 100.0);
        Background bg = new Background();
        bg.setApplicationBounds(bounds);
        BranchGroup backGeoBranch = new BranchGroup();
        Sphere skySphere = new Sphere(1.0f, Sphere.GENERATE_NORMALS | Sphere.GENERATE_NORMALS_INWARD | Sphere.GENERATE_TEXTURE_COORDS, 32);
        //        Sphere.GENERATE_NORMALS | Sphere.GENERATE_NORMALS_INWARD | Sphere.GENERATE_TEXTURE_COORDS, 32);
        Texture texSky = new TextureLoader("environment/sky.jpg", null).getTexture();
        skySphere.getAppearance().setTexture(texSky);
        Transform3D transformSky = new Transform3D();
        //transformSky.setTranslation(new Vector3d(0.0, 0.0, -0.5));
        Matrix3d rot = new Matrix3d();
        rot.rotX(Math.PI/2);
        transformSky.setRotation(rot);
        TransformGroup tgSky = new TransformGroup(transformSky);
        tgSky.addChild(skySphere);
        backGeoBranch.addChild(tgSky);
        bg.setGeometry(backGeoBranch);
        group.addChild(bg);
        //group.addChild(tgSky);
        // Ground
        QuadArray polygon1 = new QuadArray(4, QuadArray.COORDINATES | GeometryArray.TEXTURE_COORDINATE_2);
        polygon1.setCoordinate(0, new Point3f(-100f, 100f, 0f));
        polygon1.setCoordinate(1, new Point3f(100f, 100f, 0f));
        polygon1.setCoordinate(2, new Point3f(100f, -100f, 0f));
        polygon1.setCoordinate(3, new Point3f(-100f, -100f, 0f));
        polygon1.setTextureCoordinate(0, 0, new TexCoord2f(0.0f, 0.0f));
        polygon1.setTextureCoordinate(0, 1, new TexCoord2f(10.0f, 0.0f));
        polygon1.setTextureCoordinate(0, 2, new TexCoord2f(10.0f, 10.0f));
        polygon1.setTextureCoordinate(0, 3, new TexCoord2f(0.0f, 10.0f));
        Texture texGround = new TextureLoader("environment/grass2.jpg", null).getTexture();
        Appearance apGround = new Appearance();
        apGround.setTexture(texGround);
        Shape3D ground = new Shape3D(polygon1, apGround);
        Transform3D transformGround = new Transform3D();
        transformGround.setTranslation(
                new Vector3d(0.0, 0.0, 0.005 + environment.getGroundLevel(new Vector3d(0.0, 0.0, 0.0))));
        TransformGroup tgGround = new TransformGroup(transformGround);
        tgGround.addChild(ground);
        group.addChild(tgGround);
        // Light
        DirectionalLight light1 = new DirectionalLight(white, new Vector3f(4.0f, 7.0f, 12.0f));
        light1.setInfluencingBounds(sceneBounds);
        group.addChild(light1);
        AmbientLight light2 = new AmbientLight(new Color3f(0.5f, 0.5f, 0.5f));
        light2.setInfluencingBounds(sceneBounds);
        group.addChild(light2);
        universe.addBranchGraph(group);
    }

    private void createVehicle(String fileName) throws FileNotFoundException {
        ObjectFile objectFile = new ObjectFile();
        Scene scene = objectFile.load(fileName);
        vehicle3D = new TransformGroup();
        vehicle3D.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        vehicleTransform = new Transform3D();
        vehicle3D.setTransform(vehicleTransform);
        vehicle3D.addChild(scene.getSceneGroup());
        BranchGroup bg = new BranchGroup();
        bg.addChild(vehicle3D);
        universe.addBranchGraph(bg);
    }

    private void updateViewer() {
        Vector3d pos = vehicle.getPosition();
        Matrix3d mat = new Matrix3d();
        Matrix3d m1 = new Matrix3d();
        mat.rotZ(Math.PI);
        Vector3d dist = new Vector3d();
        dist.sub(pos, viewerPos);
        m1.rotY(Math.PI / 2);
        mat.mul(m1);
        m1.rotZ(-Math.PI / 2);
        mat.mul(m1);
        m1.rotY(-Math.atan2(pos.y - viewerPos.y, pos.x - viewerPos.x));
        mat.mul(m1);
        m1.rotX(-Math.asin((pos.z - viewerPos.z) / dist.length()));
        mat.mul(m1);
        viewerTransform.setRotation(mat);
        viewerTransform.setTranslation(viewerPos);
        universe.getViewingPlatform().getViewPlatformTransform().setTransform(viewerTransform);
    }

    public void update(long t) {
        if (vehicle != null) {
            vehicleTransform.setTranslation(vehicle.getPosition());
            vehicleTransform.setRotationScale(vehicle.getRotation());
            vehicle3D.setTransform(vehicleTransform);
            updateViewer();
        }
    }
}
