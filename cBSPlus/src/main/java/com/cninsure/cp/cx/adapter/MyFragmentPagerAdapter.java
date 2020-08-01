package com.cninsure.cp.cx.adapter;


import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.PagerAdapter;

import java.util.Map;

public class MyFragmentPagerAdapter extends FragmentStatePagerAdapter {

    private Map<Integer, Fragment> fragmentMap;

    public MyFragmentPagerAdapter(FragmentManager fm, Map<Integer, Fragment> fragmentMap) {
        super(fm);
        this.fragmentMap = fragmentMap;
    }


    @Override
    public Fragment getItem(int position) {
        int i =0;
        for (Map.Entry<Integer,Fragment> me: fragmentMap.entrySet()){
            if (i==position){
                return me.getValue();
            }
            i++;
        }
        return fragmentMap.get(1);
    }

    @Override
    public int getCount() {
        return fragmentMap.size();
    }

    /**
     * 使用这个方式，让页面不缓存，能够在清除fragment的时候对其做了删除
     * @param object
     * @return
     */
    @Override
    public int getItemPosition(Object object) {
        return PagerAdapter.POSITION_NONE;
    }
}