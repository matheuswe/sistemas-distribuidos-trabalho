package br.edu.utfpr.td.tsi.emailservice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class EmailListener {

    private static final Logger logger = LoggerFactory.getLogger(EmailListener.class);

    @RabbitListener(queues = "fila.email")
    public void receberEmail(String mensagem) {
        logger.info("[EMAIL-SERVICE] Simulando envio de e-mail: {}", mensagem);
        System.out.println(">>> [E-MAIL ENVIADO] " + mensagem);
    }
}
