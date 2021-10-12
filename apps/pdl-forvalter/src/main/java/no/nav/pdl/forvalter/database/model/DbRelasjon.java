package no.nav.pdl.forvalter.database.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import no.nav.testnav.libs.dto.pdlforvalter.v1.RelasjonType;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DbRelasjon {

    @Id
    private Long id;

    private LocalDateTime sistOppdatert;

    private RelasjonType relasjonType;

    private Long personId;

    private Long relatertPersonId;

    @Transient
    private DbPerson relatertPerson;
}