import React, { useState } from 'react'
import { Enhetstre } from '~/components/enhetstre'
import { Detaljer } from './Detaljer'
import SubOverskrift from '~/components/ui/subOverskrift/SubOverskrift'
import { EnhetData, EnhetBestilling } from '../types'
import { BestillingSammendragModal } from '~/components/bestilling/sammendrag/SammendragModal'

type OrganisasjonVisning = {
	data: EnhetData
	bestillinger: Array<EnhetBestilling>
}

export const OrganisasjonVisning = ({ data, bestillinger }: OrganisasjonVisning) => {
	if (!data) return null

	const [selectedId, setSelectedId] = useState(data.id)

	const enheterListe: Array<EnhetData> = []

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
			<div className="flexbox--align-center--justify-end info-block">
				<BestillingSammendragModal
					bestilling={bestillinger.filter(bestilling => bestilling.id === data.bestillingId)[0]}
				/>
			</div>
		</div>
	)
}
