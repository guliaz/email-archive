/*
package com.barley.processor;

import com.auxilii.msgparser.MsgParser;
import com.barley.model.Attachment;

import javax.activation.DataHandler;
import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.*;
import java.util.Date;
import java.util.Properties;

public class Main {
    private static final MsgParser parser = new MsgParser();

    public static void main(String[] args) throws Exception {
        Main main = new Main();
        File file = new File(main.getClass().getClassLoader().getResource("email2.eml").getFile());
        Properties props = System.getProperties();
        props.put("mail.host", "smtp.dummydomain.com");
        props.put("mail.transport.protocol", "smtp");
        Session mailSession = Session.getDefaultInstance(props, null);
        InputStream source = new FileInputStream(file);
        MimeMessage mimeMessage = new MimeMessage(mailSession, source);
        System.out.println("subject - " + mimeMessage.getSubject());
        System.out.println("messageId - " + mimeMessage.getMessageID());
        System.out.println("contentId - " + mimeMessage.getContentID());
        //printMessage(mimeMessage);
        System.out.println("##################################");
        System.out.println("##################################");
        System.out.println(" Content type mimeMessage content - " + mimeMessage.getContentType());
        MimeMultipart mimeMultipart = (MimeMultipart) mimeMessage.getContent();
        exploreMessage(mimeMultipart);

        System.out.println("Count: " + mimeMultipart.getCount());
        System.out.println("content - " + mimeMessage.getContent().toString());
        System.out.println("content md5 - " + mimeMessage.getContentMD5());
        System.out.println("desc - " + mimeMessage.getDescription());
        System.out.println("disposotion - " + mimeMessage.getDisposition());
        System.out.println("encoding - " + mimeMessage.getEncoding());
        System.out.println("filename - " + mimeMessage.getFileName());
        System.out.println("all header lines - " + mimeMessage.getAllHeaderLines());
        System.out.println("headers - " + mimeMessage.getAllHeaders());
        System.out.println("recipients - " + mimeMessage.getAllRecipients());
        System.out.println("content lang - " + mimeMessage.getContentLanguage());
        System.out.println("flags - " + mimeMessage.getFlags());
        System.out.println("from - " + mimeMessage.getFrom());
        System.out.println("receive date - " + mimeMessage.getReceivedDate());
        System.out.println("reply to - " + mimeMessage.getReplyTo());
        System.out.println("sender - " + mimeMessage.getSender());
        System.out.println("sent date - " + mimeMessage.getSentDate());
        System.out.println("size - " + mimeMessage.getSize());
        System.out.println("folder - " + mimeMessage.getFolder());
        System.out.println("message number - " + mimeMessage.getMessageNumber());
    }

    private static void exploreMessage(MimeMultipart mimeMultipart) throws MessagingException, IOException {
        for (int i = 0; i < mimeMultipart.getCount(); i++) {
            BodyPart bodyPart = mimeMultipart.getBodyPart(i);
            System.out.println("################################## " + i);
            System.out.println("################################## " + i);
            String contentType = bodyPart.getContentType();
            System.out.println(contentType);
            if (contentType.contains("multipart")) {
                MimeMultipart multipart = (MimeMultipart) bodyPart.getContent();
                exploreMessage(multipart);
            } else if (contentType.contains("message")) {
                MimeMessage mimeMessage = (MimeMessage) bodyPart.getContent();
                MimeMultipart mimeMessageContent = (MimeMultipart) mimeMessage.getContent();
                exploreMessage(mimeMessageContent);
            } else
                printMessage(bodyPart);
        }
    }

    private static void printMessage(Part mimeMessage) throws IOException, MessagingException {
        // body_text
        if (mimeMessage.getContentType().contains("text/plain")) {
            System.out.println(mimeMessage.getContent());
        }
        // body_html
        else if (mimeMessage.getContentType().contains("text/html")) {
            System.out.println(mimeMessage.getContent());
        }
        // file - attachment
        else if (mimeMessage.getContentType().contains("octet")) {
            // File
            com.barley.model.File file = new com.barley.model.File();
            DataHandler handler = mimeMessage.getDataHandler();
            file.setFileName(handler.getName());
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            handler.writeTo(os);
            os.flush();
            file.setData(os.toByteArray());
            file.setDate_created(new Date());
            System.out.println(handler.getContentType().split(";")[0]);
            file.setExtension(handler.getContentType());
            file.setLongFileName(handler.getName());
            os.close();

            // Attachment

            Attachment attachment = new Attachment();
            attachment.setAttachment_type("FILE");
            attachment.setDate_created(new Date());
            //attachment.setFile_id();
            //attachment.setMessageId();
        } else {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            mimeMessage.writeTo(os);
            os.flush();
            System.out.println(new String(os.toByteArray()));
            os.close();
        }
    }
}
*/
