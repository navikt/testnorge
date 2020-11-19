package no.nav.registre.orkestratoren.consumer;

import java.time.LocalDate;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;
import no.nav.registre.orkestratoren.consumer.command.GetHendelseCommand;
import no.nav.registre.testnorge.libs.dto.hendelse.v1.HendelseDTO;
import no.nav.registre.testnorge.libs.dto.hendelse.v1.HendelseType;

@Slf4j
@Service
public class OrgnummerConsumerService {

    private final RestTemplate restTemplate;

}
