package com.cninsure.cp.utils;


import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;

import android.app.Activity;
import android.os.Environment;
import android.widget.Toast;

public class FileDownOpenUtil2 {

	/**
     * 下载指定路径的文件，并写入到指定的位置
     *
     * @param dirName
     * @param fileName
     * @param urlStr
     * @return 返回0表示下载成功，返回1表示下载出错
     */
//    public int downloadFile(String dirName, String fileName, String urlStr) {
//    	
////    	String dirName = Environment.getExternalStorageDirectory().getAbsolutePath();
//        OutputStream output = null;
//        try {
//            //将字符串形式的path,转换成一个url
//        	
//        	String TempfileName = fileName.replace(" ", URLEncoder.encode(" ", "UTF-8"));
//        	TempfileName = TempfileName.replace("·", URLEncoder.encode("·", "UTF-8"));
//        	TempfileName = TempfileName.replace(":", URLEncoder.encode(":", "UTF-8"));
//        	urlStr = urlStr.substring(0, urlStr.indexOf(fileName)) + TempfileName;
//            URL url = new URL(urlStr);
//            //得到url之后，将要开始连接网络，以为是连接网络的具体代码
//            //首先，实例化一个HTTP连接对象conn
//            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//            //定义请求方式为GET，其中GET的大小写不要搞错了。
//            conn.setRequestMethod("GET");
//            //定义请求时间，在ANDROID中最好是不好超过10秒。否则将被系统回收。
//            conn.setConnectTimeout(6 * 1000);
//            //请求成功之后，服务器会返回一个响应码。如果是GET方式请求，服务器返回的响应码是200，post请求服务器返回的响应码是206（貌似）。
//            if (conn.getResponseCode() == 200) {
//                //返回码为真
//                //从服务器传递过来数据，是一个输入的动作。定义一个输入流，获取从服务器返回的数据
//                InputStream input = conn.getInputStream();
//                File file = createFile(dirName + fileName);
//                output = new FileOutputStream(file);
//                //读取大文件
//                byte[] buffer = new byte[1024];
//                //记录读取内容
//                int n = input.read(buffer);
//                    //写入文件
//                    output.write(buffer, 0, n);
//                    n = input.read(buffer);
//                    input.close();
//                }
//                output.flush();
//        } catch (MalformedURLException e) {
//            e.printStackTrace();
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            try {
//                output.close();
//                System.out.println("success");
//                return 0;
//            } catch (IOException e) {
//                System.out.println("fail");
//                e.printStackTrace();
//            }
//        }
//        return 1;
//    }
//
//
    /**
     * 在SD卡的指定目录上创建文件
     *
     * @param fileName
     */
    public File createFile(String fileName) {
        File file = new File(fileName);
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }
    
    
	private static Thread thread;
	public static void downloadAndOpen(final Activity context,final String dirName, final String fileName, final String urlStr){
		thread = new Thread(){
			@Override
			public void run() {
				super.run();
				try {
					startDownload(context, dirName, fileName, urlStr);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		thread.start();
	}
    
    
private static void startDownload(final Activity context,String dirName, String fileName, String urlStr) throws Exception{
		
		final String target = Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+fileName; //临时文件存储路径
		
		com.lidroid.xutils.HttpUtils httpUtils = new com.lidroid.xutils.HttpUtils();

		 File file = new File(target);
		    if (file.exists() && file.length()>0) {
		    	 LoadDialogUtil.dismissDialog();
		    	 OpenFileUtil.openFileByPath(context, target);
		    	 return;
			}else {
				 file.createNewFile();
			}
		    
		    String TempfileName = fileName.replace(" ", "%20"); //空格替换为%20
//        	TempfileName = TempfileName.replace("·", URLEncoder.encode("·", "UTF-8"));
//        	TempfileName = TempfileName.replace(":", URLEncoder.encode(":", "UTF-8"));
        	urlStr = urlStr.substring(0, urlStr.indexOf(fileName)) + TempfileName;
		
		
		
		httpUtils.download(urlStr, target, true, new RequestCallBack<File>() {

			@Override
			public void onSuccess(ResponseInfo<File> responseInfo) {
				OpenFileUtil.openFileByPath(context, target);
				LoadDialogUtil.dismissDialog();
//				Toast.makeText(MainActivity.this, "下载成功", Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onFailure(HttpException error, String msg) {
				 LoadDialogUtil.dismissDialog();
				 DialogUtil.getErrDialog(context, "下载失败!!"+msg).show();
//				Toast.makeText(context , "下载失败", Toast.LENGTH_SHORT).show();
				 error.printStackTrace();
			}
			
			@Override
			public void onLoading(long total, long current, boolean isUploading) {
				/**显示进度*/
				int progress = (int) ((current*100)/total);
				LoadDialogUtil.changeMsg("努力加载中……"+progress+"%");
			}
		});
	
		
	}
}