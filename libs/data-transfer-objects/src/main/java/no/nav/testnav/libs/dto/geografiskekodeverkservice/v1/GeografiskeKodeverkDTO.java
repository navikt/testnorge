package no.nav.testnav.libs.dto.geografiskekodeverkservice.v1;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record GeografiskeKodeverkDTO(String navn, String kode) {}
