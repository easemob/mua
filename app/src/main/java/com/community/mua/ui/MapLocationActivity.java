package com.community.mua.ui;


import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;
import com.community.mua.App;
import com.community.mua.R;
import com.community.mua.base.BaseActivity;
import com.community.mua.bean.LoginRecordBean;
import com.community.mua.bean.UserBean;
import com.community.mua.databinding.ActivityMapLocationBinding;
import com.community.mua.services.QObserver;
import com.community.mua.utils.BatteryUtils;
import com.community.mua.utils.LocationService;
import com.community.mua.utils.SharedPreferUtil;
import com.community.mua.utils.ToastUtil;
import com.community.mua.utils.UserUtils;
import com.hjq.toast.ToastUtils;
import com.tencent.bugly.proguard.A;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import pub.devrel.easypermissions.EasyPermissions;


public class MapLocationActivity extends BaseActivity<ActivityMapLocationBinding> {

    public static void start(Context context) {
        context.startActivity(new Intent(context, MapLocationActivity.class));
    }

    private BaiduMap mBaiduMap;
    private LocationService locationService;
    @Override
    protected ActivityMapLocationBinding getViewBinding() {
        return ActivityMapLocationBinding.inflate(getLayoutInflater());
    }

    @Override
    protected void initView() {
        mBinding.titleBar.tvName.setText("定位");
        mBinding.titleBar.ivSetting.setVisibility(View.VISIBLE);
        mBaiduMap = mBinding.mapView.getMap();
        handler.sendEmptyMessageDelayed(0, 3000);
    }

    @Override
    protected void initData() {
        UserBean userBean = SharedPreferUtil.getInstance().getUserBean();
        UserBean taBean = SharedPreferUtil.getInstance().getTaBean();
        Map<String, String> params = new HashMap<>();
        params.put("mineId", userBean.getUserid());
        params.put("taId", taBean.getUserid());
        App.getApi().getPairLastRecord(params).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new QObserver<List<LoginRecordBean>>(mContext) {
                    @Override
                    public void next(List<LoginRecordBean> beans) {
                        if (beans.size() < 2) {
                            showMineLocation(beans.get(0));
                            return;
                        }
                        List<LatLng> points = new ArrayList<LatLng>();
                        List<InfoWindow> infoWindows = new ArrayList<>();
                        List<OverlayOptions> options = new ArrayList<OverlayOptions>();
                        for (int i = 0; i < beans.size() ; i++) {
                            LoginRecordBean bean = beans.get(i);
                            if (bean == null || TextUtils.isEmpty(bean.getLatitude())) {
                                continue;
                            }
                            //定义Maker坐标点
                            LatLng point = new LatLng(Double.valueOf(bean.getLatitude()), Double.valueOf(bean.getLongitude()));
                            points.add(point);
                            //构建Marker图标
//                            BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.drawable.ease_icon_marka);
                            View marker = LayoutInflater.from(mContext).inflate(R.layout.location_marker_layout, null);
                            if (TextUtils.equals(bean.getUserId(),taBean.getUserid())) {
                                marker.findViewById(R.id.fl_avatar).setBackgroundResource(taBean.getGender() == 0 ? R.drawable.pin_red: R.drawable.pin_green);
                                ImageView avatar = marker.findViewById(R.id.iv_avatar);
                                UserUtils.setAvatar(mContext,taBean,avatar);
                                marker.findViewById(R.id.tv_location).setBackgroundResource(R.drawable.location_ta);
                            } else {
                                marker.findViewById(R.id.fl_avatar).setBackgroundResource(userBean.getGender() == 0 ? R.drawable.pin_red : R.drawable.pin_green);
                                ImageView avatar = marker.findViewById(R.id.iv_avatar);
                                UserUtils.setAvatar(mContext,userBean,avatar);
                                marker.findViewById(R.id.tv_location).setBackgroundResource(R.drawable.location_mine);
                            }
                            BitmapDescriptor bitmap = BitmapDescriptorFactory.fromView(marker);
                            //构建MarkerOption，用于在地图上添加Marker
                            OverlayOptions option = new MarkerOptions().position(point).icon(bitmap);
                            options.add(option);

                            View view = LayoutInflater.from(mContext).inflate(R.layout.battery_layout, null);

                            if (TextUtils.equals(bean.getUserId(),userBean.getUserid())) {
                                view.findViewById(R.id.iv_battery).setBackgroundResource(BatteryUtils.getBatteryRes(Integer.valueOf(bean.getMobileInfo()),true));
                                ((TextView) view.findViewById(R.id.tv_battery)).setText(bean.getMobileInfo()+"%");
                            } else {
                                view.findViewById(R.id.iv_battery).setBackgroundResource(BatteryUtils.getBatteryRes(Integer.valueOf(bean.getMobileInfo()),false));
                                ((TextView) view.findViewById(R.id.tv_battery)).setText(bean.getMobileInfo()+"%");
                            }

                            InfoWindow infoWindow = new InfoWindow(view, point, -70);
                            infoWindows.add(infoWindow);
                        }

                        //设置折线的属性
                        if (points.size() > 1) {
                            OverlayOptions mOverlayOptions = new PolylineOptions()
                                    .width(8)
                                    .color(0xAAFF93D1)
                                    .points(points)
                                    .dottedLine(true);
                            //在地图上绘制折线
                            //mPloyline 折线对象
                            options.add(mOverlayOptions);
                            mBaiduMap.addOverlays(options);
                        }
                        mBaiduMap.showInfoWindows(infoWindows);
                        if (points.size() > 0) {
                            mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(points.get(0)));
                        }
                    }
                });
    }

    @Override
    protected void initListener() {
        mBinding.titleBar.ivSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MapSettingActivity.start(mContext);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        boolean hasPermissions = EasyPermissions.hasPermissions(this, Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION);
        if (!hasPermissions){
            EasyPermissions.requestPermissions(this,
                    "权限申请原理对话框 : 描述申请权限的原理",
                    100,Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION);
        }
    }

   private void showMineLocation(LoginRecordBean recordBean){
        if (recordBean != null) {
            if (TextUtils.isEmpty(recordBean.getLatitude()) || TextUtils.isEmpty(recordBean.getLongitude())) {
                ToastUtil.show("请开启定位权限并同步登录记录!");
                return;
            }
            double latitude = Double.valueOf(recordBean.getLatitude());
            double longitude = Double.valueOf(recordBean.getLongitude());
            UserBean userBean = SharedPreferUtil.getInstance().getUserBean();
            LatLng point = new LatLng(latitude, longitude);
            View marker = LayoutInflater.from(mContext).inflate(R.layout.location_marker_layout, null);

            marker.findViewById(R.id.fl_avatar).setBackgroundResource(userBean.getGender() == 0 ? R.drawable.pin_red : R.drawable.pin_green);
            ImageView avatar = marker.findViewById(R.id.iv_avatar);
            UserUtils.setAvatar(mContext,userBean,avatar);
            //构建MarkerOption，用于在地图上添加Marker
            BitmapDescriptor bitmap = BitmapDescriptorFactory.fromView(marker);
            OverlayOptions option = new MarkerOptions().position(point).icon(bitmap);

            boolean batteryStatus = SharedPreferUtil.getInstance().getBatteryStatus();
            String batteryManager = SharedPreferUtil.getInstance().getBatteryManager();
            View view = LayoutInflater.from(mContext).inflate(R.layout.battery_layout, null);
            view.findViewById(R.id.iv_battery).setBackgroundResource(BatteryUtils.getBatteryRes(Integer.valueOf(batteryManager),batteryStatus));
            ((TextView) view.findViewById(R.id.tv_battery)).setText(batteryManager+"%");
            InfoWindow infoWindow = new InfoWindow(view, point, -70);

            mBaiduMap.addOverlay(option);
            mBaiduMap.showInfoWindow(infoWindow);
            mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(point));
        } else {
            ToastUtil.show("请开启定位权限并同步登录记录!");
        }
   }

    private IntentFilter   mIntentFilter;
    private MyHandler handler = new MyHandler(this);

    private class MyHandler extends Handler {
        public MyHandler(Context context) {
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0) {
                mIntentFilter = new IntentFilter();
                mIntentFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
                registerReceiver(mIntentReceiver, mIntentFilter);
                sendEmptyMessageDelayed(0, 3000);
            }
        }
    }

    private BroadcastReceiver mIntentReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            //要看看是不是我们要处理的消息
            if (action.equals(Intent.ACTION_BATTERY_CHANGED)) {
                //电池电量，数字
                int currentLevel = intent.getIntExtra("level", 0);
                Log.d("Battery", " 电量 " + currentLevel );
                SharedPreferUtil.getInstance().setBatteryManager(currentLevel);
                //电池最大容量
//                Log.d("Battery", "" + intent.getIntExtra("scale", 0));
//                //电池伏数
//                Log.d("Battery", "" + intent.getIntExtra("voltage", 0));
//                //电池温度
//                Log.d("Battery", "" + intent.getIntExtra("temperature", 0));

                //电池状态，返回是一个数字
                // BatteryManager.BATTERY_STATUS_CHARGING 表示是充电状态
                // BatteryManager.BATTERY_STATUS_DISCHARGING 放电中
                // BatteryManager.BATTERY_STATUS_NOT_CHARGING 未充电
                // BatteryManager.BATTERY_STATUS_FULL 电池满
                Log.d("Battery", "" + intent.getIntExtra("status", BatteryManager.BATTERY_STATUS_CHARGING));

                int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
                boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING || status == BatteryManager.BATTERY_STATUS_FULL;
                Log.d("Battery", "isCharging " + isCharging);
                SharedPreferUtil.getInstance().setBatteryStatus(isCharging);
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeMessages(0);
        if (mIntentFilter != null) {
            unregisterReceiver(mIntentReceiver);
        }

    }
}