package com.community.mua.ui;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMapOptions;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.CoordinateConverter;
import com.community.mua.R;
import com.community.mua.base.BaseActivity;
import com.community.mua.databinding.ActivityDistanceBinding;
import com.community.mua.utils.SoundUtil;

import pub.devrel.easypermissions.EasyPermissions;

public class DistanceActivity extends BaseActivity<ActivityDistanceBinding> {

    private LocationClient mLocationClient;
    private BaiduMap mBaiduMap;
    private BDLocation lastLocation;

    protected double latitude;
    protected double longtitude;
    protected String address;

    public static void start(Context context) {
        Intent intent = new Intent(context, DistanceActivity.class);
        context.startActivity(intent);
    }

    public static void start(Fragment context,int requestCode) {
        Intent intent = new Intent(context.getContext(), DistanceActivity.class);
        context.startActivityForResult(intent,requestCode);
    }

    public static void actionStart(Context context, double latitude, double longtitude, String address) {
        Intent intent = new Intent(context, DistanceActivity.class);
        intent.putExtra("latitude", latitude);
        intent.putExtra("longtitude", longtitude);
        intent.putExtra("address", address);
        context.startActivity(intent);
    }
    @Override
    protected ActivityDistanceBinding getViewBinding() {
        return ActivityDistanceBinding.inflate(getLayoutInflater());
    }

    private void initIntent() {
        latitude = getIntent().getDoubleExtra("latitude", 0);
        longtitude = getIntent().getDoubleExtra("longtitude", 0);
        address = getIntent().getStringExtra("address");
    }

    @Override
    protected void initView() {
        initIntent();
        mBinding.titleBar.tvName.setText("位置");

        mBinding.titleBar.ivMore.setVisibility(View.GONE);
        mBinding.titleBar.tvSend.setVisibility(View.GONE);
        mBinding.titleBar.tvSend.setText("发送");
        mBinding.titleBar.tvSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SoundUtil.getInstance().playBtnSound();
                Intent intent = getIntent();
                intent.putExtra("latitude", lastLocation.getLatitude());
                intent.putExtra("longitude", lastLocation.getLongitude());
                intent.putExtra("address", lastLocation.getAddrStr());
                intent.putExtra("buildingName", lastLocation.getBuildingName() == null ? "" : lastLocation.getBuildingName());
                setResult(RESULT_OK, intent);
                finish();
            }
        });
        mBaiduMap = mBinding.mapView.getMap();
        mBaiduMap.setMyLocationEnabled(true);
        LocationClient.setAgreePrivacy(true);
        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
        mBaiduMap.setMapStatus(MapStatusUpdateFactory.zoomTo(15));
    }

    @Override
    protected void initData() {
        boolean hasPermissions = EasyPermissions.hasPermissions(this, Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION);

        if(latitude == 0) {
            mBinding.titleBar.ivMore.setVisibility(View.GONE);
            mBinding.titleBar.tvSend.setVisibility(View.VISIBLE);
            if (hasPermissions) {
                //拥有权限，执行操作
                initLocationOption();
            } else {
                //没有权限，向用户请求权限
                EasyPermissions.requestPermissions(this,
                        "权限申请原理对话框 : 描述申请权限的原理",
                        100,Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION);
            }
        } else {
            mBinding.titleBar.ivMore.setVisibility(View.GONE);
            mBinding.titleBar.tvSend.setVisibility(View.GONE);
            LatLng lng = new LatLng(latitude, longtitude);
            new MapView(this,
                    new BaiduMapOptions().mapStatus(new MapStatus.Builder().target(lng).build()));
            showMap(latitude,longtitude);
        }

    }


    protected void showMap(double latitude, double longtitude) {
        LatLng lng = new LatLng(latitude, longtitude);
        CoordinateConverter converter = new CoordinateConverter();
        converter.coord(lng);
        converter.from(CoordinateConverter.CoordType.COMMON);
        LatLng convertLatLng = converter.convert();
        OverlayOptions ooA = new MarkerOptions().position(convertLatLng).icon(BitmapDescriptorFactory
                        .fromResource(R.drawable.ease_icon_marka))
                .zIndex(4).draggable(true);
        mBaiduMap.addOverlay(ooA);
        MapStatusUpdate u = MapStatusUpdateFactory.newLatLngZoom(convertLatLng, 17.0f);
        mBaiduMap.animateMapStatus(u);
    }

    @Override
    protected void initListener() {

    }

    @Override
    protected void onResume() {
        super.onResume();
//        LocationClient.setAgreePrivacy(false);

        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        mBinding.mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        mBinding.mapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        if (mLocationClient != null) {
            mLocationClient.stop();
        }
        mBaiduMap.setMyLocationEnabled(false);
        mBinding.mapView.onDestroy();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        //通过requestCode来识别是否同一个请求
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //用户同意，执行操作
                initLocationOption();
            } else {
                //用户不同意，向用户展示该权限作用
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION)) {
                    new AlertDialog.Builder(DistanceActivity.this)
                            .setMessage("R.string.storage_permissions_remind")
                            .setPositiveButton("OK", (dialog1, which) ->
                                    ActivityCompat.requestPermissions(this,
                                            new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                                            1))
                            .setNegativeButton("Cancel", null)
                            .create()
                            .show();
                }
            }
        }
    }

    /**
     * 初始化定位参数配置
     */

    private void initLocationOption() {
//定位服务的客户端。宿主程序在客户端声明此类，并调用，目前只支持在主线程中启动

        try {
            mLocationClient = new LocationClient(getApplicationContext());
            //声明LocationClient类实例并配置定位参数
            LocationClientOption locationOption = new LocationClientOption();
            MyLocationListener myLocationListener = new MyLocationListener();
//注册监听函数
            mLocationClient.registerLocationListener(myLocationListener);
//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
            locationOption.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
//可选，默认gcj02，设置返回的定位结果坐标系，如果配合百度地图使用，建议设置为bd09ll;
            locationOption.setCoorType("bd09ll");
//可选，默认0，即仅定位一次，设置发起连续定位请求的间隔需要大于等于1000ms才是有效的
            locationOption.setScanSpan(1000);
//可选，设置是否需要地址信息，默认不需要
            locationOption.setIsNeedAddress(true);
//可选，设置是否需要地址描述
            locationOption.setIsNeedLocationDescribe(true);
//可选，设置是否需要设备方向结果
            locationOption.setNeedDeviceDirect(false);
//可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
            locationOption.setLocationNotify(true);
//可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
            locationOption.setIgnoreKillProcess(true);
//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
            locationOption.setIsNeedLocationDescribe(true);
//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
            locationOption.setIsNeedLocationPoiList(true);
//可选，默认false，设置是否收集CRASH信息，默认收集
            locationOption.SetIgnoreCacheException(false);
//可选，默认false，设置是否开启Gps定位
            locationOption.setOpenGps(true);
//可选，默认false，设置定位时是否需要海拔信息，默认不需要，除基础定位版本都可用
            locationOption.setIsNeedAltitude(false);
//设置打开自动回调位置模式，该开关打开后，期间只要定位SDK检测到位置变化就会主动回调给开发者，该模式下开发者无需再关心定位间隔是多少，定位SDK本身发现位置变化就会及时回调给开发者
            locationOption.setOpenAutoNotifyMode();
//设置打开自动回调位置模式，该开关打开后，期间只要定位SDK检测到位置变化就会主动回调给开发者
            locationOption.setOpenAutoNotifyMode(3000, 1, LocationClientOption.LOC_SENSITIVITY_HIGHT);
//需将配置好的LocationClientOption对象，通过setLocOption方法传递给LocationClient对象使用
            mLocationClient.setLocOption(locationOption);
//开始定位
            mLocationClient.start();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 实现定位回调
     */
    public class MyLocationListener extends BDAbstractLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            //此处的BDLocation为定位结果信息类，通过它的各种get方法可获取定位相关的全部结果
            //以下只列举部分获取经纬度相关（常用）的结果信息
            //更多结果信息获取说明，请参照类参考中BDLocation类中的说明
            lastLocation = location;
            //获取纬度信息
            double latitude = location.getLatitude();
            //获取经度信息
            double longitude = location.getLongitude();
            //获取定位精度，默认值为0.0f
            float radius = location.getRadius();
            //获取经纬度坐标类型，以LocationClientOption中设置过的坐标类型为准
            String coorType = location.getCoorType();
            //获取定位类型、定位错误返回码，具体信息可参照类参考中BDLocation类中的说明
            int errorCode = location.getLocType();
            MyLocationData locData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())
                    // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(location.getDirection()).latitude(location.getLatitude())
                    .longitude(location.getLongitude()).build();

            mBaiduMap.setMyLocationData(locData);

            LatLng point = new LatLng(location.getLatitude(), location.getLongitude());
            // 构建Marker图标
//            BitmapDescriptor bitmap = null;
//            if (iscal == 0) {
//                bitmap = BitmapDescriptorFactory.fromResource(R.drawable.icon_openmap_mark); // 非推算结果
//            } else {
//                bitmap = BitmapDescriptorFactory.fromResource(R.drawable.icon_openmap_focuse_mark); // 推算结果
//            }
//
//            // 构建MarkerOption，用于在地图上添加Marker
//            OverlayOptions option = new MarkerOptions().position(point).icon(bitmap);
//            // 在地图上添加Marker，并显示
//            mBaiduMap.addOverlay(option);
            mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(point));
        }
    }
}