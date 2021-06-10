package com.cninsure.cp.cx.adapter;


import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.PagerAdapter;

import com.cninsure.cp.cx.fragment.BaseFragment;

import java.util.Map;

public class MyFragmentPagerAdapter extends FragmentStatePagerAdapter {

    private Map<Integer, BaseFragment> fragmentMap;
    private BaseFragment mCurrentFragment;

    public MyFragmentPagerAdapter(FragmentManager fm, Map<Integer, BaseFragment> fragmentMap) {
        super(fm);
        this.fragmentMap = fragmentMap;
    }


    @Override
    public Fragment getItem(int position) {
        int i =0;
        for (Map.Entry<Integer,BaseFragment> me: fragmentMap.entrySet()){
            if (i==position){
                return me.getValue();
            }
            i++;
        }
        mCurrentFragment = fragmentMap.get(1);
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
        //return PagerAdapter.POSITION_NONE;//清空，重新加载
        if (mCurrentFragment==fragmentMap.get(0) || mCurrentFragment==fragmentMap.get(1)) { //清空，重新加载
            return PagerAdapter.POSITION_UNCHANGED;
        }else{//不清空
            return PagerAdapter.POSITION_NONE;
        }
    }
}