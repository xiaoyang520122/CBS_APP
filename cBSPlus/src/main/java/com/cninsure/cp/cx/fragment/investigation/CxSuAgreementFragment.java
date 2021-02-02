package com.cninsure.cp.cx.fragment.investigation;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.cninsure.cp.R;
import com.cninsure.cp.cx.CxInjuryExamineActivity;
import com.cninsure.cp.cx.fragment.BaseFragment;
import com.lidroid.xutils.ViewUtils;

public class CxSuAgreementFragment extends BaseFragment {

    private View contentView;
    private CxInjuryExamineActivity activity;
    /**签字标识 0甲方签字，1乙方签字*/
    public int signFlag; //签字标识 0甲方签字，1乙方签字


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        contentView = inflater.inflate(R.layout.cxrs_injury_mediate_agreement_fragment, null);
        activity = (CxInjuryExamineActivity) getActivity();
        ViewUtils.inject(this, contentView); //注入view和事件
        initView();
        return contentView;
    }

    private void initView() {
//        poSign.setOnClickListener(this);
//        pbSign.setOnClickListener(this);
//        poSignImg.setOnClickListener(this);
//        pbSignImg.setOnClickListener(this);

    }

    @Override
    public void SaveDataToEntity() {
        if (activity==null) return;  //activity说明没有初始化这个Fragment，也就没有任何操作，没有不要保存了，

    }
    }

