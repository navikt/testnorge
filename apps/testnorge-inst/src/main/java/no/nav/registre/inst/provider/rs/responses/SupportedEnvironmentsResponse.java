package no.nav.registre.inst.provider.rs.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SupportedEnvironmentsResponse {

    private List<String> institusjonsoppholdEnvironments;
    private List<String> kdiEnvironments;

    public List<String> getInstitusjonsoppholdEnvironments() {
        if (isNull(institusjonsoppholdEnvironments)) {
            institusjonsoppholdEnvironments = new ArrayList<>();
        }
        return institusjonsoppholdEnvironments;
    }

    public List<String> getKdiEnvironments() {
        if (isNull(kdiEnvironments)) {
            kdiEnvironments = new ArrayList<>();
        }
        return kdiEnvironments;
    }
}
