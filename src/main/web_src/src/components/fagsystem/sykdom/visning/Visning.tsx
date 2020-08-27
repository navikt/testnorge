import React from 'react'
import _get from 'lodash/get'
import SubOverskrift from '~/components/ui/subOverskrift/SubOverskrift'
import { SyntSykemelding } from './partials/SyntSykemelding'
import { DetaljertSykemelding } from './partials/DetaljertSykemelding'
import { Sykemelding, SykemeldingSynt, SykemeldingDetaljert } from '../SykemeldingTypes'

export const SykemeldingVisning = ({ data }: Sykemelding) => {
	// Viser foreløpig bestillingsdata
	if (!data || data.length < 1 || !data[0]) return null
	return (
		<div>
			<SubOverskrift label="Sykemelding" iconKind="sykdom" />
			{data.map((bestilling: SykemeldingSynt | SykemeldingDetaljert, idx: number) => {
				const syntSykemelding = _get(bestilling, 'syntSykemelding')
				const detaljertSykemelding = _get(bestilling, 'detaljertSykemelding')

				return syntSykemelding ? (
					<SyntSykemelding sykemelding={syntSykemelding} idx={idx} />
				) : detaljertSykemelding ? (
					<DetaljertSykemelding sykemelding={detaljertSykemelding} idx={idx} />
				) : null
			})}
		</div>
	)
}

SykemeldingVisning.filterValues = (bestillinger: Array<Sykemelding>) => {
	if (!bestillinger) return null
	return bestillinger.map((bestilling: any) => bestilling.sykemelding)
}
