package com.cninsure.cp.utils.permission_util;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;

/**
 * Created by YuShuangPing on 2018/11/1.
 */
public class PermissionApplicationUtil implements PermissionInterface {
    private int requestCode = 10000;
//    private Button btn_phone;//打电话
//    private Button btn_camera;//打开相机
    public PermissionHelper mPermissionHelper;
    private Activity activity;
    //读写权限，定位权限
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.CAMERA};
    /**申请权限成功后所做的操作封装*/
    private permissionSuccessListener writeLisen, cameraLisen;

    private PermissionApplicationUtil(){} //禁用

    public PermissionApplicationUtil(Activity activity ) {
        this.activity = activity;
        //初始化
        mPermissionHelper = new PermissionHelper(activity, this);
        //发起权限申请
        mPermissionHelper.requestPermissions(PERMISSIONS_STORAGE);

    }

    //执行请求
    public void openWritePermission(permissionSuccessListener pl) {
        writeLisen = pl;
        requestCode = 0;
        mPermissionHelper.requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE});
    }

    //执行请求
    public void openCameraPermission( permissionSuccessListener pl) {
        cameraLisen = pl;
        requestCode = 1;
        mPermissionHelper.requestPermissions(new String[]{Manifest.permission.CAMERA});
    }

    /**
     * 可设置请求权限请求码
     */
    @Override
    public int getPermissionsRequestCode() {
        //设置权限请求requestCode，只有不跟onRequestPermissionsResult方法中的其他请求码冲突即可。
        return requestCode;
    }


    /**
     * 请求权限成功回调
     */
    @Override
    public void requestPermissionsSuccess() {
        //权限请求用户已经全部允许
        if (requestCode == 10000) {

        } else if (requestCode == 0) { //这里有读写权限后的动作
            writeLisen.succcessDo();
//            Intent intent = new Intent(Intent.ACTION_DIAL);
//            Uri data = Uri.parse("tel:" + 10086);
//            intent.setData(data);
//            startActivity(intent);
        } else if (requestCode == 1) {  //这里有相机权限后的动作
            cameraLisen.succcessDo();
//            Intent intent1 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//            startActivityForResult(intent1, 0);
        }
    }


    /**获取权限成功后需要做的操作，通过下面的接口封装*/
    public interface permissionSuccessListener{
        public void succcessDo();
    }

    /**
     * 请求权限失败回调
     */
    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void requestPermissionsFail() {
        //权限请求不被用户允许。可以提示并退出或者提示权限的用途并重新发起权限申请。
        if (requestCode==10000){

        }else if (requestCode == 0) {
            //如果拒绝授予权限,且勾选了再也不提醒
            if (!activity.shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                PermissionDialogUtil.showSelectDialog(activity, "说明", "APP需要访问设备上的照片、媒体内容和文件才能正常工作，请允许！", "取消", "确定", new PermissionDialogUtil.DialogClickListener() {
                    @Override
                    public void confirm() {
                        //用于在用户勾选“不再提示”并且拒绝时，再次提示用户
                        PermissionDialogUtil.showSelectDialog(activity, "媒体读写权限不可用", "请在-应用设置-权限中，允许APP拥有权限", "取消", "立即开启", new PermissionDialogUtil.DialogClickListener() {
                            @Override
                            public void confirm() {
                                goToAppSetting();
                            }

                            @Override
                            public void cancel() {

                            }
                        }).show();
                    }

                    @Override
                    public void cancel() {

                    }
                }).show();
            } else {
                PermissionDialogUtil.showSelectDialog(activity, "电话权限不可用", "请在-应用设置-权限中，允许APP使用读写权限", "取消", "立即开启", new PermissionDialogUtil.DialogClickListener() {
                    @Override
                    public void confirm() {
                        goToAppSetting();
                    }

                    @Override
                    public void cancel() {

                    }
                }).show();
            }

        }else if (requestCode==1){
            //如果拒绝授予权限,且勾选了再也不提醒
            if (!activity.shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
                PermissionDialogUtil.showSelectDialog(activity, "说明", "需要使用相机权限，进行资料拍摄", "取消", "确定", new PermissionDialogUtil.DialogClickListener() {
                    @Override
                    public void confirm() {
                        //用于在用户勾选“不再提示”并且拒绝时，再次提示用户
                        PermissionDialogUtil.showSelectDialog(activity, "相机权限不可用", "请在-应用设置-权限中，允许APP使用相机权限来进行资料拍摄", "取消", "立即开启", new PermissionDialogUtil.DialogClickListener() {
                            @Override
                            public void confirm() {
                                goToAppSetting();
                            }

                            @Override
                            public void cancel() {

                            }
                        }).show();
                    }

                    @Override
                    public void cancel() {

                    }
                }).show();
            } else {
                PermissionDialogUtil.showSelectDialog(activity, "相机权限不可用", "请在-应用设置-权限中，允许APP使用相机权限来进行资料拍摄", "取消", "立即开启", new PermissionDialogUtil.DialogClickListener() {
                    @Override
                    public void confirm() {
                        goToAppSetting();
                    }

                    @Override
                    public void cancel() {

                    }
                }).show();
            }
        }


    }

    //如果是在Activity中，这个方法返回用户权限允许操作的数据，可以通过判断提示用户是否授权成功。
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        if (mPermissionHelper.requestPermissionsResult(requestCode, permissions, grantResults)) {
//            //权限请求结果，并已经处理了该回调
//            return;
//        }
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//    }

    /**
     * 打开Setting
     */
    private void goToAppSetting() {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", activity.getPackageName(), null);
        intent.setData(uri);
        activity.startActivityForResult(intent, 123);
    }


}
