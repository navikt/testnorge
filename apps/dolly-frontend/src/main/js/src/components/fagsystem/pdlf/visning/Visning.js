import React from 'react'
import { UtenlandsId } from './partials/UtenlandsId'
import { FalskIdentitet } from './partials/FalskIdentitet'
import { KontaktinformasjonForDoedsbo } from './partials/KontaktinformasjonForDoedsbo'
import { ErrorBoundary } from '~/components/ui/appError/ErrorBoundary'
import Loading from '~/components/ui/loading/Loading'

export const PdlfVisning = ({ data, loading }) => {
	if (loading) return <Loading label="Laster PDL-data" />
	// if (!data || !data.data || !data.data.hentPerson) return false
	if (!data || data.length < 1 || !data[0].person) return false
	const { person } = data[0]
	console.log('data', data)
	return (
		<ErrorBoundary>
			<div>
				<UtenlandsId data={person.utenlandskIdentifikasjonsnummer} />
				<FalskIdentitet data={person.falskIdentitet} />
				<KontaktinformasjonForDoedsbo data={person.kontaktinformasjonForDoedsbo} />
			</div>
		</ErrorBoundary>
	)
}
