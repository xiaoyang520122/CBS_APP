package com.cninsure.cp.cx.adapter;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.viewpager.widget.PagerAdapter;

import com.cninsure.cp.R;

public class MyPagerAdapter extends PagerAdapter {

    private String[] mainTitlesArray;
    private Context context;

    public MyPagerAdapter(Context context , String[] mainTitlesArray){
        this.mainTitlesArray = mainTitlesArray;
        this.context = context;
    }

    @Override
    public int getCount() {
        return mainTitlesArray.length;
    }

    //判断是否是否为同一张图片，这里返回方法中的两个参数做比较就可以
    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view==object;
    }
    //设置viewpage内部东西的方法，如果viewpage内没有子空间滑动产生不了动画效果
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        TextView textView = new TextView(context);
        textView.setText(mainTitlesArray[position]);
        textView.setTextColor(context.getResources().getColor(R.color.bulue_main));
        textView.setTextSize(35);
        textView.setGravity(Gravity.CENTER);
        container.addView(textView);
        //最后要返回的是控件本身
        return textView;
    }
    //因为它默认是看三张图片，第四张图片的时候就会报错，还有就是不要返回父类的作用
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
        //         super.destroyItem(container, position, object);
    }
    //目的是展示title上的文字，
    @Override
    public CharSequence getPageTitle(int position) {
        return mainTitlesArray[position];
    }
}