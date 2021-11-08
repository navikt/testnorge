package no.nav.testnav.apps.tpsmessagingservice.provider.v1;

import java.util.List;
import java.util.Map;
import org.springframework.jms.JmsException;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import no.nav.testnav.apps.tpsmessagingservice.service.EndringsmeldingService;
import no.nav.testnav.libs.dto.tpsmessagingservice.v1.KontaktopplysningerRequestDTO;

@RestController
@RequestMapping("/api/v1/endringsmeldinger")
@RequiredArgsConstructor
public class EndringsmeldingController {

   private final JmsTemplate jmsTemplate;
   private final EndringsmeldingService endringsmeldingService;

    @GetMapping("/send")
    String send(){
        try{
            jmsTemplate.convertAndSend("DEV.QUEUE.1", "Hello World!");
            return "OK";
        }catch(JmsException ex){
            ex.printStackTrace();
            return "FAIL";
        }
    }

    @PostMapping("/kontaktopplysninger")
    public Map<String, String> sendKontaktopplysninger(@RequestParam List<String> miljoer, @RequestBody KontaktopplysningerRequestDTO kontaktopplysninger) {

        return endringsmeldingService.sendKontaktopplysninger(kontaktopplysninger, miljoer);
    }
}
