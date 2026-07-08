package br.edu.utfpr.td.tsi.gerenciadoreventos.config;

import jakarta.annotation.PostConstruct;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilasConfig {

    private final RabbitAdmin rabbitAdmin;

    public FilasConfig(ConnectionFactory connectionFactory) {
        this.rabbitAdmin = new RabbitAdmin(connectionFactory);
    }

    @Bean
    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
        return this.rabbitAdmin;
    }

    @PostConstruct
    public void configurarFilas() {
        Queue filaPagamento = QueueBuilder.durable("fila.pagamento")
                .withArgument("x-dead-letter-exchange", "")
                .withArgument("x-dead-letter-routing-key", "fila.pagamento.dlq")
                .build();
        rabbitAdmin.declareQueue(filaPagamento);

        rabbitAdmin.declareQueue(new Queue("fila.pagamento.dlq", true));
        rabbitAdmin.declareQueue(new Queue("fila.compra.concluida", true));
        rabbitAdmin.declareQueue(new Queue("fila.email", true));
    }
}
