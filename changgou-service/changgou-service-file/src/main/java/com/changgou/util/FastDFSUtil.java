package com.changgou.util;

import com.changgou.file.FastDFSFile;
import org.csource.common.NameValuePair;
import org.csource.fastdfs.*;
import org.springframework.core.io.ClassPathResource;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * @Author: HeWei·Yuan
 * @CreateTime: 2021-04-20 15:41
 * @Description: 文件管理
 */
public class FastDFSUtil {
    static {
        try {
            String path = new ClassPathResource("fdfs_client.conf").getPath();
            ClientGlobal.init(path);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 文件上传
     *
     * @param fastDFSFile
     * @return
     */
    public static String[] upload(FastDFSFile fastDFSFile) throws Exception {
        // 附加参数
        NameValuePair[] meta_list = new NameValuePair[1];
        meta_list[0] = new NameValuePair("author", fastDFSFile.getAuthor());

        // 1.创建trackerClient
        TrackerClient trackerClient = new TrackerClient();
        // 2.获取trackerServer
        TrackerServer trackerServer = trackerClient.getConnection();
        // 3.获取storage连接信息
        StorageClient storageClient = new StorageClient(trackerServer, null);
        // 4.操作
        // 返回两个参数第一个uploads[0] 组名字  uploads[1] 创建的文件的名字
        String[] uploads = storageClient.upload_file(fastDFSFile.getContent(), fastDFSFile.getExt(), meta_list);

        return uploads;
    }

    /**
     * 获取文件信息
     * @param groupName
     * @param remoteFileName
     * @return
     */
    public static FileInfo getFile(String groupName, String remoteFileName) throws Exception {
        TrackerClient trackerClient = new TrackerClient();

        TrackerServer trackerServer = trackerClient.getConnection();

        StorageClient storageClient = new StorageClient(trackerServer, null);

        return storageClient.get_file_info(groupName, remoteFileName);
    }

    /**
     * 文件下载
     * @param groupName
     * @param remoteFileName
     * @return
     * @throws Exception
     */
    public static InputStream downloadFile(String groupName, String remoteFileName) throws Exception {
        TrackerClient trackerClient = new TrackerClient();

        TrackerServer trackerServer = trackerClient.getConnection();

        StorageClient storageClient = new StorageClient(trackerServer, null);

        byte[] buffer = storageClient.download_file(groupName, remoteFileName);
        return new ByteArrayInputStream(buffer);
    }

    /**
     * 删除文件
     * @param groupName
     * @param remoteFileName
     * @throws Exception
     */
    public static void deleteFile(String groupName, String remoteFileName) throws Exception {
        TrackerClient trackerClient = new TrackerClient();

        TrackerServer trackerServer = trackerClient.getConnection();

        StorageClient storageClient = new StorageClient(trackerServer, null);

        storageClient.delete_file(groupName, remoteFileName);
    }

    /**
     * 获取storage信息
     * @return
     * @throws Exception
     */
    public static StorageServer getStorage() throws Exception{
        TrackerClient trackerClient = new TrackerClient();
        TrackerServer trackerServer = trackerClient.getConnection();
        return trackerClient.getStoreStorage(trackerServer);
    }

    /**
     * 获取storage的ip喝port
     * @param groupName
     * @param remoteFileName
     * @return
     * @throws Exception
     */
    public static ServerInfo[] getServerInfo(String groupName, String remoteFileName) throws Exception{
        TrackerClient trackerClient = new TrackerClient();
        TrackerServer trackerServer = trackerClient.getConnection();
        return trackerClient.getFetchStorages(trackerServer, groupName, remoteFileName);
    }


    public static String getTrackerInfo() throws Exception{
        TrackerClient trackerClient = new TrackerClient();
        TrackerServer trackerServer = trackerClient.getConnection();
        String ip = trackerServer.getInetSocketAddress().getHostString();
        int port = ClientGlobal.getG_tracker_http_port();
        String url = "http://" + ip + ":" + port;
        return url;
    }

    public static void main(String[] args) throws Exception {
        String groupName = "group1";
        String remoteFileName = "M00/00/00/wKjThGB-j82AEteAAAFPrXQUAjI496.jpg";
        // http://192.168.211.132:8080/group1/M00/00/00/wKjThGB-j82AEteAAAFPrXQUAjI496.jpg
        // FileInfo fileInfo = getFile("group1", "M00/00/00/wKjThGB-j82AEteAAAFPrXQUAjI496.jpg");
        // System.out.println(fileInfo.getSourceIpAddr());
        // System.out.println(fileInfo.getFileSize());

        // 文件下载
        //InputStream is = downloadFile(groupName, remoteFileName);
        //FileOutputStream os = new FileOutputStream("D:/1.jpg");
        //
        //byte[] buffer = new byte[1024];
        //while (is.read(buffer) != -1) {
        //    os.write(buffer);
        //}
        //os.flush();
        //os.close();
        //is.close();

        // 文件删除
        // downloadFile(groupName, remoteFileName);
        //System.out.println(getTrackerInfo());
    }
}
