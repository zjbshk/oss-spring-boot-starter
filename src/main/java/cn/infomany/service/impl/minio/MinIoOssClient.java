package cn.infomany.service.impl.minio;

import cn.infomany.beans.FileDetail;
import cn.infomany.service.OssService;
import io.minio.MinioClient;
import io.minio.ObjectStat;
import io.minio.PutObjectOptions;
import io.minio.errors.*;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * MinIo对象存储服务
 *
 * @author zjb
 * @date 2020/7/24
 */
public class MinIoOssClient implements OssService {

    protected MinioClient minioClient;
    protected String bucket;

    @Override
    public <T> T uploadFile(InputStream inputStream, String fileName, Class<T> clazz) throws IOException {
        try {
            minioClient.putObject(bucket, fileName, inputStream, new PutObjectOptions(-1L, PutObjectOptions.MIN_MULTIPART_SIZE));
        } catch (ErrorResponseException |
                InsufficientDataException |
                InternalException |
                InvalidBucketNameException |
                InvalidKeyException |
                InvalidResponseException |
                NoSuchAlgorithmException |
                XmlParserException e) {
            throw new IOException(e.getMessage());
        }
        return null;
    }

    @Override
    public String createAccessFileUrl(String fileName, long expireInSeconds) {
        int expires;
        if (expireInSeconds > 7 * 24 * 3600) {
            expires = 7 * 24 * 3600;
        } else {
            expires = Long.valueOf(expireInSeconds).intValue();
        }
        try {
            return minioClient.presignedGetObject(bucket, fileName, expires);
        } catch (ErrorResponseException |
                InsufficientDataException |
                InternalException |
                InvalidBucketNameException |
                InvalidKeyException |
                InvalidResponseException |
                NoSuchAlgorithmException |
                XmlParserException | IOException | InvalidExpiresRangeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public FileDetail getFileInfo(String fileName) throws IOException {
        try {
            ObjectStat objectStat = minioClient.statObject(bucket, fileName);
            FileDetail fileDetail = new FileDetail();
            fileDetail.setKey(objectStat.name());
            fileDetail.setFileSize(objectStat.length());
            fileDetail.setPutTime(objectStat.createdTime().toEpochSecond());
            fileDetail.setMimeType(objectStat.contentType());
            return fileDetail;
        } catch (ErrorResponseException |
                InsufficientDataException |
                InternalException |
                InvalidBucketNameException |
                InvalidKeyException |
                InvalidResponseException |
                NoSuchAlgorithmException |
                XmlParserException e) {
            throw new IOException(e.getMessage());
        }
    }

    @Override
    public boolean fileExists(String fileName) throws IOException {
        try {
            minioClient.statObject(bucket, fileName);
            return true;
        } catch (InsufficientDataException |
                InternalException |
                InvalidBucketNameException |
                InvalidKeyException |
                InvalidResponseException |
                NoSuchAlgorithmException |
                XmlParserException e) {
            throw new IOException(e.getMessage());
        } catch (ErrorResponseException e) {
            return false;
        }
    }

    @Override
    public boolean deleteFile(String fileName) throws IOException {
        try {
            minioClient.removeObject(bucket, fileName);
            return true;
        } catch (ErrorResponseException |
                InsufficientDataException |
                InternalException |
                InvalidBucketNameException |
                InvalidKeyException |
                InvalidResponseException |
                NoSuchAlgorithmException |
                XmlParserException e) {
            throw new IOException(e.getMessage());
        }
    }

    /**
     * 该实现中通过了三次访问文件服务器实现，实现不太安全，
     * 如果不是业务需要，不要轻易使用
     *
     * @param srcFileName    源文件名
     * @param targetFileName 目标文件名
     * @param force          是否强制覆盖
     * @return
     * @throws IOException
     */
    @Override
    public boolean changeFileName(String srcFileName, String targetFileName, boolean force) throws IOException {
        try {
            InputStream srcIS = minioClient.getObject(bucket, srcFileName);
            minioClient.putObject(bucket, targetFileName, srcIS,
                    new PutObjectOptions(-1L, PutObjectOptions.MIN_MULTIPART_SIZE));
            minioClient.removeObject(bucket, srcFileName);
            return true;
        } catch (ErrorResponseException |
                InsufficientDataException |
                InternalException |
                InvalidBucketNameException |
                InvalidKeyException |
                InvalidResponseException |
                NoSuchAlgorithmException |
                XmlParserException e) {
            e.printStackTrace();
            return false;
        }

    }
}
