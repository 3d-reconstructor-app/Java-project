package com.example.a3dmodel.visualisation;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

import com.example.a3dmodel.App;
import com.example.a3dmodel.project.ProjectFileManager;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class GLView extends GLSurfaceView {
    private final Context c;
    private Renderer renderer;
    ScaleGestureDetector SGD;
    private float mPreviousX;
    private float mPreviousY;

    public GLView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        setEGLContextClientVersion(2);
        c = context;
    }

    public void makeRenderer(String model) {
        renderer = new Renderer(c, model);
        setRenderer(renderer);
        SGD = new ScaleGestureDetector(c, new ScaleListener());
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            renderer.scale *= detector.getScaleFactor();
            return true;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        float x = e.getX();
        float y = e.getY();
        int motionAction = e.getAction() & MotionEvent.ACTION_MASK;
        switch (motionAction) {
            case MotionEvent.ACTION_DOWN:
                mPreviousX = x;
                mPreviousY = y;
                break;
            case MotionEvent.ACTION_MOVE:
                if (renderer != null) {
                    float deltaX = (x - mPreviousX) / 2f;
                    float deltaY = (y - mPreviousY) / 2f;
                    renderer.mDeltaX += deltaX;
                    renderer.mDeltaY += deltaY;
                }
                mPreviousX = x;
                mPreviousY = y;
                break;
        }
        SGD.onTouchEvent(e);
        return true;
    }

    public void Reset() {
        renderer.scale = 1;
        Matrix.setIdentityM(renderer.mAccumulatedRotation, 0);
    }

    public static class Renderer implements GLSurfaceView.Renderer {
        private Mesh mesh;
        private final float[] mModelMatrix = new float[16];
        private final float[] mViewMatrix = new float[16];
        private final float[] mProjectionMatrix = new float[16];
        private final float[] mMVPMatrix = new float[16];
        private final float[] mAccumulatedRotation = new float[16];
        private final float[] mCurrentRotation = new float[16];
        public volatile float mDeltaX;
        public volatile float mDeltaY;
        public volatile float scale = 1;
        public Context current_context;
        private InputStream plyInput;

        public Renderer(Context context, String model) {
            current_context = context;
            try {
                String modelPath = ProjectFileManager.getProjectModelsDirPath(App.getProjectStorage().getCurrentProject().getProjectName()).resolve(model).toString();
                System.out.println("Model path: " + modelPath);
                plyInput = new FileInputStream(modelPath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.4f);
            Matrix.setIdentityM(mAccumulatedRotation, 0);
            try {
                mesh = new Mesh(plyInput);
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                mesh.createProgram();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void onDrawFrame(GL10 gl) {
            float[] mTemporaryMatrix = new float[16];
            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
            Matrix.setIdentityM(mModelMatrix, 0);
            Matrix.scaleM(mModelMatrix, 0, scale, scale, scale);
            Matrix.setLookAtM(mViewMatrix, 0, 0, 0, -3, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
            Matrix.setIdentityM(mCurrentRotation, 0);
            Matrix.rotateM(mCurrentRotation, 0, mDeltaX, 0.0f, 1.0f, 0.0f);
            Matrix.rotateM(mCurrentRotation, 0, mDeltaY, -1.0f, 0.0f, 0.0f);
            mDeltaX = 0.0f;
            mDeltaY = 0.0f;
            Matrix.multiplyMM(mTemporaryMatrix, 0,
                    mCurrentRotation, 0,
                    mAccumulatedRotation, 0);
            System.arraycopy(mTemporaryMatrix, 0,
                    mAccumulatedRotation, 0, 16);
            Matrix.multiplyMM(mTemporaryMatrix, 0,
                    mModelMatrix, 0,
                    mAccumulatedRotation, 0);
            System.arraycopy(mTemporaryMatrix, 0, mModelMatrix, 0, 16);
            Matrix.multiplyMM(mTemporaryMatrix, 0, mViewMatrix, 0, mModelMatrix, 0);
            Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mTemporaryMatrix, 0);
            mesh.draw(mMVPMatrix);
        }

        public void onSurfaceChanged(GL10 gl, int width, int height) {
            GLES20.glViewport(0, 0, width, height);
            float ratio = (float) width / height;
            Matrix.frustumM(mProjectionMatrix, 0, -ratio, ratio, -1, 1, 1, 100);
        }
    }
}
