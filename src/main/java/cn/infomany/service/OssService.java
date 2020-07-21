package cn.infomany.service;

import cn.infomany.beans.FileDetail;
import com.qiniu.common.QiniuException;

import java.io.IOException;
import java.io.InputStream;

/**
 * 对象存储云接口
 *
 * @author zjb
 * @date 2020/7/20
 */
public interface OssService {

    /**
     * 上传文件
     *
     * @param inputStream 文件流
     * @param fileName        文件名
     * @return 返回泛型
     */
    <T> T uploadFile(InputStream inputStream, String fileName, Class<T> clazz) throws IOException;

    /**
     * 生成文件访问链接
     *
     * @param fileName 文件名
     * @return 访问链接
     */
    String createAccessFileUrl(String fileName, long expireInSeconds);

    /**
     * 获取指定文件的信息
     *
     * @param fileName 文件名
     * @return 文件信息对象
     */
    FileDetail getFileInfo(String fileName) throws QiniuException, IOException;

    /**
     * 判断文件是否存在
     *
     * @param fileName 文件名
     * @return 文件是否存在
     */
    boolean fileExists(String fileName) throws IOException;

    /**
     * 删除指定文件名的文件
     *
     * @param fileName 文件名
     * @return
     */
    boolean deleteFile(String fileName) throws QiniuException, IOException;

    /**
     * 修改指定文件名称
     *
     * @param srcFileName    源文件名
     * @param targetFileName 目标文件名
     * @return
     */
    default boolean changeFileName(String srcFileName, String targetFileName) throws IOException {
        throw new UnsupportedOperationException("该方法未实现");
    }

    /**
     * 复制文件
     *
     * @param srcFileName    源文件名
     * @param targetFileName 目标文件名
     * @return
     */
    default boolean copyFile(String srcFileName, String targetFileName, boolean force) {
        throw new UnsupportedOperationException("该方法未实现");
    }


}
