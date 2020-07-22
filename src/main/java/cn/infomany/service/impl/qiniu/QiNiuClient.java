package cn.infomany.service.impl.qiniu;

import cn.infomany.UploadTokenCreate;
import cn.infomany.service.QiNiuService;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.BatchStatus;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.storage.model.FetchRet;
import com.qiniu.util.Auth;
import com.qiniu.util.StringUtils;

import java.io.InputStream;
import java.util.Objects;

/**
 * 七牛云服务实现类
 *
 * @author zjb
 * @date 2020/7/20
 */
public class QiNiuClient extends QiNiuOssClient implements QiNiuService {


    @Override
    public DefaultPutRet uploadFile(InputStream inputStream, String fileName) throws QiniuException {
        return uploadFile(inputStream, fileName, DefaultPutRet.class);
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


        public QiNiuClient build() {
            checkMustArgs();
            QiNiuClient tQiNiuClient = new QiNiuClient();
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
