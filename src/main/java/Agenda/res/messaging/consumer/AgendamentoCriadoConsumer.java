package Agenda.res.messaging.consumer;

import Agenda.res.messaging.config.RabbitMQConfig;
import Agenda.res.messaging.event.AgendamentoCriadoEvent;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class AgendamentoCriadoConsumer {
    @RabbitListener(queues = RabbitMQConfig.QUEUE, ackMode="MANUAL")
    public void processar(AgendamentoCriadoEvent evento, Channel channel,
                          @Header(AmqpHeaders.DELIVERY_TAG) long tag) throws IOException {
        try{
            System.out.println(" [x] Processando notificação de agendamento: " + evento);
            System.out.println(" [x] Enviando lembrete para: " + evento.nomeCliente());

            channel.basicAck(tag,false);//confirma  qode apagar afila
            System.out.println(" [x] Mensagem confirmada (ack)");
        }catch(Exception e){
            System.out.println(" [x] Erro ao processar, enviando para DLQ: " + e.getMessage());
            channel.basicNack(tag,false,false);//rejeitou a msg, false-igual ao ack so a msg, o segundo = n recoloca na fila pra tentar de novo
        }
    }
}
