package cn.infomany.service.impl.minio;

import cn.infomany.service.MinIoService;
import com.qiniu.util.StringUtils;
import io.minio.MinioClient;
import io.minio.errors.InvalidEndpointException;
import io.minio.errors.InvalidPortException;

/**
 * MinIo客户端
 *
 * @author zjb
 * @date 2020/7/24
 */
public class MinIoClient extends MinIoOssClient implements MinIoService {


    public static MinIoClient.Builder defaultBuilder() {
        return new MinIoClient.Builder();
    }

    public static class Builder {

        private String accessKey;
        private String secretKey;
        private String bucket;
        private String domain;

        public MinIoClient.Builder domain(String domain) {
            this.domain = domain;
            return this;
        }

        public MinIoClient.Builder bucket(String bucket) {
            this.bucket = bucket;
            return this;
        }

        public MinIoClient.Builder accessKey(String accessKey) {
            this.accessKey = accessKey;
            return this;
        }

        public MinIoClient.Builder secretKey(String secretKey) {
            this.secretKey = secretKey;
            return this;
        }


        public MinIoClient build() {
            checkMustArgs();
            MinIoClient minIoClient = new MinIoClient();
            // 必要参数同步到tQiNiuClient
            minIoClient.bucket = bucket;

            // 根据基础必要参数设置其他信息
            try {
                minIoClient.minioClient = new MinioClient(domain, accessKey, secretKey);
            } catch (InvalidEndpointException e) {
                throw new RuntimeException("domain 解析发生错误：" + e.getMessage());
            } catch (InvalidPortException e) {
                throw new RuntimeException("连接端口无效：" + e.getMessage());
            }

            return minIoClient;
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
