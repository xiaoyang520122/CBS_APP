package com.cninsure.cp.navi;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.overlayutil.DrivingRouteOverlay;
import com.baidu.mapapi.overlayutil.TransitRouteOverlay;
import com.baidu.mapapi.overlayutil.WalkingRouteOverlay;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.route.BikingRouteResult;
import com.baidu.mapapi.search.route.DrivingRoutePlanOption;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.IndoorRouteResult;
import com.baidu.mapapi.search.route.MassTransitRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.TransitRoutePlanOption;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRoutePlanOption;
import com.baidu.mapapi.search.route.WalkingRouteResult;
import com.cninsure.cp.BaseActivity;
import com.cninsure.cp.R;
import com.cninsure.cp.utils.CallUtils;
import com.cninsure.cp.utils.DialogUtil;
import com.cninsure.cp.utils.ToastUtil;

public class NavigationActivtity extends BaseActivity {
	private MapView mMapView = null;
	private BaiduMap mBaiduMap;
	public LocationClient mLocationClient = null;
	private MyLocationListener myListener = new MyLocationListener();
	boolean isFirstLoc = true; // 是否在定位成功后启动线路规划

	private RoutePlanSearch mSearch;
	private LatLng pt_start, pt_end;
	private TextView startAddTv, endAddTv, callTv;
	private RadioGroup radioGroup;
	// private RadioButton busRB,taxiRB,walkRB;
	private BDLocation mlocation;

	// BDAbstractLocationListener为7.2版本新增的Abstract类型的监听接口
	// 原有BDLocationListener接口暂时同步保留。具体介绍请参考后文中的说明

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		// 在使用SDK各组件之前初始化context信息，传入ApplicationContext
		// 注意该方法要再setContentView方法之前实现
		SDKInitializer.initialize(getApplicationContext());
		setContentView(R.layout.navigation_activity);
		// 获取地图控件引用
		intiView();
		mMapView = (MapView) findViewById(R.id.bmapView);

		mBaiduMap = mMapView.getMap();
		// 开启定位图层
		mBaiduMap.setMyLocationEnabled(true);
		initLocation();
	}

	private void initLocation() {
		mLocationClient = new LocationClient(getApplicationContext());
		// 声明LocationClient类
		mLocationClient.registerLocationListener(myListener);
		// 注册监听函数
		initLocationClent();
		mLocationClient.start();
		// mLocationClient为第二步初始化过的LocationClient对象
		// 调用LocationClient的start()方法，便可发起定位请求
		getserch();
	}

	private void intiView() {
		startAddTv = (TextView) findViewById(R.id.NAVIACT_startADD);
		endAddTv = (TextView) findViewById(R.id.NAVIACT_endADD);
		callTv = (TextView) findViewById(R.id.NAVIACT_callphone);
		radioGroup = (RadioGroup) findViewById(R.id.NAVIACT_radiogroup);

		endAddTv.setText(getIntent().getStringExtra("biaodiAddress"));
		callTv.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				String phoneString = NavigationActivtity.this.getIntent().getStringExtra("phoneNumber");
				CallUtils.call(NavigationActivtity.this, phoneString);
			}
		});

		/** 切换线路规划方式 **/
		radioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup arg0, int arg1) {
				switch (arg1) {
				case R.id.NAVIACT_bybus:
					ToastUtil.showToastShort(NavigationActivtity.this, "开始公交线路规划");
					setDistance(3);
					break;

				case R.id.NAVIACT_bytaxi:
					ToastUtil.showToastShort(NavigationActivtity.this, "开始驾车线路规划");
					setDistance(2);
					break;

				case R.id.NAVIACT_bywalk:
					ToastUtil.showToastShort(NavigationActivtity.this, "开始步行线路规划");
					setDistance(1);
					break;

				default:
					break;
				}
			}
		});
		findViewById(R.id.NAVIACT_backImg).setOnClickListener(new OnClickListener() {// NAVIACT_backImg
					@Override
					public void onClick(View arg0) {
						NavigationActivtity.this.finish();
					}
				});
	}

	/***
	 * 初始化步行检索
	 */
	private void getserch() {
		mSearch = RoutePlanSearch.newInstance();
		mSearch.setOnGetRoutePlanResultListener(listener);
	}

	/**
	 * 发起路线规划
	 */
	public void routePlan(int type) {
		if (mSearch == null) {
			mSearch = RoutePlanSearch.newInstance();
			mSearch.setOnGetRoutePlanResultListener(listener);
		}
		// 起点与终点
		PlanNode stNode = PlanNode.withLocation(pt_start);
		PlanNode enNode = PlanNode.withLocation(pt_end);
		boolean res;
		switch (type) {
		case 1:
			// 步行路线规划
			res = mSearch.walkingSearch(new WalkingRoutePlanOption().from(stNode).to(enNode));
			break;
		case 2:
			// 驾车路线规划
			res = mSearch.drivingSearch(new DrivingRoutePlanOption().from(stNode).to(enNode));
			break;
		case 3:
			// 公交路线规划
			String cityname = mlocation.getCity();
			if (!TextUtils.isEmpty(cityname)) {
				res = mSearch.transitSearch(new TransitRoutePlanOption().from(stNode).to(enNode).city(cityname));
			} else {
				ToastUtil.showToastLong(NavigationActivtity.this, "无法完成线路规划，获取不到城市信息");
			}
			break;

		default:
			break;
		}
	}

	private void setDistance(final int type) {
		Intent intent = getIntent();
		final String address=intent.getStringExtra("biaodiAddress");
		String tage=intent.getStringExtra("navi");
		double mLat1 = mlocation.getLatitude();
		double mLon1 = mlocation.getLongitude();
		double mLat2 = getIntent().getFloatExtra("Latitude", 0);
		double mLon2 = getIntent().getFloatExtra("Longitude", 0);
		pt_start = new LatLng(mLat1, mLon1);
		if (mLat2==0) {
			new Thread(){ //通过现成请求地理化经纬度
				@Override
				public void run() {
					super.run();
					pt_end = GetGeoCoderUtil.getCoordinate(address);
					if (pt_end!=null) {
						GetGeoCoderhandler.sendEmptyMessage(type);
						routePlan(type);
					}else {
						GetGeoCoderhandler.sendEmptyMessage(-1);
					}
				}
			}.start();
			return;
		}
		Log.d("Navi", mLat1 + "  " + mLon1 + "  " + mLat2 + "  " + mLon2);
		pt_end = new LatLng(mLat2, mLon2);
		routePlan(type);
	}
	
	/**如果地理编码失败，就提示用户*/
	private void showErrorMsg() {
		DialogUtil.getErrDialog(NavigationActivtity.this, "通过该地址无法进行线路规划！\n地址："+getIntent().getStringExtra("biaodiAddress")).show();
	}
	
	
	@SuppressLint("HandlerLeak")
	private Handler GetGeoCoderhandler=new Handler(){
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.what==-1) {
				showErrorMsg();
			}
//			else {
//				routePlan(msg.what);
//			}
		}
	};

	class MyWalkingRouteOverlay extends WalkingRouteOverlay {

		public MyWalkingRouteOverlay(BaiduMap arg0) {
			super(arg0);
		}
	}

	/**
	 * 路线规划结果监听
	 */
	OnGetRoutePlanResultListener listener = new OnGetRoutePlanResultListener() {
		/**
		 * 步行
		 */
		public void onGetWalkingRouteResult(WalkingRouteResult result) {
			isFirstLoc = false;
			// 获取步行线路规划结果
			if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
				DialogUtil.getAlertDialog(NavigationActivtity.this,"步行线路规划失败，" + BaiDuErrorUtil.getTransitRouteResultErrorString(result.error)).show();
//				ToastUtil.showToastLong(NavigationActivtity.this, "步行线路规划失败，" + BaiDuErrorUtil.getTransitRouteResultErrorString(result.error));
				if (mBaiduMap != null) {
					mBaiduMap.clear();
				}
			}
			if (result.error == SearchResult.ERRORNO.NO_ERROR) {
				ToastUtil.showToastShort(NavigationActivtity.this, "线路规划成功!");
				if (mBaiduMap != null) {
					mBaiduMap.clear();
					WalkingRouteOverlay overlay = new MyWalkingRouteOverlay(mBaiduMap);
					mBaiduMap.setOnMarkerClickListener(overlay);
					overlay.setData(result.getRouteLines().get(0));
					overlay.addToMap();
					overlay.zoomToSpan();
				}
			}
		}

		public void onGetTransitRouteResult(TransitRouteResult result) {
			isFirstLoc = false;
			// displayLocationMap();
			// 获取公交换乘路径规划结果
			if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
				DialogUtil.getAlertDialog(NavigationActivtity.this,"公交线路规划失败，" + BaiDuErrorUtil.getTransitRouteResultErrorString(result.error)).show();
//				ToastUtil.showToastLong(NavigationActivtity.this, "公交线路规划失败，" + BaiDuErrorUtil.getTransitRouteResultErrorString(result.error));
				if (mBaiduMap != null) {
					mBaiduMap.clear();
				}
			}
			if (result.error == SearchResult.ERRORNO.NO_ERROR) {
				ToastUtil.showToastShort(NavigationActivtity.this, "线路规划成功!");
				if (mBaiduMap != null) {
					mBaiduMap.clear();
					TransitRouteOverlay overlay = new TransitRouteOverlay(mBaiduMap);
					mBaiduMap.setOnMarkerClickListener(overlay);
					overlay.setData(result.getRouteLines().get(0));
					overlay.addToMap();
					overlay.zoomToSpan();
				}
			}
		}

		@Override
		public void onGetMassTransitRouteResult(MassTransitRouteResult massTransitRouteResult) {

		}

		public void onGetDrivingRouteResult(DrivingRouteResult result) {
			isFirstLoc = false;
			// displayLocationMap();
			// 获取驾车线路规划结果
			if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
				DialogUtil.getAlertDialog(NavigationActivtity.this,"驾车线路规划失败，" + BaiDuErrorUtil.getTransitRouteResultErrorString(result.error)).show();
//				ToastUtil.showToastLong(NavigationActivtity.this, "驾车线路规划失败，" + BaiDuErrorUtil.getTransitRouteResultErrorString(result.error));
				if (mBaiduMap != null) {
					mBaiduMap.clear();
				}
			}
			if (result.error == SearchResult.ERRORNO.NO_ERROR) {
				ToastUtil.showToastShort(NavigationActivtity.this, "线路规划成功!");
				if (mBaiduMap != null) {
					mBaiduMap.clear();
					DrivingRouteOverlay overlay = new DrivingRouteOverlay(mBaiduMap);
					mBaiduMap.setOnMarkerClickListener(overlay);
					overlay.setData(result.getRouteLines().get(0));
					overlay.addToMap();
					overlay.zoomToSpan();
				}
			}
		}

		@Override
		public void onGetIndoorRouteResult(IndoorRouteResult indoorRouteResult) {

		}

		@Override
		public void onGetBikingRouteResult(BikingRouteResult bikingRouteResult) {

		}
	};

	private void initLocationClent() {
		LocationClientOption option = new LocationClientOption();

		option.setLocationMode(LocationMode.Hight_Accuracy);
		// 可选，设置定位模式，默认高精度
		// LocationMode.Hight_Accuracy：高精度；
		// LocationMode. Battery_Saving：低功耗；
		// LocationMode. Device_Sensors：仅使用设备；

		option.setCoorType("bd09ll");
		// 可选，设置返回经纬度坐标类型，默认gcj02
		// gcj02：国测局坐标；
		// bd09ll：百度经纬度坐标；
		// bd09：百度墨卡托坐标；
		// 海外地区定位，无需设置坐标类型，统一返回wgs84类型坐标

		option.setScanSpan(1000);
		// 可选，设置发起定位请求的间隔，int类型，单位ms
		// 如果设置为0，则代表单次定位，即仅定位一次，默认为0
		// 如果设置非0，需设置1000ms以上才有效

		option.setOpenGps(true);
		// 可选，设置是否使用gps，默认false
		// 使用高精度和仅用设备两种定位模式的，参数必须设置为true

		option.setLocationNotify(true);
		// 可选，设置是否当GPS有效时按照1S/1次频率输出GPS结果，默认false

		option.setIgnoreKillProcess(false);
		// 可选，定位SDK内部是一个service，并放到了独立进程。
		// 设置是否在stop的时候杀死这个进程，默认（建议）不杀死，即setIgnoreKillProcess(true)

		option.SetIgnoreCacheException(false);
		// 可选，设置是否收集Crash信息，默认收集，即参数为false

		option.setTimeOut(5 * 60 * 1000);
		// 可选，7.2版本新增能力
		// 如果设置了该接口，首次启动定位时，会先判断当前WiFi是否超出有效期，若超出有效期，会先重新扫描WiFi，然后定位

		// 城市名称和地址都为空，我当时就纳闷了，我以前都能用呀，现在这么这样就不行了，后来我查看了相关资料，需要添加一个option.setIsNeedAddress(true);
		option.setIsNeedAddress(true);

		// option.setEnableSimulateGps(false);
		// 可选，设置是否需要过滤GPS仿真结果，默认需要，即参数为false

		mLocationClient.setLocOption(option);
		// mLocationClient为第二步初始化过的LocationClient对象
		// 需将配置好的LocationClientOption对象，通过setLocOption方法传递给LocationClient对象使用
		// 更多LocationClientOption的配置，请参照类参考中LocationClientOption类的详细说明
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		// 在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
		mMapView.onDestroy();
	}

	@Override
	protected void onResume() {
		super.onResume();
		// 在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
		mMapView.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
		// 在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
		mMapView.onPause();
	}

	public class MyLocationListener implements BDLocationListener {
		@Override
		public void onReceiveLocation(BDLocation location) {
			// 此处的BDLocation为定位结果信息类，通过它的各种get方法可获取定位相关的全部结果
			// 以下只列举部分获取经纬度相关（常用）的结果信息
			// 更多结果信息获取说明，请参照类参考中BDLocation类中的说明

			// double latitude = location.getLatitude(); // 获取纬度信息
			// double longitude = location.getLongitude(); // 获取经度信息
			// float radius = location.getRadius(); // 获取定位精度，默认值为0.0f
			//
			// String coorType = location.getCoorType();
			// // 获取经纬度坐标类型，以LocationClientOption中设置过的坐标类型为准
			//
			// int errorCode = location.getLocType();
			// Toast.makeText(getApplicationContext(), "定位相应"+(crrunt++),
			// 0).show();

			mlocation = location;
			// 获取定位类型、定位错误返回码，具体信息可参照类参考中BDLocation类中的说明
			if (isFirstLoc) {

				setLocation(location);

				LocationClientOption option = new LocationClientOption();
				option.setIsNeedAddress(true);
				mLocationClient.setLocOption(option);

				String address = location.getAddrStr();
				startAddTv.setText(address);
				ToastUtil.showToastShort(NavigationActivtity.this, "开始公交线路规划");
				setDistance(3);
			}
		}
	}

	int crrunt = 0;

	/** 使用百度定位SDK获取相应的位置信息，然后利用地图SDK中的接口，您可以在地图上展示实时位置信息 **/
	private void setLocation(BDLocation location) {
		// 构造定位数据
		MyLocationData locData = new MyLocationData.Builder().accuracy(location.getRadius())
		// 此处设置开发者获取到的方向信息，顺时针0-360
				.direction(100).latitude(location.getLatitude()).longitude(location.getLongitude()).build();
		// 设置定位数据
		mBaiduMap.setMyLocationData(locData);

		// 设置定位图层的配置（定位模式，是否允许方向信息，用户自定义定位图标）
		// BitmapDescriptor mCurrentMarker = BitmapDescriptorFactory
		// .fromResource(R.drawable.icon_geo);
		// MyLocationConfiguration config = new MyLocationConfiguration(
		// com.baidu.mapapi.map.MyLocationConfiguration.LocationMode.NORMAL,
		// true, mCurrentMarker);
		// mBaiduMap.setMyLocationConfigeration(config);

		// // // 当不需要定位图层时关闭定位图层
		// // mBaiduMap.setMyLocationEnabled(false);
		// if (isFirstLoc) {
		// isFirstLoc = false;
		LatLng ll = new LatLng(location.getLatitude(), location.getLongitude());
		MapStatus.Builder builder = new MapStatus.Builder();
		builder.target(ll).zoom(18.0f);
		mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
		// }
	}

}
