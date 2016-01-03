package com.barley.processor;

import com.auxilii.msgparser.Message;
import com.auxilii.msgparser.MsgParser;
import com.auxilii.msgparser.RecipientEntry;
import com.auxilii.msgparser.attachment.FileAttachment;
import com.auxilii.msgparser.attachment.MsgAttachment;
import com.barley.model.Attachment;
import com.barley.model.Email;
import com.barley.model.Recipient;
import com.barley.repository.AttachmentRepository;
import com.barley.repository.EmailRepository;
import com.barley.repository.FileRepository;
import com.barley.repository.RecipientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

@Component
public class EmailService {

    @Autowired
    private EmailRepository emailRepository;

    @Autowired
    private AttachmentRepository attachmentRepository;

    @Autowired
    private FileRepository fileRepository;

    @Autowired
    private RecipientRepository recipientRepository;

    private final MsgParser parser = new MsgParser();

    public boolean insertEmail(File file) {
        try {
            FileInputStream fis = new FileInputStream(file);
            MimeMessage mimeMessage = new MimeMessage(getMailSession(), fis);
            return insertEmail(mimeMessage);
        } catch (FileNotFoundException | MessagingException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean insertEmail(MultipartFile file) {
        if (file == null)
            return false;
        String name = file.getOriginalFilename();
        if (name != null && name.endsWith(".eml")) {
            try {
                MimeMessage mimeMessage = new MimeMessage(getMailSession(), file.getInputStream());
                return insertEmail(mimeMessage);
            } catch (IOException | MessagingException ioe) {
                ioe.printStackTrace();
                return false;
            }
        } else {
            try {
                Message msg = parser.parseMsg(file.getInputStream());
                System.out.println("Email inserted: " + loadMessage(msg, null));
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
    }

    public boolean insertEmail(MimeMessage mimeMessage) {
        if (mimeMessage == null) {
            return false;
        }
        try {
            com.independentsoft.msg.Message message = convertToMsg(mimeMessage);
            Message msg = parser.parseMsg(message.getInputStream());
            loadMessage(msg, message.getMessageDeliveryTime());
            //System.out.println("Processed parent email: " + processEmail(mimeMessage));
            return true;
        } catch (IOException | MessagingException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Convert mime to msg
     *
     * @param mimeMessage MimeMessage to be converted to Message
     * @return com.independentsoft.msg.Message message returned
     * @throws MessagingException
     * @throws IOException
     */
    private com.independentsoft.msg.Message convertToMsg(MimeMessage mimeMessage) throws MessagingException, IOException {
        List<BodyPart> allBodyParts = new ArrayList<>();

        com.independentsoft.msg.Message msgMessage = new com.independentsoft.msg.Message();

        InternetAddress[] toRecipients = (InternetAddress[]) mimeMessage.getRecipients(javax.mail.Message.RecipientType.TO);
        InternetAddress[] ccRecipients = (InternetAddress[]) mimeMessage.getRecipients(javax.mail.Message.RecipientType.CC);
        InternetAddress[] bccRecipients = (InternetAddress[]) mimeMessage.getRecipients(MimeMessage.RecipientType.BCC);

        if (toRecipients != null) {
            String displayTo = "";

            for (int i = 0; i < toRecipients.length; i++) {
                com.independentsoft.msg.Recipient recipient = new com.independentsoft.msg.Recipient();
                recipient.setAddressType("SMTP");
                recipient.setRecipientType(com.independentsoft.msg.RecipientType.TO);
                recipient.setEmailAddress(toRecipients[i].getAddress());

                if (toRecipients[i].getPersonal() != null) {
                    recipient.setDisplayName(toRecipients[i].getPersonal());
                    displayTo += toRecipients[i].getPersonal();
                } else {
                    recipient.setDisplayName(toRecipients[i].getAddress());
                    displayTo += toRecipients[i].getAddress();
                }

                msgMessage.getRecipients().add(recipient);

                if (i < toRecipients.length - 1) {
                    displayTo += "; ";
                }
            }

            msgMessage.setDisplayTo(displayTo);
        }

        if (ccRecipients != null) {
            String displayCc = "";

            for (int i = 0; i < ccRecipients.length; i++) {
                com.independentsoft.msg.Recipient recipient = new com.independentsoft.msg.Recipient();
                recipient.setAddressType("SMTP");
                recipient.setRecipientType(com.independentsoft.msg.RecipientType.CC);
                recipient.setEmailAddress(ccRecipients[i].getAddress());

                if (ccRecipients[i].getPersonal() != null) {
                    recipient.setDisplayName(ccRecipients[i].getPersonal());
                    displayCc += ccRecipients[i].getPersonal();
                } else {
                    recipient.setDisplayName(ccRecipients[i].getAddress());
                    displayCc += ccRecipients[i].getAddress();
                }

                msgMessage.getRecipients().add(recipient);

                if (i < ccRecipients.length - 1) {
                    displayCc += "; ";
                }
            }

            msgMessage.setDisplayCc(displayCc);
        }

        if (bccRecipients != null) {
            String displayBcc = "";

            for (int i = 0; i < bccRecipients.length; i++) {
                com.independentsoft.msg.Recipient recipient = new com.independentsoft.msg.Recipient();
                recipient.setAddressType("SMTP");
                recipient.setRecipientType(com.independentsoft.msg.RecipientType.BCC);
                recipient.setEmailAddress(bccRecipients[i].getAddress());

                if (bccRecipients[i].getPersonal() != null) {
                    recipient.setDisplayName(bccRecipients[i].getPersonal());
                    displayBcc += bccRecipients[i].getPersonal();
                } else {
                    recipient.setDisplayName(bccRecipients[i].getAddress());
                    displayBcc += bccRecipients[i].getAddress();
                }

                msgMessage.getRecipients().add(recipient);

                if (i < bccRecipients.length - 1) {
                    displayBcc += "; ";
                }
            }

            msgMessage.setDisplayBcc(displayBcc);
        }

        System.out.println(mimeMessage.getSentDate() + " || " + mimeMessage.getReceivedDate());

        msgMessage.setCreationTime(mimeMessage.getSentDate());
        msgMessage.setMessageDeliveryTime(mimeMessage.getSentDate());
        msgMessage.setClientSubmitTime(mimeMessage.getSentDate());
        msgMessage.setInternetMessageId(mimeMessage.getMessageID());
        msgMessage.setSubject(mimeMessage.getSubject());

        if (mimeMessage.getFrom() != null && mimeMessage.getFrom().length > 0) {
            InternetAddress from = (InternetAddress) mimeMessage.getFrom()[0];

            if (from.getPersonal() != null) {
                msgMessage.setSenderName(from.getPersonal());
            } else {
                msgMessage.setSenderName(from.getAddress());
            }

            msgMessage.setSenderEmailAddress(from.getAddress());
            msgMessage.setSenderAddressType("SMTP");
        }

        if (mimeMessage.getContent() instanceof Multipart) {
            getAllBodyParts((Multipart) mimeMessage.getContent(), allBodyParts);

            for (int i = 0; i < allBodyParts.size(); i++) {
                if (allBodyParts.get(i).isMimeType("text/plain") && allBodyParts.get(i).getDisposition() != null) {
                    String content = (String) allBodyParts.get(i).getContent();
                    byte[] buffer = content.getBytes("UTF-8"); //here you can use char set from allBodyParts.get(i)

                    com.independentsoft.msg.Attachment attachment = new com.independentsoft.msg.Attachment(allBodyParts.get(i).getFileName(), buffer);

                    attachment.setRenderingPosition(0xFFFFFFFF);
                    msgMessage.getAttachments().add(attachment);
                } else if (allBodyParts.get(i).isMimeType("text/plain")) {
                    if (allBodyParts.get(i).getContent() instanceof MimeBodyPart) {
                        MimeBodyPart mimeBodyPart = (MimeBodyPart) allBodyParts.get(i).getContent();
                        msgMessage.setBody(mimeBodyPart.getDescription());
                    } else if (allBodyParts.get(i).getContent() instanceof String) {
                        msgMessage.setBody((String) allBodyParts.get(i).getContent());
                    }
                } else if (allBodyParts.get(i).isMimeType("text/html")) {
                    if (allBodyParts.get(i).getContent() instanceof MimeBodyPart) {
                        MimeBodyPart mimeBodyPart = (MimeBodyPart) allBodyParts.get(i).getContent();

                        String htmlText = mimeBodyPart.getDescription();

                        if (msgMessage.getBodyHtmlText() == null) {
                            msgMessage.setBodyHtmlText(htmlText);
                        }
                    } else if (allBodyParts.get(i).getContent() instanceof String) {
                        String htmlText = (String) allBodyParts.get(i).getContent();

                        if (msgMessage.getBodyHtmlText() == null) {
                            msgMessage.setBodyHtmlText(htmlText);
                        }
                    }
                } else if (allBodyParts.get(i).getContent() instanceof InputStream) {
                    InputStream is = (InputStream) allBodyParts.get(i).getContent();
                    com.independentsoft.msg.Attachment attachment = new com.independentsoft.msg.Attachment(allBodyParts.get(i).getFileName(), is);

                    if (allBodyParts.get(i).getHeader("Content-Location") != null && allBodyParts.get(i).getHeader("Content-Location").length > 0) {
                        String contentId = (String) allBodyParts.get(i).getHeader("Content-Location")[0];
                        attachment.setContentId(contentId);
                        attachment.setContentLocation(contentId);
                    } else if (allBodyParts.get(i).getHeader("Content-ID") != null && allBodyParts.get(i).getHeader("Content-ID").length > 0) {
                        String contentId = (String) allBodyParts.get(i).getHeader("Content-ID")[0];
                        attachment.setContentId(contentId);
                        attachment.setContentLocation(contentId);
                    }

                    attachment.setRenderingPosition(0xFFFFFFFF);
                    msgMessage.getAttachments().add(attachment);
                } else if (allBodyParts.get(i).getContent() instanceof MimeMessage) {
                    com.independentsoft.msg.Message embeddedMessage = convertToMsg((MimeMessage) allBodyParts.get(i).getContent());

                    com.independentsoft.msg.Attachment attachment = new com.independentsoft.msg.Attachment();
                    attachment.setDisplayName(embeddedMessage.getSubject() + ".msg");
                    attachment.setFileName(embeddedMessage.getSubject() + ".msg");
                    attachment.setData(embeddedMessage.toByteArray());

                    attachment.setRenderingPosition(0xFFFFFFFF);
                    msgMessage.getAttachments().add(attachment);
                }
            }
        } else if (mimeMessage.getContent() instanceof String) {
            if (mimeMessage.isMimeType("text/html")) {
                String htmlText = (String) mimeMessage.getContent();

                if (msgMessage.getBodyHtmlText() == null) {
                    msgMessage.setBodyHtmlText(htmlText);
                }
            } else {
                String plainText = (String) mimeMessage.getContent();

                if (msgMessage.getBody() == null) {
                    msgMessage.setBody(plainText);
                }
            }
        }


        if (msgMessage.getBody() == null) {
            msgMessage.setBody(mimeMessage.getDescription());
        }

        return msgMessage;
    }

    private static void getAllBodyParts(Multipart multipart, List<BodyPart> allBodyParts) throws MessagingException, IOException {
        for (int i = 0; i < multipart.getCount(); i++) {
            BodyPart bodyPart = multipart.getBodyPart(i);
            allBodyParts.add(bodyPart);

            if (bodyPart.getContent() instanceof Multipart) {
                getAllBodyParts((Multipart) bodyPart.getContent(), allBodyParts);
            }
        }
    }

    /**
     * @return
     */
    /*private Long processEmail(MimeMessage mimeMessage) {
        try {
            System.out.println("subject - " + mimeMessage.getSubject());
            System.out.println("messageId - " + mimeMessage.getMessageID());
            System.out.println("content - " + mimeMessage.getContent().toString());
            System.out.println("content md5 - " + mimeMessage.getContentMD5());
            System.out.println("content type - " + mimeMessage.getContentType());
            System.out.println("desc - " + mimeMessage.getDescription());
            System.out.println("disposotion - " + mimeMessage.getDisposition());
            System.out.println("encoding - " + mimeMessage.getEncoding());
            System.out.println("filename - " + mimeMessage.getFileName());
            System.out.println("all header lines - " + mimeMessage.getAllHeaderLines());
            System.out.println("headers - " + mimeMessage.getAllHeaders());
            System.out.println("recipients - " + getAddressString(mimeMessage.getAllRecipients()));
            System.out.println("content lang - " + getString(mimeMessage.getContentLanguage()));
            System.out.println("flags - " + getFlag(mimeMessage.getFlags()));
            System.out.println("from - " + getAddressString(mimeMessage.getFrom()));
            System.out.println("receive date - " + mimeMessage.getReceivedDate());
            System.out.println("reply to - " + getAddressString(mimeMessage.getReplyTo()));
            System.out.println("sender - " + mimeMessage.getSender());
            System.out.println("sent date - " + mimeMessage.getSentDate());
            System.out.println("size - " + mimeMessage.getSize());
            System.out.println("message number - " + mimeMessage.getMessageNumber());
            Email email = new Email();
            email.setOutlook_id(mimeMessage.getMessageID());
            email.setSubject(trim(mimeMessage.getSubject() == null ? "No Subject" : mimeMessage.getSubject(), 200));
            //email.setBody_html();
            //email.setBody_text();
            email.setFrom_email(trim(getAddressString(mimeMessage.getFrom()), 200));
            email.setFrom_name(trim(mimeMessage.getSender() == null ? "" : mimeMessage.getSender().toString(), 200));
            email.setMessage_creation_date(mimeMessage.getSentDate() == null ? new Date() : mimeMessage.getSentDate());
            email.setMessage_date(mimeMessage.getReceivedDate() == null ? new Date() : mimeMessage.getReceivedDate());
            email.setDate_created(new Date());
            String toName = trim(getAddressString(mimeMessage.getAllRecipients()), 200);
            email.setTo_email(toName);
            email.setTo_name(toName);
            email.setDisplay_to(toName);
            email.setDisplay_cc("");
            email.setDisplay_bcc("");
            email = emailRepository.save(email);
            System.out.println(email.getMessage_id());
            insertRecipients(mimeMessage.getAllRecipients(), email.getMessage_id());
            MimeMultipart mimeMultipart = (MimeMultipart) mimeMessage.getContent();
            if (mimeMultipart != null) {
                email = exploreEmail(mimeMultipart, email);
            }
            return email.getMessage_id();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }*/

    /*private void insertRecipients(Address[] addresses, long id) {
        if (addresses != null) {
            List<Recipient> recipients = new ArrayList<>();
            for (Address address : addresses) {
                Recipient recipient = new Recipient();
                recipient.setMessage_id(id);
                recipient.setRecipient_email(trim(address.toString(), 200));
                recipient.setRecipient_name(trim(address.toString(), 200));
                recipient.setDate_created(new Date());
                recipients.add(recipient);
            }
            recipientRepository.save(recipients);
        }
    }*/

    /*private Email exploreEmail(MimeMultipart message, Email email) {
        if (message == null || email == null) {
            return email;
        }
        try {
            for (int i = 0; i < message.getCount(); i++) {
                BodyPart bodyPart = message.getBodyPart(i);
                String contentType = bodyPart.getContentType();
                if (contentType != null && contentType.contains("multipart")) {
                    MimeMultipart multipart = (MimeMultipart) bodyPart.getContent();
                    exploreEmail(multipart, email);
                } else if (contentType != null && contentType.contains("message")) {
                    MimeMessage mimeMessage = (MimeMessage) bodyPart.getContent();
                    long attachmentId = processEmail(mimeMessage);
                    // Attachment
                    Attachment attachment = new Attachment();
                    attachment.setAttachment_type("FILE");
                    attachment.setDate_created(new Date());
                    attachment.setFile_id(attachmentId);
                    attachment.setMessageId(email.getMessage_id());
                    attachmentRepository.save(attachment);
                } else if (contentType.contains("text/plain")) {
                    email.setBody_text(bodyPart.getContent().toString());
                    email = emailRepository.save(email);
                } else if (contentType.contains("text/html")) {
                    email.setBody_html(bodyPart.getContent().toString());
                    email = emailRepository.save(email);
                } else if (contentType.contains("octet")) {
                    // File
                    com.barley.model.File file = new com.barley.model.File();
                    DataHandler handler = bodyPart.getDataHandler();
                    file.setFileName(trim(handler.getName(), 100));
                    File file1 = new File(handler.getName());
                    FileOutputStream fos = new FileOutputStream(file1);
                    handler.writeTo(fos);
                    fos.flush();
                    fos.close();
                    FileInputStream fis = new FileInputStream(file1);
                    file.setData(IOUtils.toByteArray(fis));
                    fis.close();
                    file1.deleteOnExit();
                    file.setDate_created(new Date());
                    file.setExtension(handler.getContentType().split(";")[0]);
                    file.setLongFileName(trim(handler.getName(), 200));
                    file.setMime_tag(".eml");
                    file = fileRepository.save(file);
                    // Attachment
                    Attachment attachment = new Attachment();
                    attachment.setAttachment_type("FILE");
                    attachment.setDate_created(new Date());
                    attachment.setFile_id(file.getFile_id());
                    attachment.setMessageId(email.getMessage_id());
                    attachmentRepository.save(attachment);
                }
            }
            return email;
        } catch (IOException | MessagingException e) {
            e.printStackTrace();
            return email;
        }
    }*/
    private Long loadMessage(Message msg, Date date) {
        final Email email = new Email();
        Long returnVal = null;
        try {
            email.setOutlook_id(msg.getMessageId());
            email.setSubject(trim(msg.getSubject(), 200));
            email.setBody_text(msg.getBodyText());
            email.setBody_html(msg.getConvertedBodyHTML() == null ? msg.getBodyHTML() : msg.getConvertedBodyHTML());
            email.setFrom_email(trim(msg.getFromEmail(), 200));
            email.setFrom_name(trim(msg.getFromName() == null ? msg.getFromEmail() : msg.getFromName(), 200));
            email.setMessage_creation_date(msg.getCreationDate() == null ? date : msg.getCreationDate());
            email.setMessage_date(msg.getDate() == null ? date : msg.getDate());
            email.setTo_email(trim(msg.getToEmail(), 200));
            email.setTo_name(trim(msg.getToName(), 200));
            email.setDisplay_to(trim(msg.getDisplayTo(), 200));
            email.setDisplay_cc(trim(msg.getDisplayCc(), 200));
            email.setDisplay_bcc(trim(msg.getDisplayBcc(), 200));
            final Email returnedEmail = emailRepository.save(email);
            returnVal = returnedEmail.getMessage_id();
            List<RecipientEntry> recipientEntries = msg.getRecipients();
            final List<Recipient> recipients = new ArrayList<>();
            recipientEntries.forEach(recipientEntry -> {
                Recipient recipient = new Recipient();
                recipient.setMessage_id(returnedEmail.getMessage_id());
                recipient.setRecipient_email(trim(recipientEntry.getToEmail(), 200));
                recipient.setRecipient_name(trim(recipientEntry.getToName(), 200));
                recipient.setDate_created(new Date());
                recipients.add(recipient);
            });
            recipientRepository.save(recipients);

            List<com.auxilii.msgparser.attachment.Attachment> attachmentList = msg.getAttachments();
            attachmentList.forEach(attachment -> {
                try {
                    insertEmail(attachment, returnedEmail.getMessage_id(), date);
                } catch (ClassCastException cce) {
                    System.out.println("Cant convert attachment to msg - will try parsing again" + email.getSubject() + ", e: " + cce.getLocalizedMessage());
                    try {
                        FileAttachment fileAttachment = (FileAttachment) attachment;
                        if (fileAttachment.getFilename() != null && fileAttachment.getFilename().contains(".msg")) {
                            try {
                                Message message = parser.parseMsg(new ByteArrayInputStream(fileAttachment.getData()));
                                MsgAttachment msgAttachment = new MsgAttachment();
                                msgAttachment.setMessage(message);
                                insertEmail(msgAttachment, returnedEmail.getMessage_id(), date);
                            } catch (IOException | UnsupportedOperationException e) {
                                System.out.println("Exception while parsing file att to msg " + e.getLocalizedMessage());
                                insertFile(attachment, returnedEmail.getMessage_id());
                            }
                        } else {
                            insertFile(attachment, returnedEmail.getMessage_id());
                        }
                    } catch (ClassCastException ce) {
                        System.out.println("Cant convert attachment to File - dropping it" + email.getSubject() + ", e: " + ce.getLocalizedMessage());
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
        return returnVal;
    }

    private void insertEmail(com.auxilii.msgparser.attachment.Attachment attachment, long messageId, Date date) {
        Attachment attach = new Attachment();
        MsgAttachment msgAttachment = (MsgAttachment) attachment;
        attach.setAttachment_type("MSG");
        attach.setMessageId(messageId);
        attach.setFile_id(loadMessage(msgAttachment.getMessage(), date));
        attach.setDate_created(new Date());
        attachmentRepository.save(attach);
    }

    private void insertFile(com.auxilii.msgparser.attachment.Attachment attachment, long messageId) {
        FileAttachment fileAttachment = (FileAttachment) attachment;
        Attachment attach = new Attachment();
        attach.setAttachment_type("FILE");
        attach.setMessageId(messageId);
        com.barley.model.File file = new com.barley.model.File();
        file.setFileName(trim(fileAttachment.getFilename(), 100));
        file.setLongFileName(trim(fileAttachment.getLongFilename() == null ? fileAttachment.getFilename() : fileAttachment.getLongFilename(), 200));
        file.setMime_tag(trim(fileAttachment.getMimeTag() == null ? "" : fileAttachment.getMimeTag(), 100));
        file.setExtension(trim(fileAttachment.getExtension() == null ? getExtension(file.getLongFileName()) : fileAttachment.getExtension(), 45));
        file.setData(fileAttachment.getData());
        final com.barley.model.File returnedFile = fileRepository.save(file);
        attach.setFile_id(returnedFile.getFile_id());
        attachmentRepository.save(attach);
    }

    private String getExtension(String fileName) {
        if (fileName != null) {
            String[] strings = fileName.split("\\.");
            int length = strings.length;
            if (length > 0)
                return strings[length - 1].replaceAll("\\u0000", "");
            else
                return "";
        }
        return "";
    }

    private static String trim(String value, int length) {
        if (value != null) {
            if (value.length() > length) {
                return value.substring(0, length);
            } else {
                return value;
            }
        } else
            return "";
    }

    private static Session getMailSession() {
        Properties props = System.getProperties();
        props.put("mail.host", "smtp.dummydomain.com");
        props.put("mail.transport.protocol", "smtp");
        return Session.getDefaultInstance(props, null);
    }

    private String getAddressString(Address[] addresses) {
        if (addresses == null) {
            return "";
        }
        StringBuilder stringBuilder = new StringBuilder();
        for (Address address : addresses) {
            System.out.println(address.getType());
            System.out.println(address.toString());
            stringBuilder.append(address.toString()).append(", ");
        }
        return stringBuilder.toString();
    }

    private String getFlag(Flags flags) {
        String[] flgs = flags.getUserFlags();
        StringBuilder stringBuilder = new StringBuilder();
        for (String flg : flgs) {
            stringBuilder.append(flg).append(", ");
        }
        Flags.Flag[] systemFlags = flags.getSystemFlags();
        stringBuilder.append("SYSTEM FLAG: ");
        for (Flags.Flag systemFlag : systemFlags) {
            stringBuilder.append(systemFlag).append(", ");
        }
        return stringBuilder.toString();
    }

    private String getString(String[] strings) {
        StringBuilder stringBuilder = new StringBuilder();
        for (String str : strings) {
            stringBuilder.append(str).append(", ");
        }
        return stringBuilder.toString();
    }
}
