package cn.infomany.service.impl.qiniu;

import cn.infomany.UploadTokenCreate;
import cn.infomany.beans.FileDetail;
import cn.infomany.service.OssService;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.FileInfo;
import com.qiniu.util.Auth;
import com.qiniu.util.StringUtils;

import java.io.IOException;
import java.io.InputStream;

/**
 * 七牛基础Oss实现
 *
 * @author zjb
 * @date 2020/7/22
 */
public class QiNiuOssClient implements OssService {

    protected UploadTokenCreate uploadTokenCreate;

    protected String bucket;
    protected String domain;

    protected Auth auth;

    protected BucketManager bucketManager;
    protected UploadManager uploadManager;


    @Override
    public <T> T uploadFile(InputStream inputStream, String fileName, Class<T> clazz) throws QiniuException {
        String upToken = uploadTokenCreate.getUploadToken();
        Response response = uploadManager
                .put(inputStream, fileName, upToken, null, null);
        return response.jsonToObject(clazz);
    }

    @Override
    public String createAccessFileUrl(String fileName, long expireInSeconds) {
        if (StringUtils.isNullOrEmpty(fileName)) {
            throw new IllegalArgumentException("fileName为空");
        }
        String publicUrl = String.format("http://%s/%s", domain, fileName);
        return auth.privateDownloadUrl(publicUrl, expireInSeconds);
    }

    @Override
    public FileDetail getFileInfo(String fileName) throws IOException {
        FileInfo stat = bucketManager.stat(bucket, fileName);
        FileDetail fileDetail = new FileDetail();
        fileDetail.setEndUser(stat.endUser);
        fileDetail.setFileSize(stat.fsize);
        fileDetail.setHash(stat.hash);
        fileDetail.setKey(stat.key);
        fileDetail.setMd5(stat.md5);
        fileDetail.setPutTime(stat.putTime);
        fileDetail.setMimeType(stat.mimeType);
        fileDetail.setStatus(stat.status);
        fileDetail.setType(stat.type);
        return fileDetail;
    }

    @Override
    public boolean fileExists(String fileName) throws IOException {
        try {
            bucketManager.stat(bucket, fileName);
        } catch (QiniuException e) {
            if (e.code() == 612) {
                return false;
            }
            throw new IOException(e.error());
        }

        return true;
    }


    @Override
    public boolean deleteFile(String fileName) throws IOException {
        return bucketManager.delete(bucket, fileName).isOK();
    }

    @Override
    public boolean changeFileName(String srcFileName, String targetFileName, boolean force) throws IOException {
        return bucketManager.move(bucket, srcFileName, bucket, targetFileName, force).isOK();
    }

    @Override
    public boolean copyFile(String srcFileName, String targetFileName, boolean force) throws IOException {
        return bucketManager.copy(bucket, srcFileName, bucket, targetFileName, force).isOK();
    }
}
