package com.cninsure.cp.cx.jiebaoanfragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.cninsure.cp.R;
import com.cninsure.cp.cx.CxJieBaoanInfoActivity;
import com.cninsure.cp.cx.fragment.BaseFragment;
import com.cninsure.cp.entity.cx.CxBaoanTable;
import com.cninsure.cp.entity.cx.JieBaoanEntity;

public class TaskBasicInfoFragment extends BaseFragment {



    private View contentView ,footerView;
    private ListView mlistView;
    private LayoutInflater inflater;
    //    private MyAdapter myAdapter;
    private CxJieBaoanInfoActivity activity;

    private JieBaoanEntity baoanInfo; //接报案信息


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.inflater = inflater;
        contentView = inflater.inflate(R.layout.cx_baoan_basic_info_fragment,null);
        activity = (CxJieBaoanInfoActivity) getActivity();
//        downloadBaoanInfo();
//        initView();

        return contentView;
    }

    @Override
    public void SaveDataToEntity() {

    }
}
