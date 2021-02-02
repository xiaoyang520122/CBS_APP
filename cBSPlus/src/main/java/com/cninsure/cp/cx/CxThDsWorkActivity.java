package com.cninsure.cp.cx;

import android.os.Bundle;
import android.view.Window;

import com.cninsure.cp.BaseActivity;
import com.cninsure.cp.R;

/**
 * @author :xy-wm
 * date:2021/2/1 9:09
 * usefuLness: CBS_APP
 */
public class CxThDsWorkActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.account_activity);
    }
}
