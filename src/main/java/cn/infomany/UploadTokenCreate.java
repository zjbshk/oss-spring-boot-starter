package cn.infomany;

/**
 * 文件上传token生成器
 *
 * @author zjb
 * @date 2020/7/20
 */
public interface UploadTokenCreate {

    /**
     * 获取文件上传token
     *
     * @return 生成的token
     */
    String getUploadToken();
}
