package notification.services;

import events.OrderPlacedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final JavaMailSender javaMailSender;

    @KafkaListener(topics = "placed-orders")
    public void listen(ConsumerRecord<String, OrderPlacedEvent> record) {
//    public void listen(ConsumerRecord<String, OrderPlacedEvent> record) {
        OrderPlacedEvent orderPlacedEvent = record.value();
        log.info("received message from placed orders topic {}", orderPlacedEvent);
        this.sendMail(orderPlacedEvent);
    }


    private void sendMail(OrderPlacedEvent orderPlacedEvent) {
        MimeMessagePreparator messagePreparator = mimeMessage -> {
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage);

            messageHelper.setFrom("hey@keteku.dev");
            messageHelper.setTo(orderPlacedEvent.getEmail().toString());
            messageHelper.setSubject(String.format("Your order with order number %s is placed successfully", orderPlacedEvent.getOrderNumber()));
            messageHelper.setText(String.format("""
                    Hi %s, %s
                    
                    Your order with order number %s is now placed successfully.
                    
                    Best regards,
                    Spring shop
                    """,
                    orderPlacedEvent.getFirstname().toString(),
                    orderPlacedEvent.getLastname().toString(),
                    orderPlacedEvent.getOrderNumber().toString()
            ));
        };

        try {
            javaMailSender.send(messagePreparator);
            log.info("order notification email sent !!");
        } catch (MailException exception) {
            log.error("exception occurred when sending mail", exception);
            throw new RuntimeException("exception occured when sending email from crave", exception);
        }
    }
}
