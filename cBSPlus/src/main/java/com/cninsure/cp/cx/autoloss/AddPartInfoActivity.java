package com.cninsure.cp.cx.autoloss;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.cninsure.cp.AppApplication;
import com.cninsure.cp.BaseActivity;
import com.cninsure.cp.R;
import com.cninsure.cp.cx.autoloss.entity.damagedarea.DamagedAreaChildrens;
import com.cninsure.cp.cx.autoloss.entity.damagedarea.DamagedAreaParent;
import com.cninsure.cp.cx.autoloss.entity.damagedarea.DamagedAreaTable;
import com.cninsure.cp.entity.URLs;
import com.cninsure.cp.entity.cx.CxDsWorkEntity;
import com.cninsure.cp.utils.DialogUtil;
import com.cninsure.cp.utils.HttpRequestTool;
import com.cninsure.cp.utils.HttpUtils;
import com.cninsure.cp.utils.LoadDialogUtil;
import com.cninsure.cp.utils.SetTextUtil;
import com.cninsure.cp.utils.ToastUtil;
import com.cninsure.cp.utils.regex.RegexUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import org.apache.http.NameValuePair;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * @author :xy-wm
 * date:2021/5/25 20:06
 * usefuLness: CBS_APP
 */
public class AddPartInfoActivity extends BaseActivity {

    @ViewInject(R.id.cxdsAddPart_listView) private ListView listView;
    @ViewInject(R.id.cxdsAddPart_paiJianKu) private TextView peiJKTv; //配件库

    private View footerView;
    private Button addButton;
    private PartShowAdapter adapter;

    private CxDsWorkEntity workEntity; //智能定损信息。
    private List<CxDsWorkEntity.CxDsReplaceInfos> replaceInfoList; //换件项目
    private DamagedAreaTable damagedAreaTable; //换件项目
    private Dialog dialog; //选着受损部位的弹框


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cx_ds_add_parts_info);
        EventBus.getDefault().register(this);
        ViewUtils.inject(this);
        workEntity = (CxDsWorkEntity) getIntent().getSerializableExtra("CxDsWorkEntity");
        replaceInfoList = new ArrayList<>();
        downloadInfo();
    }

    private void initView() {
        findViewById(R.id.cxdsAddPart_back).setOnClickListener(v -> AddPartInfoActivity.this.finish()); //返回按钮
        findViewById(R.id.cxdsAddPart_paiJianKu).setOnClickListener(v -> {
        }); //返回按钮
        initFooterView();
    }

    private void initFooterView() {
        footerView = LayoutInflater.from(this).inflate(R.layout.add_layout,null);
        addButton = footerView.findViewById(R.id.add_Layout_button);
        addButton.setText("+ 添加");
        listView.addFooterView(footerView);
        addButton.setOnClickListener(v -> {
            if (replaceInfoList==null) replaceInfoList = new ArrayList<>();
            replaceInfoList.add(new CxDsWorkEntity.CxDsReplaceInfos());
            adapter.notifyDataSetChanged();
        });
        if (workEntity!=null && workEntity.replaceInfos!=null){
            replaceInfoList.addAll(workEntity.replaceInfos);
        }
        adapter = new PartShowAdapter();
        listView.setAdapter(adapter);
    }

    /**获取配件部位信息*/
    private void downloadInfo() {
        LoadDialogUtil.setMessageAndShow(this,"载入中……");
        List<String> paramlist = new ArrayList<String>();
        HttpUtils.requestGet(URLs.CX_GET_CAR_PEIJIAN_LIST, paramlist, HttpRequestTool.CX_GET_CAR_PEIJIAN_LIST);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void evnet(List<NameValuePair> values) {
        int responsecode = Integer.parseInt(values.get(0).getName());
        switch (responsecode) {
            case HttpRequestTool.CX_GET_CAR_PEIJIAN_LIST:
                LoadDialogUtil.dismissDialog();
                getPeijianInfo(values.get(0).getValue());
                break;
            default:
                break;
        }
    }

    private void getPeijianInfo(String value) {
        if (!TextUtils.isEmpty(value)){
            try {
                damagedAreaTable = JSON.parseObject(value,DamagedAreaTable.class);
                if (damagedAreaTable==null || damagedAreaTable.tableData==null || damagedAreaTable.tableData.size()==0
                 || damagedAreaTable.tableData.get(0)==null || damagedAreaTable.tableData.get(0).childrens==null){
                    ToastUtil.showToastLong(AppApplication.getInstance(),"获取损失部位信息失败！！");
                    this.finish();
                }
                initView();
            } catch (Exception e) {
                e.printStackTrace();
                ToastUtil.showToastLong(AppApplication.getInstance(),"获取损失部位信息失败！！");
                this.finish();
            }
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }




    private class PartShowAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            if (replaceInfoList!=null) return replaceInfoList.size();
            return 0;
        }

        @Override
        public Object getItem(int position) {
            if (replaceInfoList!=null) return replaceInfoList.get(position);
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder vh = new ViewHolder();
            convertView = AddPartInfoActivity.this.getLayoutInflater().inflate(R.layout.cx_ds_add_parts_item,null);
            ViewUtils.inject(vh,convertView);

            CxDsWorkEntity.CxDsReplaceInfos replaceInfos = replaceInfoList.get(position);
            if (replaceInfos!=null){
                displayPartInfo(vh,replaceInfos,position);
            }

            return convertView;
        }

        /**
         * 显示信息
         * @param vh
         * @param replaceInfos
         */
        private void displayPartInfo(ViewHolder vh, CxDsWorkEntity.CxDsReplaceInfos replaceInfos,int position) {
            SetTextUtil.setTextViewText(vh.partNumberTv, (position+1)+"");
            SetTextUtil.setTextViewText(vh.partNameEdt, replaceInfos.partName);
            SetTextUtil.setTextViewText(vh.unitTotalPriceTv, "￥"+(replaceInfos.unitTotalPrice==null?"0":replaceInfos.unitTotalPrice+""));
            SetTextUtil.setTextViewText(vh.unitPriceEdt, replaceInfos.unitPrice+"");
            SetTextUtil.setTextViewText(vh.partPosIdOrlocalPriceEdt, replaceInfos.localPrice+"");
            SetTextUtil.setTextViewText(vh.partCodeEdt, replaceInfos.partCode);
            SetTextUtil.setTextViewText(vh.unitCountTv, replaceInfos.unitCount+"");
            SetTextUtil.setTextViewText(vh.remarkEdt, replaceInfos.remark);
//            SetTextUtil.setTextViewText(vh.changeTitleTv, (TextUtils.isEmpty(replaceInfos.partId)?"配件部位":"辅助定价"));
            if (TextUtils.isEmpty(replaceInfos.partId)){
                SetTextUtil.setTextViewText(vh.changeTitleTv, "配件部位");
                displayPosName(vh.partPosIdOrlocalPriceEdt,replaceInfos);
            }else{
                SetTextUtil.setTextViewText(vh.changeTitleTv, "辅助定价");
            }
            if (TextUtils.isEmpty(replaceInfos.partId)) showDamageAreaChoiceDialog(vh,replaceInfos,vh.partPosIdOrlocalPriceEdt);

            //设置监听，输入值后立马保存到对象
            addTextChange(vh.partNameEdt,replaceInfos,1,vh);
            addTextChange(vh.unitTotalPriceTv,replaceInfos,2,vh);
            addTextChange(vh.unitPriceEdt,replaceInfos,3,vh);
            addTextChange(vh.partPosIdOrlocalPriceEdt,replaceInfos,4,vh);
            addTextChange(vh.partCodeEdt,replaceInfos,5,vh);
            addTextChange(vh.remarkEdt,replaceInfos,6,vh);


            //删除配件
            vh.deleteTv.setOnClickListener(v -> {
                if (replaceInfoList!=null){
                    replaceInfoList.remove(position);
                    adapter.notifyDataSetChanged();
                }
            });
            //配件数量减
            vh.hsUnitCountJianTv.setOnClickListener(v -> {
                if (replaceInfos.unitCount==null) replaceInfos.unitCount=0;
                if (replaceInfos.unitCount>0) replaceInfos.setUnitCount(replaceInfos.unitCount-1);
                adapter.notifyDataSetChanged();
            });
            //配件数量加
            vh.hsUnitCountAddTv.setOnClickListener(v -> {
                if (replaceInfos.unitCount==null) replaceInfos.unitCount=0;
                replaceInfos.setUnitCount(replaceInfos.unitCount+1);
                adapter.notifyDataSetChanged();
            });

        }

        /**
         * 显示配件部位名称
         * @param posNameTv
         * @param replaceInfos
         */
        private void displayPosName(TextView posNameTv, CxDsWorkEntity.CxDsReplaceInfos replaceInfos) {
            for (DamagedAreaParent parent:damagedAreaTable.tableData.get(0).childrens){
                for (DamagedAreaChildrens childrens:parent.childrens){
                    if (childrens.posId.equals(replaceInfos.partPosId)){
                        SetTextUtil.setTextViewText(posNameTv,childrens.posName);
                        return;
                    }
                }
            }
        }


        class ViewHolder{
            @ViewInject(R.id.cxdsAddPart_partNumber) TextView partNumberTv; //配件序号
            @ViewInject(R.id.cxdsAddPart_partName) EditText partNameEdt; //配件名称
            @ViewInject(R.id.cxdsAddPart_unitTotalPrice) TextView unitTotalPriceTv; //定损小计
            @ViewInject(R.id.cxdsAddPart_delete) TextView deleteTv; //删除配件
            @ViewInject(R.id.cxdsAddPart_unitPrice) EditText unitPriceEdt; //定损单价
            @ViewInject(R.id.cxdsAddPart_partPosIdOrlocalPrice) TextView partPosIdOrlocalPriceEdt; //辅助定价
            @ViewInject(R.id.cxdsAddPart_changeTitle) TextView changeTitleTv; //辅助定价标题，如果是自定义控件，标题为“配件部位”且点击后面的内容选着配件部位
            @ViewInject(R.id.cxdsAddPart_partCode) EditText partCodeEdt; //配件编号
            @ViewInject(R.id.cxdsAddPart_hsUnitCountJian) TextView hsUnitCountJianTv; //配件数量减号
            @ViewInject(R.id.cxdsAddPart_hsUnitCount) TextView unitCountTv; //配件数量
            @ViewInject(R.id.cxdsAddPart_hsUnitCountAdd) TextView hsUnitCountAddTv; //配件数量加
            @ViewInject(R.id.cxdsAddPart_remark) EditText remarkEdt; //定损备注
        }
    }


    /**
     * 设置监听，录入数据后保存到实体类
     * @param tv
     * @param replaceInfos
     * @param type
     */
    private void addTextChange(TextView tv, CxDsWorkEntity.CxDsReplaceInfos replaceInfos, int type, PartShowAdapter.ViewHolder vh){
        tv.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override public void afterTextChanged(Editable s) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String str = s.toString();
               switch (type){
                   case 1:replaceInfos.partName = s.toString();break; //配件名称
                   case 2:replaceInfos.unitTotalPrice = (RegexUtils.checkDecimals(str)?Float.valueOf(str):0);break;//定损小计
                   case 3:replaceInfos.setUnitPrice( (RegexUtils.checkDecimals(str)?Float.valueOf(str):0));
                       SetTextUtil.setTextViewText(vh.unitTotalPriceTv, "￥"+(replaceInfos.unitTotalPrice==null?"0":replaceInfos.unitTotalPrice+""));
                       break;//定损单价
                   case 4:replaceInfos.localPrice = (RegexUtils.checkDecimals(str)?Float.valueOf(str):0);break;//辅助定价
                   case 5:replaceInfos.partCode = s.toString();break;//配件编号
                   case 6:replaceInfos.remark = s.toString();break;//定损备注
               }
            }
        });
    }



    /**
     * 弹框选择受损部位
     * @param vh
     * @param replaceInfos
     */
    private void showDamageAreaChoiceDialog(PartShowAdapter.ViewHolder vh, CxDsWorkEntity.CxDsReplaceInfos replaceInfos,TextView posNameTv) {
        vh.partPosIdOrlocalPriceEdt.setOnClickListener(v -> {
            dialog = new AlertDialog.Builder(AddPartInfoActivity.this).setTitle("配件部位选择")
                    .setView(getChoiceDamageAreaView(replaceInfos,posNameTv))
                    .setNeutralButton("取消", null).create();
            dialog.show();
        });
    }

    /**
     * 获取选着受损部位的View
     * @param replaceInfos
     * @return
     */
    private View getChoiceDamageAreaView(CxDsWorkEntity.CxDsReplaceInfos replaceInfos,TextView posNameTv) {
        View view = AddPartInfoActivity.this.getLayoutInflater().inflate(R.layout.two_listview_linkage_layout,null);
        DamageAreaAdapter parentAdapter = new DamageAreaAdapter(replaceInfos,1,null,view);
        ListView listView = view.findViewById(R.id.twoListLinkage_listOne);
        listView.setAdapter(parentAdapter);
        listView.setOnItemClickListener((parent, view1, position, id) -> {
            parentAdapter.parentId = damagedAreaTable.tableData.get(0).childrens.get(position).posId;
            disPlayChildListView(view, replaceInfos, parentAdapter.parentId);
            parentAdapter.notifyDataSetChanged();
        });
        return view;
    }

    /**显示配件部位子类列表*/
    private void disPlayChildListView(View view,CxDsWorkEntity.CxDsReplaceInfos replaceInfos,String parentId) {
        ListView listView = view.findViewById(R.id.twoListLinkage_listThree);
        DamageAreaAdapter childAdapter = new DamageAreaAdapter(replaceInfos,2,parentId,view);
        listView.setAdapter(childAdapter);
        listView.setOnItemClickListener((parent, view1, position, id) -> {
            replaceInfos.partPosId = childAdapter.DaChildrens.get(position).posId;
            adapter.notifyDataSetChanged();
            dialog.dismiss();
        });
    }


    private class DamageAreaAdapter extends BaseAdapter{
        private CxDsWorkEntity.CxDsReplaceInfos replaceInfos;
        public String parentId;
//        private String damageAreaPatentId,damageAreaChildId; //选择损失部位的父级id和子id
        /**1=显示父级信息，2=显示子类信息**/
        private int type; //显示类型：1=显示父级信息，2=显示子类信息
        private List<DamagedAreaChildrens> DaChildrens; //配件部位子类集合
        private View view;


        private DamageAreaAdapter(){}
        public DamageAreaAdapter(CxDsWorkEntity.CxDsReplaceInfos replaceInfos,int type,String parentId,View view){
            this.replaceInfos = replaceInfos;
            this.type = type;
            this.view = view;
            this.parentId = parentId;
            if (TextUtils.isEmpty(parentId) && type == 1)getParentId(); //从父类点击进入的时候会传一个parentId过来，子类parentId为null.
            if (type == 2) getDaChildrens();
        }

        /**
         * 获取配件部位子类集合
         */
        private void getDaChildrens() {
            if (TextUtils.isEmpty(parentId)){
                parentId = damagedAreaTable.tableData.get(0).childrens.get(0).posId;
            }
            for (DamagedAreaParent parent:damagedAreaTable.tableData.get(0).childrens) {
                if (parentId.equals(parent.posId)) {
                    DaChildrens = parent.childrens;
                    return;
                }
            }
        }

        /**
         * 获取 配件部位
         */
        private void getParentId() {
            if (!TextUtils.isEmpty(parentId)) {
            } else if (TextUtils.isEmpty(replaceInfos.partPosId)){ //如果换件项目没有partPosId，则默认 配件部位父级id为第一个
                parentId = damagedAreaTable.tableData.get(0).childrens.get(0).posId;
            }else{
                //如果换件项目有partPosId，则找到对应的配件部位父级id
                for (DamagedAreaParent parent:damagedAreaTable.tableData.get(0).childrens){
                    for (DamagedAreaChildrens childrens:parent.childrens){
                        if (childrens.posId.equals(replaceInfos.partPosId)){
                            parentId = parent.posId;
                            break;
                        }
                    }
                }
            }
            disPlayChildListView(view, replaceInfos, parentId);
        }


        @Override
        public int getCount() {
            if (type==1) return damagedAreaTable.tableData.get(0).childrens.size();
            if (type==2) return DaChildrens.size();
            return 0;
        }

        @Override
        public Object getItem(int position) {
            if (type==1) return damagedAreaTable.tableData.get(0).childrens.get(position);
            if (type==2) return DaChildrens.get(position);
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = AddPartInfoActivity.this.getLayoutInflater().inflate(android.R.layout.simple_list_item_1,null);
            if (type==1) { //父节点
                DamagedAreaParent damageParent = damagedAreaTable.tableData.get(0).childrens.get(position);
                SetTextUtil.setTextViewText(((TextView) convertView), damageParent.posName);
                convertView.setBackground(AddPartInfoActivity.this.getResources().getDrawable(R.color.hui_bg));
                if (TextUtils.isEmpty(parentId) && position == 0) { //如果换件项目中 配件所属部位id 为空，则默认选择受损部位第一个父节点。
                    convertView.setBackground(AddPartInfoActivity.this.getResources().getDrawable(R.color.white));
                } else if (parentId.equals(damageParent.posId)) {
                    convertView.setBackground(AddPartInfoActivity.this.getResources().getDrawable(R.color.white));
                }
            }else{ //子节点
                DamagedAreaChildrens damageChildrens = DaChildrens.get(position);
                SetTextUtil.setTextViewText(((TextView) convertView), damageChildrens.posName);
                convertView.setBackground(AddPartInfoActivity.this.getResources().getDrawable(R.color.white));
                if (damageChildrens.posId.equals(replaceInfos.partPosId)) {
                    convertView.setBackground(AddPartInfoActivity.this.getResources().getDrawable(R.color.hui_bg));
                }
            }
            return convertView;
        }
    }
}
