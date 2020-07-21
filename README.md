#### oss-spring-boot-starter

> 对象存储`oss-spring-boot-starter`主要目的就是为了将各个平台的OSS提供的接口进行统一的封装，然后再提供统一的接口。
>
> 简单来说就是进行一层封装，屏蔽各个平台OSS提供接口的差异。

就所有OSS都需要提供的方法接口，抽取定义了`OssService`

```java
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
```

> 对于各个平台上提供的自己特有的方法，抽取除了各个平台的接口，例如：七牛云对应的接口是`QiNiuService`

```java
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
```

这里默认实现一个对接七牛Client--->`QiNiuClient`，并将其放进了spring容器当中了，只要配置了oss，就可以直接使用。

