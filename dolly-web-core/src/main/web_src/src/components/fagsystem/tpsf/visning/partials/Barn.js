import React from 'react'
import { TitleValue } from '~/components/ui/titleValue/TitleValue'
import Formatters from '~/utils/DataFormatter'

export const Barn = ({ data, bestilling }) => {
	if (!data) return false

	// Finn ut hva vi skal returnere for borHos og barnType :-)
	const adresse = borHos => {
		if (borHos === 'MEG') return 'Hovedperson'
		if (borHos === 'DEG') return `Partner ${bestilling.partnerNr ? bestilling.partnerNr : ''}`
		if (borHos === 'OSS')
			return `Hovedperson og partner ${bestilling.partnerNr ? bestilling.partnerNr : ''}`
	}
	const foreldre = barnType => {
		if (barnType === 'MITT') return 'Hovedperson'
		if (barnType === 'DITT') return `Partner ${bestilling.partnerNr ? bestilling.partnerNr : ''}`
		if (barnType === 'FELLES')
			return `Hovedperson og partner ${bestilling.partnerNr ? bestilling.partnerNr : ''}`
	}
	return (
		<div className="person-visning_content">
			<TitleValue title={data.identtype} value={data.ident} />
			<TitleValue title="Fornavn" value={data.fornavn} />
			<TitleValue title="Mellomnavn" value={data.mellomnavn} />
			<TitleValue title="Etternavn" value={data.etternavn} />
			<TitleValue title="Kjønn" value={Formatters.kjonn(data.kjonn, data.alder)} />
			<TitleValue title="Alder" value={Formatters.formatAlder(data.alder, data.doedsdato)} />
			{/* Midlertidig!! */}
			{/* <TitleValue title="Foreldre" value={foreldre(bestilling.barnType)} />
			<TitleValue title="Bor hos" value={adresse(bestilling.borHos)} /> */}
		</div>
	)
}
