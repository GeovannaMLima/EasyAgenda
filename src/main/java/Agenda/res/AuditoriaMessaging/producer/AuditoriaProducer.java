package Agenda.res.AuditoriaMessaging.producer;

import Agenda.res.AuditoriaMessaging.config.AuditoriaConfig;
import Agenda.res.AuditoriaMessaging.event.AgendamentoAuditoriaEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;

@Configuration
public class AuditoriaProducer {

    private final KafkaTemplate<String, AgendamentoAuditoriaEvent> kafkaTemplate;

    public AuditoriaProducer(KafkaTemplate<String, AgendamentoAuditoriaEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publicar(AgendamentoAuditoriaEvent evento){
        String chave = String.valueOf(evento.profissionalId());
        kafkaTemplate.send(AuditoriaConfig.TOPIC, chave, evento);
        System.out.println(" [kafka] Auditoria publicada: " + evento);
    }
}
