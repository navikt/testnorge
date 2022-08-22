package no.nav.testnav.apps.tpsmessagingservice.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.xml.bind.annotation.XmlType;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@XmlType(propOrder = {"offentligIdent", "fom"})
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class TpsEndringsopplysninger {

    private String offentligIdent;
    private String fom;
}
