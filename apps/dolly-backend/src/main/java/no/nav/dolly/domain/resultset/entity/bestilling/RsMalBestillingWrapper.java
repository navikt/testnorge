package no.nav.dolly.domain.resultset.entity.bestilling;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static java.util.Objects.isNull;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RsMalBestillingWrapper {

    private Map<String, List<RsMalBestillingUtenFavoritter>> malbestillinger;

    public Map<String, List<RsMalBestillingUtenFavoritter>> getMalbestillinger() {

        if (isNull(malbestillinger)) {
            malbestillinger = new TreeMap<>();
        }
        return malbestillinger;
    }
}
