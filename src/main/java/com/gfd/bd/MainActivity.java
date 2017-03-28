package com.gfd.bd;

import android.annotation.TargetApi;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;

public class MainActivity extends AppCompatActivity {

    public LocationClient mLocationClient = null;
    public BDLocationListener myListener = new MyLocationListener();
    private Button bt;
    private PermissionHelper permissionHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestPremission();//动态申请权限
        bt = (Button) findViewById(R.id.bt_city);
        //1.创建定位的客户端（LocationClient类）
        mLocationClient = new LocationClient(getApplicationContext());
        //2.配置定位的参数
        initLocation();
        //3.设置定位监听
        mLocationClient.registerLocationListener( myListener );



    }

    @TargetApi(23)
    public void requestPremission(){
        permissionHelper = new PermissionHelper(this);
        permissionHelper.applyPermission();
    }

    @TargetApi(23)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        permissionHelper.onRequestPermissionsResult(requestCode,permissions,grantResults);
    }

    //开始定位
    public void bt_location(View view){
        mLocationClient.start();
    }

    private void initLocation(){
        LocationClientOption option = new LocationClientOption();
        option.setCoorType("bd09ll");
        option.setScanSpan(5000);
        option.setIsNeedAddress(true);//设置是否需要地址信息
        option.setOpenGps(true);
        option.setLocationNotify(true);
        option.setIsNeedLocationDescribe(true);
        option.setIgnoreKillProcess(false);
        option.SetIgnoreCacheException(false);
        option.setEnableSimulateGps(false);
        mLocationClient.setLocOption(option);
    }

    public class MyLocationListener implements BDLocationListener {

        /**
         * 定位回调
         * @param location ：所有的定位信息封装类
         */
        @Override
        public void onReceiveLocation(BDLocation location) {
            int locType = location.getLocType();
            String city = location.getCity();
            String addrStr = location.getAddrStr();
            bt.setText(locType +  "   " + city + "addrStr:" + addrStr + "getFloor:" + 	location.getFloor());

        }

        @Override
        public void onConnectHotSpotMessage(String s, int i) {

        }
    }
}
