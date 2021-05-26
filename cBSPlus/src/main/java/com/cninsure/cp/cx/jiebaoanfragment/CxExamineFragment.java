package com.cninsure.cp.cx.jiebaoanfragment;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.alibaba.fastjson.JSON;
import com.cninsure.cp.AppApplication;
import com.cninsure.cp.R;
import com.cninsure.cp.cx.CxJieBaoanInfoActivity;
import com.cninsure.cp.cx.adapter.RemarkAdapter;
import com.cninsure.cp.cx.fragment.BaseFragment;
import com.cninsure.cp.entity.PublicOrderEntity;
import com.cninsure.cp.entity.URLs;
import com.cninsure.cp.entity.cx.CxDictEntity;
import com.cninsure.cp.entity.cx.CxExamineEntity;
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
import java.util.List;

public class CxExamineFragment extends BaseFragment {
    private LayoutInflater inflater;
    private Activity activity;
    private View contentView;

    public String QorderUid;
    public PublicOrderEntity orderInfoEn; //任务信息

    @ViewInject(R.id.examineMsg_listView) private ListView examineListView;
    @ViewInject(R.id.LeavingMsg_text_submit_button) private Button submitButton;
    @ViewInject(R.id.examine_input_Edittext) private EditText contentEdit;

    private List<CxExamineEntity> examineList;



    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.inflater = inflater;
        contentView = inflater.inflate(R.layout.examine_layout, null);
        activity = getActivity();
        ViewUtils.inject(this, contentView);
        orderInfoEn = (PublicOrderEntity) activity.getIntent().getSerializableExtra("PublicOrderEntity");
        QorderUid = activity.getIntent().getStringExtra("orderUid");
        initView();
        getExamineInfo();
        return contentView;
    }

    /**
     * 下载留言信息
     */
    private void getExamineInfo() {
        if (examineList==null || examineList.size()==0) {
            downLoadExamineInfo();
        }else{
            displayExamine();
        }
    }
    /**
     * 下载留言信息
     */
    private void downLoadExamineInfo() {
            LoadDialogUtil.setMessageAndShow(activity,"载入中……");
            List<String> params = new ArrayList<>(2);
            params.add("userId");
            params.add(AppApplication.getInstance().USER.data.id+"");
            params.add("orderUid");
            params.add(orderInfoEn.uid);
            HttpUtils.requestGet(URLs.CX_GET_EXAMINE_INFO, params, HttpRequestTool.CX_GET_EXAMINE_INFO);
    }



    private void initView() {
        examineListView.setEmptyView(contentView.findViewById(R.id.examineMsg_empty_textView));
        submitButton.setOnClickListener(v -> displayExamineMsg());
    }

    /**弹出上传文本信息Dialog填写并上传文本备注**/
    private void displayExamineMsg() {
        String mmsg=contentEdit.getText().toString();
        if (TextUtils.isEmpty(mmsg)) {
            ToastUtil.showToastLong(activity, "未录入任何内容！");
        }else {
            submitTextExamine(mmsg);
        }
    }

    /**保存文本备注信息**/
    private void submitTextExamine(String msg) {
        List<NameValuePair> httpParams = new ArrayList<>();
        httpParams.add(new BasicNameValuePair("userId", AppApplication.USER.data.userId+""));
        httpParams.add(new BasicNameValuePair("orderUid", orderInfoEn.uid));
        httpParams.add(new BasicNameValuePair("content", msg));
        HttpUtils.requestPost(URLs.CX_SAVE_EXAMINE_INFO, httpParams, HttpRequestTool.CX_SAVE_EXAMINE_INFO);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void evnetResult(List<NameValuePair> values) {
        int responsecode = Integer.parseInt(values.get(0).getName());
        switch (responsecode) {
            case HttpRequestTool.CX_GET_EXAMINE_INFO: //获取留言信息
                LoadDialogUtil.dismissDialog();
                analysisExamine(values.get(0).getValue());
                break;
            case HttpRequestTool.CX_SAVE_EXAMINE_INFO: //保存留言信息
                LoadDialogUtil.dismissDialog();
                downLoadExamineInfo();
                contentEdit.setText("");
                break;
            default:
                break;
        }
    }

    /**解析留言并显示*/
    private void analysisExamine(String value) {
        examineList = JSON.parseArray(value,CxExamineEntity.class);
        displayExamine();
    }

    private void displayExamine(){
        RemarkAdapter adapter  = new RemarkAdapter(activity,examineList);
        examineListView.setAdapter(adapter);
    }


    @Override
    public void SaveDataToEntity() {  }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();  }
    @Override
    public void onResume() {
        super.onResume();
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }  }
    @Override
    public void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }
}
