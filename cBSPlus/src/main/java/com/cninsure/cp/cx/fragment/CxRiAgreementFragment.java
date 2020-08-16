package com.cninsure.cp.cx.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.cninsure.cp.AppApplication;
import com.cninsure.cp.R;
import com.cninsure.cp.cx.CxInjuryMediateActivity;
import com.cninsure.cp.entity.cx.InjuryMediateWorkEntity;
import com.cninsure.cp.ocr.LinePathActivity;
import com.cninsure.cp.utils.HttpRequestTool;
import com.cninsure.cp.utils.SetTextUtil;
import com.cninsure.cp.utils.cx.TypePickeUtil;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

public class CxRiAgreementFragment  extends BaseFragment {

    private View contentView;
    private CxInjuryMediateActivity activity;
    /**签字标识 0甲方签字，1乙方签字*/
    public int signFlag; //签字标识 0甲方签字，1乙方签字


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        contentView = inflater.inflate(R.layout.cxrs_injury_mediate_agreement_fragment, null);
        activity = (CxInjuryMediateActivity) getActivity();
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

