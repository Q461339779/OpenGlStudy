package com.g.openglstudy.util;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.ImageFormat;
import android.graphics.Point;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.util.Size;
import android.view.Surface;



import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class CameraHelper {
    private static final String TAG = "Camera2Helper";
    private SurfaceTexture mSurfaceTexture;
    private HandlerThread mBackGroundThread;
    private Handler mBackGroundHandler;
    private Size mPreviewSize;
    private OnPreviewSizeListener onPreviewSizeListener;
    private OnPreviewListener onPreviewListener;
    private ImageReader imageReader;
    private String mCameraId;
    private CameraDevice mCameraDevice;
    private CameraManager cameraManager;
    private CaptureRequest.Builder mPreviewRequestBuilder;
    private CameraCaptureSession mCaptureSession;
    //代表一次捕获请求，用于描述捕获图片的各种参数设置
    private CaptureRequest mPreviewRequest;
    private Size[] aa;

    public CameraHelper(Activity mContext) {
        this.mContext = mContext;
        Log.i("Magic-"+TAG,"CameraHelper---4");
    }

    private Activity mContext;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void openCamera(int width, int height, SurfaceTexture surfaceTexture) {
        Log.i("Magic-"+TAG,"openCamera---15");
        this.mSurfaceTexture = surfaceTexture;
        startBackgroundThread();

        //设置预览图像大小
        setUpCameraOutputs(width, height);
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (mContext.checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
            }

            cameraManager.openCamera(mCameraId, mStateCallback, mBackGroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void setUpCameraOutputs(int width, int height) {
        Log.i("Magic-"+TAG,"setUpCameraOutputs---18");
        cameraManager = (CameraManager) mContext.getSystemService(Context.CAMERA_SERVICE);
        try {
            //用于描述摄像头所支持的各种特性；
            CameraCharacteristics characteristics = cameraManager.getCameraCharacteristics("1");
            Integer facing = characteristics.get(CameraCharacteristics.LENS_FACING);
            StreamConfigurationMap map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
            Point displaySize = new Point();
            mContext.getWindowManager().getDefaultDisplay().getSize(displaySize);
            int rotatedPreviewWidth = width;
            int rotatedPreviewHeight = height;
            int maxPreviewWidth = displaySize.x;
            int maxPreviewHeight = displaySize.y;
            Size largest = Collections.max(
                    Arrays.asList(map.getOutputSizes(ImageFormat.YUV_420_888)),
                    new CompareSizesByArea());

            aa =  map.getOutputSizes(ImageFormat.YUV_420_888);
            mPreviewSize = chooseOptimalSize(map.getOutputSizes(SurfaceTexture.class),
                    rotatedPreviewWidth, rotatedPreviewHeight, maxPreviewWidth,
                    maxPreviewHeight, largest);

            if (onPreviewSizeListener != null) {
                onPreviewSizeListener.onSize(mPreviewSize.getWidth(), mPreviewSize.getHeight());
            }

//            imageReader = ImageReader.newInstance(mPreviewSize.getWidth(), mPreviewSize.getHeight(), ImageFormat.YUV_420_888, 2);
//            imageReader.setOnImageAvailableListener(mOnImageAvailableListener, mBackGroundHandler);
            mCameraId = "1";
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }


    private final CameraDevice.StateCallback mStateCallback = new CameraDevice.StateCallback() {

        @Override
        public void onOpened(@NonNull CameraDevice cameraDevice) {
            // This method is called when the camera is opened.  We start camera preview here.
            Log.i("Magic-"+TAG,"onOpened---23");
            mCameraDevice = cameraDevice;
            //创建相机预览会话
            createCameraPreviewSession();
        }

        @Override
        public void onDisconnected(@NonNull CameraDevice cameraDevice) {
            Log.i("Magic-"+TAG,"onDisconnected");
            cameraDevice.close();
            mCameraDevice = null;
        }

        @Override
        public void onError(@NonNull CameraDevice cameraDevice, int error) {
            Log.i("Magic-"+TAG,"onError");
            cameraDevice.close();
            mCameraDevice = null;
        }

    };



    /**
     * Creates a new {@link CameraCaptureSession} for camera preview.
     */
    private void createCameraPreviewSession() {
        Log.i("Magic-"+TAG,"createCameraPreviewSession");
        try {

            // This is the output Surface we need to start preview.
            //设置缓冲区大小为预览尺寸的大小
            mSurfaceTexture.setDefaultBufferSize(mPreviewSize.getWidth(), mPreviewSize.getHeight());

            //SurfaceTexture从图像流中捕获帧作为OpenGL ES纹理
            //开始预览的输出Surface
            Surface preViewSurface = new Surface(mSurfaceTexture);

            // We set up a CaptureRequest.Builder with the output Surface.
            //关联 CaptureRequest.Builder 和 Surface
            mPreviewRequestBuilder
                    = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            mPreviewRequestBuilder.addTarget(preViewSurface);
            //mPreviewRequestBuilder.addTarget(imageReader.getSurface());

            // Here, we create a CameraCaptureSession for camera preview.
            //创建相机捕获会话请求 预览数据同时输出到   preViewSurface
            //mCameraDevice.createCaptureSession(Arrays.asList(preViewSurface, imageReader.getSurface()),
            //创建相机捕获会话请求 预览数据同时输出到   preViewSurface 和 imageReaderSurface
            mCameraDevice.createCaptureSession(Arrays.asList(preViewSurface),
                    new CameraCaptureSession.StateCallback() {
                        @Override
                        public void onConfigured(@NonNull CameraCaptureSession cameraCaptureSession) {
                            // The camera is already closed
                            if (null == mCameraDevice) {
                                return;
                            }

                            // When the session is ready, we start displaying the preview.
                            mCaptureSession = cameraCaptureSession;
                            try {
                                // Auto focus should be continuous for camera preview.
                                mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE,
                                        CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
                                // Flash is automatically enabled when necessary.

                                // Finally, we start displaying the camera preview.
                                //生成 CameraRequest 对象
                                mPreviewRequest = mPreviewRequestBuilder.build();
                                mCaptureSession.setRepeatingRequest(mPreviewRequest,
                                        mCaptureCallback, mBackGroundHandler);
                            } catch (CameraAccessException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onConfigureFailed(
                                @NonNull CameraCaptureSession cameraCaptureSession) {
                            Log.d(TAG, "onConfigureFailed: ");
                        }
                    }, null
            );
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }


    /**
     * A {@link CameraCaptureSession.CaptureCallback} that handles events related to JPEG capture.
     */
    private CameraCaptureSession.CaptureCallback mCaptureCallback
            = new CameraCaptureSession.CaptureCallback() {

        @Override
        public void onCaptureProgressed(@NonNull CameraCaptureSession session,
                                        @NonNull CaptureRequest request,
                                        @NonNull CaptureResult partialResult) {
            Log.i("onCaptureProgressed","onCaptureProgressed");
        }

        @Override
        public void onCaptureCompleted(@NonNull CameraCaptureSession session,
                                       @NonNull CaptureRequest request,
                                       @NonNull TotalCaptureResult result) {

            Log.i("onCaptureCompleted","onCaptureCompleted");
        }

    };



    private ImageReader.OnImageAvailableListener mOnImageAvailableListener = new ImageReader.OnImageAvailableListener() {
        @Override
        public void onImageAvailable(ImageReader reader) {
            Log.i("Magic-"+TAG,"onImageAvailable---32");
            Image image = reader.acquireNextImage();
            if (image == null) {
                return;
            }

            Image.Plane[] planes = image.getPlanes();
            int width = image.getWidth();
            int height = image.getHeight();

            byte[] yBytes = new byte[width * height];
            byte[] uBytes = new byte[width * height / 4];
            byte[] vBytes = new byte[width * height / 4];
            byte[] i420 = new byte[width * height * 3 / 2];


            for (int i = 0; i < planes.length; i++) {
                int dstIndex = 0;
                int uIndex = 0;
                int vIndex = 0;
                int pixelStride = planes[i].getPixelStride();
                int rowStride = planes[i].getRowStride();

                ByteBuffer buffer = planes[i].getBuffer();

                byte[] bytes = new byte[buffer.capacity()];

                buffer.get(bytes);
                int srcIndex = 0;
                if (i == 0) {
                    for (int j = 0; j < height; j++) {
                        System.arraycopy(bytes, srcIndex, yBytes, dstIndex, width);
                        srcIndex += rowStride;
                        dstIndex += width;
                    }
                } else if (i == 1) {
                    for (int j = 0; j < height / 2; j++) {
                        for (int k = 0; k < width / 2; k++) {
                            uBytes[dstIndex++] = bytes[srcIndex];
                            srcIndex += pixelStride;
                        }

                        if (pixelStride == 2) {
                            srcIndex += rowStride - width;
                        } else if (pixelStride == 1) {
                            srcIndex += rowStride - width / 2;
                        }
                    }
                } else if (i == 2) {
                    for (int j = 0; j < height / 2; j++) {
                        for (int k = 0; k < width / 2; k++) {
                            vBytes[dstIndex++] = bytes[srcIndex];
                            srcIndex += pixelStride;
                        }

                        if (pixelStride == 2) {
                            srcIndex += rowStride - width;
                        } else if (pixelStride == 1) {
                            srcIndex += rowStride - width / 2;
                        }
                    }
                }
                System.arraycopy(yBytes, 0, i420, 0, yBytes.length);
                System.arraycopy(uBytes, 0, i420, yBytes.length, uBytes.length);
                System.arraycopy(vBytes, 0, i420, yBytes.length + uBytes.length, vBytes.length);

                if (onPreviewListener != null) {
                    onPreviewListener.onPreviewFrame(i420, i420.length);
                }


            }
            image.close();
        }
    };

    private static Size chooseOptimalSize(Size[] choices, int textureViewWidth,
                                          int textureViewHeight, int maxWidth, int maxHeight, Size aspectRatio) {
        Log.i("Magic-"+TAG,"chooseOptimalSize");
        return choices[13];
    }

    public void setPreviewSizeListener(OnPreviewSizeListener onPreviewSizeListener) {
        Log.i("Magic-"+TAG,"setPreviewSizeListener---13");
        this.onPreviewSizeListener = onPreviewSizeListener;
    }

    /**
     * Compares two {@code Size}s based on their areas.
     */
    static class CompareSizesByArea implements Comparator<Size> {

        @Override
        public int compare(Size lhs, Size rhs) {
            // We cast here to ensure the multiplications won't overflow
            Log.i("Magic-"+TAG,"compare---19");
            return Long.signum((long) lhs.getWidth() * lhs.getHeight() -
                    (long) rhs.getWidth() * rhs.getHeight());
        }

    }

    private void startBackgroundThread() {
        Log.i("Magic-"+TAG,"startBackgroundThread---16");
        mBackGroundThread = new HandlerThread("CameraBackground");
        mBackGroundThread.start();
        mBackGroundHandler = new Handler(mBackGroundThread.getLooper());
    }


    @SuppressLint("NewApi")
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    private void stopBackgroundThread() {
        mBackGroundThread.quitSafely();
        try {
            mBackGroundThread.join();
            mBackGroundThread = null;
            mBackGroundHandler = null;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }



    public void setOnPreviewListener(OnPreviewListener onPreviewListener) {
        Log.i("Magic-"+TAG,"setOnPreviewListener---14");
        this.onPreviewListener = onPreviewListener;
    }

    public interface OnPreviewSizeListener {

        void onSize(int width, int height);
    }

    public interface OnPreviewListener {
        void onPreviewFrame(byte[] data, int len);
    }


    public void closeCamera() {

        if (null != mCaptureSession) {
            mCaptureSession.close();
            mCaptureSession = null;
        }
        if (null != mCameraDevice) {
            mCameraDevice.close();
            mCameraDevice = null;
        }

        stopBackgroundThread();

    }




}
