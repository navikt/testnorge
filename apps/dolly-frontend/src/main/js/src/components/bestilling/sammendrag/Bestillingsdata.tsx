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
		</>
	)
}