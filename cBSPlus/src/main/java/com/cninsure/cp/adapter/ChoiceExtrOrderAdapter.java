package com.cninsure.cp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cninsure.cp.R;
import com.cninsure.cp.entity.cx.CxOrderEntity;
import com.cninsure.cp.utils.SetTextUtil;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChoiceExtrOrderAdapter extends BaseAdapter {

    private List<CxOrderEntity.CxOrderTable> list;
    private Context context;
    public Map<Long,Boolean> checkMap;//储存选中订单
    private ChoiceExtCallBack ccb; //方法回调修改Activity中的显示

    private ChoiceExtrOrderAdapter(){}
    public ChoiceExtrOrderAdapter(List<CxOrderEntity.CxOrderTable> list,Context context,ChoiceExtCallBack ccb){
        this.list = list;
        this.context=context;
        checkMap = new HashMap<>();
        this.ccb = ccb;
    }


    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder vHolder=null;
        if(convertView==null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.choice_extract_order_item,null);
            vHolder = new ViewHolder();
            ViewUtils.inject(vHolder, convertView);
            convertView.setTag(vHolder);
        }else{
            vHolder = (ViewHolder) convertView.getTag();
        }
        CxOrderEntity.CxOrderTable cOTable = list.get(position);

        SetTextUtil.setTextViewText(vHolder.orderNumTv,cOTable.uid);
        SetTextUtil.setTextViewText(vHolder.baoanNumTv,"报案号："+cOTable.caseBaoanNo);
        SetTextUtil.setTextViewText(vHolder.orderAmountTv,"￥"+cOTable.canPosAmount.toString());
        if (checkMap.get(cOTable.id)!=null){
            vHolder.cb.setChecked(true);
        }else{
            vHolder.cb.setChecked(false);
        }

        //实现点击选择和取消选择功能
       final CheckBox finalCb =  vHolder.cb;
        vHolder.checkLayout.setOnClickListener(v -> {
            if (checkMap.get(cOTable.id)==null){
                checkMap.put(cOTable.id,true);
                finalCb.setChecked(true);
            }else{
                checkMap.remove(Long.valueOf(cOTable.id));
                finalCb.setChecked(false);
            }
            ccb.setChang();
        });

        return convertView;
    }

    /**
     * 选中所有
     */
    public void choiceAll(boolean isAll) {
        if (isAll)  //全选
        for (CxOrderEntity.CxOrderTable tempTable:list){
            checkMap.put(tempTable.id,true);
        }
        else checkMap.clear(); //取消全选
        this.notifyDataSetChanged();
        ccb.setChang();
    }

    public class ViewHolder{
        @ViewInject(R.id.extr_order_item_checkLayout) public LinearLayout checkLayout; //item布局
        @ViewInject(R.id.extr_order_item_checkBox) public CheckBox cb; //是否选中
        @ViewInject(R.id.extr_order_item_orderNumber) public TextView orderNumTv; //任务（订单）编号
        @ViewInject(R.id.extr_order_item_baoanNumber) public TextView baoanNumTv; //报案号
        @ViewInject(R.id.extr_order_item_amount) public TextView orderAmountTv; //可提现金额
    }

    public interface ChoiceExtCallBack{
        public void setChang();
    }
}
