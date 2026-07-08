package br.edu.utfpr.td.tsi.fiscalservice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class FiscalListener {

    private static final Logger logger = LoggerFactory.getLogger(FiscalListener.class);

    @Autowired
    private FiscalPublisher publisher;

    @RabbitListener(queues = "fila.fiscal")
    public void processarNotaFiscal(String mensagem) {
        logger.info("[FISCAL-SERVICE] Gerando nota fiscal para: {}", mensagem);

        String numeroNF = "NF-" + System.currentTimeMillis();
        System.out.println(">>> [NOTA FISCAL GERADA] " + numeroNF + " | Pedido: " + mensagem);

        String emailMsg = "Nota Fiscal " + numeroNF + " gerada para seu pedido: " + mensagem;
        publisher.enviarEmailNotaFiscal(emailMsg);
        logger.info("[FISCAL-SERVICE] E-mail com nota fiscal enviado para fila.email");
    }
}
