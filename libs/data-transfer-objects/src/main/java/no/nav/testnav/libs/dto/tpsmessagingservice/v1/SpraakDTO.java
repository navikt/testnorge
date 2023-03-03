package no.nav.testnav.libs.dto.tpsmessagingservice.v1;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.xml.bind.annotation.XmlType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@XmlType(propOrder = { "sprakKode", "datoSprak" })
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class SpraakDTO {

    private String sprakKode;
    private LocalDateTime datoSprak;
}