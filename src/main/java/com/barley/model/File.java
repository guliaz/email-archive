package com.barley.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Date;

@Entity
public class File {

    @Id
    @GeneratedValue
    private Long file_id;
    @Column(name = "file_name")
    private String fileName;
    @Column(name = "long_file_name")
    private String longFileName;
    private String mime_tag;
    private String extension;
    private byte[] data;
    private Date date_created;

    public Long getFile_id() {
        return file_id;
    }

    public void setFile_id(Long file_id) {
        this.file_id = file_id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getLongFileName() {
        return longFileName;
    }

    public void setLongFileName(String longFileName) {
        this.longFileName = longFileName;
    }

    public String getMime_tag() {
        return mime_tag;
    }

    public void setMime_tag(String mime_tag) {
        this.mime_tag = mime_tag;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public Date getDate_created() {
        return date_created;
    }

    public void setDate_created(Date date_created) {
        this.date_created = date_created;
    }
}
