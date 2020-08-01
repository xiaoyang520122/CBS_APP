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
import androidx.fragment.app.Fragment;

import com.cninsure.cp.R;
import com.cninsure.cp.cx.CxWorkActivity;
import com.cninsure.cp.entity.cx.CxWorkEntity;
import com.cninsure.cp.utils.cx.TypePickeUtil;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.zcw.togglebutton.ToggleButton;

import java.util.ArrayList;
import java.util.List;

public class CxDamageFragment extends Fragment {

    private View contentView ,footerView;
    private LayoutInflater inflater;
    private ListView mlistView;
    private MyAdapter adapter;
    public List<CxWorkEntity.DamageInfosEntity> DamageInfos; //物损信息
    private CxWorkActivity activity;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.inflater = inflater;
        contentView =inflater.inflate(R.layout.cx_damage_fragment,null);
        activity = (CxWorkActivity) getActivity();
        initData();
        initView();
        return contentView;
    }

    /**初始化数据*/
    private void initData() {
        DamageInfos  = new ArrayList<>();
        DamageInfos.add(new CxWorkEntity.DamageInfosEntity());
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
                DamageInfos.add(new CxWorkEntity.DamageInfosEntity());
                adapter.notifyDataSetChanged();
            }
        });
        return footerView;
    }

    private class MyAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return DamageInfos.size();
        }

        @Override
        public Object getItem(int position) {
            return DamageInfos.get(position);
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
            return convertView;
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
                        DamageInfos.remove(position);
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
