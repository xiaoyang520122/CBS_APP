package com.cninsure.cp.cx.adapter;


import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.PagerAdapter;

import com.cninsure.cp.cx.fragment.BaseFragment;

import java.util.Map;

public class NoRefreshFragmentPagerAdapter extends FragmentPagerAdapter {

    private Map<Integer, BaseFragment> fragmentMap;

    public NoRefreshFragmentPagerAdapter(FragmentManager fm, Map<Integer, BaseFragment> fragmentMap) {
        super(fm);
        this.fragmentMap = fragmentMap;
    }


    @Override
    public Fragment getItem(int position) {
        return fragmentMap.get(position);
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
//    @Override
//    public int getItemPosition(Object object) {
//        return PagerAdapter.POSITION_NONE;//清空，重新加载
//        if (mCurrentFragment==fragmentMap.get(0) || mCurrentFragment==fragmentMap.get(1)) { //清空，重新加载
//            return PagerAdapter.POSITION_UNCHANGED;
//        }else{//不清空
//            return PagerAdapter.POSITION_NONE;
//        }
//    }
}