# Sistemas Distribuídos — E-commerce Trabalho
Matheus Rodrigo Weber
a2568764
## Execução dos serviços Docker 
Use os comandos: (`docker run -d --hostname my-rabbit --name some-rabbit -p 8080:15672 -p 5672:5672 rabbitmq:management `), (`docker run -d --name mongo -p 27017:27017 mongo:7 --replSet rs0`) e `docker exec mongo mongosh --eval "rs.initiate()"`

## Fluxo de Compra do e-Commerce 

1. Loja Web exibe catálogo de produtos
2. Usuário escolhe produtos para comprar
3. Usuário informa dados de pagamento e endereço de entrega
4. Usuário informa CEP para preenchimento automático do endereço
5. Usuário confirma a compra *(Loja Web grava o pedido no MongoDB)*
6. Sistema envia e-mail de confirmação da compra *(disparado pelo Gerenciador de Eventos)*
7. Sistema realiza a transação de pagamento
8. Sistema envia e-mail com resultado do pagamento
9. Sistema gera nota fiscal e baixa o estoque
10. Sistema atualiza o status da compra no banco de dados
11. Sistema envia e-mail com a nota fiscal
12. Sistema disponibiliza produtos para entrega
13. Sistema envia e-mail com dados da entrega
