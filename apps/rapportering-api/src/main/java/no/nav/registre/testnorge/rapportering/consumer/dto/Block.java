package no.nav.registre.testnorge.rapportering.consumer.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;

@Value
@Builder
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class Block {
    String type = "section";
    Text text;

    public static Block from(String text){
        return Block.builder().text(Text.builder().text(text).build()).build();
    }
}
