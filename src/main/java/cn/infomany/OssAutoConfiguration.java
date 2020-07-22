package cn.infomany;

import cn.infomany.props.OssProperties;
import cn.infomany.props.QiNiuProperties;
import cn.infomany.service.impl.qiniu.QiNiuClient;
import cn.infomany.service.impl.qiniu.QiNiuOssClient;
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
@EnableConfigurationProperties({OssProperties.class, QiNiuProperties.class})
@ConditionalOnProperty(prefix = "oss", name = "enable", havingValue = "true")
public class OssAutoConfiguration {

    @Autowired
    private OssProperties ossProperties;

    @Autowired
    private QiNiuProperties qiNiuProperties;

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
}
