package br.edu.utfpr.td.tsi.cepservice;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.InputStream;
import java.util.List;

@RestController
@RequestMapping("/cep")
public class CepController {

    @GetMapping("/{cep}")
    public ResponseEntity<Endereco> buscarPorCep(@PathVariable String cep) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            InputStream is = new ClassPathResource("enderecos.json").getInputStream();
            List<Endereco> enderecos = mapper.readValue(is, new TypeReference<List<Endereco>>() {});

            String cepLimpo = cep.replaceAll("[^0-9]", "");

            java.util.Optional<Endereco> resultado = enderecos.stream()
                    .filter(e -> e.getCep().equals(cepLimpo))
                    .findFirst();

            if (resultado.isEmpty() && cepLimpo.length() >= 5) {
                resultado = enderecos.stream()
                        .filter(e -> e.getCep().startsWith(cepLimpo.substring(0, 5)))
                        .findFirst();
            }

            if (resultado.isEmpty() && !enderecos.isEmpty()) {
                Endereco original = enderecos.get(0);

                Endereco fallback = new Endereco();
                fallback.setCep(cepLimpo);
                fallback.setLogradouro(original.getLogradouro());
                fallback.setBairro(original.getBairro());
                fallback.setCidade(original.getCidade());
                fallback.setEstado(original.getEstado());

                return ResponseEntity.ok(fallback);
            }

            return resultado
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());

        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}