package br.edu.utfpr.td.tsi.produtosservice;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.InputStream;
import java.util.List;

@RestController
@RequestMapping("/produtos")
public class ProdutosController {

    @GetMapping
    public ResponseEntity<List<Produto>> listar() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            InputStream is = new ClassPathResource("produtos.json").getInputStream();
            List<Produto> produtos = mapper.readValue(is, new TypeReference<List<Produto>>() {});
            return ResponseEntity.ok(produtos);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Produto> buscarPorId(@PathVariable int id) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            InputStream is = new ClassPathResource("produtos.json").getInputStream();
            List<Produto> produtos = mapper.readValue(is, new TypeReference<List<Produto>>() {});
            return produtos.stream()
                    .filter(p -> p.getId() == id)
                    .findFirst()
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
