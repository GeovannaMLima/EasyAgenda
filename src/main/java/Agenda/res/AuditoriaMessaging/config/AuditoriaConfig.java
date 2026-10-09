package Agenda.res.AuditoriaMessaging.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class AuditoriaConfig {

    public static final String TOPIC = "agendamento.auditoria";

    @Bean
    public NewTopic Auditoriatopic(){
        return TopicBuilder.name(TOPIC)
                .partitions(3)
                .replicas(1)
                .build();
    }
}
