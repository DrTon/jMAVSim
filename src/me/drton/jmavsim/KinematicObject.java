package me.drton.jmavsim;

import com.sun.j3d.loaders.Scene;
import com.sun.j3d.loaders.objectfile.ObjectFile;

import javax.media.j3d.BranchGroup;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.Matrix3d;
import javax.vecmath.Vector3d;
import java.io.FileNotFoundException;

/**
 * Abstract kinematic object class.
 * Stores all kinematic parameters (attitude, attitude rates, position, velocity, acceleration) but doesn't calculate it.
 * These parameters may be set directly for objects moving by fixed trajectory or simulated from external forces (see DynamicObject).
 */
public abstract class KinematicObject extends WorldObject {
    protected Vector3d position = new Vector3d();
    protected Vector3d velocity = new Vector3d();
    protected Vector3d acceleration = new Vector3d();
    protected Matrix3d rotation = new Matrix3d();
    protected Vector3d rotationRate = new Vector3d();

    private Transform3D transform;
    protected TransformGroup transformGroup;
    private BranchGroup branchGroup;

    public KinematicObject(World world) {
        super(world);
        rotation.setIdentity();
        transformGroup = new TransformGroup();
        transformGroup.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        transform = new Transform3D();
        transformGroup.setTransform(transform);
        branchGroup = new BranchGroup();
        branchGroup.addChild(transformGroup);
    }

    /**
     * Helper method to create model from .obj file.
     *
     * @param modelFile file name
     * @throws java.io.FileNotFoundException
     */
    protected void modelFromFile(String modelFile) throws FileNotFoundException {
        ObjectFile objectFile = new ObjectFile();
        Scene scene = objectFile.load(modelFile);
        transformGroup.addChild(scene.getSceneGroup());
    }

    public BranchGroup getBranchGroup() {
        return branchGroup;
    }

    public void updateBranchGroup() {
        transform.setTranslation(position);
        transform.setRotationScale(rotation);
        transformGroup.setTransform(transform);
    }

    public Vector3d getPosition() {
        return position;
    }

    public Vector3d getVelocity() {
        return velocity;
    }

    public Vector3d getAcceleration() {
        return acceleration;
    }

    public Matrix3d getRotation() {
        return rotation;
    }

    public Vector3d getRotationRate() {
        return rotationRate;
    }
}
