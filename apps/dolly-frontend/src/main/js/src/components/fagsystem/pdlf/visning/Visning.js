import React from 'react'
import { UtenlandsId } from './partials/UtenlandsId'
import { FalskIdentitet } from './partials/FalskIdentitet'
import { KontaktinformasjonForDoedsbo } from './partials/KontaktinformasjonForDoedsbo'
import { ErrorBoundary } from '~/components/ui/appError/ErrorBoundary'
import Loading from '~/components/ui/loading/Loading'
import { Fullmakt } from '~/components/fagsystem/pdlf/visning/partials/Fullmakt'
import { UtenlandskBoadresse } from '~/components/fagsystem/pdlf/visning/partials/UtenlandskBoadresse'

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
				{dataPdlforvalter && (
					<UtenlandskBoadresse data={dataPdlforvalter[0]?.person?.bostedsadresse} />
				)}
				{dataPdlforvalter && (
					<Fullmakt
						data={dataPdlforvalter[0]?.person?.fullmakt}
						relasjoner={dataPdlforvalter[0]?.relasjoner}
					/>
				)}
				{dataPdlforvalter && (
					<UtenlandsId data={dataPdlforvalter[0]?.person?.utenlandskIdentifikasjonsnummer} />
				)}
				{dataPdlforvalter && <FalskIdentitet data={dataPdlforvalter[0]?.person?.falskIdentitet} />}
				{dataPdl && (
					<KontaktinformasjonForDoedsbo
						data={dataPdl?.data?.hentPerson?.kontaktinformasjonForDoedsbo}
					/>
				)}
			</div>
		</ErrorBoundary>
	)
}
