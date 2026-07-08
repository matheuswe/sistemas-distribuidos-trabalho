package br.edu.utfpr.td.tsi.lojaweb.repository;

import br.edu.utfpr.td.tsi.lojaweb.model.Pedido;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PedidoRepository extends MongoRepository<Pedido, String> {
}
