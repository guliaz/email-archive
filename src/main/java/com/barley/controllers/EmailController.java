package com.barley.controllers;

import com.barley.model.Attachment;
import com.barley.model.Email;
import com.barley.model.File;
import com.barley.repository.AttachmentRepository;
import com.barley.repository.EmailRepository;
import com.barley.repository.FileRepository;
import com.barley.repository.RecipientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(value = "/emails")
public class EmailController {

    @Autowired
    private EmailRepository emailRepository;

    @Autowired
    private AttachmentRepository attachmentRepository;

    @Autowired
    private FileRepository fileRepository;

    @Autowired
    private RecipientRepository recipientRepository;

    @RequestMapping(value = "/list", produces = "application/json")
    public Iterable<Email> list() {
        return emailRepository.findAll();
    }

    @RequestMapping(value = "/count", method = RequestMethod.GET)
    public String count() {
        return "{ \"count\":" + emailRepository.count() + "}";
    }

    @RequestMapping(value = "/list", method = RequestMethod.GET, produces = "application/json")
    public Iterable<Email> list(@RequestParam("page") Long page, @RequestParam("number") Long numPerPage) {
        return getEmails(page, numPerPage);
    }

    public Iterable<Email> getEmails(Long page, Long numPerPage) {
        final List<Long> ids = new ArrayList<>();
        Long start = (page - 1) * numPerPage + 1;
        final Long end = ((page - 1) * numPerPage) + numPerPage;
        while (end > start) {
            ids.add(start);
            start += 1;
        }
        final Iterable<Email> emails = emailRepository.findAll(ids);
        for (Email email : emails) {
            final Iterable<Attachment> attachments = attachmentRepository.findByMessageId(email.getMessage_id());
            System.out.println(attachments);
            final List<Long> fileIds = new ArrayList<>();
            for (Attachment attachment : attachments) {
                fileIds.add(attachment.getFile_id());
            }
            final Iterable<File> fileIterable = fileRepository.findAll(fileIds);
            System.out.println(fileIterable);
            final List<File> files = new ArrayList<>();
            for (File file : fileIterable) {
                files.add(file);
            }
            System.out.println(files);
            email.setFileArray(files);
        }
        return emails;
    }

}
