import React from 'react'
import Loading from '@/components/ui/loading/Loading'
import Icon from '@/components/ui/icon/Icon'
import _get from 'lodash/get'
import { BrregErFrVisning } from '@/components/fagsystem/skatteetaten/visning/BrregErFrVisning'
import { InntektVisning } from '@/components/fagsystem/skatteetaten/visning/InntektVisning'

type SkatteetatenVisningProps = {
	data: {
		tenorRelasjoner: any
	}
	loading: boolean
}

// Midlertidig visning av data fra Tenor. Skrives kanskje om naar det er behov for aa vise flere Tenor-data.
export const SkatteetatenVisning = ({ data, loading }: SkatteetatenVisningProps) => {
	if (loading) {
		return <Loading label="Laster Tenor-data ..." />
	}

	const tenorRelasjoner = data?.tenorRelasjoner
	if (!data || !tenorRelasjoner) {
		return null
	}

	const inntektListe = tenorRelasjoner.inntekt
	const harDagligLederRolle = _get(tenorRelasjoner, 'brreg-er-fr')?.length > 0

	if (!data && !harDagligLederRolle) {
		return null
	}

	return (
		<div style={{ margin: '15px 0 20px 0' }}>
			<div className="sub-overskrift" style={{ backgroundColor: '#4B797A', color: '#fff' }}>
				<Icon fontSize={'1.5rem'} kind="tenor" />
				<h3>Data fra Tenor</h3>
			</div>
			<InntektVisning inntektListe={inntektListe} />
			<BrregErFrVisning harDagligLederRolle={harDagligLederRolle} />
		</div>
	)
}
