import React, { useState } from 'react'
import { Enhetstre } from '~/components/enhetstre'
import { Detaljer } from './Detaljer'
import SubOverskrift from '~/components/ui/subOverskrift/SubOverskrift'
import { EnhetData } from '../types'

type OrganisasjonVisning = {
	data: EnhetData
}

export const OrganisasjonVisning = ({ data }: OrganisasjonVisning) => {
	if (!data) return null

	const [selectedId, setSelectedId] = useState(data.id)

	let enheterListe: Array<EnhetData> = []

	const enheterFlat = (enheter: Array<EnhetData>) => {
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
