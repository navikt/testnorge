package no.nav.registre.testnorge.applikasjonsanalyseservice.domain.yml.topic.v1;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.util.List;

@Value
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class Spec {
    List<Acl> acl;
}
