package me.drton.jmavsim;

import com.sun.j3d.loaders.Scene;
import com.sun.j3d.loaders.objectfile.ObjectFile;

import javax.media.j3d.BranchGroup;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import java.io.FileNotFoundException;

/**
 * User: ton Date: 02.02.14 Time: 11:56
 */
public abstract class VisualObject extends MechanicalObject {
    protected Transform3D transform;
    protected TransformGroup transformGroup;
    protected BranchGroup branchGroup;

    public VisualObject(World world) {
        super(world);
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
     * @throws FileNotFoundException
     */
    protected void modelFromFile(String modelFile) throws FileNotFoundException {
        ObjectFile objectFile = new ObjectFile();
        Scene scene = objectFile.load(modelFile);
        transformGroup.addChild(scene.getSceneGroup());
    }

    public BranchGroup getBranchGroup() {
        return branchGroup;
    }

    @Override
    public void update(long t) {
        super.update(t);
        transform.setTranslation(getPosition());
        transform.setRotationScale(getRotation());
        transformGroup.setTransform(transform);
    }
}
