package no.nav.registre.sdForvalter.database.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.List;

import no.nav.registre.sdForvalter.util.database.CreatableFromString;

@Entity
@ToString
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
@Table(name = "krr")
public class KrrModel extends AuditModel implements CreatableFromString {

    @Id
    private String fnr;

    private String name;
    private String email;
    private String sms;
    private boolean reserved;
    private boolean sdp;
    private boolean emailValid;
    private boolean smsValid;

    @Override
    public void updateFromString(List<String> input, List<String> headers) {

        for (int i = 0; i < headers.size(); i++) {
            try {
                String fieldName = headers.get(i);

                if (input.size() != headers.size()) {
                    if (input.size() == i || input.size() == 0)
                        break;
                }

                String fieldValue = input.get(i);

                switch (fieldName.toLowerCase()) {
                    case "navn":
                        this.setName(fieldValue);
                        break;
                    case "epost":
                        this.setEmail(fieldValue);
                        break;
                    case "reservert":
                        if ("nei".equals(fieldValue.toLowerCase())) {
                            this.setReserved(false);
                        } else {
                            this.setReserved(true);
                        }
                        break;
                    case "fnr":
                        this.setFnr(fieldValue);
                        break;
                    case "sms":
                        this.setSms(fieldValue);
                        break;
                    case "sdp":
                        if ("nei".equals(fieldValue.toLowerCase())) {
                            this.setSdp(false);
                        } else {
                            this.setSdp(true);
                        }
                        break;
                    case "epostgyldig":
                        if ("gyldig".equals(fieldValue.toLowerCase())) {
                            this.setEmailValid(true);
                        } else {
                            this.setEmailValid(false);
                        }
                        break;
                    case "smsgyldig":
                        if ("gyldig".equals(fieldValue.toLowerCase())) {
                            this.setSmsValid(true);
                        } else {
                            this.setSmsValid(false);
                        }
                        break;
                    default:
                        throw new NoSuchFieldException(fieldName);
                }

            } catch (NoSuchFieldException e) {
                log.warn("Mismatch between input data when creating from string for field: {}", headers.get(i));
                log.warn(e.getMessage(), e);
            }
        }
    }
}
