package br.edu.utfpr.td.tsi.lojaweb.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "pedidos")
public class Pedido {

    @Id
    private String id;

    private String produto;
    private double valor;
    private String endereco;
    private String emailCliente;

    private String status;

    public Pedido() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getProduto() { return produto; }
    public void setProduto(String produto) { this.produto = produto; }
    public double getValor() { return valor; }
    public void setValor(double valor) { this.valor = valor; }
    public String getEndereco() { return endereco; }
    public void setEndereco(String endereco) { this.endereco = endereco; }
    public String getEmailCliente() { return emailCliente; }
    public void setEmailCliente(String emailCliente) { this.emailCliente = emailCliente; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
