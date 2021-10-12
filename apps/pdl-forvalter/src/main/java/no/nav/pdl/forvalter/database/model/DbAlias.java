package no.nav.pdl.forvalter.database.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DbAlias {

    @Id
    private Long id;

    private LocalDateTime sistOppdatert;

    private Long personId;

    private String tidligereIdent;
}