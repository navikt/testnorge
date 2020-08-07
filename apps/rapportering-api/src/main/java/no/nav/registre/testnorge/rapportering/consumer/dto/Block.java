package no.nav.registre.testnorge.rapportering.consumer.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;

@Value
@Builder
@NoArgsConstructor(force = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@AllArgsConstructor
public class Block {
    String type;
    Text text;

    public static Block from(String text, String type){
        return Block.builder().type(type).text(Text.builder().text(text).build()).build();
    }

    public static Block from(String text){
        return Block.builder().type("section").text(Text.builder().text(text).build()).build();
    }
}
