package Agenda.res.AuditoriaMessaging.consumer;

import Agenda.res.AuditoriaMessaging.config.AuditoriaConfig;
import Agenda.res.AuditoriaMessaging.event.AgendamentoAuditoriaEvent;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class AuditoriaConsumer {
    @KafkaListener(topics= AuditoriaConfig.TOPIC,groupId = "auditoria-service")
    public void receive(ConsumerRecord<String, AgendamentoAuditoriaEvent> record) {
        System.out.println(" [kafka] Auditoria registrada: " + record.value()
                + " | partição=" + record.partition()
                + " offset=" + record.offset());
    }
}
