package com.cninsure.cp.cx.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.cninsure.cp.R;
import com.cninsure.cp.cx.CxSurveyWorkActivity;
import com.cninsure.cp.entity.cx.CxSurveyWorkEntity;
import com.cninsure.cp.utils.SetTextUtil;
import com.cninsure.cp.utils.cx.TypePickeUtil;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import java.util.ArrayList;

public class CxInjuredFragment extends BaseFragment {

    private View contentView ,footerView;
    private ListView mlistView;
    private LayoutInflater inflater;
    private MyAdapter myAdapter;
    private CxSurveyWorkActivity activity;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.inflater = inflater;
        contentView = inflater.inflate(R.layout.cx_injured_fragment,null);
        activity = (CxSurveyWorkActivity) getActivity();
        initData();
        initView();

        return contentView;
    }

    private void initData() {
        if (activity.cxWorkEntity.injuredInfos == null) {
            activity.cxWorkEntity.injuredInfos = new ArrayList<>();
            activity.cxWorkEntity.injuredInfos.add(new CxSurveyWorkEntity.InjuredInfosEntity());
        }
    }


    private void initView() {
        mlistView = contentView.findViewById(R.id.ci_listview);
        myAdapter = new MyAdapter();
        mlistView.setAdapter(myAdapter);
        mlistView.addFooterView(getFooterView());
    }

    private View getFooterView() {
        footerView = inflater.inflate(R.layout.add_view_buttom,null);
        footerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SaveDataToEntity(); //先把已经填写好的数据存起来
                activity.cxWorkEntity.injuredInfos.add(new CxSurveyWorkEntity.InjuredInfosEntity());
                myAdapter.notifyDataSetChanged();
            }
        });
        return footerView;
    }

    @Override
    public void onPause() {
        super.onPause();
        SaveDataToEntity();
    }

    /**保存数据到实体类*/
    @Override
    public void SaveDataToEntity() {
        if (activity==null || mlistView==null) return;
        for (int i= 0;i<activity.cxWorkEntity.injuredInfos.size();i++){
            if (mlistView.getChildAt(i) == null) continue;
            ViewHolder vHolder = (ViewHolder) mlistView.getChildAt(i).getTag();
            getHolderDate(vHolder,i);
        }
    }

    /**获取ViewHolder中控件上的数据，封装到ThirdPartyEntity对象中*/
    private void getHolderDate(ViewHolder vHolder, int position) {
        CxSurveyWorkEntity.InjuredInfosEntity tempinjured = activity.cxWorkEntity.injuredInfos.get(position);
        tempinjured.injuredInfoNo = position;
        tempinjured.injuredName = vHolder.injuredNameEdt.getText().toString();//	姓名
        tempinjured.injuredCarNo = vHolder.injuredCarNoEdt.getText().toString();//	身份证号
        tempinjured.injuredPhone = vHolder.injuredPhoneEdt.getText().toString();//	伤者电话
        tempinjured.injuredType = TypePickeUtil.getValue(vHolder.injuredTypeTv.getText().toString(),activity.cxSurveyDict,"injured_type"); //	伤者类型	0本车司机、1本车乘客、2三者车内人伤、3其他三者人员
        //	是否选择快赔	1是，0否
        switch (vHolder.isQuickPaidRg.getCheckedRadioButtonId()){
            case R.id.cii_isQuickPaid_rb_T:  tempinjured.isQuickPaid = 1;break;
            case R.id.cii_isQuickPaid_rb_F:  tempinjured.isQuickPaid = 0;
        }
    }


    private class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return activity.cxWorkEntity.injuredInfos.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder vHolder;
            if (convertView==null){
                convertView = inflater.inflate(R.layout.cx_injured_fragment_item,null);
                vHolder = new ViewHolder();
                ViewUtils.inject(vHolder, convertView);
                convertView.setTag(vHolder);
            }else{
                vHolder = (ViewHolder) convertView.getTag();
            }
            vHolder.injuredInfoNoTv.setText("伤者"+(position+1));
            setDeleteOnclick(position,convertView); //移除伤者方法
            setPickeOnclick(vHolder);
            displayInfo(vHolder,position);
            return convertView;
        }
        /**显示内容*/
        private void displayInfo(ViewHolder vHolder, int position) {
            if (!(activity.cxWorkEntity.injuredInfos!=null && activity.cxWorkEntity.injuredInfos.size()>0))  //没有数据就跳过，以免报错
                return;
            CxSurveyWorkEntity.InjuredInfosEntity injuredEnt = activity.cxWorkEntity.injuredInfos.get(position);
            SetTextUtil.setEditText(vHolder.injuredNameEdt,injuredEnt.injuredName);
            SetTextUtil.setEditText(vHolder.injuredCarNoEdt,injuredEnt.injuredCarNo);
            SetTextUtil.setEditText(vHolder.injuredPhoneEdt,injuredEnt.injuredPhone);
            SetTextUtil.setTvTextForArr(vHolder.injuredTypeTv,TypePickeUtil.getDictLabelArr(activity.cxSurveyDict.getDictByType("injured_type")),injuredEnt.injuredType);
            //是否快赔
            if (injuredEnt.isQuickPaid==1) vHolder.isQuickPaidRg.check(R.id.cii_isQuickPaid_rb_T);
            if (injuredEnt.isQuickPaid==0) vHolder.isQuickPaidRg.check(R.id.cii_isQuickPaid_rb_F);
            if (injuredEnt.isQuickPaid==-1) vHolder.isQuickPaidRg.check(R.id.cii_isQuickPaid_rb_F);

            vHolder.isQuickPaidRg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {

                }
            });
        }

        /**给组件绑定监听事件*/
        private void setPickeOnclick(ViewHolder vHolder){
            //绑定类型选择
            TypePickeUtil.setTypePickerDialog(activity,vHolder.injuredTypeTv,activity.cxSurveyDict,"injured_type");
        }

        /***
         * 删除人伤信息的监听器
         * @param position
         * @param convertView
         */
        private void setDeleteOnclick(int position, View convertView) {
            if(position>0){
                convertView.findViewById(R.id.cii_delete).setVisibility(View.VISIBLE);
                convertView.findViewById(R.id.cii_delete).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SaveDataToEntity(); //先把已经填写好的数据存起来
                        activity.cxWorkEntity.injuredInfos.remove(position);
                        myAdapter.notifyDataSetChanged();
                    }
                });
            }else{
                convertView.findViewById(R.id.cii_delete).setVisibility(View.GONE);
            }
        }
    }

    /** 创建 ViewHolder */
    class ViewHolder{
        @ViewInject(R.id.cii_injuredInfoNo)  private TextView injuredInfoNoTv;
        @ViewInject(R.id.cii_injuredName)  private EditText injuredNameEdt;
        @ViewInject(R.id.cii_injuredCarNo)  private EditText injuredCarNoEdt;
        @ViewInject(R.id.cii_injuredPhone)  private EditText injuredPhoneEdt;
        @ViewInject(R.id.cii_injuredType)  private TextView injuredTypeTv;
        @ViewInject(R.id.cii_radiogroup_isQuickPaid)  private RadioGroup isQuickPaidRg;
    }
}
