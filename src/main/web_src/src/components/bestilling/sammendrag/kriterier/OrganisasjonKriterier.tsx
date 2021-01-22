import React, { useState } from 'react'
import { Enhetstre } from '~/components/enhetstre'
import { mapBestillingData } from './BestillingKriterieMapper'

export const OrganisasjonKriterier = ({ data, render }) => {
	if (!data) return null

	const [selectedId, setSelectedId] = useState(0)

	const dataCopy = JSON.parse(JSON.stringify(data))

	const addId = (data, type, nivaa, path) => {
		data.forEach((enhet, idx) => {
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

	let enheterListe = []

	const enheterFlat = enheter => {
		enheter.forEach(enhet => {
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
