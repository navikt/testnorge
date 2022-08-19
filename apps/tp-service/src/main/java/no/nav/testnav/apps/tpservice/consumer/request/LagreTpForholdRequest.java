package no.nav.testnav.apps.tpservice.consumer.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LagreTpForholdRequest {

    private List<String> miljoer;

    private String fnr;
    private String ordning;
}
