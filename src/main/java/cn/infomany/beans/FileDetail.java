package cn.infomany.beans;

public final class FileDetail {
    /**
     * 文件名
     */
    private String key;
    /**
     * 文件hash值
     */
    private String hash;
    /**
     * 文件大小，单位：字节
     */
    private long fileSize;
    /**
     * 文件上传时间，单位为：100纳秒
     */
    private long putTime;
    /**
     * 文件的mimeType
     */
    private String mimeType;
    /**
     * 文件上传时设置的endUser
     */
    private String endUser;
    /**
     * 文件的存储类型，0为普通存储，1为低频存储
     */
    private int type;
    /**
     * 文件的状态，0表示启用，1表示禁用
     */
    private int status;
    /**
     * 文件的md5值
     */
    private String md5;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public long getPutTime() {
        return putTime;
    }

    public void setPutTime(long putTime) {
        this.putTime = putTime;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public String getEndUser() {
        return endUser;
    }

    public void setEndUser(String endUser) {
        this.endUser = endUser;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }
}
