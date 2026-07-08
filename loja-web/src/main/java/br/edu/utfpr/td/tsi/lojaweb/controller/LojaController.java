package br.edu.utfpr.td.tsi.lojaweb.controller;

import br.edu.utfpr.td.tsi.lojaweb.model.Endereco;
import br.edu.utfpr.td.tsi.lojaweb.model.Pedido;
import br.edu.utfpr.td.tsi.lojaweb.model.Produto;
import br.edu.utfpr.td.tsi.lojaweb.repository.PedidoRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class LojaController {

    private static final Logger logger = LoggerFactory.getLogger(LojaController.class);

    @Autowired
    private PedidoRepository pedidoRepository;

    @Value("${servicos.cep.url}")
    private String cepServiceUrl;

    @Value("${servicos.produtos.url}")
    private String produtosServiceUrl;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @GetMapping("/")
    public String catalogo(Model model) {
        try {
            String json = restTemplate.getForObject(produtosServiceUrl, String.class);
            List<Produto> produtos = objectMapper.readValue(json, new TypeReference<List<Produto>>() {});
            model.addAttribute("produtos", produtos);
        } catch (Exception e) {
            logger.error("Erro ao buscar produtos: {}", e.getMessage());
            model.addAttribute("erro", "Serviço de produtos indisponível.");
        }
        return "catalogo";
    }

    @GetMapping("/checkout/{produtoId}")
    public String checkout(@PathVariable int produtoId, Model model) {
        try {
            String json = restTemplate.getForObject(produtosServiceUrl + "/" + produtoId, String.class);
            Produto produto = objectMapper.readValue(json, Produto.class);
            model.addAttribute("produto", produto);
        } catch (Exception e) {
            logger.error("Erro ao buscar produto {}: {}", produtoId, e.getMessage());
            return "redirect:/";
        }
        return "checkout";
    }

    @GetMapping("/cep/{cep}")
    @ResponseBody
    public Endereco buscarCep(@PathVariable String cep) {
        try {
            return restTemplate.getForObject(cepServiceUrl + "/" + cep, Endereco.class);
        } catch (Exception e) {
            logger.error("CEP não encontrado: {}", cep);
            return null;
        }
    }

    @PostMapping("/finalizar")
    public String finalizarCompra(
            @RequestParam String produtoNome,
            @RequestParam double produtoPreco,
            @RequestParam String cep,
            @RequestParam String logradouro,
            @RequestParam String bairro,
            @RequestParam String cidade,
            @RequestParam String estado,
            @RequestParam double valor,
            RedirectAttributes redirect) {

        Pedido pedido = new Pedido();
        pedido.setProduto(produtoNome);
        pedido.setValor(valor);
        pedido.setEndereco(logradouro + ", " + bairro + ", " + cidade + " - " + estado + " (CEP: " + cep + ")");
        pedido.setEmailCliente("cliente@gmail.com");
        pedido.setStatus("PENDENTE");

        pedidoRepository.save(pedido);

        logger.info("[LOJA-WEB] Pedido gravado no MongoDB com status PENDENTE: id={}, produto={}, valor={}",
                pedido.getId(), produtoNome, valor);

        redirect.addFlashAttribute("pedidoId", pedido.getId());
        redirect.addFlashAttribute("produto", produtoNome);
        redirect.addFlashAttribute("valor", valor);
        redirect.addFlashAttribute("erroPagamento", valor < 0);

        return "redirect:/compra-concluida";
    }

    @GetMapping("/compra-concluida")
    public String compraConcluida() {
        return "compra-concluida";
    }

    @GetMapping("/pedido/{id}/status")
    @ResponseBody
    public String statusPedido(@PathVariable String id) {
        return pedidoRepository.findById(id)
                .map(Pedido::getStatus)
                .orElse("DESCONHECIDO");
    }
}
