package com.cninsure.cp.cx;

import android.os.Bundle;
import android.view.Window;

import androidx.viewpager.widget.ViewPager;

import com.cninsure.cp.BaseActivity;
import com.cninsure.cp.R;
import com.google.android.material.tabs.TabLayout;

public class CxWorkActivity extends BaseActivity {

    private TabLayout mTabLayout;
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.cx_work_activity);
        initView();
    }

    private void initView() {
        mViewPager = findViewById(R.id.cxworkA_viewpager);
        mTabLayout = findViewById(R.id.cxworkA_tablayout);
        initTab();
    }

    private void initTab() {
        TabLayout.Tab tabOne;

        tabOne = mTabLayout.newTab().setText("标的信息");
        mTabLayout.addTab(mTabLayout.newTab().setText("标的信息"));
        mTabLayout.addTab(mTabLayout.newTab().setText("查勘信息"));
        mTabLayout. setSelectedTabIndicatorColor(getResources().getColor(R.color.bulue_main));
        mTabLayout.setupWithViewPager(mViewPager);
    }
}
