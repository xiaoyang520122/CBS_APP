package com.cninsure.cp.activity.yjx;

import java.util.List;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.cninsure.cp.R;
import com.cninsure.cp.entity.yjx.YjxCaseBaoanInjuredTable;
import com.cninsure.cp.utils.DialogUtil;
import com.cninsure.cp.utils.IDCardUtil;
import com.cninsure.cp.utils.SetTextUtil;
import com.cninsure.cp.utils.ViewDisableUtil;

public class InjAdapter extends BaseAdapter {
	private List<YjxCaseBaoanInjuredTable> inLists;
	private LayoutInflater inflater;
	private Context context;
	private Handler addInjuredHandler;

	@SuppressWarnings("unused")
	// 私有化。
	private InjAdapter() {
	};

	public InjAdapter(List<YjxCaseBaoanInjuredTable> injuredLists, Context context,Handler handler) {
		this.inLists = injuredLists;
		inflater = LayoutInflater.from(context);
		this.context = context;
		this.addInjuredHandler = handler;
	}

	@Override
	public int getCount() {
		if (inLists==null || inLists.size()==0) {
			return 1;
		}else {
			return inLists.size();
		}
	}

	@Override
	public Object getItem(int arg0) {
		if (inLists==null || inLists.size()==0) {
			return null;
		}else {
			return inLists.get(arg0);
		}
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(int arg0, View conView, ViewGroup arg2) {
		if (inLists==null || inLists.size()==0) {
			return inflater.inflate(R.layout.yjx_injured_empty, null);
		}
		conView = inflater.inflate(R.layout.yjx_shangzhe_info_view, null);
		SetTextUtil.setTextViewText((TextView) conView.findViewById(R.id.YJXSZV_SZtitle), " ▋伤者" + (arg0 + 1));
		final YjxCaseBaoanInjuredTable InJEn = inLists.get(arg0);
		if (InJEn != null) {
			SetTextUtil.setTextViewText((TextView) conView.findViewById(R.id.YJXSZV_SZname), InJEn.name); //伤者姓名
			final TextView cardTv = (TextView) conView.findViewById(R.id.YJXSZV_SZcardNo);
			SetTextUtil.setTextViewText(cardTv, InJEn.idCard); // 伤者身份号码
			SetTextUtil.setTextViewText((TextView) conView.findViewById(R.id.YJXSZV_SZHospital), InJEn.hospital); // 医院
			SetTextUtil.setTextViewText((TextView) conView.findViewById(R.id.YJXSZV_SZDiagnosis), InJEn.diagnostic); // 诊断结果
			final TextView sexTv = (TextView) conView.findViewById(R.id.YJXSZV_SZsex);
			SetTextUtil.setTextViewText(sexTv, IDCardUtil.getSex(InJEn.idCard));
			setDeleteOnclick(arg0, (TextView) conView.findViewById(R.id.YJXSZV_SZDelete));
			setConViewOnclick(conView.findViewById(R.id.YJXSZV_SZtitle),arg0);
		}
		ViewDisableUtil.disableSubControls((LinearLayout)conView);
		return conView;
	}

	private void setConViewOnclick(View conView, final int itemCode) {
		
		conView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				addInjuredHandler.sendEmptyMessage(itemCode);
			}
		});
	}

	private void setDeleteOnclick(final int postion, final TextView deleteTv) {
		deleteTv.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				showHintDialog(postion, deleteTv);
			}
		});
	}

	private void showHintDialog(final int postion, TextView deleteTv) {
		DialogUtil.getAlertOnelistener(context, "您确定要删除该条伤者信息吗？", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				inLists.remove(postion);
				notifyDataSetChanged();
			}
		}).show();
	}
}
