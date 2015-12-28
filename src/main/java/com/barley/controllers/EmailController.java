package com.barley.controllers;

import com.barley.common.Utilities;
import com.barley.model.Attachment;
import com.barley.model.Email;
import com.barley.model.File;
import com.barley.repository.AttachmentRepository;
import com.barley.repository.EmailRepository;
import com.barley.repository.FileRepository;
import com.barley.repository.RecipientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(value = "/emails")
@CrossOrigin
public class EmailController {

    @Autowired
    private EmailRepository emailRepository;

    @Autowired
    private AttachmentRepository attachmentRepository;

    @Autowired
    private FileRepository fileRepository;

    @Autowired
    private RecipientRepository recipientRepository;

    @Autowired
    private Utilities utilities;

    @RequestMapping(value = "/list/count", method = RequestMethod.GET)
    public String count() {
        return "{ \"count\":" + emailRepository.count() + "}";
    }

    @RequestMapping(value = "/list/{message_id}", method = RequestMethod.DELETE)
    public void delete(@PathVariable(value = "message_id") Long message_id) {
        System.out.println("Received email to delete FIRST BLOCK " + message_id);
    }

    @RequestMapping(value = "/list/{message_id}", method = RequestMethod.GET)
    public Email getOne(@PathVariable(value = "message_id") Long message_id) {
        return emailRepository.findOne(message_id);
    }

    @RequestMapping(value = "/list/{message_id}/html", method = RequestMethod.GET)
    public void getHtml(@PathVariable(value = "message_id") Long message_id, HttpServletResponse response) throws IOException {
        Email email = emailRepository.findOne(message_id);
        if (email != null && (email.getBody_html() != null || email.getBody_text() != null)) {
            response.setHeader("Content-Type", "text/html");
            response.setHeader("X-Frame-Options", "");
            byte[] bytes = null;
            String html = email.getBody_html();
            if (html != null && html.length() > 0)
                bytes = html.getBytes();
            else
                bytes = email.getBody_text().getBytes();
            utilities.prepareResponse(response, bytes);
        }
    }

    @RequestMapping(value = "/list", method = RequestMethod.GET, produces = "application/json")
    public List<Email> list(Pageable pageable) {
        return getEmails(pageable);
    }

    public List<Email> getEmails(Pageable pageable) {
        final Page<Email> emails = emailRepository.findAll(pageable);
        emails.forEach(email -> {
            attachmentRepository
                    .findByMessageId(email.getMessage_id())
                    .stream()
                    .filter(att -> att != null)
                    .forEach(attachment -> {
                        if (attachment.getAttachment_type().equalsIgnoreCase("FILE")) {
                            email.getFileArray().add(fileRepository.findOne(attachment.getFile_id()).getLongFileName());
                        } else {
                            email.getEmailArray().add(emailRepository.findOne(attachment.getMessageId()).getMessage_id());
                        }
                    });
        });
        return emails.getContent();
    }

}
