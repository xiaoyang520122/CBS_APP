package com.cninsure.cp.utils.permission_util;

/**
 * 权限请求接口
 * Created by YuShuangPing on 2018/11/1.
 */
public interface PermissionInterface {

    /**
     * 可得到请求权限请求码
     */
    int getPermissionsRequestCode();

    /**
     * 请求权限成功回调
     */
    void requestPermissionsSuccess();

    /**
     * 请求权限失败回调
     */
    void requestPermissionsFail();

}
