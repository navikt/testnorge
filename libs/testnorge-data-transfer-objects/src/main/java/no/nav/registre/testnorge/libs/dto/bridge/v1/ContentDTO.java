package no.nav.registre.testnorge.libs.dto.bridge.v1;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;

@Value
@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class ContentDTO {
    String key;
    byte[] content;
}
//
//
//    var test = Endringsdokument
//            .newBuilder()
//            .setOrganisasjonBuilder(Organisasjon
//                    .newBuilder()
//                    .setEnhetstype("AS")
//                    .setOrgnummer("dummy")
//                    .setUnderenheter(Collections.emptyList())
//            )
//            .setMetadata(Metadata.newBuilder().setMiljo("t3").build()).build();
//
//    byte[] v2 = test.toByteBuffer().array();
//
//return Endringsdokument.fromByteBuffer(ByteBuffer.wrap(v2));