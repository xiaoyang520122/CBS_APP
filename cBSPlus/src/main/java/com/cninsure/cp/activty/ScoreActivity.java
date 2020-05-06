package com.cninsure.cp.activty;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.cninsure.cp.AppApplication;
import com.cninsure.cp.BaseActivity;
import com.cninsure.cp.R;
import com.cninsure.cp.entity.ScoreCXEntity;
import com.cninsure.cp.entity.URLs;
import com.cninsure.cp.entity.fc.ScoreFCEntity;
import com.cninsure.cp.utils.CalendarUtil;
import com.cninsure.cp.utils.CheckHttpResult;
import com.cninsure.cp.utils.HttpRequestTool;
import com.cninsure.cp.utils.HttpUtils;
import com.cninsure.cp.view.CircleProgress;
import com.cninsure.cp.view.LoadingDialog;

public class ScoreActivity extends BaseActivity {
	
	private TextView actionTV1,actionTV2,actionTV3;
	private TextView feeAll ,countAll , cXProportionTv , FCProportionTv;
	private ProgressBar progressBar;
	private GridView gridViewCX,gridViewFC;
	private ScoreCXEntity MscoreCX,YscoreCX;
	private ScoreFCEntity MscoreFC,YscoreFC;
	private LoadingDialog loaddialog;
	private List<NameValuePair> params;
	/**等于2时代表已经请求获得所有车险何非车业务数据*/
	private int connectCount=0;
	private GridAdapter CXadapter,FCadapter;
	private float CXMScore,CXYScore,CXMFee,CXYFee,FCMScore,FCYScore,FCMFee,FCYFee;
	private ListView listView;
	private View scoreView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.score_activity);
		EventBus.getDefault().register(this);
		initActionView();
		initView();
		downloadScoreInfoCX(0);
		downloadScoreInfoFC(0);
	}


	private void initView() {
		listView=(ListView) findViewById(R.id.SCOREAC_listView);
		scoreView=LayoutInflater.from(this).inflate(R.layout.score_activity_gridview, null);
		gridViewCX=(GridView) scoreView.findViewById(R.id.SCOREAC_CX_gridView1);
		gridViewFC=(GridView) scoreView.findViewById(R.id.SCOREAC_FC_gridView1);
		feeAll=(TextView) findViewById(R.id.SCOREAC_FeeAll) ;
		countAll=(TextView) findViewById(R.id.SCOREAC_countAll) ;
		cXProportionTv=(TextView) findViewById(R.id.SCOREAC_CX_zb) ;
		FCProportionTv=(TextView) findViewById(R.id.SCOREAC_FC_zb) ;
		progressBar=(ProgressBar) findViewById(R.id.progressBar1) ;
	}

	private void initActionView() {
		loaddialog=new LoadingDialog(this);
		actionTV1 = (TextView) findViewById(R.id.SCOREACTION_V_LTV);
		actionTV2 = (TextView) findViewById(R.id.SCOREACTION_V_CTV);
		actionTV3 = (TextView) findViewById(R.id.SCOREACTION_V_RTV);
		
		actionTV2.setText("我的业绩");
		actionTV3.setCompoundDrawables(null, null, null, null);
		actionTV3.setText("返回");
		actionTV1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				ScoreActivity.this.finish();
			}
		});
		actionTV3.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				ScoreActivity.this.finish();
			}
		});
	}
	
	
	/**获取非车月或者年的车险考核收入信息，type=0就是月度，type=1就是年度*/
	private void downloadScoreInfoFC(int i) {
		params=new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("userId", AppApplication.getUSER().data.targetOid+""));
		params.add(new BasicNameValuePair("endDate", CalendarUtil.getEndDayofMonthLong()));
		if (i==0) { // 非车月度合计
			params.add(new BasicNameValuePair("startDate", CalendarUtil.getBeginDayofMonthShort()));//测试后恢复
//			params.add(new BasicNameValuePair("startDate", "2017-10-29"));//测试后删除
			HttpUtils.requestPost(URLs.GET_FC_GGS_SCORE, params, HttpRequestTool.GET_FC_GGS_SCORE);
		}else { //非车年度合计
			params.add(new BasicNameValuePair("startDate", CalendarUtil.getEndDayofYearShort()));//测试后恢复
//			params.add(new BasicNameValuePair("startDate", "2017-05-29"));//测试后删除
			HttpUtils.requestPost(URLs.GET_FC_GGS_SCORE, params, HttpRequestTool.GET_FC_GGS_SCOREY);
		}
		
	}

	
	/**获取车险月或者年的车险考核收入信息，type=0就是月度，type=1就是年度*/
	private void downloadScoreInfoCX(int type) {
		loaddialog.setMessage("努力加载中……").show();
		List<String> params=new ArrayList<String>();
		params.add("userOid");
		params.add(AppApplication.getUSER().data.id+"");//测试后恢复
//		params.add(7603+"");//测试后删除
		params.add("source");
		params.add("10");
		params.add("endDate");
		params.add(CalendarUtil.getEndDayofMonthLong());//2018-01-29 15:20:49
		params.add("startDate");
		if (type==1) {//年度
			params.add(CalendarUtil.getBeginDayofYearLong());//2018-01-01 15:20:49
			HttpUtils.requestGet(URLs.GET_CX_GGS_SCORE, params, HttpRequestTool.GET_CX_GGS_SCOREY);
		}else {//月度
			params.add(CalendarUtil.getBeginDayofMonthLong());//2018-01-01 15:20:49
			HttpUtils.requestGet(URLs.GET_CX_GGS_SCORE, params, HttpRequestTool.GET_CX_GGS_SCORE);
		}
	}
	
	@Subscribe(threadMode=ThreadMode.MAIN)
	public void eventdata(List<NameValuePair> value){
		int code=Integer.parseInt(value.get(0).getName());
		if (code==HttpRequestTool.GET_CX_GGS_SCORE || code==HttpRequestTool.GET_CX_GGS_SCOREY
			|| code==HttpRequestTool.GET_FC_GGS_SCORE  || code==HttpRequestTool.GET_FC_GGS_SCOREY) {
			loaddialog.dismiss();
		}
		switch (CheckHttpResult.checkList(value, this, HttpRequestTool.GET_CX_GGS_SCORE
				,HttpRequestTool.GET_CX_GGS_SCOREY,HttpRequestTool.GET_FC_GGS_SCOREY)) {
		case HttpRequestTool.GET_CX_GGS_SCORE:
			getMonthScore(value.get(0).getValue());//MscoreCX
			break;
		case HttpRequestTool.GET_CX_GGS_SCOREY:
			getYearScore(value.get(0).getValue());//YscoreCX
			break;
		case HttpRequestTool.GET_FC_GGS_SCORE:
			getFCMonthScore(value.get(0).getValue());//YscoreCX
			break;
		case HttpRequestTool.GET_FC_GGS_SCOREY:
			getFCYearScore(value.get(0).getValue());//YscoreCX
			break;

		default:
			break;
		}
	}
	
	/**获取非车当月数据**/
	private void getFCMonthScore(String value) {
		MscoreFC=JSON.parseObject(value, ScoreFCEntity.class);
		downloadScoreInfoFC(1);
	}
	/**获取非车当年数据**/
	private void getFCYearScore(String value) {
		YscoreFC=JSON.parseObject(value, ScoreFCEntity.class);
		connectCount++;
		isconnectEnd();
	}


	/**获取下载的当月数据**/
	private void getMonthScore(String value) {
		MscoreCX=JSON.parseObject(value, ScoreCXEntity.class);
		downloadScoreInfoCX(1);
	}
	/**获取下载的当年数据**/
	private void getYearScore(String value) {
		YscoreCX=JSON.parseObject(value, ScoreCXEntity.class);
		connectCount++;
		isconnectEnd();
	}
	
	private void isconnectEnd() {
		if (connectCount == 2) {
			CXadapter = new GridAdapter(0);
			gridViewCX.setAdapter(CXadapter);
			FCadapter = new GridAdapter(1);
			gridViewFC.setAdapter(FCadapter);
			setGridToListView();
			setTotaldatatoView();
		}
	}
	

	public void setTotaldatatoView(){
		float sumF=CXYFee+FCYFee;
		float sumS=CXYScore+FCYScore;
		if ((sumF+"").indexOf(".")>-1) {//有小数点就截取掉
			feeAll.setText((sumF+"").substring(0, (sumF+"").indexOf(".")));
		}else {
			feeAll.setText((sumF+""));
		}
		if ((sumS+"").indexOf(".")>-1) {
			countAll.setText((sumS+"").substring(0, (sumS+"").indexOf(".")));
		}else {
			countAll.setText(sumS+"") ;
		}
		int CXprogress=(int)(CXYScore/sumS*100);
		int FCprogress=(int)(FCYScore/sumS*100);
		cXProportionTv.setText("车险"+CXprogress+"%") ;
		FCProportionTv.setText("非车"+FCprogress+"%") ;
		if (CXprogress>FCprogress) {
			progressBar.setProgress(FCprogress);
			progressBar.setSecondaryProgress(CXprogress);
			progressBar.setProgressDrawable(getResources().getDrawable(R.drawable.progressbar2));
		}else {
			progressBar.setProgress(CXprogress);
			progressBar.setSecondaryProgress(FCprogress);
			progressBar.setProgressDrawable(getResources().getDrawable(R.drawable.progressbar));
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		EventBus.getDefault().unregister(this);
	}
	
	private class GridAdapter extends BaseAdapter{
		/**0是车险，1是非车**/
		private int type;
		private LayoutInflater inflater;
		
		private int preColor = ScoreActivity.this.getResources().getColor(R.color.bule_text_h);  
	    private int progressColor = ScoreActivity.this.getResources().getColor(R.color.hui_text_m);  
	    private int CircleColor = ScoreActivity.this.getResources().getColor(R.color.white);  
	    private int textColor = ScoreActivity.this.getResources().getColor(R.color.hui_text_xxxh); 
	    private int textSize = dp2px(15);// 文字大小  
	    
	    public int dp2px(int dp) {  
	        return (int) ((getResources().getDisplayMetrics().density * dp) + 0.5);  
	    }  
		
		public GridAdapter (int type){
			this.type=type;
			inflater=LayoutInflater.from(ScoreActivity.this);
			getValue();
		}

		@Override
		public int getCount() {
			return 4;
		}
		@Override
		public Object getItem(int arg0) {
			return null;
		}
		@Override
		public long getItemId(int arg0) {
			return 0;
		}

		@Override
		public View getView(int point, View conView, ViewGroup arg2) {
			ViewHoder vh;
			if (conView==null) {
				vh=new ViewHoder();
				conView=inflater.inflate(R.layout.score_activity_item, null);
				vh.title=(TextView) conView.findViewById(R.id.SCOREAC_ITEM_title);
				vh.amount=(TextView) conView.findViewById(R.id.SCOREAC_ITEM_value);
				vh.point=(TextView) conView.findViewById(R.id.SCOREAC_ITEM_progressText);
//				vh.img=(ImageView) conView.findViewById(R.id.SCOREAC_ITEM_img);
				vh.progressbar=(CircleProgress) conView.findViewById(R.id.SCOREAC_ITEM_progressBar);
				
				vh.progressbar.setTextColor(textColor).setCircleBackgroud(CircleColor)  
                .setPreProgress(progressColor).setProgress(preColor)  
                .setProdressWidth(dp2px(3)).setPaddingscale(1.0f).setTextSize(dp2px(10));
				
				conView.setTag(vh);
			}else {
				vh=(ViewHoder) conView.getTag();
			}
				switch (point) {
				case 0:
					vh.title.setText("本月产能");
					vh.point.setText("本月产能占本年产能比例");
					setvalue(type,vh.amount,vh.progressbar,CXMScore,FCMScore,CXYScore,FCYScore);
					break;
					
				case 1:
					vh.title.setText("本月可用人力成本");
					setvalue(type,vh.amount,vh.progressbar,CXMFee,FCMFee,CXYFee,FCYFee);
					vh.point.setText("本月占本年可用人力成本比例");
					break;
					
				case 2:
					vh.title.setText("本年产能");
					setvalue(type,vh.amount,vh.progressbar,CXYScore,FCYScore,(CXYScore+FCYScore),(CXYScore+FCYScore));
					vh.point.setText("占本年车险非车总产能比例");
					break;
					
				case 3:
					vh.title.setText("本年可用人力成本");
					setvalue(type,vh.amount,vh.progressbar,CXYFee,FCYFee,(CXYFee+FCYFee),(CXYFee+FCYFee));
					vh.point.setText("占本年可用人力成本总和比例");
					break;

				default:
					break;
				}
			return conView;
		}
		
		/**通过数据计算和复制到控件**/
	private void setvalue(int type2, TextView textView, CircleProgress progressbar, 
			float cXMScore, float fCMScore, float cXYScore, float fCYScore) {
		if (type==0){
			if (("￥"+cXMScore).indexOf(".")>-1) {//有小数点就截取
				textView.setText(("￥"+cXMScore).substring(0, ("￥"+cXMScore).indexOf(".")));
			}else {
				textView.setText(("￥"+cXMScore));
			}
			if (cXYScore>0) {
				float temp=cXMScore/cXYScore*100;
				progressbar.setValue((int)temp);
			}else {
				progressbar.setValue(0);
			}
		}
		if (type==1) {
			if (("￥"+fCMScore).indexOf(".")>-1) {
				textView.setText(("￥"+fCMScore).substring(0, ("￥"+fCMScore).indexOf(".")));
			}else {
				textView.setText(("￥"+fCMScore));
			}
			if (fCYScore>0) {
				float temp=fCMScore/fCYScore*100;
//				DecimalFormat decimalFomat=new DecimalFormat(".00");//构造方法的字符格式这里如果小数不足2位,会以0补足.
//				String p=decimalFomat.format(temp);//format 返回的是字符串
				progressbar.setValue((int)temp);
			}else {
				progressbar.setValue(0);
			}
		}
		}

		private void getValue() {
			if (type == 0 && MscoreCX != null && MscoreCX.data != null) {
				CXMScore = Float.parseFloat(MscoreCX.data.yuguGgAmount);

				float r = Float.parseFloat(MscoreCX.data.zySalesGgsSalary);
				float c = Float.parseFloat(MscoreCX.data.zySalesCarFee);
				float y = Float.parseFloat(MscoreCX.data.zySalesGasolineFee);
				CXMFee = (r + c + y);
			}
			if (type == 0 && YscoreCX != null && YscoreCX.data != null) {
				CXYScore = Float.parseFloat(YscoreCX.data.yuguGgAmount);

				float r2 = Float.parseFloat(YscoreCX.data.zySalesGgsSalary);
				float c2 = Float.parseFloat(YscoreCX.data.zySalesCarFee);
				float y2 = Float.parseFloat(YscoreCX.data.zySalesGasolineFee);
				CXYFee = (r2 + c2 + y2);
			}
			if (type == 1 && MscoreFC != null && MscoreFC.code == 0 && null != MscoreFC.data) {
				FCMScore = MscoreFC.data.realYwl;
				FCMFee = MscoreFC.data.yybGgsxc;
			}
			if (type == 1 && YscoreFC != null && YscoreFC.code == 0 && null != YscoreFC.data) {
				FCYScore = YscoreFC.data.realYwl;
				FCYFee = YscoreFC.data.yybGgsxc;
			}
		}
		
	private class ViewHoder{
		public TextView title,amount,point;
		public ImageView img;
		public CircleProgress progressbar;
	}
		
	}
	/**将gridview所在的view添加到ListView中以实现上下滑动**/
private void setGridToListView() {
		listView.setAdapter(new BaseAdapter() {
			
			@Override
			public View getView(int arg0, View arg1, ViewGroup arg2) {
				return scoreView;
			}
			
			@Override
			public long getItemId(int arg0) { return arg0; }
			
			@Override
			public Object getItem(int arg0) {
				return null;
			}
			
			@Override
			public int getCount() {
				return 1;
			}
		});
	}
	 
}
