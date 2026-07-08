package br.edu.utfpr.td.tsi.gerenciadoreventos.repository;

import br.edu.utfpr.td.tsi.gerenciadoreventos.model.Pedido;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PedidoRepository extends MongoRepository<Pedido, String> {
}
