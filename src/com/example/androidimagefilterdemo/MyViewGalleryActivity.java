package com.example.androidimagefilterdemo;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import cn.Ragnarok.BitmapFilter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;

/**
 * 1/2/15  5:33 PM
 * Created by JustinZhang.
 */
public class MyViewGalleryActivity extends Activity implements View.OnClickListener {
    public static final String PARAM_IMAGE_PATH ="PARAM_IMAGE_PATH";

    private static final String TAG = "MyViewGalleryActivity";
    @InjectView(R.id.iv_image)
    ImageView mIvImage;
    @InjectView(R.id.gallery)
    HorizontalScrollView mGallery;
    @InjectView(R.id.iv_average_blue_style)
    ImageView mIvAverageBlueStyle;
    @InjectView(R.id.iv_block_style)
    ImageView mIvBlockStyle;
    @InjectView(R.id.iv_gaussian_blur_style)
    ImageView mIvGaussianBlurStyle;
    @InjectView(R.id.iv_gotham_style)
    ImageView mIvGothamStyle;
    @InjectView(R.id.iv_gray_style)
    ImageView mIvGrayStyle;
    @InjectView(R.id.iv_hdr_style)
    ImageView mIvHdrStyle;
    @InjectView(R.id.iv_invert_style)
    ImageView mIvInvertStyle;
    @InjectView(R.id.iv_light_style)
    ImageView mIvLightStyle;
    @InjectView(R.id.iv_lomo_style)
    ImageView mIvLomoStyle;
    @InjectView(R.id.iv_motion_blur_style)
    ImageView mIvMotionBlurStyle;
    @InjectView(R.id.iv_neon_style)
    ImageView mIvNeonStyle;
    @InjectView(R.id.iv_oil_style)
    ImageView mIvOilStyle;
    @InjectView(R.id.iv_old_style)
    ImageView mIvOldStyle;
    @InjectView(R.id.iv_pixelate_style)
    ImageView mIvPixelateStyle;
    @InjectView(R.id.iv_relief_style)
    ImageView mIvReliefStyle;
    @InjectView(R.id.iv_sharpen_style)
    ImageView mIvSharpenStyle;
    @InjectView(R.id.iv_sketch_style)
    ImageView mIvSketchStyle;
    @InjectView(R.id.iv_soft_glow_style)
    ImageView mIvSoftGlowStyle;
    @InjectView(R.id.iv_tv_style)
    ImageView mIvTvStyle;
    @InjectView(R.id.rect_view)
    RectView mRectView;
    @InjectView(R.id.tv_title)
    TextView mTvTitle;
    @InjectView(R.id.bt_confirm)
    Button mBtConfirm;
    @InjectView(R.id.bt_choose_mosaic)
    Button mBtChooseMosaic;
    @InjectView(R.id.bt_confirm_mosaic)
    Button mBtConfirmMosaic;
    @InjectView(R.id.bt_cancel_mosaic)
    Button mBtCancelMosaic;

    private Bitmap mCurrentBitmap;
    private Bitmap mOriginBitmap;
    private ProgressDialog mProgressDialog;
    private Rect mFinalRect;

    private int maxImageWidth = 768;
    private int maxImageHeight = 1024;

    private String mImagePath = null;

    /**
     * 马赛克问题：
     * 我们点击马赛克之后怎么取消和确定呢？？？
     * 在当中添加两个按钮，确定
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_view_gallery_activity);
        ButterKnife.inject(this);
        mImagePath= getIntent().getStringExtra(PARAM_IMAGE_PATH);

        if (TextUtils.isEmpty(mImagePath)){
           throw new RuntimeException("YOU MUST PASS **PARAM_IMAGE_PATH** TO THIS CLASS");
        }

        mIvImage = (ImageView) findViewById(R.id.iv_image);
        mCurrentBitmap = Utils.decodeSampledBitmapFromResource(mImagePath,maxImageWidth,maxImageHeight);
        mOriginBitmap = Utils.decodeSampledBitmapFromResource(mImagePath,maxImageWidth,maxImageHeight);
        mIvImage.setImageBitmap(mCurrentBitmap);

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("正在转换请稍后...");

        mIvAverageBlueStyle.setOnClickListener(this);
        mIvBlockStyle.setOnClickListener(this);
        mIvGaussianBlurStyle.setOnClickListener(this);
        mIvGothamStyle.setOnClickListener(this);
        mIvGrayStyle.setOnClickListener(this);
        mIvHdrStyle.setOnClickListener(this);
        mIvInvertStyle.setOnClickListener(this);
        mIvLightStyle.setOnClickListener(this);
        mIvLomoStyle.setOnClickListener(this);
        mIvMotionBlurStyle.setOnClickListener(this);
        mIvNeonStyle.setOnClickListener(this);
        mIvOilStyle.setOnClickListener(this);
        mIvOldStyle.setOnClickListener(this);
        mIvPixelateStyle.setOnClickListener(this);
        mIvReliefStyle.setOnClickListener(this);
        mIvSharpenStyle.setOnClickListener(this);
        mIvSketchStyle.setOnClickListener(this);
        mIvSoftGlowStyle.setOnClickListener(this);
        mIvTvStyle.setOnClickListener(this);


        mBtChooseMosaic.setOnClickListener(this);
        mBtConfirmMosaic.setOnClickListener(this);
        mBtCancelMosaic.setOnClickListener(this);

        mRectView.setmSelectedListener(new RectView.OnRectSelectedListener() {
            @Override
            public void onRectSelected(Rect r) {

                if (r == null) {
                    return;
                }
                mFinalRect = new Rect();
                mFinalRect.left = RectView.float2int((float) mCurrentBitmap.getWidth() / mIvImage.getWidth() * r.left);
                mFinalRect.top = RectView.float2int((float) mCurrentBitmap.getHeight() / mIvImage.getHeight() * r.top);
                int finalWidth = RectView.float2int((float) mCurrentBitmap.getWidth() / mIvImage.getWidth() * r.width());
                int finalHeight = RectView.float2int((float) mCurrentBitmap.getHeight() / mIvImage.getHeight() * r.height());
                mFinalRect.right = mFinalRect.left + finalWidth;
                mFinalRect.bottom = mFinalRect.top + finalHeight;

                //当点击滤镜时，加载新图片,并使用该滤镜
                //此时有马赛克，那么重新把打马赛克的部分自动打一次马赛克：）
                //todo 图片需要重新计算一下sampleSize
                // mFinalRect.right = mFinalRect.left+100;
                // mFinalRect.bottom = mFinalRect.top+100;

                //Log.e(TAG, mFinalRect + "  -------------final rect---------");
             //   mCurrentBitmap = MosaicProcessor.makeMosaic(mCurrentBitmap, mFinalRect, 10);
              //  mHandler.sendEmptyMessage(10);
               // Log.e(TAG, r + "");
            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_average_blue_style:
                changeStyle(BitmapFilter.AVERAGE_BLUR_STYLE);
                break;
            case R.id.iv_block_style:
                changeStyle(BitmapFilter.BLOCK_STYLE);
                break;
            case R.id.iv_gaussian_blur_style:
                changeStyle(BitmapFilter.GAUSSIAN_BLUR_STYLE);
                break;
            case R.id.iv_gotham_style:
                changeStyle(BitmapFilter.GOTHAM_STYLE);
                break;
            case R.id.iv_gray_style:
                changeStyle(BitmapFilter.GRAY_STYLE);
                break;
            case R.id.iv_hdr_style:
                changeStyle(BitmapFilter.HDR_STYLE);
                break;
            case R.id.iv_invert_style:
                changeStyle(BitmapFilter.INVERT_STYLE);
                break;
            case R.id.iv_light_style:
                changeStyle(BitmapFilter.LIGHT_STYLE);
                break;
            case R.id.iv_lomo_style:
                changeStyle(BitmapFilter.LOMO_STYLE);
                break;
            case R.id.iv_motion_blur_style:
                changeStyle(BitmapFilter.MOTION_BLUR_STYLE);
                break;
            case R.id.iv_neon_style:
                changeStyle(BitmapFilter.NEON_STYLE);
                break;
            case R.id.iv_oil_style:
                changeStyle(BitmapFilter.OIL_STYLE);
                break;
            case R.id.iv_old_style:
                changeStyle(BitmapFilter.OLD_STYLE);
                break;
            case R.id.iv_pixelate_style:
                changeStyle(BitmapFilter.PIXELATE_STYLE);
                break;
            case R.id.iv_relief_style:
                changeStyle(BitmapFilter.RELIEF_STYLE);
                break;
            case R.id.iv_sharpen_style:
                changeStyle(BitmapFilter.SHARPEN_STYLE);
                break;
            case R.id.iv_sketch_style:
                changeStyle(BitmapFilter.SKETCH_STYLE);
                break;
            case R.id.iv_soft_glow_style:
                changeStyle(BitmapFilter.SOFT_GLOW_STYLE);
                break;
            case R.id.iv_tv_style:
                changeStyle(BitmapFilter.TV_STYLE);
                break;
            case R.id.bt_choose_mosaic:
                mRectView.setVisibility(View.VISIBLE);
                break;
            case R.id.bt_confirm_mosaic:
                mosaic();
                break;
            case R.id.bt_cancel_mosaic:
                mRectView.setVisibility(View.GONE);
                break;
        }
    }
    private void mosaic (){
        if(mFinalRect==null){
            return;
        }
        mCurrentBitmap = MosaicProcessor.makeMosaic(mCurrentBitmap, mFinalRect, 10);
        mHandler.sendEmptyMessage(10);
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            mIvImage.setImageBitmap(mCurrentBitmap);

            File file = new File("/mnt/sdcard/filters");
            if (!file.exists()) {
                file.mkdirs();
            }
            try {
                OutputStream out;
                out = new FileOutputStream(new File(file, msg.what + ".png"));
                mCurrentBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            mProgressDialog.dismiss();
        }

    };

    public void changeStyle(final int styleNum) {
        mProgressDialog.show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (mCurrentBitmap != null) {
                    mCurrentBitmap.recycle();
                    mCurrentBitmap = null;
                }
                applyStyle(styleNum);
                /*
                if (mFinalRect != null) {
                    mCurrentBitmap = MosaicProcessor.makeMosaic(mCurrentBitmap, mFinalRect, 10);
                }
                */
                mHandler.sendEmptyMessage(styleNum);
            }
        }).start();
    }

    private void applyStyle(int styleNo) {
        switch (styleNo) {
            case BitmapFilter.AVERAGE_BLUR_STYLE:
                mCurrentBitmap = BitmapFilter.changeStyle(mOriginBitmap, BitmapFilter.AVERAGE_BLUR_STYLE, 5); // maskSize, must odd
                break;
            case BitmapFilter.GAUSSIAN_BLUR_STYLE:
                mCurrentBitmap = BitmapFilter.changeStyle(mOriginBitmap, BitmapFilter.GAUSSIAN_BLUR_STYLE, 1.2); // sigma
                break;
            case BitmapFilter.SOFT_GLOW_STYLE:
                mCurrentBitmap = BitmapFilter.changeStyle(mOriginBitmap, BitmapFilter.SOFT_GLOW_STYLE, 0.6);
                break;
            case BitmapFilter.LIGHT_STYLE:
                int width = mOriginBitmap.getWidth();
                int height = mOriginBitmap.getHeight();
                mCurrentBitmap = BitmapFilter.changeStyle(mOriginBitmap, BitmapFilter.LIGHT_STYLE, width / 3, height / 2, width / 2);
                break;
            case BitmapFilter.LOMO_STYLE:
                mCurrentBitmap = BitmapFilter.changeStyle(mOriginBitmap, BitmapFilter.LOMO_STYLE,
                        (mOriginBitmap.getWidth() / 2.0) * 95 / 100.0);
                break;
            case BitmapFilter.NEON_STYLE:
                mCurrentBitmap = BitmapFilter.changeStyle(mOriginBitmap, BitmapFilter.NEON_STYLE, 200, 100, 50);
                break;
            case BitmapFilter.PIXELATE_STYLE:
                mCurrentBitmap = BitmapFilter.changeStyle(mOriginBitmap, BitmapFilter.PIXELATE_STYLE, 10);
                break;
            case BitmapFilter.MOTION_BLUR_STYLE:
                mCurrentBitmap = BitmapFilter.changeStyle(mOriginBitmap, BitmapFilter.MOTION_BLUR_STYLE, 10, 1);
                break;
            case BitmapFilter.OIL_STYLE:
                mCurrentBitmap = BitmapFilter.changeStyle(mOriginBitmap, BitmapFilter.OIL_STYLE, 5);
                break;
            default:
                mCurrentBitmap = BitmapFilter.changeStyle(mOriginBitmap, styleNo);
                break;
        }
    }
}
