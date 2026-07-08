package br.edu.utfpr.td.tsi.pagamentoservice;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PagamentoPublisher {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void enviarEmail(String mensagem) {
        rabbitTemplate.convertAndSend("fila.email", mensagem);
    }

    public void atualizarEstoque(String mensagem) {
        rabbitTemplate.convertAndSend("fila.estoque", mensagem);
    }

    public void emitirNotaFiscal(String mensagem) {
        rabbitTemplate.convertAndSend("fila.fiscal", mensagem);
    }

    public void agendarEntrega(String mensagem) {
        rabbitTemplate.convertAndSend("fila.entrega", mensagem);
    }

    public void compraConcluida(String pedidoJson) {
        rabbitTemplate.convertAndSend("fila.compra.concluida", pedidoJson);
    }
}
