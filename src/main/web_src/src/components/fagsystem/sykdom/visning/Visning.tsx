import React from 'react'
import _get from 'lodash/get'
import SubOverskrift from '~/components/ui/subOverskrift/SubOverskrift'
import { SyntSykemelding } from './partials/SyntSykemelding'
import { DetaljertSykemelding } from './partials/DetaljertSykemelding'
import { Sykemelding, SykemeldingDetaljert, SykemeldingSynt } from '../SykemeldingTypes'
import { erGyldig } from '~/components/transaksjonid/GyldigeBestillinger'

export const SykemeldingVisning = ({ data }: Sykemelding) => {
	// Viser foreløpig bestillingsdata
	if (!data || data.length < 1) return null

	return (
		<div>
			<SubOverskrift label="Sykemelding" iconKind="sykdom" />
			{data.map((bestilling: SykemeldingSynt | SykemeldingDetaljert, idx: number) => {
				if (!bestilling.erGjenopprettet) {
					const syntSykemelding = _get(bestilling, 'data.sykemelding.syntSykemelding')
					const detaljertSykemelding = _get(bestilling, 'data.sykemelding.detaljertSykemelding')

					return syntSykemelding ? (
						<SyntSykemelding sykemelding={syntSykemelding} idx={idx} key={idx} />
					) : detaljertSykemelding ? (
						<DetaljertSykemelding sykemelding={detaljertSykemelding} idx={idx} key={idx} />
					) : null
				}
			})}
		</div>
	)
}

SykemeldingVisning.filterValues = (bestillinger: Array<Sykemelding>, ident: string) => {
	if (!bestillinger) return null
	return bestillinger.filter(
		(bestilling: any) =>
			bestilling.data.sykemelding && erGyldig(bestilling.id, 'SYKEMELDING', ident)
	)
}
