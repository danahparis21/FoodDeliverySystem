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
    // Company logo URL
    String logoUrl = "https://scontent.fmnl17-3.fna.fbcdn.net/v/t39.30808-6/347786176_976718813761742_8493521819122192726_n.jpg?_nc_cat=103&ccb=1-7&_nc_sid=6ee11a&_nc_ohc=2b1zyR6bAXIQ7kNvwGTE2RN&_nc_oc=AdnDoGzOR1Yf0l9vMVkBUBX_NXE8oxT-olswfN3JlbdpBklV7WteI9HePdOf6VgUrUQ&_nc_zt=23&_nc_ht=scontent.fmnl17-3.fna&_nc_gid=kySCRfnK7q0GhLoOU2J7Pg&oh=00_AfHJLSV6lLs6USAWt80SDy_4AskHh4eai1_aiwg2Z7LAJQ&oe=68092B08";
    
    // Format the message content - generic for any message type
    String formattedContent = content
        // Format order numbers
        .replaceAll("order #(\\d+)", "order <span style=\"font-weight: bold; color: #d35400;\">#$1</span>")
        // Format key phrases based on different email types
        .replaceAll("(ready for pick-up|successfully picked up|out for delivery|cancelled|placed an order)", 
                    "<span style=\"font-weight: bold; color: #d35400;\">$1</span>")
        // Format star ratings
        .replace("⭐⭐⭐⭐⭐", "<span style=\"font-size: 20px; color: #f39c12;\">⭐⭐⭐⭐⭐</span>")
        // Format price values
        .replaceAll("(₱[\\d,.]+)", "<span style=\"font-weight: bold;\">$1</span>")
        // Format paragraph breaks and line breaks
        .replace("\n\n", "</p><p>")
        .replace("\n", "<br>")
        // Format signature
        .replace("Best regards,", "<p style=\"font-style: italic;\">Best regards,")
        .replace("Love,", "<p style=\"font-style: italic;\">Love,")
        .replace("— Andok's", "<p style=\"font-style: italic;\">— Andok's</p>")
        .replace("The Andok's Team ❤️", "The Andok's Team ❤️</p>");
    
    // Get appropriate heading from content for the email
    String emailHeading = "Andok's Notification";
    if (content.contains("ready for pick-up")) {
        emailHeading = "Order Ready for Pick-up! 🍗";
    } else if (content.contains("successfully picked up")) {
        emailHeading = "Order Completed! 🎉";
    } else if (content.contains("out for delivery")) {
        emailHeading = "Out for Delivery! 🚚";
    } else if (content.contains("placing an order") || content.contains("placed an order")) {
        emailHeading = "Order Confirmation 🛒";
    } else if (content.contains("cancelled")) {
        emailHeading = "Order Cancelled ⚠️";
    }
    
    return "<!DOCTYPE html>" +
           "<html>" +
           "<head>" +
           "<meta charset=\"UTF-8\">" +
           "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">" +
           "</head>" +
           "<body style=\"margin: 0; padding: 0; font-family: Arial, sans-serif; color: #333333;\">" +
           "<table role=\"presentation\" style=\"width: 100%; border-collapse: collapse;\">" +
           "  <tr>" +
           "    <td style=\"padding: 0;\">" +
           "      <table role=\"presentation\" style=\"width: 100%; max-width: 600px; margin: 0 auto; border-collapse: collapse; background-color: #ffffff; border-radius: 8px; overflow: hidden; box-shadow: 0 0 10px rgba(0,0,0,0.1);\">" +
           
           // Header with logo
           "        <tr>" +
           "          <td style=\"padding: 30px 20px; text-align: center; background-color: #f8f1e9;\">" +
           "            <img src=\"" + logoUrl + "\" alt=\"Andok's Logo\" style=\"width: 180px; height: auto;\">" +
           "          </td>" +
           "        </tr>" +
           
           // Main content
           "        <tr>" +
           "          <td style=\"padding: 40px 30px; background-color: #ffffff;\">" +
           "            <table role=\"presentation\" style=\"width: 100%; border-collapse: collapse;\">" +
           "              <tr>" +
           "                <td>" +
           "                  <h1 style=\"margin: 0 0 20px 0; font-size: 26px; line-height: 30px; color: #d35400; text-align: center;\">" + emailHeading + "</h1>" +
           "                  <p style=\"margin: 0 0 15px 0; font-size: 16px; line-height: 24px;\">" + formattedContent + "</p>" +
           "                </td>" +
           "              </tr>" +
           "            </table>" +
           "          </td>" +
           "        </tr>" +
           
           // Footer
           "        <tr>" +
           "          <td style=\"padding: 20px; text-align: center; background-color: #552200; color: #ffffff;\">" +
           "            <p style=\"margin: 0 0 10px 0; font-size: 14px;\">" +
           "              <strong>Andok's</strong> - Masarap. Juicy. Linamnam-Sarap!" +
           "            </p>" +
           "            <p style=\"margin: 0; font-size: 12px; color: #dddddd;\">" +
           "              © 2025 Andok's Food Delivery. All rights reserved." +
           "            </p>" +
           "          </td>" +
           "        </tr>" +
           
           "      </table>" +
           "    </td>" +
           "  </tr>" +
           "</table>" +
           "</body>" +
           "</html>";
}
}
