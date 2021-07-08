package com.cninsure.cp.utils;

import java.io.File;

import com.cninsure.cp.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.StrictMode;
import android.widget.Toast;

/**
 * @Description: 打开文件工具类
 * @author: ZhangYW
 * @time: 2019/1/10 10:52
 */
public class OpenFileUtil {
    private static final String[][] MATCH_ARRAY={
            //{后缀名，    文件类型}
            {".3gp",    "video/3gpp"},
            {".apk",    "application/vnd.android.package-archive"},
            {".asf",    "video/x-ms-asf"},
            {".avi",    "video/x-msvideo"},
            {".bin",    "application/octet-stream"},
            {".bmp",      "image/bmp"},
            {".c",        "text/plain"},
            {".class",    "application/octet-stream"},
            {".conf",    "text/plain"},
            {".cpp",    "text/plain"},
            {".doc",    "application/msword"},
            {".docx",    "application/msword"},
            {".xls",    "application/msword"},
            {".xlsx",    "application/msword"},
            {".exe",    "application/octet-stream"},
            {".gif",    "image/gif"},
            {".gtar",    "application/x-gtar"},
            {".gz",        "application/x-gzip"},
            {".h",        "text/plain"},
            {".htm",    "text/html"},
            {".html",    "text/html"},
            {".jar",    "application/java-archive"},
            {".java",    "text/plain"},
            {".jpeg",    "image/jpeg"},
            {".jpg",    "image/jpeg"},
            {".js",        "application/x-javascript"},
            {".log",    "text/plain"},
            {".m3u",    "audio/x-mpegurl"},
            {".m4a",    "audio/mp4a-latm"},
            {".m4b",    "audio/mp4a-latm"},
            {".m4p",    "audio/mp4a-latm"},
            {".m4u",    "video/vnd.mpegurl"},
            {".m4v",    "video/x-m4v"},
            {".mov",    "video/quicktime"},
            {".mp2",    "audio/x-mpeg"},
            {".mp3",    "audio/x-mpeg"},
            {".mp4",    "video/mp4"},
            {".mpc",    "application/vnd.mpohun.certificate"},
            {".mpe",    "video/mpeg"},
            {".mpeg",    "video/mpeg"},
            {".mpg",    "video/mpeg"},
            {".mpg4",    "video/mp4"},
            {".mpga",    "audio/mpeg"},
            {".msg",    "application/vnd.ms-outlook"},
            {".ogg",    "audio/ogg"},
            {".pdf",    "application/pdf"},
            {".png",    "image/png"},
            {".pps",    "application/vnd.ms-powerpoint"},
            {".ppt",    "application/vnd.ms-powerpoint"},
            {".prop",    "text/plain"},
            {".rar",    "application/x-rar-compressed"},
            {".rc",        "text/plain"},
            {".rmvb",    "audio/x-pn-realaudio"},
            {".rtf",    "application/rtf"},
            {".sh",        "text/plain"},
            {".tar",    "application/x-tar"},
            {".tgz",    "application/x-compressed"},
            {".txt",    "text/plain"},
            {".wav",    "audio/x-wav"},
            {".wma",    "audio/x-ms-wma"},
            {".wmv",    "audio/x-ms-wmv"},
            {".wps",    "application/vnd.ms-works"},
            {".xml",    "text/plain"},
            {".z",        "application/x-compress"},
            {".zip",    "application/zip"},
            {"",        "*/*"}
    };

    /**
     * 根据路径打开文件
     * @param context 上下文
     * @param path 文件路径
     */
    public static void openFileByPath(Context context, String path) {
        if(context==null||path==null)
            return;
        Intent intent = new Intent();
        //设置intent的Action属性
        intent.setAction(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addCategory(Intent.CATEGORY_DEFAULT);

        //文件的类型
        String type = "*/*";
        String typeStr = path.substring(path.lastIndexOf("."));
        for(int i =0;i < MATCH_ARRAY.length;i++){
            //判断文件的格式
            if(typeStr.equals(MATCH_ARRAY[i][0])){
                type = MATCH_ARRAY[i][1];
                break;
            }
        }
        try {
            File out = new File(path);
            Uri fileURI;
            if (Build.VERSION.SDK_INT >= 24) { // 24之前为Build.VERSION_CODES.N
            // 由于7.0以后文件访问权限，可以通过定义xml在androidmanifest中申请，也可以直接跳过权限
            // 通过定义xml在androidmanifest中申请
//                fileURI = FileProvider.getUriForFile(context,
//                        "com.lonelypluto.zyw_test.provider",
//                        out);
            // 直接跳过权限
                StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                StrictMode.setVmPolicy(builder.build());
                fileURI = Uri.fromFile(out);
            }else{
                fileURI = Uri.fromFile(out);
            }
            //设置intent的data和Type属性
            intent.setDataAndType(fileURI, type);
            //跳转
            if (context.getPackageManager().resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY) != null) {
                context.startActivity(intent);
            } else {
                Toast.makeText(context, "没有找到对应的程序，需要下载对应的编辑软件方能打开操作。", Toast.LENGTH_SHORT).show();
            }

        } catch (Exception e) { //当系统没有携带文件打开软件，提示
            Toast.makeText(context, "无法打开该格式文件", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }
    
/**主要类型video、apk、excel、text、pdf、ppt、rar、other*/
    private static final String[][] MATCH_TYPE={
            //{后缀名，    文件类型}
            {".3gp",    "video"},
            {".apk",    "apk"},
            {".asf",    "excel"},
            {".avi",    "video"},
            {".bin",    "other"},
            {".bmp",    "image"},
            {".c",      "text"},
            {".class",    "other"},
            {".conf",    "other"},
            {".cpp",    "other"},
            {".doc",    "world"},
            {".docx",    "world"},
            {".xls",    "excel"},
            {".xlsx",    "excel"},
            {".exe",    "other"},
            {".gif",    "image"},
            {".gtar",    "other"},
            {".gz",        "other"},
            {".h",        "other"},
            {".htm",    "other"},
            {".html",    "other"},
            {".jar",    "other"},
            {".java",    "text"},
            {".jpeg",    "image"},
            {".jpg",    "image"},
            {".js",        "other"},
            {".log",    "text"},
            {".m3u",    "other"},
            {".m4a",    "other"},
            {".m4b",    "other"},
            {".m4p",    "other"},
            {".m4u",    "other"},
            {".m4v",    "other"},
            {".mov",    "other"},
            {".mp2",    "other"},
            {".mp3",    "video"},
            {".mp4",    "video"},
            {".mpc",    "other"},
            {".mpe",    "other"},
            {".mpeg",    "other"},
            {".mpg",    "video"},
            {".mpg4",    "video"},
            {".mpga",    "other"},
            {".msg",    "other"},
            {".ogg",    "other"},
            {".pdf",    "pdf"},
            {".png",    "image"},
            {".pps",    "ppt"},
            {".ppt",    "ppt"},
            {".prop",    "other"},
            {".rar",    "rar"},
            {".rc",      "text"},
            {".rmvb",    "audio"},
            {".rtf",    "other"},
            {".sh",        "text"},
            {".tar",    "other"},
            {".tgz",    "other"},
            {".txt",    "text"},
            {".wav",    "audio"},
            {".wma",    "audio"},
            {".wmv",    "audio"},
            {".wps",    "world"},
            {".xml",    "text"},
            {".z",        "rar"},
            {".zip",    "rar"},
            {"",        "other"}
    };
    
    /**获取文件类型图标 主要类型 image、video、apk、excel、world、text、pdf、ppt、rar、other*/
    public static int getTypeResouse(String filePath , Activity activity){
    	String type=getFileType(filePath);
    	if (type.equals("image")) {
			return R.drawable.jpg;
			
		}else if (type.equals("video")) {
			return R.drawable.mp4;
			
		}else if (type.equals("excel")) {
			return R.drawable.xlsx;
			
		}else if (type.equals("text")) {
			return R.drawable.txt;
			
		}else if (type.equals("pdf")) {
			return R.drawable.pdf;
			
		}else if (type.equals("world")) {
			return R.drawable.docx;
			
		}else if (type.equals("ppt")) {
			return R.drawable.ppt;
			
		}else if (type.equals("rar")) {
			return R.drawable.zip;
		}else  {
			return R.drawable.none2;
		}
    }
    
    public static String getFileType(String filePath){
    	String type="";
		int pointP=filePath.lastIndexOf(".");
		String typeStr = filePath.substring(pointP, filePath.length());
    	for (int i = 0; i < MATCH_TYPE.length; i++) {
    		  if(typeStr.equals(MATCH_ARRAY[i][0])){
                  type = MATCH_TYPE[i][1];
                  break;
              }
		}
		return type;
    }
    
    
    
}
