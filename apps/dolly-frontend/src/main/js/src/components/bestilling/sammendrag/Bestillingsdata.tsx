import { Foedsel } from '@/components/fagsystem/pdlf/bestilling/Foedsel'
import styled from 'styled-components'
import React from 'react'
import { Alder } from '@/components/fagsystem/pdlf/bestilling/Alder'
import { Doedsfall } from '@/components/fagsystem/pdlf/bestilling/Doedsfall'
import { Statsborgerskap } from '@/components/fagsystem/pdlf/bestilling/Statsborgerskap'
import { Innvandring } from '@/components/fagsystem/pdlf/bestilling/Innvandring'
import { Utvandring } from '@/components/fagsystem/pdlf/bestilling/Utvandring'
import { Kjoenn } from '@/components/fagsystem/pdlf/bestilling/Kjoenn'
import { Navn } from '@/components/fagsystem/pdlf/bestilling/Navn'
import { Skjerming } from '@/components/fagsystem/pdlf/bestilling/Skjerming'
import { NorskBankkonto } from '@/components/fagsystem/pdlf/bestilling/NorskBankkonto'
import { UtenlandskBankkonto } from '@/components/fagsystem/pdlf/bestilling/UtenlandskBankkonto'
import { Telefonnummer } from '@/components/fagsystem/pdlf/bestilling/Telefonnummer'
import { Vergemaal } from '@/components/fagsystem/pdlf/bestilling/Vergemaal'
import { Fullmakt } from '@/components/fagsystem/pdlf/bestilling/Fullmakt'
import { Sikkerhetstiltak } from '@/components/fagsystem/pdlf/bestilling/Sikkerhetstiltak'
import { TilrettelagtKommunikasjon } from '@/components/fagsystem/pdlf/bestilling/TilrettelagtKommunikasjon'
import { Bostedsadresse } from '@/components/fagsystem/pdlf/bestilling/Bostedsadresse'
import { Oppholdsadresse } from '@/components/fagsystem/pdlf/bestilling/Oppholdsadresse'
import { Kontaktadresse } from '@/components/fagsystem/pdlf/bestilling/Kontaktadresse'
import { Adressebeskyttelse } from '@/components/fagsystem/pdlf/bestilling/Adressebeskyttelse'
import { Sivilstand } from '@/components/fagsystem/pdlf/bestilling/Sivilstand'
import { ForelderBarnRelasjon } from '@/components/fagsystem/pdlf/bestilling/ForelderBarnRelasjon'
import { Foreldreansvar } from '@/components/fagsystem/pdlf/bestilling/Foreldreansvar'
import { DoedfoedtBarn } from '@/components/fagsystem/pdlf/bestilling/DoedfoedtBarn'
import { FalskIdentitet } from '@/components/fagsystem/pdlf/bestilling/FalskIdentitet'
import { UtenlandskIdent } from '@/components/fagsystem/pdlf/bestilling/UtenlandskIdent'
import { NyIdentitet } from '@/components/fagsystem/pdlf/bestilling/NyIdentitet'
import { KontaktinformasjonForDoedsbo } from '@/components/fagsystem/pdlf/bestilling/KontaktinformasjonForDoedsbo'
import { Arbeidsforhold } from '@/components/fagsystem/aareg/bestilling/Arbeidsforhold'
import { SigrunstubPensjonsgivende } from '@/components/fagsystem/sigrunstubPensjonsgivende/bestilling/SigrunstubPensjonsgivende'
import { Inntektstub } from '@/components/fagsystem/inntektstub/bestilling/Inntektstub'
import { Inntektsmelding } from '@/components/fagsystem/inntektsmelding/bestilling/Inntektsmelding'
import { Skattekort } from '@/components/fagsystem/skattekort/bestilling/Skattekort'
import { Arbeidsplassen } from '@/components/fagsystem/arbeidsplassen/bestilling/Arbeidsplassen'
import { PensjonsgivendeInntekt } from '@/components/fagsystem/pensjon/bestilling/PensjonsgivendeInntekt'
import { GenerertPensjonsgivendeInntekt } from '@/components/fagsystem/pensjon/bestilling/GenerertPensjonsgivendeInntekt'
import { Pensjonsavtale } from '@/components/fagsystem/pensjonsavtale/bestilling/Pensjonsavtale'
import { Tjenestepensjon } from '@/components/fagsystem/tjenestepensjon/bestilling/Tjenestepensjon'
import { Alderspensjon } from '@/components/fagsystem/alderspensjon/bestilling/Alderspensjon'
import { Uforetrygd } from '@/components/fagsystem/uforetrygd/bestilling/Uforetrygd'
import { AfpOffentlig } from '@/components/fagsystem/afpOffentlig/bestilling/AfpOffentlig'
import { Arena } from '@/components/fagsystem/arena/bestilling/Arena'
import { Sykemelding } from '@/components/fagsystem/sykdom/bestilling/Sykemelding'
import { Yrkesskader } from '@/components/fagsystem/yrkesskader/bestilling/Yrkesskader'
import { Brregstub } from '@/components/fagsystem/brregstub/bestilling/Brregstub'
import { Inst } from '@/components/fagsystem/inst/bestilling/Inst'
import { Krrstub } from '@/components/fagsystem/krrstub/bestilling/Krrstub'
import { Medl } from '@/components/fagsystem/medl/bestilling/Medl'
import { Udistub } from '@/components/fagsystem/udistub/bestilling/Udistub'
import { Dokarkiv } from '@/components/fagsystem/dokarkiv/bestilling/Dokarkiv'
import { Histark } from '@/components/fagsystem/histark/bestilling/Histark'
import { Foedested } from '@/components/fagsystem/pdlf/bestilling/Foedested'
import { Foedselsdato } from '@/components/fagsystem/pdlf/bestilling/Foedselsdato'
import { Arbeidssoekerregisteret } from '@/components/fagsystem/arbeidssoekerregisteret/bestilling/Arbeidssoekerregisteret'
import { DeltBosted } from '@/components/fagsystem/pdlf/bestilling/DeltBosted'
import { NavAnsatt } from '@/components/fagsystem/nom/bestilling/NavAnsatt'
import { AlderspensjonNyUttaksgrad } from '@/components/fagsystem/alderspensjon/bestilling/AlderspensjonNyUttaksgrad'
import { SigrunstubSummertSkattegrunnlag } from '@/components/fagsystem/sigrunstubSummertSkattegrunnlag/bestilling/SigrunstubSummertSkattegrunnlag'
import './Bestillingsdata.less'

export const BestillingTitle = styled.h4`
	margin: 5px 0 15px 0;
`

export const BestillingData = styled.div`
	display: flex;
	flex-wrap: wrap;
	margin-bottom: 10px;
	//padding-left: 10px;
	//border-left: 2px solid #eee;

	&& {
		.title-value {
			margin-bottom: 15px;
		}
	}
`

const StyledText = styled.p`
	margin: 5px 0;
`

export const EmptyObject = () => <StyledText>Ingen verdier satt</StyledText>

export const Bestillingsdata = ({ bestilling }: any) => {
	console.log('bestilling: ', bestilling) //TODO - SLETT MEG
	// const windowHeight = window.innerHeight
	// console.log('windowHeight: ', windowHeight) //TODO - SLETT MEG

	return (
		<div className="bestilling-data">
			<Alder opprettNyPerson={bestilling.pdldata?.opprettNyPerson} />
			<Foedested foedestedListe={bestilling.pdldata?.person?.foedested} />
			<Foedselsdato foedselsdatoListe={bestilling.pdldata?.person?.foedselsdato} />
			<Foedsel foedselListe={bestilling.pdldata?.person?.foedsel} />
			<Doedsfall doedsfallListe={bestilling.pdldata?.person?.doedsfall} />
			<Statsborgerskap statsborgerskapListe={bestilling.pdldata?.person?.statsborgerskap} />
			<Innvandring innvandringListe={bestilling.pdldata?.person?.innflytting} />
			<Utvandring utvandringListe={bestilling.pdldata?.person?.utflytting} />
			<Kjoenn kjoennListe={bestilling.pdldata?.person?.kjoenn} />
			<Navn navnListe={bestilling.pdldata?.person?.navn} />
			<Telefonnummer telefonnummerListe={bestilling.pdldata?.person?.telefonnummer} />
			<NorskBankkonto norskBankkonto={bestilling.bankkonto?.norskBankkonto} />
			<UtenlandskBankkonto utenlandskBankkonto={bestilling.bankkonto?.utenlandskBankkonto} />
			<Vergemaal vergemaalListe={bestilling.pdldata?.person?.vergemaal} />
			<Fullmakt fullmaktListe={bestilling.fullmakt || bestilling.pdldata?.person?.fullmakt} />
			<Sikkerhetstiltak sikkerhetstiltakListe={bestilling.pdldata?.person?.sikkerhetstiltak} />
			<TilrettelagtKommunikasjon
				tilrettelagtKommunikasjonListe={bestilling.pdldata?.person?.tilrettelagtKommunikasjon}
			/>
			<Bostedsadresse bostedsadresseListe={bestilling.pdldata?.person?.bostedsadresse} />
			<Oppholdsadresse oppholdsadresseListe={bestilling.pdldata?.person?.oppholdsadresse} />
			<Kontaktadresse kontaktadresseListe={bestilling.pdldata?.person?.kontaktadresse} />
			<DeltBosted deltBosted={bestilling.pdldata?.person?.deltBosted?.[0]} />
			<Adressebeskyttelse
				adressebeskyttelseListe={bestilling.pdldata?.person?.adressebeskyttelse}
			/>
			<Sivilstand sivilstandListe={bestilling.pdldata?.person?.sivilstand} />
			<ForelderBarnRelasjon forelderBarnListe={bestilling.pdldata?.person?.forelderBarnRelasjon} />
			<Foreldreansvar foreldreansvarListe={bestilling.pdldata?.person?.foreldreansvar} />
			<DoedfoedtBarn doedfoedtBarnListe={bestilling.pdldata?.person?.doedfoedtBarn} />
			<FalskIdentitet falskIdentitetListe={bestilling.pdldata?.person?.falskIdentitet} />
			<UtenlandskIdent
				utenlandskIdentListe={bestilling.pdldata?.person?.utenlandskIdentifikasjonsnummer}
			/>
			<NyIdentitet nyIdentitetListe={bestilling.pdldata?.person?.nyident} />
			<NavAnsatt nomdata={bestilling.nomdata} />
			<Skjerming skjerming={bestilling.skjerming} />
			<KontaktinformasjonForDoedsbo
				kontaktinformasjonForDoedsboListe={bestilling.pdldata?.person?.kontaktinformasjonForDoedsbo}
			/>
			<Arbeidsforhold arbeidsforholdListe={bestilling.aareg} />
			<SigrunstubPensjonsgivende
				sigrunstubPensjonsgivendeListe={bestilling.sigrunstubPensjonsgivende}
			/>
			<SigrunstubSummertSkattegrunnlag
				summertSkattegrunnlagListe={bestilling.sigrunstubSummertSkattegrunnlag}
			/>
			<Inntektstub inntektstub={bestilling.inntektstub} />
			<Inntektsmelding inntektsmelding={bestilling.inntektsmelding} />
			<Skattekort skattekort={bestilling.skattekort} />
			<Arbeidssoekerregisteret arbeidssoekerregisteret={bestilling.arbeidssoekerregisteret} />
			<Arbeidsplassen arbeidsplassenCV={bestilling.arbeidsplassenCV} />
			<PensjonsgivendeInntekt pensjon={bestilling.pensjonforvalter?.inntekt} />
			<GenerertPensjonsgivendeInntekt
				pensjon={bestilling.pensjonforvalter?.generertInntekt?.generer}
			/>
			<Pensjonsavtale pensjon={bestilling.pensjonforvalter?.pensjonsavtale} />
			<Tjenestepensjon pensjon={bestilling.pensjonforvalter?.tp} />
			<Alderspensjon pensjon={bestilling.pensjonforvalter?.alderspensjon} />
			<AlderspensjonNyUttaksgrad apNy={bestilling.pensjonforvalter?.alderspensjonNyUtaksgrad} />
			<Uforetrygd pensjon={bestilling.pensjonforvalter?.uforetrygd} />
			<AfpOffentlig pensjon={bestilling.pensjonforvalter?.afpOffentlig} />
			<Arena arbeidsytelse={bestilling.arenaforvalter} />
			<Sykemelding sykemelding={bestilling.sykemelding} />
			<Yrkesskader yrkesskader={bestilling.yrkesskader} />
			<Brregstub brregstub={bestilling.brregstub} />
			<Inst inst={bestilling.instdata} />
			<Krrstub krrstub={bestilling.krrstub} />
			<Medl medl={bestilling.medl} />
			<Udistub udistub={bestilling.udistub} />
			<Dokarkiv dokarkivListe={bestilling.dokarkiv} />
			<Histark histark={bestilling.histark} />
		</div>
	)
}
