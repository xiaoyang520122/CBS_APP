package com.cninsure.cp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.cninsure.cp.R;
import com.cninsure.cp.entity.extract.CxOrderPosApplyStatus;
import com.cninsure.cp.entity.extract.CxOrderPosApplyTable;
import com.cninsure.cp.utils.CopyUtils;
import com.cninsure.cp.utils.SetTextUtil;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import java.util.List;

/**
 * @author :xy-wm
 * date:2020/11/5 16:31
 * usefuLness: CBS_APP
 */
public class ExtractHistoryAdapter extends BaseAdapter {
    List<CxOrderPosApplyTable> list;//申请列表
    private Context context;

    private ExtractHistoryAdapter(){}
    public ExtractHistoryAdapter(List<CxOrderPosApplyTable> list, Context context){
        this.list = list;
        this.context=context;
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
            convertView = LayoutInflater.from(context).inflate(R.layout.cextract_history_item,null);
            vHolder = new ViewHolder();
            ViewUtils.inject(vHolder, convertView);
            convertView.setTag(vHolder);
        }else{
            vHolder = (ViewHolder) convertView.getTag();
        }
        CxOrderPosApplyTable copat = list.get(position);
        SetTextUtil.setTextViewText(vHolder.piciNumTv,copat.uid); //申请批次号
        CopyUtils.setCopyOnclickListener(context,vHolder.piciNumTv,copat.uid);
//        SetTextUtil.setTextViewText(vHolder.ggsInfoTv,copat.ggsName+"-"+copat.orgName); //公估师名称和归属
        SetTextUtil.setTextViewText(vHolder.applyTimeTv,copat.applyTime);//提交时间
        SetTextUtil.setTextViewText(vHolder.amountTv,copat.posAmount+""); //提现金额
        SetTextUtil.setTextViewText(vHolder.statuTv, CxOrderPosApplyStatus.getExtractApplyStatus(copat.status));//申请状态
        return convertView;
    }

    public class ViewHolder{
        @ViewInject(R.id.extHistory_piciNum) public TextView piciNumTv; //申请批次号
//        @ViewInject(R.id.extHistory_ggsInfo) public TextView ggsInfoTv; //公估师名称和归属
        @ViewInject(R.id.extHistory_applyTime) public TextView applyTimeTv; //提交时间
        @ViewInject(R.id.extHistory_status) public TextView statuTv; //申请状态
        @ViewInject(R.id.extHistory_amount) public TextView amountTv; //提现金额
    }
}
