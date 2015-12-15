package com.barley.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Date;

@Entity
public class File {

    @Id
    @GeneratedValue
    private Long file_id;
    private String file_name;
    private String long_file_name;
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

    public String getFile_name() {
        return file_name;
    }

    public void setFile_name(String file_name) {
        this.file_name = file_name;
    }

    public String getLong_file_name() {
        return long_file_name;
    }

    public void setLong_file_name(String long_file_name) {
        this.long_file_name = long_file_name;
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
