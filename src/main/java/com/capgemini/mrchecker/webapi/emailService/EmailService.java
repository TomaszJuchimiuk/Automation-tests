package com.capgemini.mrchecker.webapi.emailService;

import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.*;
import javax.mail.search.SubjectTerm;
import javax.sql.DataSource;
import java.io.File;
import java.io.IOException;
import java.util.*;

@Slf4j
public class EmailService {
    private static Session session;
    private static Store store;
    private static Folder inbox;
    static Message[] messages;
    private static final String host = "outlook.office365.com";

    private final String attachmentPath = "C:\\Users\\tjuchimi\\Desktop\\simple_files";

    @Step("Set properties for IMAP and SMTP connection")
    public void setUp(String userName, String password) {
        // Set properties for IMAP and SMTP connection
        Properties properties = new Properties();
        properties.setProperty("mail.store.protocol", "imaps");
        properties.setProperty("mail.imap.ssl.enable", "true");
        properties.setProperty("mail.imap.host", host);
        properties.setProperty("mail.imap.port", "993");
        properties.setProperty("mail.smtp.auth", "true");
        properties.setProperty("mail.smtp.starttls.enable", "true");
        properties.setProperty("mail.smtp.host", "smtp.office365.com");
        properties.setProperty("mail.smtp.port", "587");
        // session open
        session = Session.getInstance(properties, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(userName, password);
            }
        });
    }

    @Step("Connect with the server for {emailUser}")
    public void connectServer(String emailUser, String password) throws MessagingException {
        // Uzyskanie połączenia z serwerem
        store = session.getStore("imaps");
        store.connect(host, emailUser, password);
    }

    @Step("Go to the folder {folderName}")
    public void goToFolder(String folderName) throws MessagingException {
        inbox = store.getFolder(folderName);
        inbox.open(Folder.READ_WRITE);
    }

    public void relogin(String username, String password) throws MessagingException {
        // Close the existing store if it's open
        if (store != null && store.isConnected()) {
            store.close();
        }
        // Create a new session using the new credentials
        session = Session.getDefaultInstance(session.getProperties());
        // Connect to the store with the new session and credentials
        store = session.getStore("imap");
        store.connect(host, username, password);
    }

    @Step("Send new email from {sender} to {recipient} {emailsNumber} times")
    public void testSendEmailMultipleTimes(String sender, String recipient, int emailsNumber) throws RuntimeException, MessagingException {
        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress("sender"));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse("recipient"));
        message.setSubject("Testing Subject");
        message.setText("Dear Mail Crawler," + "\n\n No spam to my email, please!");

        for (int i = 0; i < emailsNumber; i++) {
            Transport.send(message);
        }
    }

    @Step("Send new email from {sender} to {recipient} {emailsNumber} times")
    public void sendEmailWithAttachment(String sender, String recipient, String attachmentPath, int emailsNumber) throws MessagingException, IOException {
        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(sender));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipient));

        // Create the message body part
        BodyPart messageBodyPart = new MimeBodyPart();
        messageBodyPart.setText("Dear Mail Crawler," + "\n\n No spam to my email, please!");

        // Create the attachment body part
        MimeBodyPart attachmentBodyPart = new MimeBodyPart();
        FileDataSource source = new FileDataSource(attachmentPath);
        attachmentBodyPart.setDataHandler(new DataHandler(source));
        attachmentBodyPart.setFileName(new File(attachmentPath).getName());

        // Create Multipart and add body parts to it
        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(messageBodyPart);
        multipart.addBodyPart(attachmentBodyPart);

        // Set the Multipart as the message's content
        message.setContent(multipart);

        for (int i = 0; i < emailsNumber; i++) {
            message.setSubject("Veraktung" + i);
            Transport.send(message);
        }
        log.info(emailsNumber+" emails are sent");
    }

    @Step("Forward email {emailSubject} from {sourceEmail} to {targetEmail} {forwardNumber} times")
    public void forwardEmailMultipleTimes(int forwardNumber, String emailSubject, String sourceEmail, String targetEmail) throws MessagingException, IOException {
        inbox = store.getFolder("INBOX");
        inbox.open(Folder.READ_WRITE);
        // Search for messages with the target subject
        Message[] messages = inbox.search(new SubjectTerm(emailSubject), inbox.getMessages());
        Message message = messages[0];
        // Create a new message for forwarding
        Message forward = new MimeMessage(session);
//        forward.setSubject("Fwd: " + message.getSubject());
        forward.setFrom(new InternetAddress(sourceEmail));
        forward.setRecipient(Message.RecipientType.TO, new InternetAddress(targetEmail));
        // Copy the content of the original message into the forwarded message
        if (message.isMimeType("text/plain")) {
            forward.setText((String) message.getContent());
        } else if (message.isMimeType("multipart/*")) {
            Multipart multipart = (Multipart) messages[0].getContent();
            Multipart forwardedMultipart = new MimeMultipart();
            for (int i = 0; i < multipart.getCount(); i++) {
                BodyPart bodyPart = multipart.getBodyPart(i);
                forwardedMultipart.addBodyPart(bodyPart);
            }
            forward.setContent(forwardedMultipart);
        }
        for (int i = 0; i < forwardNumber; i++) {
            forward.setSubject("Fwd: " + message.getSubject() + i);
            Transport.send(forward);
        }
    }

    @Step("Delete emails in {folderName}")
    public void deleteMessagesFromFolder(String folderName) throws MessagingException {
        inbox = store.getFolder(folderName);
        inbox.open(Folder.READ_WRITE);
        Message[] messages = inbox.getMessages();
//        Mark messages for Object deletion;
        for (Message message : messages) {
            message.setFlag(Flags.Flag.DELETED, true);
        }
        // Expunge the folder to permanently delete the marked messages
        inbox.expunge();
    }

    @Step("Return the number of emails in the folder {folderName}")
    public int readEmailsNumber(String folderName) throws MessagingException {
        Folder folder = store.getFolder(folderName);
        folder.open(Folder.READ_ONLY); // Re-open the folder in read-only mode
        Message[] messages = folder.getMessages();
        return messages.length;
    }

    static void refreshFolder(String folderName) throws MessagingException {
        Folder folder = store.getFolder(folderName);
        if (folder != null) {
            folder.close(false); // Close the folder without expunging (deleting) messages
            folder.open(Folder.READ_ONLY); // Re-open the folder in read-only mode
        } else {
            throw new IllegalArgumentException("Folder " + folderName + " does not exist.");
        }
    }


    private static Message[] getMessages() throws MessagingException {
        return messages = inbox.getMessages();
    }

    @Step("Close connection")
    public void close() throws MessagingException {
        inbox.close(false);
        store.close();
    }
}


