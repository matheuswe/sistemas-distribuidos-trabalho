package br.edu.utfpr.td.tsi.pagamentoservice;

public class PedidoPagamento {
    private String id;
    private String produto;
    private String endereco;
    private double valor;
    private String emailCliente;

    public PedidoPagamento() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getProduto() { return produto; }
    public void setProduto(String produto) { this.produto = produto; }
    public String getEndereco() { return endereco; }
    public void setEndereco(String endereco) { this.endereco = endereco; }
    public double getValor() { return valor; }
    public void setValor(double valor) { this.valor = valor; }
    public String getEmailCliente() { return emailCliente; }
    public void setEmailCliente(String emailCliente) { this.emailCliente = emailCliente; }
}
