package no.nav.testnav.libs.dto.tpsmessagingservice.v1;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class TpsServiceRoutineEndringDTO extends TpsServiceRoutineDTO {
    private String offentligIdent;
    
    public TpsServiceRoutineEndringDTO(String serviceRutinenavn, String offentligIdent) {
        super(serviceRutinenavn);
        this.offentligIdent = offentligIdent;
    }
}
