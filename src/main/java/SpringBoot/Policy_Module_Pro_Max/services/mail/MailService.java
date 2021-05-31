package SpringBoot.Policy_Module_Pro_Max.services.mail;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class MailService {

    @Autowired
    private JavaMailSender javaMailSender;

    private static final Logger log = LoggerFactory.getLogger(MailService.class);

    public void sendMail(String to, String body, String topic) {
        log.info("Sending Mail To : " + to + " With Topic : " + topic);
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("sritt4688665@gmail.com");
            message.setTo(to);
            message.setText(body);
            message.setSubject(topic);
            javaMailSender.send(message);
            log.info("Mail Sent !!");
        } catch (Exception e) {
            log.info("Some Error In Sending The Email !");
        }
    }
}
