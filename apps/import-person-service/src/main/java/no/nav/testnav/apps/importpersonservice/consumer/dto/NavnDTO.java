package no.nav.testnav.apps.importpersonservice.consumer.dto;


import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;

import no.nav.testnav.libs.dto.pdlforvalter.v1.DbVersjonDTO;

@Value
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class NavnDTO {
    DbVersjonDTO.Master master;
}
