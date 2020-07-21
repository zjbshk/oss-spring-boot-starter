package cn.infomany.service.impl;

import cn.infomany.UploadTokenCreate;
import cn.infomany.beans.FileDetail;
import cn.infomany.service.QiNiuService;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.BatchStatus;
import com.qiniu.storage.model.FetchRet;
import com.qiniu.storage.model.FileInfo;
import com.qiniu.util.Auth;
import com.qiniu.util.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.Objects;

/**
 * 七牛云服务实现类
 *
 * @author zjb
 * @date 2020/7/20
 */
public class QiNiuClient<T> implements QiNiuService {

    private UploadTokenCreate uploadTokenCreate;

    private String bucket;
    private String domain;

    private Auth auth;

    private BucketManager bucketManager;
    private UploadManager uploadManager;


    @Override
    public T uploadFile(InputStream fileInputStream, String fileName) throws IOException {
        String upToken = uploadTokenCreate.getUploadToken();
        Response response = uploadManager
                .put(fileInputStream, fileName, upToken, null, null);
        Type type = new TypeToken<T>() {
        }.getType();
        //解析上传成功的结果
        return new Gson().fromJson(response.bodyString(), type);
    }

    @Override
    public String createAccessFileUrl(String fileName, long expireInSeconds) {
        if (StringUtils.isNullOrEmpty(fileName)) {
            throw new IllegalArgumentException("fileName为空");
        }
        String publicUrl = String.format("%s/%s", domain, fileName);
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
            FileInfo stat = bucketManager.stat(bucket, fileName);
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
    public String createUploadFileToken() {
        return uploadTokenCreate.getUploadToken();
    }

    @Override
    public boolean changeFileMime(String fileName, String newMimeType) throws QiniuException {
        return bucketManager.changeMime(bucket, fileName, newMimeType).isOK();
    }

    @Override
    public boolean fileExpired(String fileName, int days) throws QiniuException {
        return bucketManager.deleteAfterDays(bucket, fileName, days).isOK();
    }

    @Override
    public BucketManager.FileListIterator listDir(String path, String delimiter, int limit) {
        return bucketManager.createFileListIterator(bucket, path, limit, delimiter);
    }

    @Override
    public boolean changeFileName(String srcFileName, String targetFileName) throws IOException {
        return bucketManager.rename(bucket, srcFileName, targetFileName).isOK();
    }

    @Override
    public boolean copyFileName(String srcFileName, String targetFileName, boolean force) throws QiniuException {
        return bucketManager.copy(bucket, srcFileName, bucket, targetFileName, force).isOK();
    }

    @Override
    public FetchRet fetch(String remoteUrl, String fileName) throws QiniuException {
        return bucketManager.fetch(remoteUrl, bucket, fileName);
    }

    @Override
    public BatchStatus[] operationsBatch(BucketManager.BatchOperations batchOperations) throws QiniuException {
        Response response = bucketManager.batch(batchOperations);
        return response.jsonToObject(BatchStatus[].class);
    }

    public static Builder defaultBuilder() {
        return new Builder();
    }

    public static class Builder {

        private UploadTokenCreate uploadTokenCreate;
        private Configuration cfg;
        private String accessKey;
        private String secretKey;
        private String bucket;
        private String domain;

        public Builder uploadTokenCreate(UploadTokenCreate uploadTokenCreate) {
            this.uploadTokenCreate = uploadTokenCreate;
            return this;
        }

        public Builder cfg(Configuration cfg) {
            this.cfg = cfg;
            return this;
        }

        public Builder domain(String domain) {
            this.domain = domain;
            return this;
        }

        public Builder bucket(String bucket) {
            this.bucket = bucket;
            return this;
        }

        public Builder accessKey(String accessKey) {
            this.accessKey = accessKey;
            return this;
        }

        public Builder secretKey(String secretKey) {
            this.secretKey = secretKey;
            return this;
        }


        public <T> QiNiuClient<T> build() {
            checkMustArgs();
            QiNiuClient<T> tQiNiuClient = new QiNiuClient<>();
            // 必要参数同步到tQiNiuClient
            tQiNiuClient.bucket = bucket;
            tQiNiuClient.domain = domain;

            // 根据基础必要参数设置其他信息
            tQiNiuClient.auth = Auth.create(accessKey, secretKey);
            if (Objects.isNull(cfg)) {
                cfg = new Configuration();
            }
            tQiNiuClient.uploadManager = new UploadManager(cfg);
            tQiNiuClient.bucketManager = new BucketManager(tQiNiuClient.auth, cfg);

            if (Objects.isNull(uploadTokenCreate)) {
                uploadTokenCreate = () -> tQiNiuClient.auth.uploadToken(bucket);
            }
            tQiNiuClient.uploadTokenCreate = uploadTokenCreate;

            return tQiNiuClient;
        }

        private void checkMustArgs() {
            if (StringUtils.isNullOrEmpty(accessKey) ||
                    StringUtils.isNullOrEmpty(secretKey) ||
                    StringUtils.isNullOrEmpty(bucket) ||
                    StringUtils.isNullOrEmpty(domain)) {
                throw new IllegalArgumentException("必要参数[accessKey,secretKey,bucket,domain]都不能为空");
            }
        }

    }
}
