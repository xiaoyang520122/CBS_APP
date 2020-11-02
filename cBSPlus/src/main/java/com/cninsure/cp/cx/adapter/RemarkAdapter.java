package com.cninsure.cp.cx.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.cninsure.cp.R;
import com.cninsure.cp.entity.cx.CxExamineEntity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class RemarkAdapter extends BaseAdapter {

    @SuppressLint("SimpleDateFormat")
    private SimpleDateFormat sf=new SimpleDateFormat("yyyy年MM月dd日 HH点mm分");
    @SuppressLint("SimpleDateFormat")
    private SimpleDateFormat sfs=new SimpleDateFormat("录音备注 dd/MM/yy");
    private LayoutInflater inflater;
    private List<CxExamineEntity> leavingMsgList;
    private Context context;

    private RemarkAdapter(){};

    public RemarkAdapter(Context context,List<CxExamineEntity> leavingMsgList){
        inflater=LayoutInflater.from(context);
        this.leavingMsgList = leavingMsgList;
        this.context = context;
    }
    @Override
    public int getCount() {
        if (leavingMsgList.size()==0) {
            return 1;
        }else {
            return leavingMsgList.size();
        }
    }

    @Override
    public Object getItem(int arg0) {
        if (leavingMsgList.size()==0) {
            return 0;
        }else {
            return leavingMsgList.get(arg0);
        }
    }

    @Override
    public long getItemId(int arg0) {
        return arg0;
    }

    @Override
    public View getView(int arg0, View conView, ViewGroup arg2) {
        conView =inflater.inflate(R.layout.audio_list_item, null);
        if (leavingMsgList.size()==0) {
            ((TextView)conView.findViewById(R.id.audiolistitem_time)).setText("暂无留言备注信息！");
            return conView;
        }

        final CxExamineEntity entity=leavingMsgList.get(arg0);
        TextView mesgTextView;
        ImageView titleimage;
//        if (entity.docType.equals("textMemo") || entity.docType.equals("audio")) { //前端沟通记录
            conView =inflater.inflate(R.layout.audio_list_item_yellow, null);
//            String time=sf.format(new Date(entity.createDate));
            ((TextView)conView.findViewById(R.id.audiolistitemy_time)).setText(entity.createTime);
            mesgTextView=(TextView)conView.findViewById(R.id.audiolistitemy_audioLong);
//            checkBoxP=(CheckBox) conView.findViewById(R.id.audiolistitemy_checkPlay);
//            titleimage=(ImageView)conView.findViewById(R.id.audiolistitemy_audioImag);
//        }else {//后台沟通信息
//            String time=sf.format(new Date(entity.createDate));
//            ((TextView)conView.findViewById(R.id.audiolistitem_time)).setText(time);
//            mesgTextView=(TextView)conView.findViewById(R.id.audiolistitem_audioLong);
//            checkBoxP=(CheckBox) conView.findViewById(R.id.audiolistitem_checkPlay);
//            titleimage=(ImageView)conView.findViewById(R.id.audiolistitem_audioImag);
//        }
//        titleimage.setVisibility(View.VISIBLE);
//        final DownAndPlayHelper downAndPlayHelper=new DownAndPlayHelper(activity);

//        if (entity.docType.equals("textMemo")) {//文本备注
            mesgTextView.setText(entity.content);
        mesgTextView.setTextColor(context.getResources().getColor(android.R.color.black));
//            titleimage.setVisibility(View.GONE);
//            checkBoxP.setVisibility(View.GONE);
//        }else if (entity.docType.equals("audio")) {//录音备注
////            checkBoxP.setVisibility(View.VISIBLE);
//            mesgTextView.setText("录音信息");
//        }else {
//            mesgTextView.setText(entity.memo);
//            titleimage.setVisibility(View.GONE);
////            checkBoxP.setVisibility(View.GONE);
//        }
        return conView;
    }
}
