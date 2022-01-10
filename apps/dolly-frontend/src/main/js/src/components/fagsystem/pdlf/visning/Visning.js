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

export const PdlfVisning = ({ data, loading }) => {
	if (loading) return <Loading label="Laster PDL-data" />
	if (!data || data.length < 1 || !data[0]?.person) return null

	const {
		telefonnummer,
		tilrettelagtKommunikasjon,
		bostedsadresse,
		oppholdsadresse,
		kontaktadresse,
		adressebeskyttelse,
		fullmakt,
		relasjoner,
		utenlandskIdentifikasjonsnummer,
		falskIdentitet,
		kontaktinformasjonForDoedsbo,
	} = data[0].person

	return (
		<ErrorBoundary>
			<div>
				<Telefonnummer data={telefonnummer} />
				<TilrettelagtKommunikasjon data={tilrettelagtKommunikasjon} />
				<Boadresse data={bostedsadresse} />
				<Oppholdsadresse data={oppholdsadresse} />
				<Kontaktadresse data={kontaktadresse} />
				<Adressebeskyttelse data={adressebeskyttelse} />
				<Fullmakt data={fullmakt} relasjoner={relasjoner} />
				<UtenlandsId data={utenlandskIdentifikasjonsnummer} />
				<FalskIdentitet data={falskIdentitet} />
				<KontaktinformasjonForDoedsbo data={kontaktinformasjonForDoedsbo} />
			</div>
		</ErrorBoundary>
	)
}
