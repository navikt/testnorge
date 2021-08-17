import React from 'react'
import { UtenlandsId } from './partials/UtenlandsId'
import { FalskIdentitet } from './partials/FalskIdentitet'
import { KontaktinformasjonForDoedsbo } from './partials/KontaktinformasjonForDoedsbo'
import { ErrorBoundary } from '~/components/ui/appError/ErrorBoundary'
import Loading from '~/components/ui/loading/Loading'

export const PdlfVisning = ({ data, loading }) => {
	if (loading) return <Loading label="Laster PDL-data" />
	if (!data || !data.data || !data.data.hentPerson) return false
	const { hentPerson } = data.data
	return (
		<ErrorBoundary>
			<div>
				<UtenlandsId data={hentPerson.utenlandskIdentifikasjonsnummer} />
				<FalskIdentitet data={hentPerson.falskIdentitet} />
				<KontaktinformasjonForDoedsbo data={hentPerson.kontaktinformasjonForDoedsbo} />
			</div>
		</ErrorBoundary>
	)
}
