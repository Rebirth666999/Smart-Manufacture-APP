// PortraitCaptureActivity.java
package com.pzy.smart_manufacture_app;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.journeyapps.barcodescanner.CaptureActivity;

public class PortraitCaptureActivity extends CaptureActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 添加自定义扫描UI
        View scannerView = getLayoutInflater().inflate(R.layout.scanner_layout, null);
        ImageView scannerLine = scannerView.findViewById(R.id.scanner_line);
        addContentView(scannerView, new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
        ));

        // 实现扫描线动画
        ObjectAnimator animator = ObjectAnimator.ofFloat(scannerLine, "translationY", 0, 800);
        animator.setDuration(1500);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.start();
    }
}