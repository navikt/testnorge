import React from 'react'
import { UtenlandsId } from './partials/UtenlandsId'
import { FalskIdentitet } from './partials/FalskIdentitet'
import { KontaktinformasjonForDoedsbo } from './partials/KontaktinformasjonForDoedsbo'
import { ErrorBoundary } from '~/components/ui/appError/ErrorBoundary'
import Loading from '~/components/ui/loading/Loading'
import { Fullmakt } from '~/components/fagsystem/pdlf/visning/partials/Fullmakt'
import { Telefonnummer } from '~/components/fagsystem/pdlf/visning/partials/Telefonnummer'
import { TilrettelagtKommunikasjon } from '~/components/fagsystem/pdlf/visning/partials/TilrettelagtKommunikasjon'
import { Boadresse } from '~/components/fagsystem/pdlf/visning/partials/Boadresse'
import { Oppholdsadresse } from '~/components/fagsystem/pdlf/visning/partials/Oppholdsadresse'
import { Kontaktadresse } from '~/components/fagsystem/pdlf/visning/partials/Kontaktadresse'
import { Adressebeskyttelse } from '~/components/fagsystem/pdlf/visning/partials/Adressebeskyttelse'
import { Sivilstand } from '~/components/fagsystem/pdlf/visning/partials/Sivilstand'

export const PdlfVisning = ({ data, loading }) => {
	if (loading) return <Loading label="Laster PDL-data" />
	if (!data) return null

	const {
		telefonnummer,
		tilrettelagtKommunikasjon,
		bostedsadresse,
		oppholdsadresse,
		kontaktadresse,
		adressebeskyttelse,
		sivilstand,
		fullmakt,
		utenlandskIdentifikasjonsnummer,
		falskIdentitet,
		kontaktinformasjonForDoedsbo,
	} = data.person

	return (
		<ErrorBoundary>
			<div>
				<Telefonnummer data={telefonnummer} />
				<TilrettelagtKommunikasjon data={tilrettelagtKommunikasjon} />
				<Boadresse data={bostedsadresse} />
				<Oppholdsadresse data={oppholdsadresse} />
				<Kontaktadresse data={kontaktadresse} />
				<Adressebeskyttelse data={adressebeskyttelse} />
				<Sivilstand data={sivilstand} relasjoner={data.relasjoner} />
				<Fullmakt data={fullmakt} relasjoner={data.relasjoner} />
				<UtenlandsId data={utenlandskIdentifikasjonsnummer} />
				<FalskIdentitet data={falskIdentitet} />
				<KontaktinformasjonForDoedsbo data={kontaktinformasjonForDoedsbo} />
			</div>
		</ErrorBoundary>
	)
}
