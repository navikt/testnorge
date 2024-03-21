package no.nav.testnav.apps.tpsmessagingservice.dto.endringsmeldinger;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.lang.reflect.InvocationTargetException;

import static java.lang.Math.min;

/**
 * Java-representasjon av skdmeldingen. Objektet bærer verdiene til de utfylte elementene i skd-meldingen.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SkdMeldingTrans1 {
	private static final int MELDINGSLENGDE_UTEN_HEADER = 1500;

	private String header;
	private String fodselsdato;
	private String personnummer;
	private String maskindato;
	private String maskintid;
	private String transtype;
	private String aarsakskode;
	private String regDato;
	private String statuskode;
	private String datoDoed;
	private String slektsnavn;
	private String fornavn;
	private String mellomnavn;
	private String slektsnavnUgift;
	private String forkortetNavn;
	private String regDatoNavn;
	private String foedekommLand;
	private String foedested;
	private String statsborgerskap;
	private String regdatoStatsb;
	private String familienummer;
	private String regdatoFamnr;
	private String personkode;
	private String spesRegType;
	private String datoSpesRegType;
	private String sivilstand;
	private String regdatoSivilstand;
	private String ektefellePartnerFoedselsdato;
	private String ektefellePartnerPersonnr;
	private String ektefellePartnerNavn;
	private String ektefellePartnerStatsb;
	private String regdatoAdr;
	private String flyttedatoAdr;
	private String kommunenummer;
	private String gateGaard;
	private String husBruk;
	private String bokstavFestenr;
	private String undernr;
	private String adressenavn;
	private String adressetype;
	private String tilleggsadresse;
	private String postnummer;
	private String valgkrets;
	private String postadresse1;
	private String postadresse2;
	private String postadresse3;
	private String postadresseLand;
	private String innvandretFraLand;
	private String fraLandRegdato;
	private String fraLandFlyttedato;
	private String fraKommune;
	private String fraKommRegdato;
	private String fraKommFlyttedato;
	private String utvandretTilLand;
	private String tilLandRegdato;
	private String tilLandFlyttedato;
	private String samemanntall;
	private String datoSamemanntall;
	private String umyndiggjort;
	private String datoUmyndiggjort;
	private String foreldreansvar;
	private String datoForeldreansvar;
	private String arbeidstillatelse;
	private String datoArbeidstillatelse;
	private String fremkonnummer;
	private String morsFodselsdato;
	private String morsPersonnummer;
	private String morsNavn;
	private String morsStatsbSkap;
	private String farsFodselsdato;
	private String farsPersonnummer;
	private String farsNavn;
	private String farsStatsbSkap;
	private String tidligereFnrDnr;
	private String datoTidlFnrDnr;
	private String nyttFnr;
	private String datoNyttFnr;
	private String levendeDoed;
	private String kjoenn;
	private String tildelingskode;
	private String foedselstype;
	private String morsSivilstand;
	private String ekteskapPartnerskapNr;
	private String ektefelleEkteskapPartnerskapNr;
	private String vigselstype;
	private String forsByrde;
	private String dombevilling;
	private String antallBarn;
	private String tidligereSivilstand;
	private String ektefelleTidligereSivilstand;
	private String hjemmel;
	private String fylke;
	private String vigselskommune;
	private String tidlSepDomBev;
	private String begjertAv;
	private String registrGrunnlag;
	private String doedssted;
	private String typeDoedssted;
	private String vigselsdato;
	private String medlKirken;
	private String sekvensnr;
	private String bolignr;
	private String dufId;
	private String brukerident;
	private String skolerets;
	private String tkNr;
	private String dnrHjemlandsadresse1;
	private String dnrHjemlandsadresse2;
	private String dnrHjemlandsadresse3;
	private String dnrHjemlandLandkode;
	private String dnrHjemlandRegDato;
	private String dnrIdKontroll;
	private String postadrRegDato;
	private String utvandringstype;
	private String grunnkrets;
	private String statsborgerskap2;
	private String regdatoStatsb2;
	private String statsborgerskap3;
	private String regdatoStatsb3;
	private String statsborgerskap4;
	private String regdatoStatsb4;
	private String statsborgerskap5;
	private String regdatoStatsb5;
	private String statsborgerskap6;
	private String regdatoStatsb6;
	private String statsborgerskap7;
	private String regdatoStatsb7;
	private String statsborgerskap8;
	private String regdatoStatsb8;
	private String statsborgerskap9;
	private String regdatoStatsb9;
	private String statsborgerskap10;
	private String regdatoStatsb10;
	private String bibehold;
	private String regdatoBibehold;
	private String saksid;
	private String embete;
	private String sakstype;
	private String vedtaksdato;
	private String internVergeid;
	private String vergeFnrDnr;
	private String vergetype;
	private String mandattype;
	private String mandatTekst;
	private String reserverFramtidigBruk;
	
	@Override
	public String toString() {
		StringBuilder skdMelding = new StringBuilder();
		if (header != null) {
			skdMelding.append(header);
		}
		
		SkdFeltDefinisjonerTrans1.getAllFeltDefinisjonerInSortedList().forEach(skdFeltDefinisjon -> {
			String parameterverdien = getMeldingsverdien(skdFeltDefinisjon);
			skdMelding.append(parameterverdien == null ?
					skdFeltDefinisjon.getDefaultVerdi() : addDefaultValueToEndOfString(parameterverdien, skdFeltDefinisjon));
		});
		
		return skdMelding.toString();
	}
	
	private String addDefaultValueToEndOfString(String parameterverdien, SkdFeltDefinisjonerTrans1 skdFeltDefinisjon) {
		if (!skdFeltDefinisjon.isValueLastInSkdField()) {
            return parameterverdien.substring(0, min(parameterverdien.length(), skdFeltDefinisjon.getAntallBytesAvsatt())) +
                    skdFeltDefinisjon.getDefaultVerdi().substring(min(skdFeltDefinisjon.getAntallBytesAvsatt(), parameterverdien.length()));
		} else {
			return skdFeltDefinisjon.getDefaultVerdi().substring(0,
					(skdFeltDefinisjon.getDefaultVerdi().length() - parameterverdien.length())) + parameterverdien;
		}
	}
	
	public String getMeldingsverdien(SkdFeltDefinisjonerTrans1 skdFeltDefinisjon) {
		try {
			return ((String) getClass().getMethod("get" + StringUtils.capitalize(skdFeltDefinisjon.getVariabelNavn()))
					.invoke(this));
		} catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage(), e);
		}
	}
}
