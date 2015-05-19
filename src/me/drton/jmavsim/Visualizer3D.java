package me.drton.jmavsim;

import com.sun.j3d.utils.geometry.Sphere;
import com.sun.j3d.utils.image.TextureLoader;
import com.sun.j3d.utils.universe.SimpleUniverse;

import javax.media.j3d.*;
import javax.swing.*;
import javax.vecmath.*;
import java.awt.*;
import java.util.Enumeration;

/**
 * 3D Visualizer, works in own thread, synchronized with "world" thread.
 */
public class Visualizer3D extends JFrame {
    private static Color3f white = new Color3f(1.0f, 1.0f, 1.0f);
    private final World world;
    private SimpleUniverse universe;
    private BoundingSphere sceneBounds = new BoundingSphere(new Point3d(0, 0, 0), 100000.0);
    private Vector3d viewerPosition = new Vector3d(0.0, 0.0, 0.0);
    private Vector3d viewerPositionOffset = new Vector3d(0.0, 0.0, 0.0);
    private Transform3D viewerTransform = new Transform3D();
    private TransformGroup viewerTransformGroup;
    private KinematicObject viewerTargetObject;
    private KinematicObject viewerPositionObject;

    public Visualizer3D(World world) {
        this.world = world;

        setSize(640, 480);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        GraphicsConfiguration gc = SimpleUniverse.getPreferredConfiguration();
        Canvas3D canvas = new Canvas3D(gc);
        getContentPane().add(canvas);

        universe = new SimpleUniverse(canvas);
        universe.getViewer().getView().setBackClipDistance(100000.0);
        viewerTransformGroup = universe.getViewingPlatform().getViewPlatformTransform();
        createEnvironment();
        for (WorldObject object : world.getObjects()) {
            if (object instanceof KinematicObject) {
                BranchGroup bg = ((KinematicObject) object).getBranchGroup();
                if (bg != null) {
                    universe.addBranchGraph(bg);
                }
            }
        }
        setVisible(true);

        Matrix3d mat = new Matrix3d();
        Matrix3d mat1 = new Matrix3d();
        mat.rotZ(Math.PI);
        mat1.rotY(Math.PI / 2);
        mat.mul(mat1);
        mat1.rotZ(-Math.PI / 2);
        mat.mul(mat1);
        viewerTransform.setRotation(mat);
    }

    /**
     * Target object to point camera, has effect only if viewerPositionObject is not set.
     *
     * @param object
     */
    public void setViewerTargetObject(KinematicObject object) {
        this.viewerTargetObject = object;
    }

    /**
     * Object to place camera on, if nullptr then camera will be placed in fixed point set by setViewerPosition().
     *
     * @param object
     */
    public void setViewerPositionObject(KinematicObject object) {
        this.viewerPositionObject = object;
    }

    /**
     * Fixed camera position, has effect only if viewerPositionObject not set.
     *
     * @param position
     */
    public void setViewerPosition(Vector3d position) {
        this.viewerPositionObject = null;
        this.viewerPosition = position;
        viewerTransform.setTranslation(viewerPosition);
    }

    /**
     * Camera position offset from object position when viewer placed on some object
     *
     * @param offset position offset
     */
    public void setViewerPositionOffset(Vector3d offset) {
        this.viewerPositionOffset = offset;
    }

    private void createEnvironment() {
        BranchGroup group = new BranchGroup();
        // Sky
        BoundingSphere bounds = new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 1000.0);
        Background bg = new Background();
        bg.setApplicationBounds(bounds);
        BranchGroup backGeoBranch = new BranchGroup();
        Sphere skySphere = new Sphere(1.0f,
                Sphere.GENERATE_NORMALS | Sphere.GENERATE_NORMALS_INWARD | Sphere.GENERATE_TEXTURE_COORDS, 32);
        //        Sphere.GENERATE_NORMALS | Sphere.GENERATE_NORMALS_INWARD | Sphere.GENERATE_TEXTURE_COORDS, 32);
        Texture texSky = new TextureLoader("environment/sky.jpg", null).getTexture();
        skySphere.getAppearance().setTexture(texSky);
        Transform3D transformSky = new Transform3D();
        //transformSky.setTranslation(new Vector3d(0.0, 0.0, -0.5));
        Matrix3d rot = new Matrix3d();
        rot.rotX(Math.PI / 2);
        transformSky.setRotation(rot);
        TransformGroup tgSky = new TransformGroup(transformSky);
        tgSky.addChild(skySphere);
        backGeoBranch.addChild(tgSky);
        bg.setGeometry(backGeoBranch);
        group.addChild(bg);
        //group.addChild(tgSky);
        // Ground
        QuadArray polygon1 = new QuadArray(4, QuadArray.COORDINATES | GeometryArray.TEXTURE_COORDINATE_2);
        polygon1.setCoordinate(0, new Point3f(-1000f, 1000f, 0f));
        polygon1.setCoordinate(1, new Point3f(1000f, 1000f, 0f));
        polygon1.setCoordinate(2, new Point3f(1000f, -1000f, 0f));
        polygon1.setCoordinate(3, new Point3f(-1000f, -1000f, 0f));
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
                new Vector3d(0.0, 0.0, 0.005 + world.getEnvironment().getGroundLevel(new Vector3d(0.0, 0.0, 0.0))));
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

        // Update behavior
        Behavior b = new UpdateBehavior();
        b.setSchedulingBounds(bounds);
        group.addChild(b);
        universe.addBranchGraph(group);
    }

    private void updateVisualizer() {
        synchronized (world) { // Synchronize with "world" thread
            // Update branch groups of all kinematic objects
            for (WorldObject object : world.getObjects()) {
                if (object instanceof KinematicObject) {
                    BranchGroup bg = ((KinematicObject) object).getBranchGroup();
                    if (bg != null) {
                        ((KinematicObject) object).updateBranchGroup();
                    }
                }
            }

            // Update view platform
            if (viewerPositionObject != null) {
                // Camera on object
                viewerPosition.set(viewerPositionOffset);
                viewerPositionObject.getRotation().transform(viewerPosition);
                viewerPosition.add(viewerPositionObject.getPosition());
                viewerTransform.setTranslation(viewerPosition);

                Matrix3d mat = new Matrix3d();
                Matrix3d mat1 = new Matrix3d();
                mat.set(viewerPositionObject.getRotation());
                mat1.rotZ(Math.PI / 2);
                mat.mul(mat1);
                mat1.rotX(-Math.PI / 2);
                mat.mul(mat1);
                viewerTransform.setRotation(mat);
            } else {
                // Fixed camera
                if (viewerTargetObject != null) {
                    // Point camera to target
                    Vector3d pos = viewerTargetObject.getPosition();
                    Vector3d dist = new Vector3d();
                    dist.sub(pos, viewerPosition);

                    Matrix3d mat = new Matrix3d();
                    Matrix3d mat1 = new Matrix3d();
                    mat.rotZ(Math.PI);
                    mat1.rotY(Math.PI / 2);
                    mat.mul(mat1);
                    mat1.rotZ(-Math.PI / 2);
                    mat.mul(mat1);
                    mat1.rotY(-Math.atan2(pos.y - viewerPosition.y, pos.x - viewerPosition.x));
                    mat.mul(mat1);
                    mat1.rotX(-Math.asin((pos.z - viewerPosition.z) / dist.length()));
                    mat.mul(mat1);
                    viewerTransform.setRotation(mat);
                }
            }
            viewerTransformGroup.setTransform(viewerTransform);
        }
    }

    class UpdateBehavior extends Behavior {
        private WakeupCondition condition = new WakeupOnElapsedFrames(0, false);

        @Override
        public void initialize() {
            wakeupOn(condition);
        }

        @Override
        public void processStimulus(Enumeration wakeup) {
            Object w;
            while (wakeup.hasMoreElements()) {
                w = wakeup.nextElement();
                if (w instanceof WakeupOnElapsedFrames) {
                    updateVisualizer();
                }
                wakeupOn(condition);
            }
        }
    }
}
