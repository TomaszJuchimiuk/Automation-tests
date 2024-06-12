package com.capgemini.mrchecker.webapi.imap;

import com.capgemini.mrchecker.webapi.BaseTestWebAPI;
import com.capgemini.mrchecker.webapi.CustomAssert;
import com.capgemini.mrchecker.webapi.emailService.EmailService;
import com.capgemini.mrchecker.webapi.example.env.GetEnvironmentParam;
import io.qameta.allure.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import javax.mail.MessagingException;
import java.io.IOException;

@Feature("Load tests for")
@Owner("tjuchimi")
@TmsLink("xxx")
@Severity(SeverityLevel.CRITICAL)
public class LoadTest extends BaseTestWebAPI {

    EmailService emailService = new EmailService();
    final String attachmentPathKowalsky = "src\\resources\\testdata\\files\\kowalski.png";
    final String attachmentPathJuliano = "src\\resources\\testdata\\files\\juliano.jpg";
    final String attachmentPath100KB = "src\\resources\\testdata\\files\\100KB.jpg";
    final String attachmentPath1MB = "src\\resources\\testdata\\files\\1MB.jpg";
    final String attachmentPath500kB = "src\\resources\\testdata\\files\\1MB.jpg";
    final String attachmentPath200kB = "src\\resources\\testdata\\files\\200kB.jpg";

    @Test
    @Story("Ladtests")
    @DisplayName("Send an email with attachment and check if it has been moved to InboxBackup")
    @TmsLink("xxx")
    @ParameterizedTest
//    @ValueSource(strings={attachmentPath100KB,attachmentPath200kB,attachmentPath500kB,attachmentPath1MB})
    @CsvSource({
            attachmentPath100KB + ", 1",
            attachmentPath200kB + ", 1",
            attachmentPath500kB + ", 1",
            attachmentPath1MB + ", 1"})
    public void loadTest(String attachment, int emailsNumber) throws MessagingException, IOException {
        emailService.setUp(GetEnvironmentParam.SOURCE_USER.getValue(), GetEnvironmentParam.SOURCE_USER_PASSWORD.getValue());
        emailService.connectServer(GetEnvironmentParam.SOURCE_USER.getValue(), GetEnvironmentParam.SOURCE_USER_PASSWORD.getValue());
        emailService.sendEmailWithAttachment(GetEnvironmentParam.SOURCE_USER.getValue(), GetEnvironmentParam.USER_A.getValue(), attachment, emailsNumber);
        emailService.relogin(GetEnvironmentParam.USER_A.getValue(), GetEnvironmentParam.USER_A_PASSWORD.getValue());
        Assertions.assertAll(
                ()->CustomAssert.assertEquals(emailsNumber, emailService.readEmailsNumber("Inbox"), 5,20),
                ()->CustomAssert.assertEquals(emailsNumber, emailService.readEmailsNumber("InboxBackup"),5, 20)
        );
        emailService.deleteMessagesFromFolder("Inbox");
        emailService.deleteMessagesFromFolder("InboxBackup");
    }

    @Test
    @Story("load tests")
    @DisplayName("Send {emailsNumber} emails with {attachment} and check the emails are moved to target email/folder")
    @ParameterizedTest
    @CsvSource({
            attachmentPath100KB + ", 100",
            attachmentPath200kB + ", 100",
            attachmentPath500kB + ", 100",
            attachmentPath1MB + ", 100"})
    public void loadTestIMAP(String attachment, int emailsNumber) throws MessagingException, IOException {
        emailService.setUp(GetEnvironmentParam.SOURCE_USER.getValue(), GetEnvironmentParam.SOURCE_USER_PASSWORD.getValue());
        emailService.connectServer(GetEnvironmentParam.SOURCE_USER.getValue(), GetEnvironmentParam.SOURCE_USER_PASSWORD.getValue());
        emailService.sendEmailWithAttachment(GetEnvironmentParam.SOURCE_USER.getValue(), GetEnvironmentParam.TARGET_USER.getValue(), attachment, emailsNumber);
        emailService.relogin(GetEnvironmentParam.TARGET_USER.getValue(), GetEnvironmentParam.TARGET_USER_PASSWORD.getValue());
        CustomAssert.assertEquals(emailsNumber, emailService.readEmailsNumber("test"), 10, 30);
        emailService.deleteMessagesFromFolder("test");
        emailService.close();
    }

}
