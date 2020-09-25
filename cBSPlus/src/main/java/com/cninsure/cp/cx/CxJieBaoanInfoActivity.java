package com.cninsure.cp.cx;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import com.alibaba.fastjson.JSON;
import com.cninsure.cp.BaseActivity;
import com.cninsure.cp.R;
import com.cninsure.cp.cx.adapter.MyFragmentPagerAdapter;
import com.cninsure.cp.cx.fragment.BaseFragment;
import com.cninsure.cp.cx.fragment.CxRiAgreementFragment;
import com.cninsure.cp.cx.fragment.CxRiListFragment;
import com.cninsure.cp.cx.jiebaoanfragment.BaoanInfoFragment;
import com.cninsure.cp.cx.jiebaoanfragment.CxImagFragment;
import com.cninsure.cp.cx.jiebaoanfragment.TaskBasicInfoFragment;
import com.cninsure.cp.entity.PublicOrderEntity;
import com.cninsure.cp.entity.URLs;
import com.cninsure.cp.entity.cx.CxDictEntity;
import com.cninsure.cp.utils.ActivityFinishUtil;
import com.cninsure.cp.utils.HttpRequestTool;
import com.cninsure.cp.utils.HttpUtils;
import com.cninsure.cp.utils.LoadDialogUtil;
import com.google.android.material.tabs.TabLayout;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import org.apache.http.NameValuePair;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class CxJieBaoanInfoActivity extends BaseActivity implements View.OnClickListener {


    public PublicOrderEntity orderInfoEn; //任务信息
    private String[] mainTitlesArray = {"报案信息", "基本信息" };
    public Map<Integer, BaseFragment> fragmentMap;
    private MyFragmentPagerAdapter adapter;
    private FragmentManager fm;
    public CxDictEntity cxDict = new CxDictEntity(); //拍照类型字典数据

    public String QorderUid;
        @ViewInject(R.id.cxJieBaoan_tablayout) private TabLayout mTabLayout;
        @ViewInject(R.id.cxJieBaoan_viewpager) public ViewPager mViewPager;

    private BaoanInfoFragment fg0;
    private TaskBasicInfoFragment fg1;
    private CxImagFragment fg2;
//    private CxRiDeliveryFragment fg3;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.cx_jie_baoan_info_activity);
        orderInfoEn = (PublicOrderEntity) getIntent().getSerializableExtra("PublicOrderEntity");
        QorderUid = getIntent().getStringExtra("orderUid");
        ViewUtils.inject(this);
        EventBus.getDefault().register(this);
        dowloadDictType();
        initView();
    }

    private void initView() {
        fm = getSupportFragmentManager();
        initFragment();
        initViewPager();
        initTab();
        setBackOnclick();
    }

    private void initFragment() {
        fg0 = new BaoanInfoFragment();
        fg1 = new TaskBasicInfoFragment();
        fg2 = new CxImagFragment();
//        fg3 = new CxRiDeliveryFragment();

        fragmentMap = new TreeMap<>();
        fragmentMap.put(0, fg0);
        fragmentMap.put(1, fg1);
        fragmentMap.put(2, fg2);
//        fragmentMap.put(3, fg3);
    }

    private void initViewPager() {
        adapter = new MyFragmentPagerAdapter(fm, fragmentMap);
        mViewPager.setAdapter(adapter);
    }

    private void initTab() {
        mTabLayout.setSelectedTabIndicatorColor(getResources().getColor(R.color.bulue_main));
        mTabLayout.setupWithViewPager(mViewPager);
        displayTabText();  //TabLayout关联ViewPager后才设置标题文字，标题个数是根据Fragment个数来确定的
    }

    /**刷新显示TabLayout菜单*/
    private void displayTabText() {
        int i = 0;
        for (Map.Entry<Integer, BaseFragment> mapEn: fragmentMap.entrySet()) {
            mTabLayout.getTabAt(i).setText(mainTitlesArray[mapEn.getKey()]);
            i++;
        }
    }

    private void setBackOnclick() {
        findViewById(R.id.CX_Act_Back_Tv).setOnClickListener(this);
        findViewById(R.id.CX_Act_More_Tv).setOnClickListener(this);
        ((TextView)findViewById(R.id.CX_Act_Title_Tv)).setText(orderInfoEn.bussTypeName);
        ((TextView)findViewById(R.id.CX_Act_More_Tv)).setText("");
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.CX_Act_Back_Tv: ActivityFinishUtil.showFinishAlert(this); break; //点击返回键
//            case R.id.CX_Act_More_Tv: showSaveDialog(); break; //点击保存或暂存键
        }
    }

    private void dowloadDictType() {
        LoadDialogUtil.setMessageAndShow(this,"载入中……");
        List<String> params = new ArrayList<String>(2);
        params.add("type");
//        params.add("medicalFee,casualtiesFee,poNature,deliveryCompany"); // 医疗费用赔偿MedicalFee ,死亡伤残赔偿casualtiesFee ,户籍性质poNature
        params.add("cxOrderWorkImageType,accident_type,accident_small_type,accident_reason,accident_small_reason,survey_type,damage_loss_type," +
                "accident_liability,loss_type,loss_object_type,compensation_method,survey_conclusion,carno_type,car_usetype,injured_type");
        HttpUtils.requestGet(URLs.CX_NEW_GET_IMG_TYPE_DICT, params, HttpRequestTool.CX_NEW_GET_IMG_TYPE_DICT);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void evnetH5(List<NameValuePair> values) {
        int responsecode = Integer.parseInt(values.get(0).getName());
        switch (responsecode) {
            case HttpRequestTool.CX_NEW_GET_IMG_TYPE_DICT:
//                dowloadOderView();
                LoadDialogUtil.dismissDialog();
                cxDict.list = JSON.parseArray(values.get(0).getValue(), CxDictEntity.DictData.class);
                break;
            case HttpRequestTool.UPLOAD_FILE_PHOTO: //上传附件成功
//                fg3.getUploadFileInfo(values);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

}
