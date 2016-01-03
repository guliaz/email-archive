package com.barley.controllers;

import com.barley.common.Utilities;
import com.barley.model.File;
import com.barley.processor.EmailService;
import com.barley.repository.FileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping(value = "/files")
@CrossOrigin
public class FileController {

    @Autowired
    private FileRepository fileRepository;

    @Autowired
    private Utilities utilities;

    @Autowired
    private EmailService emailService;

    @RequestMapping(value = "/upload", method = RequestMethod.GET)
    public String info() {
        return "You can upload a file by posting to this same URL.";
    }

    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    public String upload(@RequestParam("file") MultipartFile[] files) {
        if (files.length > 0) {
            for (MultipartFile file : files) {
                final String name = file.getOriginalFilename();
                System.out.println("File received: " + name);
                System.out.println("File inserted: " + emailService.insertEmail(file));
            }
            return "You successfully uploaded!";
        } else {
            return "You failed to upload because the file was empty.";
        }
    }

    @RequestMapping(value = "/", method = RequestMethod.GET)
    @ResponseBody
    public void getFile(@RequestParam("file_name") Long fileName, HttpServletResponse response) throws Exception {
        System.out.println(fileName);
        final File file = fileRepository.findOne(fileName);
        if (file != null) {
            System.out.println(file.getLongFileName());
            String mimeType = file.getMime_tag();
            System.out.println(mimeType);
            if (mimeType.equalsIgnoreCase(".eml")) {
                response.setHeader("Content-Type", file.getExtension());
            } else {
                response.setHeader("Content-Type", "application/" + file.getExtension().substring(1));
            }

            response.setHeader("Content-Disposition", "attachment;filename=" + file.getLongFileName());
            utilities.prepareResponse(response, file.getData());
        }
    }

}
