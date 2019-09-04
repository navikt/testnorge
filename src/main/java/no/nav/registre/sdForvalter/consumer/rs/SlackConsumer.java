package no.nav.registre.sdForvalter.consumer.rs;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ServerErrorException;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.Map;

import no.nav.registre.sdForvalter.consumer.rs.response.SlackChannelResponse;

@Component
@Slf4j
@RequiredArgsConstructor
public class SlackConsumer {

    private static final String LIMIT = "100000";
    private static final ParameterizedTypeReference<Map<String, Object>> MAP_RESPONSE = new ParameterizedTypeReference<Map<String, Object>>() {
    };
    private final RestTemplate restTemplate;
    @Value("${slack.bot.token}")
    private String slackBotToken;

    public Map<String, Object> post(String channel, Map<String, Object> data) {
        try {
            MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
            ObjectMapper mapper = new ObjectMapper();
            form.put("text", Collections.singletonList("" +
                    "Følgende data får ut om 1 måned. For å fornye dataen ta kontakt med faste testdata administratorer" +
                    "\n\n" +
                    "```" + mapper.writerWithDefaultPrettyPrinter().writeValueAsString(data) + "```"));
            form.put("channel", Collections.singletonList(channel));
            form.put("token", Collections.singletonList(slackBotToken));
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            RequestEntity<MultiValueMap<String, String>> mapRequestEntity =
                    new RequestEntity<>(form, headers,
                            HttpMethod.POST, new URI("https://slack.com/api/chat.postMessage"));

            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(mapRequestEntity, MAP_RESPONSE);
            if (response.getBody() != null) {
                return (Map<String, Object>) response.getBody().get("message");
            }
        } catch (URISyntaxException e) {
            log.error(e.getMessage(), e);
            throw new ServerErrorException(e.getReason(), e);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage(), e);
            throw new ServerErrorException(e.getMessage(), e);
        }
        throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "Kunne ikke motta svar fra slack");
    }

    public String getChannelId(String channelName) {
        return iterateChannels(channelName, "");
    }

    private String iterateChannels(String channelName, String cursor) {

        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.put("token", Collections.singletonList(slackBotToken));
        form.put("limit", Collections.singletonList(LIMIT));

        if (!"".equals(cursor)) {
            form.put("cursor", Collections.singletonList(cursor));
        }

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            RequestEntity<MultiValueMap<String, String>> slackChannelListRequest = new RequestEntity<>(form, headers, HttpMethod.POST, new URI("https://slack.com/api/conversations.list"));
            ResponseEntity<SlackChannelResponse> response = restTemplate.exchange(slackChannelListRequest, SlackChannelResponse.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                SlackChannelResponse body = response.getBody();
                if (body == null) {
                    throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "Null body fra slack");
                }
                if (!body.getOk()) {
                    throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Resultatet fra slack er ikke ok");
                }
                if (body.getChannels() == null) {
                    throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "Kanaler fra slack er null");
                }
                return body.getChannels().stream()
                        .filter(channel -> channelName.equals(channel.getOrDefault("name", "")))
                        .findFirst()
                        .map(found -> (String) found.get("id"))
                        .orElseGet(() -> {
                            if ("".equals(body.getResponseMetadata().get("next_cursor"))) {
                                throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Ingen offentlig slack kanal med dette navnet");
                            }
                            return iterateChannels(channelName, (String) body.getResponseMetadata().get("next_cursor"));
                        });
            }

        } catch (URISyntaxException e) {
            log.error(e.getMessage(), e);
            throw new ServerErrorException(e.getReason(), e);
        }
        throw new RuntimeException("Noe gikk galt mens vi henter kanal id fra slack");
    }
}
