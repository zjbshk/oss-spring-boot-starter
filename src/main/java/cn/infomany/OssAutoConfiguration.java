package cn.infomany;

import cn.infomany.props.MinIoProperties;
import cn.infomany.props.OssProperties;
import cn.infomany.props.QiNiuProperties;
import cn.infomany.service.impl.minio.MinIoClient;
import cn.infomany.service.impl.qiniu.QiNiuClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 对象存储自动配置类
 *
 * @author zjb
 * @date 2020/7/20
 */
@Configuration
@EnableConfigurationProperties({OssProperties.class, QiNiuProperties.class, MinIoProperties.class})
@ConditionalOnProperty(prefix = "oss", name = "enable", havingValue = "true")
public class OssAutoConfiguration {

    @Autowired
    private OssProperties ossProperties;

    @Autowired
    private QiNiuProperties qiNiuProperties;

    @Autowired
    private MinIoProperties minIoProperties;

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "oss.qiniu", name = "enable", havingValue = "true")
    public QiNiuClient getQiNiuClient() {
        return QiNiuClient.defaultBuilder()
                .accessKey(qiNiuProperties.getAccessKey())
                .secretKey(qiNiuProperties.getSecretKey())
                .bucket(qiNiuProperties.getBucket())
                .domain(qiNiuProperties.getDomain())
                .build();
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "oss.minio", name = "enable", havingValue = "true")
    public MinIoClient getMinIoClient() {
        return MinIoClient.defaultBuilder()
                .accessKey(minIoProperties.getAccessKey())
                .secretKey(minIoProperties.getSecretKey())
                .bucket(minIoProperties.getBucket())
                .domain(minIoProperties.getDomain())
                .build();
    }
}
