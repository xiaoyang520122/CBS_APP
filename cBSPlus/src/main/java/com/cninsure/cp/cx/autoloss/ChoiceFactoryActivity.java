package com.cninsure.cp.cx.autoloss;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.cninsure.cp.AppApplication;
import com.cninsure.cp.BaseActivity;
import com.cninsure.cp.R;
import com.cninsure.cp.cx.autoloss.entity.CategoryTable;
import com.cninsure.cp.cx.autoloss.entity.CategoryTableEntity;
import com.cninsure.cp.cx.autoloss.entity.ModelTable;
import com.cninsure.cp.cx.autoloss.entity.ModelTableEntity;
import com.cninsure.cp.entity.URLs;
import com.cninsure.cp.utils.DialogUtil;
import com.cninsure.cp.utils.HttpRequestTool;
import com.cninsure.cp.utils.HttpUtils;
import com.cninsure.cp.utils.LoadDialogUtil;
import com.cninsure.cp.utils.ToastUtil;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author :xy-wm
 * date:2021/5/24 21:22
 * usefuLness: CBS_APP
 */
public class ChoiceFactoryActivity extends BaseActivity {

    @ViewInject(R.id.chFactory_back) private TextView backTv;
    @ViewInject(R.id.chFactory_searchButton) private TextView searchTv;
    @ViewInject(R.id.chFactory_searchEdit) private EditText searchEdt;
    @ViewInject(R.id.chFactory_ListView) private ListView listView;

    private int cateLevel; //搜索类型：1：厂家；2：品牌；3：车系；4：车型
    private String cateParentId; //上一级别的id（查厂家时默认为1）
    private CategoryTableEntity ctEn; //
    private ModelTableEntity mtEn;//车型查询结果


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.choice_factory_activity);
        ViewUtils.inject(this);
        cateLevel = getIntent().getIntExtra("searchCateLevel",0);
        cateParentId = getIntent().getStringExtra("cateParentId");
        EventBus.getDefault().register(this);
        downloadInfo(null);  //默认搜索全部
        initView();
    }

    private void initView() {
        backTv.setOnClickListener(v -> ChoiceFactoryActivity.this.finish()); //点击返回按钮
        searchTv.setOnClickListener(v -> downloadInfo(searchEdt.getText().toString()));  //点击搜索按钮
    }

    /**获取厂家信息*/
    private void downloadInfo(String cateName) {
        if (cateLevel==0){
           DialogUtil.getRightDialogOnDismiss(this, "没有搜索类型！", dialog1 -> ChoiceFactoryActivity.this.finish()).show();
           return;
        }
        if (TextUtils.isEmpty(cateParentId) || "null".equals(cateParentId)){
           DialogUtil.getRightDialogOnDismiss(this, "请先选择上级类型！", dialog1 -> ChoiceFactoryActivity.this.finish()).show();
           return;
        }
        LoadDialogUtil.setMessageAndShow(this,"载入中……");
        List<String> paramlist = new ArrayList<String>();
        paramlist.add("start");
        paramlist.add("0");
        paramlist.add("size");
        paramlist.add("200");

        //modelCateId
        if (cateLevel == 4){ //车型查询，字段为modelCateId
            paramlist.add("modelCateId");
            paramlist.add(cateParentId);
        }else{ //厂家；品牌；车系 查询字段为cateParentId，值为  cateLevel
            paramlist.add("cateParentId");
            paramlist.add(cateParentId);
        }

        paramlist.add("cateCountry");
        paramlist.add("0");

        if (!TextUtils.isEmpty(cateName)){  //添加搜索条件
            if (cateLevel == 4){ //车型查询，modelStandardName
                paramlist.add("modelStandardName");
            }else{ //厂家；品牌；车系 查询字段为cateName
                paramlist.add("cateName");
            }
            paramlist.add(cateName);
        }
        if (cateLevel == 4){ //车型查询
            HttpUtils.requestGet(URLs.CX_GET_CAR_MODELS_LIST, paramlist, HttpRequestTool.CX_GET_CAR_MODELS_LIST);
        }else{ //厂家；品牌；车系 查询
            HttpUtils.requestGet(URLs.CX_GET_CAR_FACTORY_LIST, paramlist, HttpRequestTool.CX_GET_CAR_FACTORY_LIST);
        }


    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void evnet(List<NameValuePair> values) {
        int responsecode = Integer.parseInt(values.get(0).getName());
        switch (responsecode) {
            case HttpRequestTool.CX_GET_CAR_FACTORY_LIST:
                LoadDialogUtil.dismissDialog();
                getceEnInfo(values.get(0).getValue());
                break;
            case HttpRequestTool.CX_GET_CAR_MODELS_LIST:
                LoadDialogUtil.dismissDialog();
                getModelEnInfo(values.get(0).getValue());
                break;
            default:
                break;
        }
    }

    /**
     * 车型查询结果显示
     * @param value
     */
    private void getModelEnInfo(String value) {
        try {
            mtEn = JSON.parseObject(value,ModelTableEntity.class);
        } catch (Exception e) {
            ToastUtil.showToastLong(this, "获取字典信息失败！");
            ChoiceFactoryActivity.this.finish();
            e.printStackTrace();
        }
        if (mtEn!=null && mtEn.tableData !=null){
            disPlayListInfo();
        }
    }

    /**
     * //厂家；品牌；车系 查询结果显示
     * @param value
     */
    private void getceEnInfo(String value) {
        try {
            ctEn = JSON.parseObject(value,CategoryTableEntity.class);
        } catch (Exception e) {
            ToastUtil.showToastLong(this, "获取字典信息失败！");
            ChoiceFactoryActivity.this.finish();
            e.printStackTrace();
        }
        if (ctEn!=null && ctEn.tableData !=null){
            disPlayListInfo();
        }
        
    }

    private void disPlayListInfo() {
        String []nameArr = null;
        if (cateLevel == 4){ //车型查询
           nameArr = mtEn.tableData.getArrsCateName();
        }else{ //厂家；品牌；车系 查询
            nameArr = ctEn.tableData.getArrsCateName();
        }
        listView.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,nameArr));

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (cateLevel == 4){ //车型查询
                    Map<Integer, ModelTable> values = new HashMap<>();
                    values.put(cateLevel,mtEn.tableData.data.get(position));
                    EventBus.getDefault().post(values);//选择项目后通过EventBus返回选择项目及分类信息
                }else{ //厂家；品牌；车系 查询
                    Map<Integer, CategoryTable> values = new HashMap<>();
                    values.put(cateLevel,ctEn.tableData.data.get(position));
                    EventBus.getDefault().post(values);//选择项目后通过EventBus返回选择项目及分类信息
                }
                ChoiceFactoryActivity.this.finish();  //选择项目后关闭当前界面
            }
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
