package br.edu.utfpr.td.tsi.fiscalservice;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class FiscalPublisher {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void enviarEmailNotaFiscal(String mensagem) {
        rabbitTemplate.convertAndSend("fila.email", mensagem);
    }
}
