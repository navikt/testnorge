package no.nav.dolly.bestilling.instdata.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MiljoerResponse {

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
