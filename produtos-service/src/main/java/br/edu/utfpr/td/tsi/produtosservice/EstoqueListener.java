package br.edu.utfpr.td.tsi.produtosservice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class EstoqueListener {

    private static final Logger logger = LoggerFactory.getLogger(EstoqueListener.class);

    @RabbitListener(queues = "fila.estoque")
    public void processarBaixaEstoque(String mensagem) {
        logger.info("[PRODUTOS-SERVICE] Baixando estoque: {}", mensagem);
        System.out.println(">>> [ESTOQUE ATUALIZADO] " + mensagem);
    }
}
