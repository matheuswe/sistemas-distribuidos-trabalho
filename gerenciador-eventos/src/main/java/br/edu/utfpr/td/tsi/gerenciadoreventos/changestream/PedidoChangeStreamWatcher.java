package br.edu.utfpr.td.tsi.gerenciadoreventos.changestream;

import br.edu.utfpr.td.tsi.gerenciadoreventos.publisher.EventoPublisher;
import com.google.gson.Gson;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.changestream.FullDocument;
import com.mongodb.client.model.changestream.OperationType;
import jakarta.annotation.PostConstruct;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class PedidoChangeStreamWatcher {

    @Autowired
    private MongoClient mongoClient;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private EventoPublisher publisher;

    @Value("${mongo.collection.pedidos:pedidos}")
    private String collectionName;

    @PostConstruct
    public void iniciar() {
        new Thread(this::listenerFromMongoDb, "pedido-change-stream").start();
    }

    public void listenerFromMongoDb() {
        String nomeDb = mongoTemplate.getDb().getName();
        MongoCollection<Document> collection = mongoClient.getDatabase(nomeDb).getCollection(collectionName);

        System.out.println("Ouvindo MongoDB...");

        collection.watch().fullDocument(FullDocument.UPDATE_LOOKUP).forEach(event -> {
            System.out.println("===== Change Event Detectado =====");
            System.out.println("Operação: " + event.getOperationType());

            Document doc = event.getFullDocument();
            if (doc != null) {
                System.out.println("Documento: " + doc.toJson());

                if (event.getOperationType() == OperationType.INSERT) {
                    processarNovoPedido(doc);
                }
            }
            System.out.println("===================================");
        });
    }

    private void processarNovoPedido(Document doc) {
        String id = doc.getObjectId("_id").toHexString();
        String produto = doc.getString("produto");
        Double valor = doc.getDouble("valor");
        String endereco = doc.getString("endereco");
        String emailCliente = doc.getString("emailCliente");

        publisher.enviarEmailConfirmacao("Sua compra foi recebida! Produto: " + produto
                + " | Valor: R$ " + valor + ". Estamos processando seu pedido.");

        Map<String, Object> pedido = new HashMap<>();
        pedido.put("id", id);
        pedido.put("produto", produto);
        pedido.put("valor", valor);
        pedido.put("endereco", endereco);
        pedido.put("emailCliente", emailCliente);

        publisher.enviarParaPagamento(new Gson().toJson(pedido));
    }
}