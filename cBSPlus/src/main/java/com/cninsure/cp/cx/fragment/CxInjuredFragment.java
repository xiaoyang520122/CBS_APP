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

import java.util.ArrayList;
import java.util.List;

public class CxInjuredFragment extends Fragment {

    private View contentView ,footerView;
    private ListView mlistView;
    private LayoutInflater inflater;
    private MyAdapter myAdapter;
    public List<CxWorkEntity.InjuredInfosEntity> injuredInfos; //人伤信息
    private CxWorkActivity activity;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.inflater = inflater;
        contentView = inflater.inflate(R.layout.cx_injured_fragment,null);
        activity = (CxWorkActivity) getActivity();
        initData();
        initView();

        return contentView;
    }

    private void initData() {
        injuredInfos  = new ArrayList<>();
        injuredInfos.add(new CxWorkEntity.InjuredInfosEntity());
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
                injuredInfos.add(new CxWorkEntity.InjuredInfosEntity());
                myAdapter.notifyDataSetChanged();
            }
        });
        return footerView;
    }


    private class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return injuredInfos.size();
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
            return convertView;
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
                        injuredInfos.remove(position);
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
    }
}
