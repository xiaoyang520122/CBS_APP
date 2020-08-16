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
import com.cninsure.cp.utils.DateChoiceUtil;
import com.cninsure.cp.utils.HttpRequestTool;
import com.cninsure.cp.utils.SetTextUtil;
import com.cninsure.cp.utils.cx.TypePickeUtil;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

public class CxRiMediateFragment  extends BaseFragment implements View.OnClickListener {

    private View contentView;
    private CxInjuryMediateActivity activity;
    /**签字标识 0甲方签字，1乙方签字*/
    public int signFlag; //签字标识 0甲方签字，1乙方签字

    @ViewInject(R.id.cxInMe_accidentTime) private TextView accidentTime;//	事故时间
    @ViewInject(R.id.cxInMe_accidentAddress) private EditText accidentAddress;//	事故地点
    @ViewInject(R.id.cxInMe_partyOneName) private EditText partyOneName;//	甲(受害方)
    @ViewInject(R.id.cxInMe_poSex) private TextView poSex;//	甲(受害方)性别
    @ViewInject(R.id.cxInMe_poCardNo) private EditText poCardNo;//	身份证号
    @ViewInject(R.id.cxInMe_poTel) private EditText poTel;//	电话
    @ViewInject(R.id.cxInMe_poDuty) private EditText poDuty;//	事故责任
    @ViewInject(R.id.cxInMe_pooOccupation) private EditText pooOccupation;//	职业
    @ViewInject(R.id.cxInMe_poIncome) private EditText poIncome;//	收入(月薪)
    @ViewInject(R.id.cxInMe_poWorkCompany) private EditText poWorkCompany;//	工作单位
    @ViewInject(R.id.cxInMe_poLiveAddress) private EditText poLiveAddress;//	现居地址
    @ViewInject(R.id.cxInMe_poLiveStart) private TextView poLiveStart;//	居住时间开始
    @ViewInject(R.id.cxInMe_poLiveEnd) private TextView poLiveEnd;//	居住时间结束
    @ViewInject(R.id.cxInMe_poHometown) private EditText poHometown;//	户籍地
    @ViewInject(R.id.cxInMe_poNature) private TextView poNature;//	户籍性质

    @ViewInject(R.id.cxInMe_partyBName) private EditText partyBName;//	乙(标的方)
    @ViewInject(R.id.cxInMe_pbSex) private TextView pbSex;//	乙(标的方)性别
    @ViewInject(R.id.cxInMe_pbCardNo) private EditText pbCardNo;//	身份证号
    @ViewInject(R.id.cxInMe_pbTel) private EditText pbTel;//	电话
    @ViewInject(R.id.cxInMe_pbDuty) private EditText pbDuty;//	事故责任
    @ViewInject(R.id.cxInMe_pbLetterNo) private EditText pbLetterNo;//	事故认定书编号
    @ViewInject(R.id.cxInMe_pbSignDate) private TextView pbSignDate;//	签署日期
    @ViewInject(R.id.cxInMe_poSign) private TextView poSign;//	甲方签字
    @ViewInject(R.id.cxInMe_pbSign) private TextView pbSign;//	乙方签字
    @ViewInject(R.id.cxInMe_poSign_img) private ImageView poSignImg;//	甲方签字
    @ViewInject(R.id.cxInMe_pbSign_img) private ImageView pbSignImg;//	乙方签字



    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        contentView = inflater.inflate(R.layout.cxrs_injury_mediate_fragment, null);
        activity = (CxInjuryMediateActivity) getActivity();
        ViewUtils.inject(this, contentView); //注入view和事件
        initView();
        return contentView;
    }

    private void initView() {
        poSign.setOnClickListener(this);
        pbSign.setOnClickListener(this);
        poSignImg.setOnClickListener(this);
        pbSignImg.setOnClickListener(this);

        TypePickeUtil.setTypePickerDialog(getActivity(),poNature, activity.cxDict,"poNature");
        TypePickeUtil.setTypePickerDialog(getActivity(),poSex,new String[]{"男","女"});
        TypePickeUtil.setTypePickerDialog(getActivity(),pbSex,new String[]{"男","女"});
        DateChoiceUtil.setLongDatePickerDialogOnClick(getActivity(),accidentTime); //事故时间
        DateChoiceUtil.setShortDatePickerDialog(getActivity(),poLiveStart); //居住时间开始
        DateChoiceUtil.setShortDatePickerDialog(getActivity(),poLiveEnd); //居住时间结束
        DateChoiceUtil.setShortDatePickerDialog(getActivity(),pbSignDate); //签署日期
        displayTaskInfo();
    }

    @Override
    public void SaveDataToEntity() {
        if (activity==null) return;  //activity说明没有初始化这个Fragment，也就没有任何操作，没有不要保存了，
        InjuryMediateWorkEntity tempWorkEnt = activity.taskEntity.data.contentJson;
        tempWorkEnt.accidentTime = accidentTime.getText().toString();//	事故时间
        tempWorkEnt.accidentAddress = accidentAddress.getText().toString();//	事故地点
        tempWorkEnt.partyOneName = partyOneName.getText().toString();//	甲(受害方)
        tempWorkEnt.poSex = (poSex.getText().toString()).equals("男")?0:1;//	甲(受害方)性别
        tempWorkEnt.poCardNo = poCardNo.getText().toString();//	身份证号
        tempWorkEnt.poTel = poTel.getText().toString();//	电话
        tempWorkEnt.poDuty = poDuty.getText().toString();//	事故责任
        tempWorkEnt.pooOccupation = pooOccupation.getText().toString();//	职业
        tempWorkEnt.poIncome = poIncome.getText().toString();//	收入(月薪)
        tempWorkEnt.poWorkCompany = poWorkCompany.getText().toString();//	工作单位
        tempWorkEnt.poLiveAddress = poLiveAddress.getText().toString();//	现居地址
        tempWorkEnt.poLiveStart = poLiveStart.getText().toString();//	居住时间开始
        tempWorkEnt.poLiveEnd = poLiveEnd.getText().toString();//	居住时间结束
        tempWorkEnt.poHometown = poHometown.getText().toString();//	户籍地
        tempWorkEnt.poNature = TypePickeUtil.getValue(poNature.getText().toString(),activity.cxDict,"poNature");//	户籍性质

        tempWorkEnt.partyBName = partyBName.getText().toString();//	乙(标的方)
        tempWorkEnt.pbSex = (pbSex.getText().toString()).equals("男")?0:1;//	乙(标的方)性别
        tempWorkEnt.pbCardNo = pbCardNo.getText().toString();//	身份证号
        tempWorkEnt.pbTel = pbTel.getText().toString();//	电话
        tempWorkEnt.pbDuty = pbDuty.getText().toString();//	事故责任
        tempWorkEnt.pbLetterNo = pbLetterNo.getText().toString();//	事故认定书编号
        tempWorkEnt.pbSignDate = pbSignDate.getText().toString();//	签署日期
//        tempWorkEnt.poSign = poSign.getText().toString();//	甲方签字
//        tempWorkEnt.pbSign = pbSign.getText().toString();//	乙方签字
    }

    private void displayTaskInfo() {
        InjuryMediateWorkEntity tempWorkEnt = activity.taskEntity.data.contentJson;
        SetTextUtil.setTextViewText(accidentTime, tempWorkEnt.accidentTime);//	事故时间
        SetTextUtil.setEditText(accidentAddress, tempWorkEnt.accidentAddress);//	事故地点
        SetTextUtil.setEditText(partyOneName, tempWorkEnt.partyOneName);//	甲(受害方)
        if (tempWorkEnt.poSex == 0)
        SetTextUtil.setTextViewText(poSex,"男");//	甲(受害方)性别
        if (tempWorkEnt.poSex == 1)
            SetTextUtil.setTextViewText(poSex,"女");//	甲(受害方)性别
        SetTextUtil.setEditText(poCardNo, tempWorkEnt.poCardNo);//	身份证号
        SetTextUtil.setEditText(poTel, tempWorkEnt.poTel);//	电话
        SetTextUtil.setEditText(poDuty, tempWorkEnt.poDuty);//	事故责任
        SetTextUtil.setEditText(pooOccupation, tempWorkEnt.pooOccupation);//	职业
        SetTextUtil.setEditText(poIncome, tempWorkEnt.poIncome);//	收入(月薪)
        SetTextUtil.setEditText(poWorkCompany, tempWorkEnt.poWorkCompany);//	工作单位
        SetTextUtil.setEditText(poLiveAddress, tempWorkEnt.poLiveAddress);//	现居地址
        SetTextUtil.setTextViewText(poLiveStart, tempWorkEnt.poLiveStart);//	居住时间开始
        SetTextUtil.setTextViewText(poLiveEnd, tempWorkEnt.poLiveEnd);//	居住时间结束
        SetTextUtil.setEditText(poHometown, tempWorkEnt.poHometown);//	户籍地
        //	户籍性质  0农业‘1非农业’2其他
        SetTextUtil.setTvTextForArr(poNature, TypePickeUtil.getDictLabelArr(activity.cxDict.getDictByType("poNature")), tempWorkEnt.poNature);

        SetTextUtil.setEditText(partyBName, tempWorkEnt.partyBName);//	乙(标的方)
        if (tempWorkEnt.pbSex == 0)
        SetTextUtil.setTextViewText(pbSex,  "男" );//	乙(标的方)性别
        if (tempWorkEnt.pbSex == 1)
        SetTextUtil.setTextViewText(pbSex, "女");//	乙(标的方)性别
        SetTextUtil.setEditText(pbCardNo, tempWorkEnt.pbCardNo);//	身份证号
        SetTextUtil.setEditText(pbTel, tempWorkEnt.pbTel);//	电话
        SetTextUtil.setEditText(pbDuty, tempWorkEnt.pbDuty);//	事故责任
        SetTextUtil.setEditText(pbLetterNo, tempWorkEnt.pbLetterNo);//	事故认定书编号
        SetTextUtil.setTextViewText(pbSignDate, tempWorkEnt.pbSignDate);//	签署日期
        disPlaySign();//甲乙方签字
    }

    /**启动签字**/
    private void startSign(){
        Intent intent=new Intent(activity, LinePathActivity.class);
        intent.putExtra("orderUid", activity.getIntent().getStringExtra("orderUid"));
        startActivityForResult(intent, HttpRequestTool.LINEPATH);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.cxInMe_poSign:signFlag = 0;startSign();break; //甲方签字
            case R.id.cxInMe_pbSign:signFlag = 1;startSign();break; //乙方签字
            case R.id.cxInMe_poSign_img:;break; //显示甲方签字
            case R.id.cxInMe_pbSign_img:;break; //显示乙方签字
        }
    }

    /***显示签字图片*/
    public void disPlaySign() {
        if (activity.taskEntity != null && activity.taskEntity.data != null && activity.taskEntity.data.contentJson != null) {
            if (!TextUtils.isEmpty(activity.taskEntity.data.contentJson.poSign)) {  //甲方签字图片
                String imgPath = AppApplication.getUSER().data.qiniuUrl + activity.taskEntity.data.contentJson.poSign;
                Glide.with(getActivity()).load(imgPath).placeholder(R.drawable.waitcase_hui).into(poSignImg);
                poSignImg.setVisibility(View.VISIBLE);
            } else {
                poSignImg.setVisibility(View.GONE);
            }
            if (!TextUtils.isEmpty(activity.taskEntity.data.contentJson.pbSign)) {  //乙方签字图片
                String imgPath = AppApplication.getUSER().data.qiniuUrl + activity.taskEntity.data.contentJson.pbSign;
                Glide.with(getActivity()).load(imgPath).placeholder(R.drawable.waitcase_hui).into(pbSignImg);
                pbSignImg.setVisibility(View.VISIBLE);
            } else {
                pbSignImg.setVisibility(View.GONE);
            }
        }
    }
}
