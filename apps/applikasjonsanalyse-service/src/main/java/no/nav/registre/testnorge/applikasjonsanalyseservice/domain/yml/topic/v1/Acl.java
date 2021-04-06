package no.nav.registre.testnorge.applikasjonsanalyseservice.domain.yml.topic.v1;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;

@Value
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class Acl {
    String team;
    String application;
    String access;
}
