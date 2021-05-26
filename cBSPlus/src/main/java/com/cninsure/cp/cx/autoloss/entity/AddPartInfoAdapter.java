package com.cninsure.cp.cx.autoloss.entity;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.cninsure.cp.R;
import com.cninsure.cp.entity.cx.CxDsWorkEntity;
import com.cninsure.cp.utils.SetTextUtil;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import java.util.List;

/**
 * @author :xy-wm
 * date:2021/5/25 21:30
 * usefuLness: CBS_APP
 */
public class AddPartInfoAdapter extends BaseAdapter {
    private List<CxDsWorkEntity.CxDsReplaceInfos> replaceInfoList; //换件项目
    private LayoutInflater inflater;

    private AddPartInfoAdapter(){}
    public AddPartInfoAdapter(Context context, List<CxDsWorkEntity.CxDsReplaceInfos> replaceInfoList){
        this.replaceInfoList = replaceInfoList;
        inflater = LayoutInflater.from(context);
    }
    @Override
    public int getCount() {
        return replaceInfoList==null?0:replaceInfoList.size();
    }

    @Override
    public Object getItem(int position) {
        return replaceInfoList==null?null:replaceInfoList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder vh = new ViewHolder();
        convertView = inflater.inflate(R.layout.cx_ds_add_parts_item,null);
        ViewUtils.inject(vh,convertView);

        CxDsWorkEntity.CxDsReplaceInfos cxDsRep = replaceInfoList.get(position);
        if (TextUtils.isEmpty(cxDsRep.partId)){
            SetTextUtil.setTextViewText(vh.changeTitle,"配件部位");
            setSelectInfo(vh.partPosIdOrlocalPrice,cxDsRep);
        }

        return convertView;
    }


    /**
     * 如果是自定义配件，就需要添加选择配件部位。
     * @param partPosIdOrlocalPrice
     * @param cxDsRep
     */
    private void setSelectInfo(EditText partPosIdOrlocalPrice, CxDsWorkEntity.CxDsReplaceInfos cxDsRep) {
    }


    public class ViewHolder{
        @ViewInject(R.id.cxdsAddPart_partName) public EditText partName;
        @ViewInject(R.id.cxdsAddPart_unitTotalPrice) public TextView unitTotalPrice;
        @ViewInject(R.id.cxdsAddPart_delete) public TextView delete;
        @ViewInject(R.id.cxdsAddPart_unitPrice) public EditText unitPrice;
        @ViewInject(R.id.cxdsAddPart_changeTitle) public TextView changeTitle;
        @ViewInject(R.id.cxdsAddPart_partPosIdOrlocalPrice) public EditText partPosIdOrlocalPrice;
        @ViewInject(R.id.cxdsAddPart_hsUnitCount) public TextView hsUnitCount;
        @ViewInject(R.id.cxdsAddPart_remark) public EditText remark;
        @ViewInject(R.id.cxdsAddPart_hsUnitCountJian) public Button hsUnitCountJian;
        @ViewInject(R.id.cxdsAddPart_hsUnitCountAdd) public Button hsUnitCountAdd;
    }
}
