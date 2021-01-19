import React from 'react'
import { UtenlandsId } from './partials/UtenlandsId'
import { FalskIdentitet } from './partials/FalskIdentitet'
import { KontaktinformasjonForDoedsbo } from './partials/KontaktinformasjonForDoedsbo'
import { ErrorBoundary } from '~/components/ui/appError/ErrorBoundary'

export const PdlfVisning = ({ data, loading }) => {
	if (!data || !data.data || !data.data.hentPerson) return false
	const { hentPerson } = data.data
	return (
		<ErrorBoundary>
			<div>
				<UtenlandsId data={hentPerson.utenlandskIdentifikasjonsnummer} loading={loading} />
				<FalskIdentitet data={hentPerson.falskIdentitet} loading={loading} />
				<KontaktinformasjonForDoedsbo
					data={hentPerson.kontaktinformasjonForDoedsbo}
					loading={loading}
				/>
			</div>
		</ErrorBoundary>
	)
}
