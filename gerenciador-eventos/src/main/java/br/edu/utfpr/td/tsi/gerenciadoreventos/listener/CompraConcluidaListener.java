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
public class CompraConcluidaListener {

    private static final Logger logger = LoggerFactory.getLogger(CompraConcluidaListener.class);

    @Autowired
    private PedidoRepository pedidoRepository;

    @RabbitListener(queues = "fila.compra.concluida")
    public void receberCompraConcluida(String mensagem) {
        logger.info("[GERENCIADOR-EVENTOS] Mensagem recebida em fila.compra.concluida: {}", mensagem);

        try {
            JsonObject json = new Gson().fromJson(mensagem, JsonObject.class);
            String id = json.has("id") ? json.get("id").getAsString() : null;

            if (id == null) {
                logger.warn("[GERENCIADOR-EVENTOS] Mensagem de compra concluída sem id de pedido: {}", mensagem);
                return;
            }

            Optional<Pedido> pedidoOpt = pedidoRepository.findById(id);
            if (pedidoOpt.isPresent()) {
                Pedido pedido = pedidoOpt.get();
                pedido.setStatus("CONCLUIDA");
                pedidoRepository.save(pedido);
                logger.info("[GERENCIADOR-EVENTOS] Pedido {} atualizado no MongoDB com status CONCLUIDA.", id);
            } else {
                logger.warn("[GERENCIADOR-EVENTOS] Pedido {} não encontrado no MongoDB.", id);
            }
        } catch (Exception e) {
            logger.error("[GERENCIADOR-EVENTOS] Falha ao processar mensagem de compra concluída: {}", e.getMessage());
        }
    }
}
