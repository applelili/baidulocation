package com.gfd.bd;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;

/**
 * 类描述：权限申请帮助类
 * 作者：郭富东
 * 创建日期： 2017/3/20 0020 下午 4:36
 * 更新日期：
 */
public class PermissionHelper {

    private PermissionModel[] permissionModels = {
            new PermissionModel(1, Manifest.permission.READ_PHONE_STATE, "识别身份"),
            new PermissionModel(2, Manifest.permission.ACCESS_FINE_LOCATION, "定位"),
            new PermissionModel(3, Manifest.permission.ACCESS_COARSE_LOCATION, "定位"),
            new PermissionModel(3, Manifest.permission.WRITE_EXTERNAL_STORAGE, "定位"),
    };
    private Activity activity;
    private OnAlterApplyPermission listener;

    public PermissionHelper(Activity activity) {

        this.activity = activity;
    }


    /**
     * 申请的权限是否全部授予过
     *
     * @return true:全部授予了
     */
    public boolean isAllApplyPermission() {
        if (permissionModels != null && permissionModels.length != 0) {
            for (PermissionModel permissionModel : permissionModels) {
                if (ContextCompat.checkSelfPermission(activity, permissionModel.permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    /**
     * 动态申请权限
     */
    public void applyPermission() {
        for (PermissionModel permissionModel : permissionModels) {
            //如果没有授予过，再申请
            if (ContextCompat.checkSelfPermission(activity, permissionModel.permission) != PackageManager.PERMISSION_GRANTED) {
                //申请
                ActivityCompat.requestPermissions(activity, new String[]{permissionModel.permission}, permissionModel.requestCode);
                return;
            }
        }
        if (listener != null) listener.OnAlterApplyPermission();

    }

    /**
     * 根据权限获取对应的说明
     *
     * @param permission
     * @return
     */
    private String findPermissionExplain(String permission) {
        for (PermissionModel permissionModel : permissionModels) {
            if (permissionModel.permission.equals(permission)) {
                return permissionModel.explain;
            }
        }
        return "";
    }


    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {//用户没有同意
            //二次申请，二次申请的时候回多一个"不再提示"的checkBox
            //二次申请的时候需要告诉用户为什么需要这权限
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permissions[0])) {//用户拒绝了
                AlertDialog.Builder builder = new AlertDialog.Builder(activity).setTitle("权限申请")
                        .setMessage(findPermissionExplain(permissions[0]))
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                applyPermission();
                            }
                        });
                builder.setCancelable(false);
                builder.show();
            } else {
                //引导用户去应用设置页面手动开启权限
                AlertDialog.Builder builder = new AlertDialog.Builder(activity).setTitle("权限申请")
                        .setMessage("在应用设置页面打开：" + findPermissionExplain(permissions[0]) + "权限")
                        .setPositiveButton("去设置", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                openAppPermissionSettrings();
                            }


                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                activity.finish();
                            }
                        });
                builder.setCancelable(false);
                builder.show();
            }
            return;
        }
        if (isAllApplyPermission() && listener != null) {
            listener.OnAlterApplyPermission();
        } else {
            applyPermission();
        }
    }


    /**
     * 打开应用的设置界面
     */
    private void openAppPermissionSettrings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + activity.getPackageName()));
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        activity.startActivityForResult(intent, 3);

    }

    public static class PermissionModel {
        String permission;
        String explain;
        int requestCode;

        public PermissionModel(int requestCode, String permission, String explain) {
            this.permission = permission;
            this.explain = explain;
            this.requestCode = requestCode;
        }
    }

    public void setOnAlterApplyPermission(OnAlterApplyPermission listener) {

        this.listener = listener;
    }

    public interface OnAlterApplyPermission {
        void OnAlterApplyPermission();
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (isAllApplyPermission() && listener != null) {
            listener.OnAlterApplyPermission();
        } else {
            applyPermission();
        }

    }
}