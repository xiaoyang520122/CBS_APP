package com.cninsure.cp.cx.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
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

public class CxDamageFragment extends BaseFragment {

    private View contentView ,footerView;
    private LayoutInflater inflater;
    private ListView mlistView;
    private MyAdapter adapter;
    private CxSurveyWorkActivity activity;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.inflater = inflater;
        contentView =inflater.inflate(R.layout.cx_damage_fragment,null);
        activity = (CxSurveyWorkActivity) getActivity();
        initData();
        initView();
        return contentView;
    }

    /**初始化数据*/
    private void initData() {
        if (activity.cxWorkEntity.damageInfos == null) {
            activity.cxWorkEntity.damageInfos = new ArrayList<>();
            activity.cxWorkEntity.damageInfos.add(new CxSurveyWorkEntity.DamageInfosEntity());
        }
    }

    private void initView() {
        mlistView = contentView.findViewById(R.id.cd_listview);
        adapter = new MyAdapter();
        mlistView.setAdapter(adapter);
        mlistView.addFooterView(getFooterView());
    }


    private View getFooterView() {
        footerView = inflater.inflate(R.layout.add_view_buttom,null);
        footerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SaveDataToEntity(); //先把已经填写好的数据存起来
                activity.cxWorkEntity.damageInfos.add(new CxSurveyWorkEntity.DamageInfosEntity());
                adapter.notifyDataSetChanged();
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
        if (activity==null) return;
        for (int i= 0;i<activity.cxWorkEntity.damageInfos.size();i++){
            ViewHolder vHolder = (ViewHolder) mlistView.getChildAt(i).getTag();
            getHolderDate(vHolder,i);
        }
    }

    private void getHolderDate(ViewHolder vHolder, int position) {
        CxSurveyWorkEntity.DamageInfosEntity tempDamage = activity.cxWorkEntity.damageInfos.get(position);
        tempDamage.damageNo = position; //编号
        tempDamage.damageOwner = vHolder.damageOwnerEdt.getText().toString(); //	归属人
        tempDamage.damageObjectName = vHolder.damageObjectNameEdt.getText().toString();
        tempDamage.damageOwnerPhone = vHolder.damageOwnerPhoneEdt.getText().toString();
        tempDamage.damageType = TypePickeUtil.getValue(vHolder.damageTypeTv.getText().toString(),activity.cxSurveyDict,"damage_loss_type");
    }

    private class MyAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return activity.cxWorkEntity.damageInfos.size();
        }

        @Override
        public Object getItem(int position) {
            return activity.cxWorkEntity.damageInfos.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder vHolder;
            if (convertView==null){
                convertView = inflater.inflate(R.layout.cx_damage_fragment_item,null);
                vHolder = new ViewHolder();
                ViewUtils.inject(vHolder, convertView);
                convertView.setTag(vHolder);
            }else{
                vHolder = (ViewHolder) convertView.getTag();
            }
            vHolder.damageNoTv.setText("物损"+(position+1));
            setDeleteOnclick(position,convertView); //移除物损方法
            TypePickeUtil.setTypePickerDialog(activity,vHolder.damageTypeTv,activity.cxSurveyDict,"damage_loss_type");
            displayInfo(vHolder,position);
            return convertView;
        }

        private void displayInfo(ViewHolder vHolder, int position) {
            CxSurveyWorkEntity.DamageInfosEntity tempDamage = activity.cxWorkEntity.damageInfos.get(position);
            SetTextUtil.setEditText(vHolder.damageOwnerEdt,tempDamage.damageOwner); //	归属人
            SetTextUtil.setEditText(vHolder.damageObjectNameEdt,tempDamage.damageObjectName); //
            SetTextUtil.setEditText(vHolder.damageOwnerPhoneEdt,tempDamage.damageOwnerPhone); //
            SetTextUtil.setTvTextForArr(vHolder.damageTypeTv,TypePickeUtil.getDictLabelArr(activity.cxSurveyDict.getDictByType("damage_loss_type")),tempDamage.damageType);
        }

        /***
         * 删除三者信息的监听器
         * @param position
         * @param convertView
         */
        private void setDeleteOnclick(int position, View convertView) {
            if(position>0){
                convertView.findViewById(R.id.cd_delete).setVisibility(View.VISIBLE);
                convertView.findViewById(R.id.cd_delete).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SaveDataToEntity(); //先把已经填写好的数据存起来
                        activity.cxWorkEntity.damageInfos.remove(position);
                        adapter.notifyDataSetChanged();
                    }
                });
            }else{
                convertView.findViewById(R.id.cd_delete).setVisibility(View.GONE);
            }
        }
    }

    /** 创建 ViewHolder */
    class ViewHolder{
        @ViewInject(R.id.cd_damageNo)  private TextView damageNoTv;
        @ViewInject(R.id.cd_damageOwner)  private EditText damageOwnerEdt;
        @ViewInject(R.id.cd_damageObjectName)  private EditText damageObjectNameEdt;
        @ViewInject(R.id.cd_damageOwnerPhone)  private EditText damageOwnerPhoneEdt;
        @ViewInject(R.id.cd_damageType)  private TextView damageTypeTv;
    }
}
