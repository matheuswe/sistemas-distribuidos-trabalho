package br.edu.utfpr.td.tsi.pagamentoservice;

import com.google.gson.Gson;
import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class PagamentoListener {

    private static final Logger logger = LoggerFactory.getLogger(PagamentoListener.class);

    @Autowired
    private PagamentoPublisher publisher;

    @RabbitListener(queues = "fila.pagamento", ackMode = "MANUAL")
    public void processarPagamento(Message message, Channel channel) throws IOException {
        String corpo = new String(message.getBody());
        long deliveryTag = message.getMessageProperties().getDeliveryTag();

        logger.info("[PAGAMENTO-SERVICE] Mensagem recebida: {}", corpo);

        try {
            PedidoPagamento pedido = new Gson().fromJson(corpo, PedidoPagamento.class);

            if (pedido.getValor() < 0) {
                logger.warn("[PAGAMENTO-SERVICE] Pagamento recusado (valor negativo: {}). Enviando para DLQ.", pedido.getValor());
                channel.basicReject(deliveryTag, false);
                return;
            }

            String resumoPedido = "Produto: " + pedido.getProduto()
                    + " | Valor: R$ " + pedido.getValor()
                    + " | Endereço: " + pedido.getEndereco();

            logger.info("[PAGAMENTO-SERVICE] Pagamento aprovado. Orquestrando fluxo...");

            publisher.enviarEmail("Pagamento APROVADO! " + resumoPedido);

            publisher.emitirNotaFiscal(resumoPedido);
            publisher.atualizarEstoque(resumoPedido);

            String compraConcluidaJson = new Gson().toJson(
                    java.util.Map.of("id", pedido.getId(), "status", "CONCLUIDA"));
            publisher.compraConcluida(compraConcluidaJson);

            publisher.agendarEntrega(resumoPedido);

            channel.basicAck(deliveryTag, false);
            logger.info("[PAGAMENTO-SERVICE] Pedido processado com sucesso.");

        } catch (Exception e) {
            logger.error("[PAGAMENTO-SERVICE] Erro inesperado ao processar pagamento: {}. Enviando para DLQ.", e.getMessage());
            channel.basicReject(deliveryTag, false);
        }
    }
}
