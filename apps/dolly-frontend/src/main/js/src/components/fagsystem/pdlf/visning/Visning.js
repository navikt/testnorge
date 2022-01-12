import React from 'react'
import { UtenlandsId } from './partials/UtenlandsId'
import { FalskIdentitet } from './partials/FalskIdentitet'
import { KontaktinformasjonForDoedsbo } from './partials/KontaktinformasjonForDoedsbo'
import { ErrorBoundary } from '~/components/ui/appError/ErrorBoundary'
import Loading from '~/components/ui/loading/Loading'
import { Fullmakt } from '~/components/fagsystem/pdlf/visning/partials/Fullmakt'
import { UtenlandskBoadresse } from '~/components/fagsystem/pdlf/visning/partials/UtenlandskBoadresse'
import { Telefonnummer } from '~/components/fagsystem/pdlf/visning/partials/Telefonnummer'
import { TilrettelagtKommunikasjon } from '~/components/fagsystem/pdlf/visning/partials/TilrettelagtKommunikasjon'

export const PdlfVisning = ({ data, loading }) => {
	if (loading) return <Loading label="Laster PDL-data" />
	if (!data || data.length < 1 || !data?.[0]?.person) return null

	return (
		<ErrorBoundary>
			<div>
				<Telefonnummer data={data[0]?.person?.telefonnummer} />
				<TilrettelagtKommunikasjon data={data[0]?.person?.tilrettelagtKommunikasjon} />
				<UtenlandskBoadresse data={data[0]?.person?.bostedsadresse} />
				<Fullmakt data={data[0]?.person?.fullmakt} relasjoner={data[0]?.relasjoner} />
				<UtenlandsId data={data[0]?.person?.utenlandskIdentifikasjonsnummer} />
				<FalskIdentitet data={data[0]?.person?.falskIdentitet} />
				<KontaktinformasjonForDoedsbo data={data[0]?.person?.kontaktinformasjonForDoedsbo} />
			</div>
		</ErrorBoundary>
	)
}
