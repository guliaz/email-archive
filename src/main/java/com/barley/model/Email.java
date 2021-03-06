package com.barley.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Transient;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
public class Email {

    @Id
    @GeneratedValue
    private Long message_id;
    private String outlook_id;
    private String subject;
    private String body_text;
    private String body_html;
    private String from_email;
    private String from_name;
    private Date message_creation_date;
    private Date message_date;
    private Date date_created;
    private String to_email;
    private String to_name;
    private String display_to;
    private String display_cc;
    private String display_bcc;
    @Transient
    private boolean show = false;
    @Transient
    private boolean showBody = false;
    @Transient
    private List<FileAttach> fileArray = new ArrayList<>();
    @Transient
    private List<EmailAttach> emailArray = new ArrayList<>();

    public Long getMessage_id() {
        return message_id;
    }

    public void setMessage_id(Long message_id) {
        this.message_id = message_id;
    }

    public String getOutlook_id() {
        return outlook_id;
    }

    public void setOutlook_id(String outlook_id) {
        this.outlook_id = outlook_id;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getBody_text() {
        return body_text;
    }

    public void setBody_text(String body_text) {
        this.body_text = body_text;
    }

    public String getBody_html() {
        return body_html;
    }

    public void setBody_html(String body_html) {
        this.body_html = body_html;
    }

    public String getFrom_email() {
        return from_email;
    }

    public void setFrom_email(String from_email) {
        this.from_email = from_email;
    }

    public String getFrom_name() {
        return from_name;
    }

    public void setFrom_name(String from_name) {
        this.from_name = from_name;
    }

    public String getMessage_creation_date() {
        return getDateString(message_creation_date);
    }

    public void setMessage_creation_date(Date message_creation_date) {
        this.message_creation_date = message_creation_date;
    }

    public String getMessage_date() {
        return getDateString(message_date);
    }

    public void setMessage_date(Date message_date) {
        this.message_date = message_date;
    }

    public String getDate_created() {
        return getDateString(date_created);
    }

    public void setDate_created(Date date_created) {
        this.date_created = date_created;
    }

    public String getTo_email() {
        return to_email;
    }

    public void setTo_email(String to_email) {
        this.to_email = to_email;
    }

    public String getTo_name() {
        return to_name;
    }

    public void setTo_name(String to_name) {
        this.to_name = to_name;
    }

    public String getDisplay_to() {
        return display_to;
    }

    public void setDisplay_to(String display_to) {
        this.display_to = display_to;
    }

    public String getDisplay_cc() {
        return display_cc;
    }

    public void setDisplay_cc(String display_cc) {
        this.display_cc = display_cc;
    }

    public String getDisplay_bcc() {
        return display_bcc;
    }

    public void setDisplay_bcc(String display_bcc) {
        this.display_bcc = display_bcc;
    }

    public boolean isShow() {
        return show;
    }

    public void setShow(boolean show) {
        this.show = show;
    }

    public boolean isShowBody() {
        return showBody;
    }

    public void setShowBody(boolean showBody) {
        this.showBody = showBody;
    }

    public List<FileAttach> getFileArray() {
        return fileArray;
    }

    public void setFileArray(List<FileAttach> fileArray) {
        this.fileArray = fileArray;
    }

    public List<EmailAttach> getEmailArray() {
        return emailArray;
    }

    public void setEmailArray(List<EmailAttach> emailArray) {
        this.emailArray = emailArray;
    }

    @Transient
    private String getDateString(Date date) {
        if (date == null)
            return null;
        else
            return date.toLocaleString();
    }
}
