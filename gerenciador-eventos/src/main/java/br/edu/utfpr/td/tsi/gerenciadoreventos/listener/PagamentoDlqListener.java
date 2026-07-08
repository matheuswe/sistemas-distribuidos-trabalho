package br.edu.utfpr.td.tsi.gerenciadoreventos.listener;

import br.edu.utfpr.td.tsi.gerenciadoreventos.model.Pedido;
import br.edu.utfpr.td.tsi.gerenciadoreventos.repository.PedidoRepository;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class PagamentoDlqListener {

    private static final Logger logger = LoggerFactory.getLogger(PagamentoDlqListener.class);

    @Autowired
    private PedidoRepository pedidoRepository;

    @RabbitListener(queues = "fila.pagamento.dlq")
    public void receberErroDlq(String mensagem) {
        logger.error("[GERENCIADOR-EVENTOS] Mensagem recebida da DLQ de pagamento: {}", mensagem);

        try {
            JsonObject json = new Gson().fromJson(mensagem, JsonObject.class);
            String id = json.has("id") ? json.get("id").getAsString() : null;

            if (id == null) {
                logger.warn("[GERENCIADOR-EVENTOS] Mensagem da DLQ sem id de pedido, não é possível atualizar o status: {}", mensagem);
                return;
            }

            Optional<Pedido> pedidoOpt = pedidoRepository.findById(id);
            if (pedidoOpt.isPresent()) {
                Pedido pedido = pedidoOpt.get();
                pedido.setStatus("ERRO_PAGAMENTO");
                pedidoRepository.save(pedido);
                logger.info("[GERENCIADOR-EVENTOS] Pedido {} atualizado no MongoDB com status ERRO_PAGAMENTO.", id);
            } else {
                logger.warn("[GERENCIADOR-EVENTOS] Pedido {} não encontrado no MongoDB.", id);
            }
        } catch (Exception e) {
            logger.error("[GERENCIADOR-EVENTOS] Falha ao processar mensagem da DLQ: {}", e.getMessage());
        }
    }
}
