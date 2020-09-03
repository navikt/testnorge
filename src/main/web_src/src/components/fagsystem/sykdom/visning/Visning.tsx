import React from 'react'
import _get from 'lodash/get'
import SubOverskrift from '~/components/ui/subOverskrift/SubOverskrift'
import { SyntSykemelding } from './partials/SyntSykemelding'
import { DetaljertSykemelding } from './partials/DetaljertSykemelding'
import { Sykemelding, SykemeldingSynt, SykemeldingDetaljert } from '../SykemeldingTypes'
import { GyldigeBestillinger } from '~/components/transaksjonid/GyldigeBestillinger'

type SykemeldingVisning = {
	data: Sykemelding
	ident: string
	bestillingId: Array<number>
}

export const SykemeldingVisning = ({ data, ident, bestillingId }: SykemeldingVisning) => {
	// Viser foreløpig bestillingsdata
	if (!data || data.length < 1) return null

	const gyldigeBestillinger = GyldigeBestillinger(data, 'SYKEMELDING', ident)

	return (
		<div>
			<SubOverskrift label="Sykemelding" iconKind="sykdom" />
			{gyldigeBestillinger.map(
				(bestilling: SykemeldingSynt | SykemeldingDetaljert, idx: number) => {
					const syntSykemelding = _get(bestilling, 'data.sykemelding.syntSykemelding')
					const detaljertSykemelding = _get(bestilling, 'data.sykemelding.detaljertSykemelding')

					if (!bestilling.erGjenopprettet) {
						return syntSykemelding ? (
							<SyntSykemelding sykemelding={syntSykemelding} idx={idx} />
						) : detaljertSykemelding ? (
							<DetaljertSykemelding sykemelding={detaljertSykemelding} idx={idx} />
						) : null
					}
				}
			)}
		</div>
	)
}

SykemeldingVisning.filterValues = (bestillinger: Array<Sykemelding>) => {
	if (!bestillinger) return null
	return bestillinger.filter((bestilling: any) => bestilling.data.sykemelding)
}
