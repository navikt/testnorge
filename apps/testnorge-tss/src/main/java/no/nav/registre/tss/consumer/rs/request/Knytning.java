package no.nav.registre.tss.consumer.rs.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@NoArgsConstructor
@Builder
@Getter
@AllArgsConstructor
public class Knytning {

    @NonNull
    private String orgnr;
}
