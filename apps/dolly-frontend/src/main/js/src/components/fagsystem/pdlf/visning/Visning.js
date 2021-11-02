import React from 'react'
import { UtenlandsId } from './partials/UtenlandsId'
import { FalskIdentitet } from './partials/FalskIdentitet'
import { KontaktinformasjonForDoedsbo } from './partials/KontaktinformasjonForDoedsbo'
import { ErrorBoundary } from '~/components/ui/appError/ErrorBoundary'
import Loading from '~/components/ui/loading/Loading'

export const PdlfVisning = ({ dataPdl, dataPdlforvalter, loadingPdl, loadingPdlforvalter }) => {
	if (loadingPdl) return <Loading label="Laster PDL-data" />
	if (loadingPdlforvalter) return <Loading label="Laster PDL-forvalter-data" />
	if (
		(!dataPdl || dataPdl.length < 1 || !dataPdl.data.hentPerson) &&
		(!dataPdlforvalter || dataPdlforvalter.length < 1 || !dataPdlforvalter[0].person)
	)
		return null

	return (
		<ErrorBoundary>
			<div>
				<UtenlandsId data={dataPdl?.data?.hentPerson?.utenlandskIdentifikasjonsnummer} />
				<FalskIdentitet data={dataPdlforvalter[0]?.person?.falskIdentitet} />
				<KontaktinformasjonForDoedsbo
					data={dataPdl?.data?.hentPerson?.kontaktinformasjonForDoedsbo}
				/>
			</div>
		</ErrorBoundary>
	)
}
