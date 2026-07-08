package br.edu.utfpr.td.tsi.gerenciadoreventos.publisher;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class EventoPublisher {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void enviarEmailConfirmacao(String mensagem) {
        rabbitTemplate.convertAndSend("fila.email", mensagem);
    }

    public void enviarParaPagamento(String pedidoJson) {
        rabbitTemplate.convertAndSend("fila.pagamento", pedidoJson);
    }
}
