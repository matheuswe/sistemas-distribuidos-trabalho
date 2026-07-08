package br.edu.utfpr.td.tsi.entregaservice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class EntregaListener {

    private static final Logger logger = LoggerFactory.getLogger(EntregaListener.class);

    @Autowired
    private EntregaPublisher publisher;

    @RabbitListener(queues = "fila.entrega")
    public void processarEntrega(String mensagem) {
        logger.info("[ENTREGA-SERVICE] Processando entrega: {}", mensagem);

        String codigoRastreio = "BR" + System.currentTimeMillis() + "SP";
        System.out.println(">>> [ENTREGA AGENDADA] Rastreio: " + codigoRastreio + " | Pedido: " + mensagem);

        String emailMsg = "Seu pedido está a caminho! Código de rastreio: " + codigoRastreio
                + " | Detalhes: " + mensagem;
        publisher.enviarEmailEntrega(emailMsg);
        logger.info("[ENTREGA-SERVICE] E-mail com dados de entrega enviado para fila.email");
    }
}
