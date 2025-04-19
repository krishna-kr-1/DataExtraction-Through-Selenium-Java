package rentPrice;

import jakarta.mail.*;
import jakarta.mail.internet.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

public class LogMailer {

    @SuppressWarnings("unused")
	public static Properties loadEmailProperties() {
        Properties props1 = new Properties();
        try {
        	
            InputStream input = LogMailer.class.getClassLoader().getResourceAsStream("email.properties");
//        	InputStream input = new FileInputStream("email.properties");
            if (input != null) {
                props1.load(input);
            } else {
                System.out.println("❌ email.properties file not found!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return props1;
    }

    public static void sendEmailWithAttachments(String fromEmail, String password, String toEmails,
                                                String ccEmails, String subject, String bodyText,
                                                String filePaths) {
        try {
            Properties props1 = new Properties();
            props1.put("mail.smtp.auth", "true");
            props1.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.host", "smtp.gmail.com");
            props1.put("mail.smtp.port", "587");

            Session session = Session.getInstance(props1, new Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(fromEmail, password);
                }
            });

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(fromEmail));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmails));
            if(ccEmails != null && !ccEmails.trim().isEmpty())
            {
            	message.setRecipients(Message.RecipientType.CC, InternetAddress.parse(ccEmails));
            }
            message.setSubject(subject);

            // Body
            MimeBodyPart textPart = new MimeBodyPart();
            textPart.setText(bodyText);

            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(textPart);

            
            MimeBodyPart attachment = new MimeBodyPart();
            attachment.attachFile(new File(filePaths));
            multipart.addBodyPart(attachment);

            message.setContent(multipart);
            Transport.send(message);

            System.out.println("Email with Log attachments sent successfully!");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
