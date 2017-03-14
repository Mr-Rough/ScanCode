package com.example.scancode;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.example.scancode.activity.CommonScanActivity;
import com.example.scancode.activity.CreateCodeActivity;
import com.example.scancode.utils.Constant;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.create_code)
    Button mCreateCode;
    @BindView(R.id.scan_2code)
    Button mScan2code;
    @BindView(R.id.scan_bar_code)
    Button mScanBarCode;
    @BindView(R.id.scan_code)
    Button mScanCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        int mode = getIntent().getIntExtra(Constant.REQUEST_SCAN_MODE, Constant.REQUEST_SCAN_MODE_ALL_MODE);
    }

    @OnClick({R.id.create_code, R.id.scan_2code, R.id.scan_bar_code, R.id.scan_code})
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.create_code://生成码
                intent = new Intent(this, CreateCodeActivity.class);
                startActivity(intent);
                break;
            case R.id.scan_2code://扫描二维码
                intent = new Intent(this, CommonScanActivity.class);
                intent.putExtra(Constant.REQUEST_SCAN_MODE, Constant.REQUEST_SCAN_MODE_QRCODE_MODE);
                startActivity(intent);
                break;
            case R.id.scan_bar_code://扫描条形码
                intent = new Intent(this, CommonScanActivity.class);
                intent.putExtra(Constant.REQUEST_SCAN_MODE, Constant.REQUEST_SCAN_MODE_BARCODE_MODE);
                startActivity(intent);
                break;
            case R.id.scan_code://扫描条形码或者二维码
                intent = new Intent(this, CommonScanActivity.class);
                intent.putExtra(Constant.REQUEST_SCAN_MODE, Constant.REQUEST_SCAN_MODE_ALL_MODE);
                startActivity(intent);
                break;
        }
    }
}
