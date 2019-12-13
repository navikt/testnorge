import React from 'react'
import { TitleValue } from '~/components/ui/titleValue/TitleValue'
import Formatters from '~/utils/DataFormatter'

export const Barn = ({ data, bestilling }) => {
	if (!data) return false

	// Finn ut hva vi skal returnere for borHos og barnType :-)
	const adresse = borHos => {
		if (borHos === 'MEG') return borHos
		if (borHos === 'DEG') return `Partner ${bestilling.partnerNr}`
		if (borHos === 'BEGGE') return borHos
	}
	const foreldre = barnType => {
		if (barnType === 'MITT') return barnType
		if (barnType === 'DITT') return `Partner ${bestilling.partnerNr}`
		if (barnType === 'FELLES') return barnType
	}
	return (
		<div className="person-visning_content">
			<TitleValue title={data.identtype} value={data.ident} />
			<TitleValue title="Fornavn" value={data.fornavn} />
			<TitleValue title="Mellomnavn" value={data.mellomnavn} />
			<TitleValue title="Etternavn" value={data.etternavn} />
			<TitleValue title="Kjønn" value={Formatters.kjonnToString(data.kjonn)} />
			<TitleValue title="Alder" value={Formatters.formatAlder(data.alder, data.doedsdato)} />
			<TitleValue title="Bor hos" value={adresse(bestilling.borHos)} />
			<TitleValue title="Barn av" value={foreldre(bestilling.barnType)} />
		</div>
	)
}
