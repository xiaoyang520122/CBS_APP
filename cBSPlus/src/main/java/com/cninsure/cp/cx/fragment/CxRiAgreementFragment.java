package com.cninsure.cp.cx.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.cninsure.cp.R;
import com.cninsure.cp.cx.CxInjuryMediateActivity;
import com.cninsure.cp.entity.cx.InjuryMediateWorkEntity;
import com.cninsure.cp.utils.SetTextUtil;
import com.cninsure.cp.view.AutoLinefeedLayout;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.List;

public class CxRiAgreementFragment  extends BaseFragment {

    private String Str1 = "基于事故事实，关于本次交通事故人身损害赔偿，甲、乙双方本着合法、公开、自愿、诚信的原则，经各方协商一致达成本协议。";
    private String Str2 = "一、甲方充分了解自身人身损失情况，经甲、乙双方共同确认，甲方此次事故人身损失按事故责任比例计算后合计金额为@";  //@结尾代表要有录入框，无则是换行
    private String Str3 = "元(币种：人民币，下同)，该款项包括但不限于甲方的医疗费、误工费、护理费、后续治疗费、残疾赔偿金、被抚养人生活费、精神损害抚慰金等，以及其他依据法律规定应该支付的所有赔偿项目和金额。";
    private String Str4 = "二、经双方共同确认，甲方应赔偿乙方财产损失@";
    private String Str5 = "元，乙方应赔偿甲方财产损失@";
    private String Str6 = "元。";
    private String Str7 = "三、本协议签署前，乙方已经支付给甲方赔款@";
    private String Str8 = "元，扣减乙方已支付的赔款后，乙方同意在本协议正式生效后的@";
    private String Str9 = "日内一次性赔偿甲方人身损失共计@";
    private String Str10 = "元。";
    private String Str11 = "四、甲方同意乙方按本协议约定金额以转帐形式支付以下账户：";
    private String Str12 = "户名：@";
    private String Str13 = "开户行：@";
    private String Str14 = "账号：@。";
    private String Str15 = "五、本协议约定赔偿金额系乙方对本次事故造成甲方全部损害的一次性、终结性的赔偿，甲方对于自身的人身损害程度及赔偿金额予以确认。甲方获得赔偿后，不得在任何时候以任何理由、任何方式再向乙方提出索赔请求。";
    private String Str16 = "六、本协议系双方平等、自愿协商的结果，是双方真实意思的表示，本协议内容甲、乙双方均已全文阅读并理解无误，且均已充分明白本协议的内容及所涉及的法律后果，双方同意放弃通过诉讼途径解决赔偿事宜的权利。";
    private String Str17 = "七、本协议履行后，双方就本次事故的权利义务完全终结，并自双方签字盖章之日起生效。";

//    private EditText agreementA,agreementB,agreementC,agreementD,agreementE,agreementF,agreementG,agreementH,agreementI;
    private List<EditText> writeInfoEdits;


    private List<String> agreementTextList ;
    LayoutInflater inflater;

    private View contentView;
    private CxInjuryMediateActivity activity;
    /**签字标识 0甲方签字，1乙方签字*/
    public int signFlag; //签字标识 0甲方签字，1乙方签字
    @ViewInject(R.id.cxInMe_agreementAutoLine) private AutoLinefeedLayout autoLine;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        contentView = inflater.inflate(R.layout.cxrs_injury_mediate_agreement_fragment, null);
        activity = (CxInjuryMediateActivity) getActivity();
        ViewUtils.inject(this, contentView); //注入view和事件
        this.inflater = inflater;
        initView();
        return contentView;
    }

    private void initView() {
        getAgreementText();
        disPlayAgreementText();
    }

    private void disPlayAgreementText() {
        writeInfoEdits = new ArrayList<>(9);
        for (String tempText:agreementTextList){
            String [] tempTextArr = tempText.split("");
            for (int i=0;i<tempTextArr.length;i++ ){
                String textWord = tempTextArr[i];
                if ("@".equals(textWord)){
                    EditText tempEdit = (EditText) inflater.inflate(R.layout.position_edittext,null);
                    writeInfoEdits.add(tempEdit);
                    autoLine.addView(tempEdit);
                }else{
                    TextView tempTv = (TextView) inflater.inflate(R.layout.agreement_textview,null);
                    tempTv.setText(textWord);
                    tempTv.setTextColor(getActivity().getResources().getColor(R.color.result_view));
                    autoLine.addView(tempTv);
                }
                if(i==tempTextArr.length-1 && !"@".equals(textWord))
                    autoLine.addView(inflater.inflate(R.layout.position_textview,null)); //加长TextView，强制换行。
            }
        }
        displayAgreementInfo();
    }

    private void getAgreementText() {
        agreementTextList  = new ArrayList<>(18);
        agreementTextList.add(Str1);
        agreementTextList.add(Str2);
        agreementTextList.add(Str3);
        agreementTextList.add(Str4);
        agreementTextList.add(Str5);
        agreementTextList.add(Str6);
        agreementTextList.add(Str7);
        agreementTextList.add(Str8);
        agreementTextList.add(Str9);
        agreementTextList.add(Str10);
        agreementTextList.add(Str11);
        agreementTextList.add(Str12);
        agreementTextList.add(Str13);
        agreementTextList.add(Str14);
        agreementTextList.add(Str15);
        agreementTextList.add(Str16);
        agreementTextList.add(Str17);
    }

    /**回显系统数据*/
    private void displayAgreementInfo(){
        if (activity == null || activity.taskEntity==null || activity.taskEntity.data==null) return;  //说明没有初始化这个Fragment，也就没有任何操作，没有不要保存了，
        InjuryMediateWorkEntity tempWorkEnt = activity.taskEntity.data.contentJson;
        if (tempWorkEnt!=null){
            SetTextUtil.setEditText(writeInfoEdits.get(0),tempWorkEnt.agreementA);
            SetTextUtil.setEditText(writeInfoEdits.get(1),tempWorkEnt.agreementB);
            SetTextUtil.setEditText(writeInfoEdits.get(2),tempWorkEnt.agreementC);
            SetTextUtil.setEditText(writeInfoEdits.get(3),tempWorkEnt.agreementD);
            SetTextUtil.setEditText(writeInfoEdits.get(4),tempWorkEnt.agreementE);
            SetTextUtil.setEditText(writeInfoEdits.get(5),tempWorkEnt.agreementF);
            SetTextUtil.setEditText(writeInfoEdits.get(6),tempWorkEnt.agreementG);
            SetTextUtil.setEditText(writeInfoEdits.get(7),tempWorkEnt.agreementH);
            SetTextUtil.setEditText(writeInfoEdits.get(8),tempWorkEnt.agreementI);
        }
    }

    @Override
    public void SaveDataToEntity() {
        if (activity == null) return;  //说明没有初始化这个Fragment，也就没有任何操作，没有不要保存了，
        InjuryMediateWorkEntity tempWorkEnt = activity.taskEntity.data.contentJson;
        tempWorkEnt.agreementA = writeInfoEdits.get(0).getText().toString();
        tempWorkEnt.agreementB = writeInfoEdits.get(1).getText().toString();
        tempWorkEnt.agreementC = writeInfoEdits.get(2).getText().toString();
        tempWorkEnt.agreementD = writeInfoEdits.get(3).getText().toString();
        tempWorkEnt.agreementE = writeInfoEdits.get(4).getText().toString();
        tempWorkEnt.agreementF = writeInfoEdits.get(5).getText().toString();
        tempWorkEnt.agreementG = writeInfoEdits.get(6).getText().toString();
        tempWorkEnt.agreementH = writeInfoEdits.get(7).getText().toString();
        tempWorkEnt.agreementI = writeInfoEdits.get(8).getText().toString();
    }
}

