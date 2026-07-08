package br.edu.utfpr.td.tsi.entregaservice;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class EntregaPublisher {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void enviarEmailEntrega(String mensagem) {
        rabbitTemplate.convertAndSend("fila.email", mensagem);
    }
}
