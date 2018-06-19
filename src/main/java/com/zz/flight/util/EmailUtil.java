package com.zz.flight.util;

import com.sendgrid.*;
import com.sun.mail.util.MailSSLSocketFactory;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.util.Properties;

public class EmailUtil {
    public static boolean sendEmail(String from,String to,String content,String subject){

        String smtp = "smtp.gmail.com";// 设置邮件服务器
        Properties props = System.getProperties();
        props.setProperty("mail.smtp.host",smtp);
        props.put("mail.smtp.starttls.enable", "true");
        props.setProperty("mail.smtp.port", "587");
        props.setProperty("mail.debug","true");
        props.put("mail.smtp.auth", "true");

        Authenticator authenticator = new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("flihtjoe@gmail.com","");
            }
        };
        Session session = Session.getDefaultInstance(props,authenticator);

        try{

            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from));
            message.addRecipient(Message.RecipientType.TO,
                    new InternetAddress(to));
            //message.addRecipient(Message.RecipientType.TO,new InternetAddress(to));
            message.setSubject(subject);
            message.setContent(content,"text/html;charset=UTF-8");
            Transport.send(message);
        }catch (Exception e) {
            return false;
        }
        return true;
    }

    public static boolean sendgrid(String emailTo , String validate){
        Email from = new Email("flight@jing.do");
        String subject = "Please validate your email";
        Email to = new Email(emailTo);
        Content content = new Content("text/plain", validate);
        Mail mail = new Mail(from, subject, to, content);

        SendGrid sg = new SendGrid("");
        Request request = new Request();
        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            Response response = sg.api(request);
            System.out.println(response.getStatusCode());
            System.out.println(response.getBody());
            System.out.println(response.getHeaders());
        } catch (IOException ex) {
            return false;
        }

        return true;
    }
}
