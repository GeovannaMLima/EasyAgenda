package Agenda.res.messaging.publisher;

import Agenda.res.messaging.event.AgendamentoCriadoEvent;
import Agenda.res.messaging.config.RabbitMQConfig;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class AgendamentoEventPublisher {

    private final RabbitTemplate rabbitTemplate;


    public AgendamentoEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publicarEventoCriado(AgendamentoCriadoEvent evento){
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE,
                RabbitMQConfig.ROUTING_KEY,
                evento
        );
        System.out.println(" [x] Evento publicado: " + evento);
    }
}
