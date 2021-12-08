package no.nav.testnav.libs.dto.tpsmessagingservice.v1;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.XmlType;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@XmlType(propOrder = {"sprakKode", "datoSprak"})
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class SpraakDTO {

    private String sprakKode;
    private LocalDateTime datoSprak;
}