package cn.infomany.service;

import com.qiniu.common.QiniuException;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.model.BatchStatus;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.storage.model.FetchRet;

import java.io.InputStream;

/**
 * 对象存储云接口
 *
 * @author zjb
 * @date 2020/7/20
 */
public interface QiNiuService extends OssService {

    /**
     * 文件上传
     *
     * @param inputStream
     * @param fileName
     * @return
     */
    DefaultPutRet uploadFile(InputStream inputStream, String fileName) throws QiniuException;

    /**
     * 生成一个文件上传的token
     *
     * @return 返回一个授权文件上传的token
     */
    String createUploadFileToken();

    /**
     * 修改文件类型
     *
     * @param fileName    文件名
     * @param newMimeType 新的文件类型
     * @return
     */
    default boolean changeFileMime(String fileName, String newMimeType) throws QiniuException {
        throw new UnsupportedOperationException("该方法未实现");
    }

    /**
     * 设置文件过期天数
     *
     * @param fileName 文件名
     * @param days     文件过期天数
     * @return
     */
    default boolean fileExpired(String fileName, int days) throws QiniuException {
        throw new UnsupportedOperationException("该方法未实现");
    }


    /**
     * 获取指定文件路径后的文件集合
     *
     * @param path      文件路径
     * @param delimiter 文件路径目录分隔符
     * @param limit     最多获取多少个文件信息
     * @return
     */
    default BucketManager.FileListIterator listDir(String path, String delimiter, int limit) {
        throw new UnsupportedOperationException("该方法未实现");
    }


    boolean copyFileName(String srcFileName, String targetFileName, boolean force) throws QiniuException;

    /**
     * 从远程Url中获取文件并保存
     *
     * @param remoteUrl 远程文件
     * @param fileName  文件名
     * @return
     */
    FetchRet fetch(String remoteUrl, String fileName) throws QiniuException;


    /**
     * 批量操作
     *
     * @param batchOperations
     * @return
     */
    BatchStatus[] operationsBatch(BucketManager.BatchOperations batchOperations) throws QiniuException;
}
