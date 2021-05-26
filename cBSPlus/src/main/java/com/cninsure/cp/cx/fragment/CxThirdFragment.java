package com.cninsure.cp.cx.fragment;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.cninsure.cp.R;
import com.cninsure.cp.cx.CxSurveyWorkActivity;
import com.cninsure.cp.entity.OCREntity;
import com.cninsure.cp.entity.cx.CxSurveyWorkEntity;
import com.cninsure.cp.entity.cx.DictData;
import com.cninsure.cp.utils.DateChoiceUtil;
import com.cninsure.cp.utils.SetTextUtil;
import com.cninsure.cp.utils.cx.TypePickeUtil;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.List;

public class CxThirdFragment extends BaseFragment {

    private View contentView,footerView;
    private ListView mlistView;
    private LayoutInflater inflater;
    private MyAdapter myAdapter;
    private CxSurveyWorkActivity activity;
    public int OcrPosition;  //OCR拍照时点击所在的ListView Item ,以便知道OCR返回后数据显示到哪个Item上。

    /**OCR解析信息及图片路径，驾驶证，行驶证**/
    public static OCREntity ocrEntityJsz,ocrEntityXsz;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.inflater = inflater;
        contentView = inflater.inflate(R.layout.cx_third_fragment,null);
        activity = (CxSurveyWorkActivity) getActivity();
        initData();
        initView();

        return contentView;
    }

    /**初始化基础数据*/
    private void initData() {
        if (activity.cxWorkEntity!=null && activity.cxWorkEntity.thirdPartys!=null){

        }else{
            activity.cxWorkEntity.thirdPartys  = new ArrayList<>();
            activity.cxWorkEntity.thirdPartys.add(new CxSurveyWorkEntity.ThirdPartyEntity());
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
                SaveDataToEntity(); //先把已经填写好的数据存起来
                activity.cxWorkEntity.thirdPartys.add(new CxSurveyWorkEntity.ThirdPartyEntity());
                myAdapter.notifyDataSetChanged();
            }
        });
        return footerView;
    }

    /**保存上传的OCR图片，并显示已经识别的结果到控件*/
    public void disPlayOcrInfo(int type, String imgUrl) {
//        OcrPosition
        if (type== CxSurveyWorkActivity.THIRD_SZ_JSZ_OCR) saveAndDisplayJsz(imgUrl);
        if (type== CxSurveyWorkActivity.THIRD_SZ_XSZ_OCR) saveAndDisplayXsz(imgUrl);
    }

    /**保存驾驶证照片，并显示识别信息*/
    private void saveAndDisplayJsz(String imgUrl) {
        activity.cxWorkEntity.thirdPartys.get(OcrPosition).carPerson= ocrEntityJsz.bdDriverName; //驾驶员姓名
        activity.cxWorkEntity.thirdPartys.get(OcrPosition).driverLicense= ocrEntityJsz.bdDriverNo;  //驾驶证号
        activity.cxWorkEntity.thirdPartys.get(OcrPosition).szDriverRegisterDate= ocrEntityJsz.getBdDriverRegisterDate();//初次领证日期	yyyy-mm-dd
        activity.cxWorkEntity.thirdPartys.get(OcrPosition).szDriverEffectiveStar= ocrEntityJsz.getBdDriverEffectiveStar(); //有效起始日期	yyyy-mm-dd
        activity.cxWorkEntity.thirdPartys.get(OcrPosition).pathDriverLicense= imgUrl;  //驾驶证链接	保存作业图片接口返回字段fileUrl
        myAdapter.notifyDataSetChanged();
    }

    /**保存行驶证照片，并显示识别信息*/
    private void saveAndDisplayXsz(String imgUrl) {
        activity.cxWorkEntity.thirdPartys.get(OcrPosition).carNumber= ocrEntityXsz.bdCarNumber; //车牌号
        activity.cxWorkEntity.thirdPartys.get(OcrPosition).frameNumber= ocrEntityXsz.bdCarVin;  //车架号
        activity.cxWorkEntity.thirdPartys.get(OcrPosition).engineNumber= ocrEntityXsz.bdEngineNo;//发动机号
        activity.cxWorkEntity.thirdPartys.get(OcrPosition).szCarRegisterDate= ocrEntityXsz.getBdCarRegisterDate(); //初登日期
        activity.cxWorkEntity.thirdPartys.get(OcrPosition).szCarUseType= ocrEntityXsz.getBdCarUseTypeValue();  //使用性质
        activity.cxWorkEntity.thirdPartys.get(OcrPosition).pathMoveLicense= imgUrl;  //行驶证链接	保存作业图片接口返回字段fileUrl
        myAdapter.notifyDataSetChanged();
    }

    @Override
    public void onPause() {
        super.onPause();
        SaveDataToEntity();
    }

    /**保存数据到实体类*/
    @Override
    public void SaveDataToEntity() {
        if (activity==null ||mlistView==null) return;
        for (int i= 0;i<activity.cxWorkEntity.thirdPartys.size();i++){
            if ( mlistView.getChildAt(i)==null) continue;
            ViewHolder vHolder = (ViewHolder) mlistView.getChildAt(i).getTag();
            getHolderDate(vHolder,i);
        }
    }

    /**获取ViewHolder中控件上的数据，封装到ThirdPartyEntity对象中*/
    private void getHolderDate(ViewHolder vHolder,int position) {
        CxSurveyWorkEntity.ThirdPartyEntity tempThirdEnty = activity.cxWorkEntity.thirdPartys.get(position);
        tempThirdEnty.licenseMissingResult = activity.cxSurveyDict.getValueByLabel("licenseMissingResult",vHolder.licenseMissingResultTv.getText().toString()); //证件缺失原因
        tempThirdEnty.thirdPartysNo = position; //三者编号
        tempThirdEnty.carNumber = vHolder.carNumberEdt.getText().toString(); //车牌号
        tempThirdEnty.frameNumber = vHolder.frameNumberEdt.getText().toString(); //车架号
        tempThirdEnty.engineNumber = vHolder.engineNumberEdt.getText().toString();  //发动机号
        tempThirdEnty.szCarRegisterDate = vHolder.szCarRegisterDateTv.getText().toString();  //初登日期
        tempThirdEnty.szCarEffectiveDate = vHolder.szCarEffectiveDateTv.getText().toString();  //行驶证有效期至
        tempThirdEnty.szCarNumberType =  TypePickeUtil.getValue(vHolder.szCarNumberTypeTv.getText().toString(),activity.cxSurveyDict,"carno_type");   //号牌种类
        tempThirdEnty.szCarUseType =  TypePickeUtil.getValue(vHolder.szCarUseTypeTv.getText().toString(),activity.cxSurveyDict,"car_usetype");   //使用性质
        tempThirdEnty.drivingMode =  activity.cxSurveyDict.getValueByLabel("quasiDrivingType",vHolder.drivingModeTv.getText().toString());   //准驾车型
        tempThirdEnty.driverLicense = vHolder.driverLicenseTv.getText().toString();  //驾驶证
        tempThirdEnty.carPerson = vHolder.carPersonTv.getText().toString();  //驾驶员
        tempThirdEnty.carPersonPhone = vHolder.carPersonPhoneTv.getText().toString();  //驾驶员电话
        tempThirdEnty.szDriverRegisterDate = vHolder.szDriverRegisterDateTv.getText().toString();  //初次领证日期
        tempThirdEnty.szDriverEffectiveStar = vHolder.szDriverEffectiveStarTv.getText().toString();  //有效起始日期
        tempThirdEnty.szDriverEffectiveEnd = vHolder.szDriverEffectiveEndTv.getText().toString();  //驾驶证有效期至
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
            setPickeOnclick(vHolder,position);
            OcrOnclick(vHolder,position);
            displayInfo(vHolder,position);
            return convertView;
        }

        /**显示内容*/
        private void displayInfo(ViewHolder vHolder, int position) {
            if (!(activity.cxWorkEntity.thirdPartys!=null && activity.cxWorkEntity.thirdPartys.size()>0))  //没有数据就跳过，以免报错
                return;
            CxSurveyWorkEntity.ThirdPartyEntity thirdPart= activity.cxWorkEntity.thirdPartys.get(position);  //取出需要加载的数据

            SetTextUtil.setEditText(vHolder.carNumberEdt,thirdPart.carNumber,SetTextUtil.CAR_NO); //车牌号
            SetTextUtil.setEditText(vHolder.frameNumberEdt,thirdPart.frameNumber,SetTextUtil.VIN); //车架号
            SetTextUtil.setEditText(vHolder.engineNumberEdt,thirdPart.engineNumber,SetTextUtil.ENGIN);  //发动机号
            SetTextUtil.setTextViewText(vHolder.szCarRegisterDateTv,thirdPart.szCarRegisterDate);  //初登日期
            SetTextUtil.setTextViewText(vHolder. szCarEffectiveDateTv,thirdPart.szCarEffectiveDate);  //行驶证有效期至
            SetTextUtil.setTvTextForArr(vHolder.szCarNumberTypeTv,TypePickeUtil.getDictLabelArr(activity.cxSurveyDict.getDictByType("carno_type")),thirdPart.szCarNumberType);  //号牌种类
            SetTextUtil.setTvTextForArr(vHolder. szCarUseTypeTv,TypePickeUtil.getDictLabelArr(activity.cxSurveyDict.getDictByType("car_usetype")),thirdPart.szCarUseType);  //使用性质

            SetTextUtil.setTextViewText(vHolder.isLicenseKouTv,activity.cxSurveyDict.getLabelByValue("isLicenseKou",thirdPart.isLicenseKou+""));//证件查验
            SetTextUtil.setTextViewText(vHolder.licenseMissingResultTv,activity.cxSurveyDict.getLabelByValue("licenseMissingResult",thirdPart.licenseMissingResult+""));//证件缺失原因

            SetTextUtil.setEditText(vHolder.driverLicenseTv,thirdPart.driverLicense,SetTextUtil.ID_CARD);  //驾驶证
            SetTextUtil.setTextViewText(vHolder.drivingModeTv,activity.cxSurveyDict.getLabelByValue("quasiDrivingType",thirdPart.drivingMode));  //准驾车型
            SetTextUtil.setEditText(vHolder.carPersonTv,thirdPart.carPerson);  //驾驶员
            SetTextUtil.setEditText(vHolder.carPersonPhoneTv,thirdPart.carPersonPhone,SetTextUtil.MOBILE);  //驾驶员电话
            SetTextUtil.setTextViewText(vHolder.szDriverRegisterDateTv,thirdPart.szDriverRegisterDate);  //初次领证日期
            SetTextUtil.setTextViewText(vHolder.szDriverEffectiveStarTv,thirdPart.szDriverEffectiveStar);  //有效起始日期
            SetTextUtil.setTextViewText(vHolder.szDriverEffectiveEndTv,thirdPart.szDriverEffectiveEnd);  //驾驶证有效期至
        }

        /**给组件绑定监听事件*/
        private void setPickeOnclick(ViewHolder vHolder, int position){
            CxSurveyWorkEntity.ThirdPartyEntity thirdPart= activity.cxWorkEntity.thirdPartys.get(position);  //取出需要加载的数据
            //绑定日期选择
            DateChoiceUtil.setShortDatePickerDialog(activity,vHolder.szCarRegisterDateTv);
            DateChoiceUtil.setShortDatePickerDialog(activity,vHolder.szCarEffectiveDateTv);
            DateChoiceUtil.setShortDatePickerDialog(activity,vHolder.szDriverRegisterDateTv);
            DateChoiceUtil.setShortDatePickerDialog(activity,vHolder.szDriverEffectiveStarTv);
            DateChoiceUtil.setShortDatePickerDialog(activity,vHolder.szDriverEffectiveEndTv);
            //绑定类型选择
            TypePickeUtil.setTypePickerDialog(activity,vHolder.szCarNumberTypeTv,activity.cxSurveyDict,"carno_type");
            TypePickeUtil.setTypePickerDialog(activity,vHolder.szCarUseTypeTv,activity.cxSurveyDict,"car_usetype");
            TypePickeUtil.setTypePickerDialog(activity,vHolder.drivingModeTv,activity.cxSurveyDict,"quasiDrivingType");

            TypePickeUtil.setTypePickerDialog(activity, vHolder.isLicenseKouTv,activity.cxSurveyDict,"isLicenseKou");
            TypePickeUtil.setTypePickerDialog(activity, vHolder.licenseMissingResultTv,activity.cxSurveyDict,"licenseMissingResult"); //证件缺失原因
            vHolder.isLicenseKouTv.addTextChangedListener(new TextWatcher() {
                @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    thirdPart.isLicenseKou = TypePickeUtil.getValueByLabel(s.toString(),activity.cxSurveyDict,"isLicenseKou");   //证件查验
                    setlicenseMissingResultTvVisibility(vHolder,s.toString());
                }
                @Override public void afterTextChanged(Editable s) {}
            });
        }

        /**
         * 如果证件查验类型选择了“缺少驾驶证”“缺少行驶证”“缺少行驶证和驾驶证”时，显示“证件缺失原因”选择项目，供用户选择
         * @param value
         */
        private void setlicenseMissingResultTvVisibility(ViewHolder vHolder,String value) {
            List<DictData> dictListTemp = activity.cxSurveyDict.getDictByType("isLicenseKou");
            if (dictListTemp!=null && value!=null) {
                for (DictData dictTemp:dictListTemp){
                    if (value.equals(dictTemp.label)) {

                        //先全部显示出来
                        vHolder.jxzhengcameraLin.setVisibility(View.VISIBLE);
                        vHolder.pathMoveLicenseTv.setVisibility(View.VISIBLE);
                        vHolder.pathDriverLicenseTv.setVisibility(View.VISIBLE);

                        if ("01".equals(dictTemp.value)) { //两证齐全有效
                            vHolder.licenseMissingResultTv.setVisibility(View.GONE); //隐藏 证件缺失原因 选择项目
                            vHolder.licenseMissingResultTv.setText("");  //两证齐全，清空选择的 证件缺失原因
                        }else{
                            vHolder.licenseMissingResultTv.setVisibility(View.VISIBLE); //显示 证件缺失原因 选择项目
                            if ("02".equals(dictTemp.value)) { //缺少驾驶证
                                vHolder.pathDriverLicenseTv.setVisibility(View.GONE);
                            }else if ("03".equals(dictTemp.value)) { //缺少行驶证
                                vHolder.pathMoveLicenseTv.setVisibility(View.GONE);
                            } else if ("04".equals(dictTemp.value)) { //缺少驾驶证和行驶证
                                vHolder.jxzhengcameraLin.setVisibility(View.GONE);
                            }
                        }
                        break;
                    }
                }
            }
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
                        SaveDataToEntity(); //先把已经填写好的数据存起来
                        activity.cxWorkEntity.thirdPartys.remove(position);
                        myAdapter.notifyDataSetChanged();
                    }
                });
            }else{
                convertView.findViewById(R.id.cti_delete).setVisibility(View.GONE);
            }
        }

    }

    private void OcrOnclick(ViewHolder vHolder, int position) {
        // 行驶证识别或展示
        vHolder.pathMoveLicenseTv.setOnClickListener(v -> {
            OcrPosition = position;//OCR拍照时点击所在的ListView Item ,以便知道OCR返回后数据显示到哪个Item上。
            activity.cameraHelp.startCamera(activity.THIRD_SZ_XSZ_OCR);
        });
        // 驾驶证识别或展示
        vHolder.pathDriverLicenseTv.setOnClickListener(v -> {
            OcrPosition = position;//OCR拍照时点击所在的ListView Item ,以便知道OCR返回后数据显示到哪个Item上。
            activity.cameraHelp.startCamera(activity.THIRD_SZ_JSZ_OCR);
        });
    }

    /** 创建 ViewHolder */
    class ViewHolder{
        @ViewInject(R.id.csi_isLicenseKou)  private TextView isLicenseKouTv; //证件查验
        @ViewInject(R.id.csi_licenseMissingResult)  private TextView licenseMissingResultTv; //证件缺失原因

        @ViewInject(R.id.cti_thirdPartysNo)  private TextView thirdPartysTv; //三者编号
//        @ViewInject(R.id.cti_szisLicenseKou)  private ToggleButton szisLicenseKouTg; //双证被扣按钮
        @ViewInject(R.id.cti_pathMoveLicense)  private TextView pathMoveLicenseTv; //拍摄行驶证
        @ViewInject(R.id.cti_pathDriverLicense)  private TextView pathDriverLicenseTv; //拍摄驾驶证
        @ViewInject(R.id.cti_carNumber)  private EditText carNumberEdt; //车牌号
        @ViewInject(R.id.cti_frameNumber)  private EditText frameNumberEdt; //车架号
        @ViewInject(R.id.cti_engineNumber)  private EditText engineNumberEdt;  //发动机号
        @ViewInject(R.id.cti_szCarRegisterDate)  private TextView szCarRegisterDateTv;  //初登日期
        @ViewInject(R.id.cti_szCarEffectiveDate)  private TextView szCarEffectiveDateTv;  //行驶证有效期至
        @ViewInject(R.id.cti_szCarNumberType)  private TextView szCarNumberTypeTv;  //号牌种类
        @ViewInject(R.id.cti_szCarUseType)  private TextView szCarUseTypeTv;  //使用性质
        @ViewInject(R.id.cti_driverLicense)  private EditText driverLicenseTv;  //驾驶证
        @ViewInject(R.id.cti_drivingMode)  private TextView drivingModeTv;  //准驾车型
        @ViewInject(R.id.cti_carPerson)  private EditText carPersonTv;  //驾驶员
        @ViewInject(R.id.cti_carPersonPhone)  private EditText carPersonPhoneTv;  //驾驶员电话
        @ViewInject(R.id.cti_szDriverRegisterDate)  private TextView szDriverRegisterDateTv;  //初次领证日期
        @ViewInject(R.id.cti_szDriverEffectiveStar)  private TextView szDriverEffectiveStarTv;  //有效起始日期
        @ViewInject(R.id.cti_szDriverEffectiveEnd)  private TextView szDriverEffectiveEndTv;  //驾驶证有效期至
        @ViewInject(R.id.jxzheng_cameraLinThid)  private LinearLayout jxzhengcameraLin;  // 拍照按钮LinLayout
    }
}
