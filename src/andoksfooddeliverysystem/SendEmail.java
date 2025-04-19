package andoksfooddeliverysystem;

import javax.mail.*;
import javax.mail.internet.*;
import java.util.Properties;

public class SendEmail {
    // Method to send email notification
    public static void sendEmail(String to, String subject, String body) throws MessagingException {
        String from = "andoks.new21@gmail.com"; // Sender's Gmail
        String host = "smtp.gmail.com";         // Gmail SMTP server

        Properties properties = new Properties(); // Use new Properties(), not System.getProperties()
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "587");


        // Authenticator to pass Gmail credentials
        Session session = Session.getInstance(properties, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("andoks.new21@gmail.com", "lrfg tzrf lfax gucb");
            }
        });

    try {
        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(from));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
        message.setSubject(subject);

        // Create multipart email
        MimeMultipart multipart = new MimeMultipart("alternative");

        // Plain text fallback
        MimeBodyPart textPart = new MimeBodyPart();
        textPart.setText("Thank you for ordering from Andok's! View this email in HTML format.", "utf-8");

        // HTML version
        MimeBodyPart htmlPart = new MimeBodyPart();
        htmlPart.setContent(wrapInHtmlTemplate(body), "text/html; charset=utf-8");

        multipart.addBodyPart(textPart);
        multipart.addBodyPart(htmlPart);
        message.setContent(multipart);

        Transport.send(message);
    } catch (Exception e) {
        throw new MessagingException("Failed to send email", e);
    }
}


   private static String wrapInHtmlTemplate(String content) {
    // Use the direct URL you provided
    String imageUrl = "https://scontent.fmnl17-3.fna.fbcdn.net/v/t39.30808-6/347786176_976718813761742_8493521819122192726_n.jpg?_nc_cat=103&ccb=1-7&_nc_sid=6ee11a&_nc_ohc=2b1zyR6bAXIQ7kNvwGTE2RN&_nc_oc=AdnDoGzOR1Yf0l9vMVkBUBX_NXE8oxT-olswfN3JlbdpBklV7WteI9HePdOf6VgUrUQ&_nc_zt=23&_nc_ht=scontent.fmnl17-3.fna&_nc_gid=kySCRfnK7q0GhLoOU2J7Pg&oh=00_AfHJLSV6lLs6USAWt80SDy_4AskHh4eai1_aiwg2Z7LAJQ&oe=68092B08"; 

     return "<!DOCTYPE html>" +
           "<html>" +
           "<body style=\"font-family: Arial; line-height: 1.6;\">" +
           "<div style=\"max-width: 600px; margin: auto; padding: 20px;\">" +
           "<img src=\"" + imageUrl + "\" alt=\"Andok's Chicken\" style=\"width: 50%; height: auto;\">" +
           content + 
           "<p style=\"color: #777; font-size: 12px;\">" +
           "© 2023 Andok's Food Delivery" +
           "</p>" +
           "</div>" +
           "</body>" +
           "</html>";
}

}
