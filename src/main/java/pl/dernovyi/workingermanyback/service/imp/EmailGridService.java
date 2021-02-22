package pl.dernovyi.workingermanyback.service.imp;

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

import static pl.dernovyi.workingermanyback.constant.FileConstant.EMAIL_SUBJECT;
import static pl.dernovyi.workingermanyback.constant.FileConstant.MAIL_ADMINISTRATION;
@Service
public class EmailGridService {
    @Value(value = "${sendgrid_key}")
    private String sendgrid_key;
    private Logger LOGGER = LoggerFactory.getLogger(getClass());

    public void sendNewPasswordEmail(String username, String password, String emailTo) {
        Response response = createResponce(username, password, emailTo);
        LOGGER.info(String.valueOf(response.getStatusCode()));

        if(response.getStatusCode() != 202){
            sendMessageForAdmin(emailTo, password);
        }

    }



    private Response createResponce(String username, String password, String emailTo) {
        Email from = new Email(MAIL_ADMINISTRATION);
        Email to = new Email(emailTo);
        String subject = EMAIL_SUBJECT;
        Content content = new Content("text/plain", "Hello " + username + ", \n \n Your new account password is: "+ password + "\n \n The Support Team");

        Mail mail = new Mail(from, subject, to, content);

        SendGrid sg = new SendGrid(sendgrid_key);
        Request request = new Request();

        request.setMethod(Method.POST);
        request.setEndpoint("mail/send");
        try {
            request.setBody(mail.build());
        } catch (IOException e) {
            e.printStackTrace();
        }
        Response response = null;
        try {
            response = sg.api(request);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }


    private void sendMessageForAdmin(String emailRegistr ,String password) {
        Email from = new Email(MAIL_ADMINISTRATION);
        Email to = new Email(MAIL_ADMINISTRATION);
        String subject = EMAIL_SUBJECT;
        Content content = new Content("text/plain", "Для пользователя " + emailRegistr  + ", \n \n Не отправлен пароль: "+ password + "\n \n  The Support Team");
        Mail mail = new Mail(from, subject, to, content);
        SendGrid sg = new SendGrid(sendgrid_key);
        Request request = new Request();
        request.setMethod(Method.POST);
        request.setEndpoint("mail/send");
        try {
            request.setBody(mail.build());
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            sg.api(request);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



}
