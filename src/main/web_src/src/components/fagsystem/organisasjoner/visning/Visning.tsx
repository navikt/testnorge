import React, { useState } from 'react'
import { Enhetstre } from '~/components/enhetstre'
import { Detaljer } from './Detaljer'
import SubOverskrift from '~/components/ui/subOverskrift/SubOverskrift'

export const OrganisasjonVisning = ({ data }) => {
	if (!data) return null

	const [selectedId, setSelectedId] = useState(data.id)

	let enheterListe = []

	const enheterFlat = enheter => {
		enheter.forEach(enhet => {
			enheterListe.push(enhet)
			if (enhet.underenheter && enhet.underenheter.length > 0) {
				enheterFlat(enhet.underenheter)
			}
		})
	}

	enheterFlat(Array.of(data))

	return (
		<div>
			<SubOverskrift label="Organisasjonsoversikt" iconKind="organisasjon" />
			<Enhetstre enheter={Array.of(data)} selectedEnhet={selectedId} onNodeClick={setSelectedId} />
			<Detaljer data={enheterListe.filter(enhet => enhet.id === selectedId)} />
		</div>
	)
}
