package com.example.scancode.activity;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.scancode.R;
import com.example.scancode.defineview.MyImageView;
import com.example.scancode.utils.Constant;
import com.example.scancode.zxing.ScanListener;
import com.example.scancode.zxing.ScanManager;
import com.example.scancode.zxing.decode.DecodeThread;
import com.example.scancode.zxing.decode.Utils;
import com.google.zxing.Result;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CommonScanActivity extends AppCompatActivity implements ScanListener {
    static final String TAG = CommonScanActivity.class.getSimpleName();
    final int PHOTOREQUESTCODE = 1111;
    @BindView(R.id.capture_preview)
    SurfaceView mCapturePreview;
    @BindView(R.id.authorize_return)
    ImageView mAuthorizeReturn;
    @BindView(R.id.common_title_TV_center)
    TextView title;
    @BindView(R.id.title_bar)
    RelativeLayout mTitleBar;
    @BindView(R.id.tv_scan_result)
    TextView tv_scan_result;
    @BindView(R.id.top_mask)
    RelativeLayout mTopMask;
    @BindView(R.id.scan_hint)
    TextView scan_hint;
    @BindView(R.id.iv_light)
    TextView mIvLight;
    @BindView(R.id.qrcode_ic_back)
    TextView mQrcodeIcBack;
    @BindView(R.id.qrcode_g_gallery)
    TextView mQrcodeGGallery;
    @BindView(R.id.service_register_rescan)
    Button mServiceRegisterRescan;
    @BindView(R.id.bottom_mask)
    RelativeLayout mBottomMask;
    @BindView(R.id.left_mask)
    ImageView mLeftMask;
    @BindView(R.id.right_mask)
    ImageView mRightMask;
    @BindView(R.id.capture_scan_line)
    ImageView mCaptureScanLine;
    @BindView(R.id.scan_image)
    MyImageView mScanImage;
    @BindView(R.id.capture_crop_view)
    RelativeLayout mCaptureCropView;
    @BindView(R.id.capture_container)
    RelativeLayout mCaptureContainer;
    private int scanMode;//扫描模型（条形，二维码，全部）
    ScanManager scanManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_common_scan);
        ButterKnife.bind(this);
        scanMode = getIntent().getIntExtra(Constant.REQUEST_SCAN_MODE, Constant.REQUEST_SCAN_MODE_ALL_MODE);
        initView();
    }

    private void initView() {
        switch (scanMode) {
            case DecodeThread.BARCODE_MODE:
                title.setText(R.string.scan_barcode_title);
                scan_hint.setText(R.string.scan_barcode_hint);
                break;
            case DecodeThread.QRCODE_MODE:
                title.setText(R.string.scan_qrcode_title);
                scan_hint.setText(R.string.scan_qrcode_hint);
                break;
            case DecodeThread.ALL_MODE:
                title.setText(R.string.scan_allcode_title);
                scan_hint.setText(R.string.scan_allcode_hint);
                break;
        }
        //构造出扫描管理器
        scanManager = new ScanManager(this, mCapturePreview, mCaptureContainer, mCaptureCropView, mCaptureScanLine, scanMode, this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        scanManager.onResume();
        mServiceRegisterRescan.setVisibility(View.INVISIBLE);
        mScanImage.setVisibility(View.GONE);
    }

    @Override
    protected void onPause() {
        super.onPause();
        scanManager.onPause();
    }

    @Override
    public void scanResult(Result rawResult, Bundle bundle) {
        //扫描成功后，扫描器不会再连续扫描，如需连续扫描，调用reScan()方法。
        //scanManager.reScan();
//		Toast.makeText(that, "result="+rawResult.getText(), Toast.LENGTH_LONG).show();
        if (!scanManager.isScanning()) { //如果当前不是在扫描状态
            //设置再次扫描按钮出现
            mServiceRegisterRescan.setVisibility(View.VISIBLE);
            mScanImage.setVisibility(View.VISIBLE);
            Bitmap barcode = null;
            byte[] compressedBitmap = bundle.getByteArray(DecodeThread.BARCODE_BITMAP);
            if (compressedBitmap != null) {
                barcode = BitmapFactory.decodeByteArray(compressedBitmap, 0, compressedBitmap.length, null);
                barcode = barcode.copy(Bitmap.Config.ARGB_8888, true);
            }
            mScanImage.setImageBitmap(barcode);
        }
        mServiceRegisterRescan.setVisibility(View.VISIBLE);
        mScanImage.setVisibility(View.VISIBLE);
        tv_scan_result.setVisibility(View.VISIBLE);
        tv_scan_result.setText("结果：" + rawResult.getText());
    }

    void startScan() {
        if (mServiceRegisterRescan.getVisibility() == View.VISIBLE) {
            mServiceRegisterRescan.setVisibility(View.INVISIBLE);
            mScanImage.setVisibility(View.GONE);
            scanManager.reScan();
        }
    }

    @Override
    public void scanError(Exception e) {
        Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        //相机扫描出错时
        if (e.getMessage() != null && e.getMessage().startsWith("相机")) {
            mCapturePreview.setVisibility(View.INVISIBLE);
        }
    }

    public void showPictures(int requestCode) {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String photo_path;
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PHOTOREQUESTCODE:
                    String[] proj = {MediaStore.Images.Media.DATA};
                    Cursor cursor = this.getContentResolver().query(data.getData(), proj, null, null, null);
                    if (cursor.moveToFirst()) {
                        int colum_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                        photo_path = cursor.getString(colum_index);
                        if (photo_path == null) {
                            photo_path = Utils.getPath(getApplicationContext(), data.getData());
                        }
                        scanManager.scanningImage(photo_path);
                    }
            }
        }
    }

    @OnClick({R.id.authorize_return, R.id.iv_light, R.id.qrcode_ic_back, R.id.qrcode_g_gallery, R.id.service_register_rescan})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.authorize_return:
                finish();
                break;
            case R.id.iv_light:
                scanManager.switchLight();
                break;
            case R.id.qrcode_ic_back:
                finish();
                break;
            case R.id.qrcode_g_gallery:
                showPictures(PHOTOREQUESTCODE);
                break;
            case R.id.service_register_rescan://再次开启扫描
                startScan();
                break;
        }
    }
}
