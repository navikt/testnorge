package no.nav.dolly.domain.resultset.sigrunstub;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RsSummertSkattegrunnlag {

    private LocalDateTime ajourholdstidspunkt;
                "grunnlag":[

    {
        "andelOverfoertFraBarn":9007199254740991,
            "beloep":9007199254740991,
            "kategori":"string",
            "spesifisering": [
        {
            "type":"string"
        },
        {
            "type":"string"
        },
        {
            "type":"string",
                "aarForFoerstegangsregistrering":"string",
                "antattMarkedsverdi":9007199254740991,
                "antattVerdiSomNytt":9007199254740991,
                "beloep":9007199254740991,
                "eierandel":0.1,
                "fabrikatnavn":"string",
                "formuesverdi":9007199254740991,
                "formuesverdiForFormuesandel":9007199254740991,
                "registreringsnummer":"string"
        }
          ],
        "tekniskNavn":"string"
    }
      ],
              "inntektsaar":"string",
              "kildeskattPaaLoennGrunnlag":[

    {
        "andelOverfoertFraBarn":9007199254740991,
            "beloep":9007199254740991,
            "kategori":"string",
            "spesifisering": [
        {
            "type":"string"
        },
        {
            "type":"string"
        },
        {
            "type":"string",
                "aarForFoerstegangsregistrering":"string",
                "antattMarkedsverdi":9007199254740991,
                "antattVerdiSomNytt":9007199254740991,
                "beloep":9007199254740991,
                "eierandel":0.1,
                "fabrikatnavn":"string",
                "formuesverdi":9007199254740991,
                "formuesverdiForFormuesandel":9007199254740991,
                "registreringsnummer":"string"
        }
          ],
        "tekniskNavn":"string"
    }
      ],
              "personidentifikator":"string",
              "skatteoppgjoersdato":"2025-04-01",
              "skjermet":true,
              "stadie":"string",
              "svalbardGrunnlag":[

    {
        "andelOverfoertFraBarn":9007199254740991,
            "beloep":9007199254740991,
            "kategori":"string",
            "spesifisering": [
        {
            "type":"string"
        },
        {
            "type":"string"
        },
        {
            "type":"string",
                "aarForFoerstegangsregistrering":"string",
                "antattMarkedsverdi":9007199254740991,
                "antattVerdiSomNytt":9007199254740991,
                "beloep":9007199254740991,
                "eierandel":0.1,
                "fabrikatnavn":"string",
                "formuesverdi":9007199254740991,
                "formuesverdiForFormuesandel":9007199254740991,
                "registreringsnummer":"string"
        }
          ],
        "tekniskNavn":"string"
    }
      ]
}
  ]
          }

public static class Grunnlag {

    private Integer andelOverfoertFraBarn;
    private Integer beloep;
    private String kategori;
    private String spesifisering;

    {
        "type":"string"
    },

    {
        "type":"string"
    },

    {
        "type":"string",
            "aarForFoerstegangsregistrering":"string",
            "antattMarkedsverdi":9007199254740991,
            "antattVerdiSomNytt":9007199254740991,
            "beloep":9007199254740991,
            "eierandel":0.1,
            "fabrikatnavn":"string",
            "formuesverdi":9007199254740991,
            "formuesverdiForFormuesandel":9007199254740991,
            "registreringsnummer":"string"
    }
          ],
    private String tekniskNavn;
}
}
