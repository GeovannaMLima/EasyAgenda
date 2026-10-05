package Agenda.res.messaging.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class RabbitMQConfig {// guarda a infra- exchange, queue , biding...

    //def Exchange, queue e routingKey
    public static final String EXCHANGE = "agendamento.exchange";
    public static final String QUEUE = "agendamento.criado.queue";
    public static final String ROUTING_KEY = "agendamento.criado";
    public static final String DLQ = "agendamento.criado.dql";

    //def tipo de exchange
    @Bean
    public TopicExchange agendamentoExchange(){
        return new TopicExchange(EXCHANGE);
    }

    //def queue
    @Bean
    public Queue agendamentoCriadoQueue(){

        return QueueBuilder.durable(QUEUE)
                .withArgument("x-dead-letter-exchange","")//""-pois to usando default exchange -entrega direto p uma fila com nome iguala  rouing key
                .withArgument("x-dead-letter-routing-key", DLQ)
                .build();
    }

    //criando a dead queue letter
    @Bean
    public Queue dql(){
        return QueueBuilder.durable(DLQ).build();
    }
    //def a binding key
    @Bean
    public Binding binding(Queue agendamentoCriadoQueue, TopicExchange agendamentoExchange){
        return BindingBuilder.bind(agendamentoCriadoQueue)
                .to(agendamentoExchange)
                .with(ROUTING_KEY);
    }

    //converson para JSON
    @Bean
    public MessageConverter jsonMessageConverter(){
        return new JacksonJsonMessageConverter();
    }

}
