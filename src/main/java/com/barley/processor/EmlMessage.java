package com.barley.processor;

import com.barley.model.Email;
import com.barley.model.File;
import com.barley.model.Recipient;

import java.util.ArrayList;
import java.util.List;

public class EmlMessage {

    private Email email;
    private List<File> files = new ArrayList<>();
    private List<Recipient> recipients = new ArrayList<>();
    private List<EmlMessage> emlMessages = new ArrayList<>();

    public void setEmail(Email email) {
        this.email = email;
    }

    public Email getEmail() {
        return email;
    }

    public List<File> getFiles() {
        return files;
    }

    public List<Recipient> getRecipients() {
        return recipients;
    }

    public List<EmlMessage> getEmlMessages() {
        return emlMessages;
    }
}