import React, { useState } from 'react'
import { Enhetstre } from '~/components/enhetstre'
import { mapBestillingData } from './BestillingKriterieMapper'
import { EnhetBestilling } from '~/components/fagsystem/organisasjoner/types'

type OrganisasjonKriterier = {
	data: EnhetBestilling
	render: Function
}

export const OrganisasjonKriterier = ({ data, render }: OrganisasjonKriterier) => {
	if (!data) return null

	const [selectedId, setSelectedId] = useState(0)

	const dataCopy = JSON.parse(JSON.stringify(data))

	const addId = (
		data: Array<EnhetBestilling>,
		type: string,
		nivaa: number,
		path: number | string
	) => {
		data.forEach((enhet: EnhetBestilling, idx: number) => {
			const id = nivaa === 0 ? 0 : !path ? idx + 1 : `${path}.${idx + 1}`
			enhet.id = id
			enhet.organisasjonsnavn = id !== 0 ? `${type} ${id}` : type
			if (enhet.underenheter && enhet.underenheter.length > 0) {
				addId(enhet.underenheter, 'Underenhet', nivaa + 1, id)
			}
		})
	}

	// Lager en kopi av data, hvor hver organisasjon får en unik id og navn, slik at den kan brukes i Enhetstre
	addId(Array.of(dataCopy), 'Organisasjon', 0, null)

	const enheterListe: Array<EnhetBestilling> = []

	const enheterFlat = (enheter: Array<EnhetBestilling>) => {
		enheter.forEach((enhet: EnhetBestilling) => {
			enheterListe.push(enhet)
			if (enhet.underenheter && enhet.underenheter.length > 0) {
				enheterFlat(enhet.underenheter)
			}
		})
	}

	// Array av alle organisasjoner på samme nivå, slik at data enkelt kan hentes ut fra hver av dem
	enheterFlat(Array.of(dataCopy))

	return (
		<div>
			<h4>Organisasjonsoversikt</h4>
			<Enhetstre
				enheter={Array.of(dataCopy)}
				selectedEnhet={selectedId}
				onNodeClick={setSelectedId}
			/>
			{enheterListe.length > 0 &&
				render(
					mapBestillingData({ organisasjon: enheterListe.filter(enhet => enhet.id === selectedId) })
				)}
		</div>
	)
}
