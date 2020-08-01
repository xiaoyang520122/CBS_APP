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
import com.cninsure.cp.utils.DateChoiceUtil;
import com.cninsure.cp.utils.cx.TypePickeUtil;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.zcw.togglebutton.ToggleButton;

import java.util.ArrayList;

public class CxThirdFragment extends Fragment {

    private View contentView,footerView;
    private ListView mlistView;
    private LayoutInflater inflater;
    private MyAdapter myAdapter;
    private CxWorkActivity activity;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.inflater = inflater;
        contentView = inflater.inflate(R.layout.cx_third_fragment,null);
        activity = (CxWorkActivity) getActivity();
        initData();
        initView();

        return contentView;
    }

    /**初始化基础数据*/
    private void initData() {
        if (activity.cxWorkEntity!=null && activity.cxWorkEntity.thirdPartys!=null){

        }else{
            activity.cxWorkEntity.thirdPartys  = new ArrayList<>();
            activity.cxWorkEntity.thirdPartys.add(new CxWorkEntity.ThirdPartyEntity());
        }
    }

    private void initView() {
        mlistView = contentView.findViewById(R.id.ct_listview);
        myAdapter = new MyAdapter();
        mlistView.setAdapter(myAdapter);
        mlistView.addFooterView(getFooterView());
    }

    private View getFooterView() {
        footerView = inflater.inflate(R.layout.add_view_buttom,null);
        footerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.cxWorkEntity.thirdPartys.add(new CxWorkEntity.ThirdPartyEntity());
                myAdapter.notifyDataSetChanged();
            }
        });
        return footerView;
    }



    private class MyAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return activity.cxWorkEntity.thirdPartys.size();
        }

        @Override
        public Object getItem(int position) {
            return activity.cxWorkEntity.thirdPartys.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder vHolder;
            if (convertView==null){
                convertView = inflater.inflate(R.layout.cx_third_fragment_item,null);
                vHolder = new ViewHolder();
                ViewUtils.inject(vHolder, convertView);
                convertView.setTag(vHolder);
            }else{
                vHolder = (ViewHolder) convertView.getTag();
            }
            vHolder.thirdPartysTv.setText("三者"+(position+1));
            setDeleteOnclick(position,convertView); //移除三者方法
            setPickeOnclick(vHolder);
            displayInfo(vHolder);
            return convertView;
        }

        /**显示内容*/
        private void displayInfo(ViewHolder vHolder) {
        }

        /**给组件绑定监听事件*/
        private void setPickeOnclick(ViewHolder vHolder){
            //绑定日期选择
            DateChoiceUtil.setShortDatePickerDialog(activity,vHolder.szCarRegisterDateTv);
            DateChoiceUtil.setShortDatePickerDialog(activity,vHolder.szCarEffectiveDateTv);
            DateChoiceUtil.setShortDatePickerDialog(activity,vHolder.szDriverRegisterDateTv);
            DateChoiceUtil.setShortDatePickerDialog(activity,vHolder.szDriverEffectiveStarTv);
            DateChoiceUtil.setShortDatePickerDialog(activity,vHolder.szDriverEffectiveEndTv);
            //绑定类型选择
            TypePickeUtil.setTypePickerDialog(activity,vHolder.szCarNumberTypeTv,activity.cxSurveyDict,"carno_type");
            TypePickeUtil.setTypePickerDialog(activity,vHolder.szCarUseTypeTv,activity.cxSurveyDict,"car_usetype");
        }

        /***
         * 删除三者信息的监听器
         * @param position
         * @param convertView
         */
        private void setDeleteOnclick(int position, View convertView) {
            if(position>0){
                convertView.findViewById(R.id.cti_delete).setVisibility(View.VISIBLE);
                convertView.findViewById(R.id.cti_delete).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        activity.cxWorkEntity.thirdPartys.remove(position);
                        myAdapter.notifyDataSetChanged();
                    }
                });
            }else{
                convertView.findViewById(R.id.cti_delete).setVisibility(View.GONE);
            }
        }
    }

    /** 创建 ViewHolder */
    class ViewHolder{
        @ViewInject(R.id.cti_thirdPartysNo)  private TextView thirdPartysTv;
        @ViewInject(R.id.cti_szisLicenseKou)  private ToggleButton szisLicenseKouTg;
        @ViewInject(R.id.cti_pathMoveLicense)  private TextView pathMoveLicenseTv;
        @ViewInject(R.id.cti_pathDriverLicense)  private TextView pathDriverLicenseTv;
        @ViewInject(R.id.cti_carNumber)  private EditText carNumberEdt;
        @ViewInject(R.id.cti_frameNumber)  private EditText frameNumberEdt;
        @ViewInject(R.id.cti_engineNumber)  private EditText engineNumberEdt;
        @ViewInject(R.id.cti_szCarRegisterDate)  private TextView szCarRegisterDateTv;
        @ViewInject(R.id.cti_szCarEffectiveDate)  private TextView szCarEffectiveDateTv;
        @ViewInject(R.id.cti_szCarNumberType)  private TextView szCarNumberTypeTv;
        @ViewInject(R.id.cti_szCarUseType)  private TextView szCarUseTypeTv;
        @ViewInject(R.id.cti_driverLicense)  private TextView driverLicenseTv;
        @ViewInject(R.id.cti_drivingMode)  private TextView drivingModeTv;
        @ViewInject(R.id.cti_carPerson)  private TextView carPersonTv;
        @ViewInject(R.id.cti_carPersonPhone)  private TextView carPersonPhoneTv;
        @ViewInject(R.id.cti_szDriverRegisterDate)  private TextView szDriverRegisterDateTv;
        @ViewInject(R.id.cti_szDriverEffectiveStar)  private TextView szDriverEffectiveStarTv;
        @ViewInject(R.id.cti_szDriverEffectiveEnd)  private TextView szDriverEffectiveEndTv;
    }
}
