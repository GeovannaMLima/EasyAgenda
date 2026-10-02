package Agenda.res.messaging.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class RabbitMQConfig {// guarda a infra- exchange, queue , biding...

    //def Exchange, queue e routingKey
    public static final String EXCHANGE = "agendamento.exchange";
    public static final String QUEUE = "agendamento.criado.queue";
    public static final String ROUTING_KEY = "agendamento.criado";

    //def tipo de exchange
    @Bean
    public TopicExchange agendamentoExchange(){
        return new TopicExchange(EXCHANGE);
    }

    //def queue
    @Bean
    public Queue agendamentoCriadoQueue(){
        return new Queue(QUEUE,true);
    }

    //def a binding key
    @Bean
    public Binding binding(Queue agendamentoCriadoQueue, TopicExchange agendamentoExchange){
        return BindingBuilder.bind(agendamentoCriadoQueue)
                .to(agendamentoExchange)
                .with(ROUTING_KEY);
    }

}
