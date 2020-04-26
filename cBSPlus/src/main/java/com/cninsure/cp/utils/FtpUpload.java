package com.cninsure.cp.utils;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

public class FtpUpload {
	public  FTPClient ftp;
    /**
     *  
     * @param path 上传到ftp服务器哪个路径下    
     * @param addr 地址 
     * @param port 端口号 
     * @param username 用户名 
     * @param password 密码 
     * @return 
     * @throws Exception 
     */  
    public  boolean connect(String path,String addr,int port,String username,String password) throws Exception {    
        boolean result = false;    
        ftp = new FTPClient(); 
        ftp.enterLocalPassiveMode();
        int reply;    
        ftp.connect(addr,port);    
        ftp.login(username,password);    
        ftp.setFileType(FTPClient.BINARY_FILE_TYPE);   
        ftp.setControlEncoding("utf-8");
        reply = ftp.getReplyCode();    
        if (!FTPReply.isPositiveCompletion(reply)) {    
            ftp.disconnect();    
            return result;    
        }    
        result =  ftp.changeWorkingDirectory(path);    
         
        return result;    
    }

    /**
     * 默认连接
     * @param path  要访问的路径
     * @return
     * @throws Exception
     */
    public boolean connect(String path) throws Exception {
        return connect(path, Common.FTP_IP,Common.FTP_PORT,Common.FTP_USERNAME,Common.FTP_PASSWORD);
    }

    /**
     * 默认连接
     * @return
     * @throws Exception
     */
    public boolean connect() throws Exception {
        return connect(null, Common.FTP_IP,Common.FTP_PORT,Common.FTP_USERNAME,Common.FTP_PASSWORD);
    }

    /** 
     *  
     * @param file 上传的文件或文件夹 
     * @throws Exception 
     */  
    public void upload(File file) throws Exception{    
        if(file.isDirectory()){        
        	String fileName = file.getName();
            ftp.makeDirectory(fileName);       
            ftp.changeWorkingDirectory(file.getName());    
            String[] files = file.list();           
            for (int i = 0; i < files.length; i++) {    
                File file1 = new File(file.getPath()+"\\"+files[i] );    
                if(file1.isDirectory()){    
                    upload(file1);    
                    ftp.changeToParentDirectory();    
                }else{                  
                    File file2 = new File(file.getPath()+"\\"+files[i]);    
                    FileInputStream input = new FileInputStream(file2); 
                    System.out.println("开始存储文件到ftp"+file2.getName()+"--"+file2.getAbsolutePath());
                    ftp.enterLocalPassiveMode();
                    ftp.storeFile(new String(file2.getName().getBytes("GBK"), "iso-8859-1"), input);  
                    System.out.println("存储文件结束，关闭流");
                    input.close();     
                    System.out.println("关闭流结束");
                }               
            }    
        }else{    
            File file2 = new File(file.getPath());    
            FileInputStream input = new FileInputStream(file2);    
            ftp.storeFile(new String(file2.getName().getBytes("GBK"), "iso-8859-1"), input);    
            input.close();      
        }  
        
    }

    //上传指定文件到ftp的指定位置
    public boolean upload(String ftpPath, String filePath) throws IOException {
        File file = new File(ftpPath); //ftp文件
        String parent = file.getParent();   //获取所在的文件夹
        ftp.makeDirectory(parent);  //在ftp上创建相应的文件夹

        FileInputStream input = new FileInputStream(filePath);
        boolean b = ftp.storeFile(new String(ftpPath.getBytes("UTF-8"), "iso-8859-1"), input);  //保存文件到ftp
        input.close();
        return b;
    }

    /**
     * 根据ftp路径下载ftp中的文件到本地路径
     * @param localPath 下载到本地磁盘的路径
     * @param remotePath 要下载的远程ftp路径
     * @return
     * @throws IOException
     */
    public boolean download(String localPath, String remotePath) throws IOException {
//        File remoteFile = new File(remotePath);
//        FTPFile[] ftpFiles = ftp.listFiles(remoteFile.getParent()); //获取ftp当前目录下所有文件列表
//        for (FTPFile ftpFile : ftpFiles) {
//            String ftpFileName = ftpFile.getName();
//            String targetName = remoteFile.getName();
//            if (ftpFileName != null && ftpFileName.equals(targetName)) {    //找到对就的文件，就下载到本地路径
                OutputStream outputStream = new FileOutputStream(localPath);
//                String encodePath = new String(remoteFile.getPath().getBytes("UTF-8"), "ISO-8859-1");   //防止中文乱码
                boolean success = ftp.retrieveFile("ftp://119.29.173.18/uploadFiles/FHSZ2017A120002/51008/0/FHSZ2017A120002/package0111.png", outputStream);   //下载
                outputStream.close();   //关流
                return success;
//            }
//        }
//        return false;
    }

//   public List<FilePo> getFiles(String pathname){
//	   List<FilePo> list = new ArrayList<FilePo>();
//	   try {
//	    String path = "ftp://"+Common.FTP_USERNAME +":"+Common.FTP_PASSWORD +"@"+Common.FTP_IP +"/"+pathname;
//		boolean falg= connect("/"+pathname+"/", Common.FTP_IP, 21, Common.FTP_USERNAME,Common.FTP_PASSWORD);
//		if(falg){
//			String[] files = ftp.listNames();
//			if(null!=files&&files.length>0){
//				for(int i = 0;i<files.length;i++){
//					FilePo p = new FilePo();
//					p.setFileUrl(path+"/"+files[i]);
//					p.setFileName(files[i]);
//					list.add(p);
//				}
//			}
//		}
//	} catch (Exception e) {
//		e.printStackTrace();
//	}
//	   return list;
//   }
    
    public boolean downloadT(String localFileName, String remoteFileName)  
            throws IOException {  
        boolean flag = false;  
        File outfile = new File(localFileName);  
        OutputStream oStream = null;  
        try {  
            oStream = new FileOutputStream(outfile);  
            //我们可以使用BufferedOutputStream进行封装
         	//BufferedOutputStream bos=new BufferedOutputStream(oStream);
         	//flag = ftpClient.retrieveFile(remoteFileName, bos); 
            ftp.enterLocalPassiveMode();//ftp://penghh:penghh_123@119.29.173.18/uploadFiles/FHSZ2017A120002/51008/2/20180118_043113.jpg
            String remotePath=remoteFileName.substring(remoteFileName.indexOf("18/")+3);
            flag = ftp.retrieveFile(remotePath, oStream);  
        } catch (IOException e) {  
            flag = false;  
            return flag;  
        } finally {  
            oStream.close();  
        }  
        return flag;  
    } 
    
    /** 
     * 下载文件 
     * @param FilePath  要存放的文件的路径 
     * @param FileName   远程FTP服务器上的那个文件的名字 
     * @return   true为成功，false为失败 
     */  
	public boolean downLoadFile(String FilePath, String FileName) {

		if (!ftp.isConnected()) {

//			if (!initFTPSetting()) {
//				return false;
//			}

		}
		try {
			ftp.setKeepAlive(true);
			ftp.enterLocalPassiveMode();

			// 转到指定下载目录
			ftp.changeWorkingDirectory("/");
			ftp.setControlEncoding("GBK");
			// 列出该目录下所有文件
			FTPFile[] files = ftp.listFiles();

			// 遍历所有文件，找到指定的文件
			for (FTPFile file : files) {

				if (file.getName().equals(FileName)) {

					FilePath += "/" + FileName;
					// 根据绝对路径初始化文件
					File localFile = new File(FilePath);

					// 输出流
					OutputStream outputStream = new FileOutputStream(localFile);

					// 下载文件
					ftp.retrieveFile(file.getName(), outputStream);

					// 关闭流
					outputStream.close();
				}
			}

			// 退出登录FTP，关闭ftpCLient的连接
			ftp.logout();
			ftp.disconnect();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return true;
	} 

   
   /** 
    *  
    * <p>删除ftp上的文件</p> 
    * @param srcFname 
    * @return true || false 
    */  
   public boolean removeFile(String srcFname){  
       boolean flag = false;  
       if( ftp!=null ){  
           try {  
               flag = ftp.deleteFile(srcFname);  
           } catch (IOException e) {  
               e.printStackTrace();  
               this.closeCon();  
           }  
       }  
       return flag;  
   }  
     
   /** 
    *<p>销毁ftp连接</p>  
    */  
   public void closeCon(){  
       if(ftp !=null){  
           if(ftp.isConnected()){  
               try {  
            	   ftp.logout();  
            	   ftp.disconnect();  
               } catch (IOException e) {  
                   e.printStackTrace();  
               }   
           }  
       }  
   }  
   public static void main(String[] args) throws Exception{  
	   FtpUpload t = new FtpUpload();  
	   String path = "ftp://"+Common.FTP_USERNAME +":"+Common.FTP_PASSWORD +"@"+Common.FTP_IP +"/"+"67/";
	   t.connect("67/", Common.FTP_IP, 21, "wfp", "123123");
	   String[] files = t.ftp.listNames();
	   System.out.println(files.length);
   }  

}
