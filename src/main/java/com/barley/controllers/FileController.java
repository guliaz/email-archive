package com.barley.controllers;

import com.barley.common.Utilities;
import com.barley.repository.FileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.List;

@RestController
@RequestMapping(value = "/files")
@CrossOrigin
public class FileController {

    @Autowired
    private FileRepository fileRepository;

    @Autowired
    private Utilities utilities;

    @RequestMapping(value = "/upload", method = RequestMethod.GET)
    public String info() {
        return "You can upload a file by posting to this same URL.";
    }

    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    public String upload(@RequestParam("file") MultipartFile[] files) {
        if (files.length > 0) {
            for (int i = 0; i < files.length; i++) {
                MultipartFile file = files[i];
                final String name = file.getOriginalFilename();
                try {
                    final byte[] bytes = file.getBytes();
                    final BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(new File(name)));
                    stream.write(bytes);
                    stream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                    return "You failed to upload " + name + " => " + e.getMessage();
                }
            }
            return "You successfully uploaded!";
        } else {
            return "You failed to upload because the file was empty.";
        }
    }

    @RequestMapping(value = "/", method = RequestMethod.GET)
    @ResponseBody
    public void getFile(@RequestParam("file_name") String fileName, HttpServletResponse response) throws Exception {
        System.out.println(fileName);
        final List<com.barley.model.File> files = fileRepository.findByLongFileName(fileName);
        if (files != null && files.size() > 0) {
            com.barley.model.File file = files.get(0);
            System.out.println(file.getLongFileName());
            System.out.println(file.getMime_tag());
            response.setHeader("Content-Type", "application/" + file.getExtension().substring(1));
            response.setHeader("Content-Disposition", "attachment;filename=" + file.getLongFileName());
            utilities.prepareResponse(response, file.getData());
        }
    }

}
