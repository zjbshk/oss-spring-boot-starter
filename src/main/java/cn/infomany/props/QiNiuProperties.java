package cn.infomany.props;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 七牛云配置文件
 *
 * @author zjb
 * @date 2020/7/17
 */
@ConfigurationProperties(prefix = "oss.qiniu")
public class QiNiuProperties {
    /**
     * AccessKey
     */
    private String accessKey;

    /**
     * SecretKey
     */
    private String secretKey;

    /**
     * 存储空间名
     */
    private String bucket;

    /**
     * 外链
     */
    private String domain;

    public String getAccessKey() {
        return accessKey;
    }

    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public String getBucket() {
        return bucket;
    }

    public void setBucket(String bucket) {
        this.bucket = bucket;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }
}
