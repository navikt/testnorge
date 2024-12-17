import { Foedsel } from '@/components/fagsystem/pdlf/bestilling/partials/Foedsel'
import styled from 'styled-components'
import React from 'react'
import { Alder } from '@/components/fagsystem/pdlf/bestilling/partials/Alder'
import { Doedsfall } from '@/components/fagsystem/pdlf/bestilling/partials/Doedsfall'
import { Statsborgerskap } from '@/components/fagsystem/pdlf/bestilling/partials/Statsborgerskap'
import { Innvandring } from '@/components/fagsystem/pdlf/bestilling/partials/Innvandring'
import { Utvandring } from '@/components/fagsystem/pdlf/bestilling/partials/Utvandring'
import { Kjoenn } from '@/components/fagsystem/pdlf/bestilling/partials/Kjoenn'
import { Navn } from '@/components/fagsystem/pdlf/bestilling/partials/Navn'
import { Spraak } from '@/components/fagsystem/pdlf/bestilling/partials/Spraak'
import { Skjerming } from '@/components/fagsystem/pdlf/bestilling/partials/Skjerming'
import { NorskBankkonto } from '@/components/fagsystem/pdlf/bestilling/partials/NorskBankkonto'
import { UtenlandskBankkonto } from '@/components/fagsystem/pdlf/bestilling/partials/UtenlandskBankkonto'
import { Telefonnummer } from '@/components/fagsystem/pdlf/bestilling/partials/Telefonnummer'
import { Vergemaal } from '@/components/fagsystem/pdlf/bestilling/partials/Vergemaal'
import { Fullmakt } from '@/components/fagsystem/pdlf/bestilling/partials/Fullmakt'
import { Sikkerhetstiltak } from '@/components/fagsystem/pdlf/bestilling/partials/Sikkerhetstiltak'
import { TilrettelagtKommunikasjon } from '@/components/fagsystem/pdlf/bestilling/partials/TilrettelagtKommunikasjon'
import { Bostedsadresse } from '@/components/fagsystem/pdlf/bestilling/partials/Bostedsadresse'
import { Oppholdsadresse } from '@/components/fagsystem/pdlf/bestilling/partials/Oppholdsadresse'
import { Kontaktadresse } from '@/components/fagsystem/pdlf/bestilling/partials/Kontaktadresse'
import { Adressebeskyttelse } from '@/components/fagsystem/pdlf/bestilling/partials/Adressebeskyttelse'
import { Sivilstand } from '@/components/fagsystem/pdlf/bestilling/partials/Sivilstand'
import { ForelderBarnRelasjon } from '@/components/fagsystem/pdlf/bestilling/partials/ForelderBarnRelasjon'
import { Foreldreansvar } from '@/components/fagsystem/pdlf/bestilling/partials/Foreldreansvar'
import { DoedfoedtBarn } from '@/components/fagsystem/pdlf/bestilling/partials/DoedfoedtBarn'
import { FalskIdentitet } from '@/components/fagsystem/pdlf/bestilling/partials/FalskIdentitet'
import { UtenlandskIdent } from '@/components/fagsystem/pdlf/bestilling/partials/UtenlandskIdent'
import { NyIdentitet } from '@/components/fagsystem/pdlf/bestilling/partials/NyIdentitet'
import { KontaktinformasjonForDoedsbo } from '@/components/fagsystem/pdlf/bestilling/partials/KontaktinformasjonForDoedsbo'
import { Arbeidsforhold } from '@/components/fagsystem/aareg/bestilling/Arbeidsforhold'
import { Sigrunstub } from '@/components/fagsystem/sigrunstub/bestilling/Sigrunstub'
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

export const BestillingTitle = styled.h4`
	margin: 5px 0 15px 0;
`

export const BestillingData = styled.div`
	display: flex;
	flex-wrap: wrap;
	margin-bottom: 10px;

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
	return (
		<>
			<Alder opprettNyPerson={bestilling.pdldata?.opprettNyPerson} />
			<Foedsel foedselListe={bestilling.pdldata?.person?.foedsel} />
			<Doedsfall doedsfallListe={bestilling.pdldata?.person?.doedsfall} />
			<Statsborgerskap statsborgerskapListe={bestilling.pdldata?.person?.statsborgerskap} />
			<Innvandring innvandringListe={bestilling.pdldata?.person?.innflytting} />
			<Utvandring utvandringListe={bestilling.pdldata?.person?.utflytting} />
			<Kjoenn kjoennListe={bestilling.pdldata?.person?.kjoenn} />
			<Navn navnListe={bestilling.pdldata?.person?.navn} />
			<Spraak spraakKode={bestilling.tpsMessaging?.spraakKode} />
			<Skjerming
				skjerming={
					bestilling.tpsMessaging?.egenAnsattDatoFom
						? {
								egenAnsattDatoFom: bestilling.tpsMessaging?.egenAnsattDatoFom,
								egenAnsattDatoTom: bestilling.tpsMessaging?.egenAnsattDatoTom,
							}
						: bestilling.skjerming
				}
			/>
			<NorskBankkonto norskBankkonto={bestilling.bankkonto?.norskBankkonto} />
			<UtenlandskBankkonto utenlandskBankkonto={bestilling.bankkonto?.utenlandskBankkonto} />
			<Telefonnummer telefonnummerListe={bestilling.pdldata?.person?.telefonnummer} />
			<Vergemaal vergemaalListe={bestilling.pdldata?.person?.vergemaal} />
			<Fullmakt fullmaktListe={bestilling.pdldata?.person?.fullmakt} />
			<Sikkerhetstiltak sikkerhetstiltakListe={bestilling.pdldata?.person?.sikkerhetstiltak} />
			<TilrettelagtKommunikasjon
				tilrettelagtKommunikasjonListe={bestilling.pdldata?.person?.tilrettelagtKommunikasjon}
			/>
			<Bostedsadresse bostedsadresseListe={bestilling.pdldata?.person?.bostedsadresse} />
			<Oppholdsadresse oppholdsadresseListe={bestilling.pdldata?.person?.oppholdsadresse} />
			<Kontaktadresse kontaktadresseListe={bestilling.pdldata?.person?.kontaktadresse} />
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
			<KontaktinformasjonForDoedsbo
				kontaktinformasjonForDoedsboListe={bestilling.pdldata?.person?.kontaktinformasjonForDoedsbo}
			/>
			<Arbeidsforhold arbeidsforholdListe={bestilling.aareg} />
			<Sigrunstub sigrunstubListe={bestilling.sigrunstub} />
			<SigrunstubPensjonsgivende
				sigrunstubPensjonsgivendeListe={bestilling.sigrunstubPensjonsgivende}
			/>
			<Inntektstub inntektstub={bestilling.inntektstub} />
			<Inntektsmelding inntektsmelding={bestilling.inntektsmelding} />
			<Skattekort skattekort={bestilling.skattekort} />
			<Arbeidsplassen arbeidsplassenCV={bestilling.arbeidsplassenCV} />
			<PensjonsgivendeInntekt pensjon={bestilling.pensjonforvalter?.inntekt} />
			<GenerertPensjonsgivendeInntekt
				pensjon={bestilling.pensjonforvalter?.generertInntekt?.generer}
			/>
			<Pensjonsavtale pensjon={bestilling.pensjonforvalter?.pensjonsavtale} />
			<Tjenestepensjon pensjon={bestilling.pensjonforvalter?.tp} />
			<Alderspensjon pensjon={bestilling.pensjonforvalter?.alderspensjon} />
			<Uforetrygd pensjon={bestilling.pensjonforvalter?.uforetrygd} />
			<AfpOffentlig pensjon={bestilling.pensjonforvalter?.afpOffentlig} />
			<Arena arbeidsytelse={bestilling.arenaforvalter} />
			<Sykemelding sykemelding={bestilling.sykemelding} />
			<Yrkesskader yrkesskader={bestilling.yrkesskader} />
			<Brregstub brregstub={bestilling.brregstub} />
			<Inst inst={bestilling.instdata} />
			<Krrstub krrstub={bestilling.krrstub} />
		</>
	)
}
