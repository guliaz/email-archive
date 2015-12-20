package com.barley.controllers;

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
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(value = "/emails")
@CrossOrigin(methods = {RequestMethod.GET, RequestMethod.DELETE})
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

    /*@RequestMapping(value = "/list", method = RequestMethod.GET, produces = "application/json")
    public Iterable<Email> list(@RequestParam(name = "page", required = false, defaultValue = "1") Long page, @RequestParam(name = "number", required = false, defaultValue = "100") Long numPerPage) {
        return getEmails(page, numPerPage);
    }*/

    @RequestMapping(value = "/list", method = RequestMethod.GET, produces = "application/json")
    public List<Email> list(Pageable pageable, @RequestParam(name = "email_id", required = false) Long emailId, @RequestParam(name = "delete", required = false) boolean isDelete) {
        if (emailId != null && isDelete) {
            //emailRepository.delete(emailId);
            System.out.println("Received email to delete " + emailId);
            return new ArrayList<Email>();
        }
        return getEmails(pageable);
    }

    @RequestMapping(value = "/file", method = RequestMethod.GET)
    @ResponseBody
    public void getFile(@RequestParam("file_name") String fileName, HttpServletResponse response) throws Exception {
        System.out.println(fileName);
        final List<File> files = fileRepository.findByLongFileName(fileName);
        if (files != null && files.size() > 0) {
            File file = files.get(0);
            System.out.println(file.getLongFileName());
            System.out.println(file.getMime_tag());
            response.setHeader("Content-Type", "application/" + file.getExtension().substring(1));
            response.setHeader("Content-Disposition", "attachment;filename=" + file.getLongFileName());
            OutputStream os = null;
            try {
                os = response.getOutputStream();
                os.write(file.getData());
                os.flush();
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            } finally {
                if (os != null) {
                    try {
                        os.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    //public Iterable<Email> getEmails(Long page, Long numPerPage) {
    public List<Email> getEmails(Pageable pageable) {
        /*final List<Long> ids = new ArrayList<>();
        Long start = (page - 1) * numPerPage + 1;
        final Long end = ((page - 1) * numPerPage) + numPerPage;
        while (end >= start) {
            ids.add(start);
            start += 1;
        }
        final Iterable<Email> emails = emailRepository.findAll(ids);*/
        final Page<Email> emails = emailRepository.findAll(pageable);
        emails.forEach(email -> {
            attachmentRepository.findByMessageId(email.getMessage_id()).forEach(attachment -> {
                email.getFileArray().add(fileRepository.findOne(attachment.getFile_id()));
            });
        });
        return emails.getContent();
    }

}
