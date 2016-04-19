package com.barley.processor;

import com.auxilii.msgparser.Message;
import com.auxilii.msgparser.MsgParser;
import com.auxilii.msgparser.RecipientEntry;
import com.auxilii.msgparser.attachment.FileAttachment;
import com.auxilii.msgparser.attachment.MsgAttachment;
import com.barley.database.DatabaseConfig;
import com.barley.model.Attachment;
import com.barley.model.Email;
import com.barley.model.Recipient;
import com.barley.repository.AttachmentRepository;
import com.barley.repository.EmailRepository;
import com.barley.repository.FileRepository;
import com.barley.repository.RecipientRepository;
import org.apache.poi.util.IOUtils;
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

    @Autowired
    DatabaseConfig config;

    private final MsgParser parser = new MsgParser();

    /**
     * Simple method to insert any email file in database
     *
     * @param file
     * @return
     */
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

    /**
     * Method to support inserting a file of type MultipartFile
     *
     * @param file
     * @return
     * @see MultipartFile
     */
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
                System.out.println("Email inserted: " + loadMessage(msg));
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
    }

    /**
     * Method to support inserting file of type MimeMessage
     *
     * @param mimeMessage
     * @return
     * @see MimeMessage
     */
    public boolean insertEmail(MimeMessage mimeMessage) {
        if (mimeMessage == null) {
            return false;
        }
        try {
            EmlMessage message = convertToMsg(mimeMessage);
            loadMessage(message);
            return true;
        } catch (IOException | MessagingException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Method to inserting our simple POJO - EmlMessage to support file types from simple email client
     *
     * @param message
     * @return
     */
    private Long loadMessage(EmlMessage message) {
        Long returnVal = null;
        try {
            final Email returnedEmail = emailRepository.save(message.getEmail());
            returnVal = returnedEmail.getMessage_id();
            message.getRecipients().forEach(r -> r.setMessage_id(returnedEmail.getMessage_id()));
            recipientRepository.save(message.getRecipients());
            message.getFiles().forEach(f -> {
                final com.barley.model.File returnedFile = fileRepository.save(f);
                Attachment attach = new Attachment();
                attach.setAttachment_type("FILE");
                attach.setMessageId(returnedEmail.getMessage_id());
                attach.setFile_id(returnedFile.getFile_id());
                attachmentRepository.save(attach);
            });
            message.getEmlMessages().forEach(eml -> {
                Attachment attach = new Attachment();
                attach.setAttachment_type("MSG");
                attach.setMessageId(returnedEmail.getMessage_id());
                attach.setFile_id(loadMessage(eml));
                attachmentRepository.save(attach);
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return returnVal;
    }


    /**
     * @param internetAddress
     * @return
     */
    private String getDisplayname(InternetAddress internetAddress) {
        if (internetAddress.getPersonal() != null) {
            return internetAddress.getPersonal();
        } else
            return internetAddress.getAddress();
    }

    /**
     * @param internetAddresses
     * @param recipientList
     * @param email
     * @return
     */
    private String addRecipients(InternetAddress[] internetAddresses, List<Recipient> recipientList, Email email) {

        if (internetAddresses == null || internetAddresses.length <= 0)
            return null;
        boolean emailSet = false;
        String displayTo = "";
        for (InternetAddress internetAddress : internetAddresses) {
            Recipient recipient = new Recipient();
            recipient.setRecipient_email(trim(internetAddress.getAddress(), 200));
            String displayName = getDisplayname(internetAddress);
            recipient.setRecipient_name(trim(displayName, 200));
            if (email != null && !emailSet) {
                email.setTo_email(trim(internetAddress.getAddress(), 200));
                email.setTo_name(trim(displayName, 200));
                emailSet = true;
            }
            displayTo += displayName;
            displayTo += "; ";
            recipient.setDate_created(new Date());
            recipientList.add(recipient);
        }

        return displayTo;
    }

    /**
     * @param email
     * @param html
     */
    public void setEmailHtml(Email email, String html) {
        if (email != null && email.getBody_html() == null && html != null) {
            email.setBody_html(html);
        }
    }

    /**
     * @param email
     * @param text
     */
    public void setEmailText(Email email, String text) {
        if (email != null && email.getBody_text() == null && text != null) {
            email.setBody_text(text);
        }
    }


    /**
     * @param mimeMessage
     * @return
     * @throws MessagingException
     * @throws IOException
     */
    private EmlMessage convertToMsg(MimeMessage mimeMessage) throws MessagingException, IOException {

        EmlMessage emlMessage = new EmlMessage();
        Email email = new Email();

        // Subject and outlook id
        email.setSubject(trim(mimeMessage.getSubject(), 200));
        email.setOutlook_id(trim(mimeMessage.getMessageID(), 200));

        // TO, CC, BCC
        email.setDisplay_to(trim(addRecipients((InternetAddress[]) mimeMessage.getRecipients(javax.mail.Message.RecipientType.TO), emlMessage.getRecipients(), email), 200));
        email.setDisplay_cc(trim(addRecipients((InternetAddress[]) mimeMessage.getRecipients(javax.mail.Message.RecipientType.CC), emlMessage.getRecipients(), null), 200));
        email.setDisplay_bcc(trim(addRecipients((InternetAddress[]) mimeMessage.getRecipients(javax.mail.Message.RecipientType.BCC), emlMessage.getRecipients(), null), 200));

        // Dates
        Date date = mimeMessage.getSentDate();
        email.setMessage_creation_date(date);
        email.setMessage_date(date);
        email.setDate_created(new Date());

        // From values
        if (mimeMessage.getFrom() != null && mimeMessage.getFrom().length > 0) {
            InternetAddress from = (InternetAddress) mimeMessage.getFrom()[0];

            if (from.getPersonal() != null) {
                email.setFrom_name(trim(from.getPersonal(), 200));
            } else {
                email.setFrom_name(trim(from.getAddress(), 200));
            }

            email.setFrom_email(trim(from.getAddress(), 200));
        }

        if (mimeMessage.getContent() instanceof Multipart) {
            List<BodyPart> allBodyParts = new ArrayList<>();
            getAllBodyParts((Multipart) mimeMessage.getContent(), allBodyParts);

            for (int i = 0; i < allBodyParts.size(); i++) {
                if (allBodyParts.get(i).isMimeType("text/plain") && allBodyParts.get(i).getDisposition() != null) {
                    String content = (String) allBodyParts.get(i).getContent();
                    byte[] buffer = content.getBytes("UTF-8"); //here you can use char set from allBodyParts.get(i)
                    //System.out.println(new String(buffer));
                    com.barley.model.File file = new com.barley.model.File();
                    file.setData(buffer);
                    file.setDate_created(new Date());
                    file.setExtension(".txt");
                    file.setFileName(allBodyParts.get(i).getFileName());
                    file.setLongFileName(allBodyParts.get(i).getFileName());
                    file.setMime_tag(allBodyParts.get(i).getContentType());
                    emlMessage.getFiles().add(file);
                } else if (allBodyParts.get(i).isMimeType("text/plain")) {
                    if (allBodyParts.get(i).getContent() instanceof MimeBodyPart) {
                        MimeBodyPart mimeBodyPart = (MimeBodyPart) allBodyParts.get(i).getContent();
                        setEmailText(email, mimeBodyPart.getDescription());
                    } else if (allBodyParts.get(i).getContent() instanceof String) {
                        setEmailText(email, (String) allBodyParts.get(i).getContent());
                    }
                } else if (allBodyParts.get(i).isMimeType("text/html")) {
                    String htmlText = null;
                    if (allBodyParts.get(i).getContent() instanceof MimeBodyPart) {
                        MimeBodyPart mimeBodyPart = (MimeBodyPart) allBodyParts.get(i).getContent();
                        htmlText = mimeBodyPart.getDescription();

                    } else if (allBodyParts.get(i).getContent() instanceof String) {
                        htmlText = (String) allBodyParts.get(i).getContent();
                    }
                    setEmailHtml(email, htmlText);
                } else if (allBodyParts.get(i).getContent() instanceof InputStream) {
                    InputStream is = (InputStream) allBodyParts.get(i).getContent();
                    com.barley.model.File file = new com.barley.model.File();
                    file.setData(IOUtils.toByteArray(is));
                    file.setDate_created(new Date());
                    String extension = allBodyParts.get(i).getContentType();
                    System.out.println("Extension: " + extension);
                    String[] extStrings = extension.split(";");
                    for (String extString : extStrings) {
                        System.out.println(extString);
                    }
                    file.setExtension(extStrings[0]);
                    file.setFileName(allBodyParts.get(i).getFileName());
                    file.setLongFileName(allBodyParts.get(i).getFileName());
                    file.setMime_tag(".eml");
                    emlMessage.getFiles().add(file);
                } else if (allBodyParts.get(i).getContent() instanceof MimeMessage) {
                    EmlMessage emlMessage1 = convertToMsg((MimeMessage) allBodyParts.get(i).getContent());
                    emlMessage.getEmlMessages().add(emlMessage1);
                }
            }
        } else if (mimeMessage.getContent() instanceof String) {
            if (mimeMessage.isMimeType("text/html")) {
                String htmlText = (String) mimeMessage.getContent();
                setEmailHtml(email, htmlText);
            } else {
                String plainText = (String) mimeMessage.getContent();
                setEmailText(email, plainText);
            }
        }


        if (email.getBody_text() == null) {
            email.setBody_text(mimeMessage.getDescription());
        }

        emlMessage.setEmail(email);

        return emlMessage;
    }

    /**
     * @param multipart
     * @param allBodyParts
     * @throws MessagingException
     * @throws IOException
     */
    public static void getAllBodyParts(Multipart multipart, List<BodyPart> allBodyParts) throws MessagingException, IOException {
        for (int i = 0; i < multipart.getCount(); i++) {
            BodyPart bodyPart = multipart.getBodyPart(i);
            allBodyParts.add(bodyPart);

            if (bodyPart.getContent() instanceof Multipart) {
                getAllBodyParts((Multipart) bodyPart.getContent(), allBodyParts);
            }
        }
    }

    /**
     * @param msg
     * @return
     */
    private Long loadMessage(Message msg) {
        final Email email = new Email();
        Long returnVal = null;
        try {
            email.setOutlook_id(msg.getMessageId());
            email.setSubject(trim(msg.getSubject(), 200));
            email.setBody_text(msg.getBodyText());
            email.setBody_html(msg.getConvertedBodyHTML() == null ? msg.getBodyHTML() : msg.getConvertedBodyHTML());
            email.setFrom_email(trim(msg.getFromEmail(), 200));
            email.setFrom_name(trim(msg.getFromName() == null ? msg.getFromEmail() : msg.getFromName(), 200));
            email.setMessage_creation_date(msg.getCreationDate());
            email.setMessage_date(msg.getDate());
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
                    insertEmail(attachment, returnedEmail.getMessage_id());
                } catch (ClassCastException cce) {
                    System.out.println("Cant convert attachment to msg - will try parsing again" + email.getSubject() + ", e: " + cce.getLocalizedMessage());
                    try {
                        FileAttachment fileAttachment = (FileAttachment) attachment;
                        if (fileAttachment.getFilename() != null && fileAttachment.getFilename().contains(".msg")) {
                            try {
                                Message message = parser.parseMsg(new ByteArrayInputStream(fileAttachment.getData()));
                                MsgAttachment msgAttachment = new MsgAttachment();
                                msgAttachment.setMessage(message);
                                insertEmail(msgAttachment, returnedEmail.getMessage_id());
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

    /**
     * @param attachment
     * @param messageId
     */
    private void insertEmail(com.auxilii.msgparser.attachment.Attachment attachment, long messageId) {
        Attachment attach = new Attachment();
        MsgAttachment msgAttachment = (MsgAttachment) attachment;
        attach.setAttachment_type("MSG");
        attach.setMessageId(messageId);
        attach.setFile_id(loadMessage(msgAttachment.getMessage()));
        attach.setDate_created(new Date());
        attachmentRepository.save(attach);
    }

    /**
     * @param attachment
     * @param messageId
     */
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

    /**
     * @param fileName
     * @return
     */
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

    /**
     * @param value
     * @param length
     * @return
     */
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

    /**
     * @return
     */
    public static Session getMailSession() {
        Properties props = System.getProperties();
        props.put("mail.host", "smtp.dummydomain.com");
        props.put("mail.transport.protocol", "smtp");
        return Session.getDefaultInstance(props, null);
    }

    /**
     * @param addresses
     * @return
     */
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

    /**
     * @param flags
     * @return
     */
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

    /**
     * @param strings
     * @return
     */
    private String getString(String[] strings) {
        StringBuilder stringBuilder = new StringBuilder();
        for (String str : strings) {
            stringBuilder.append(str).append(", ");
        }
        return stringBuilder.toString();
    }
}
