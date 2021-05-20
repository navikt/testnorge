package no.nav.registre.hodejegeren.consumer.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class Data1DTO {
    String datoDo;
    String fnr;
}