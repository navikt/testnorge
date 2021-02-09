import React, { useState } from 'react'
import { Enhetstre } from '~/components/enhetstre'
import { Detaljer } from './Detaljer'
import SubOverskrift from '~/components/ui/subOverskrift/SubOverskrift'
import { EnhetBestilling, EnhetData } from '../types'
import { BestillingSammendragModal } from '~/components/bestilling/sammendrag/SammendragModal'
import { SlettButton } from '~/components/ui/button/SlettButton/SlettButton'
import { DollyApi } from '~/service/Api'

type OrganisasjonVisning = {
	data: EnhetData
	bestillinger: Array<EnhetBestilling>
}

export const OrganisasjonVisning = ({ data, bestillinger }: OrganisasjonVisning) => {
	if (!data) return null

	const [selectedId, setSelectedId] = useState(data.id)

	const enheterListe: Array<EnhetData> = []

	const slettOrganisasjon = () => {
		DollyApi.deleteOrganisasjonOrgnummer(data.organisasjonsnummer).then(() => {
			window.location.reload()
		})
	}

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
				{/*TODO: Slett fungerer greit, men de finnes fortsatt deployet i miljøene etter slett. Venter*/}
				{/*til sletting av disse er implementert før sletting i Dolly tas i bruk*/}
				{/*<SlettButton action={slettOrganisasjon} loading={undefined}>*/}
				{/*	Er du sikker på at du vil slette denne organisasjonen?*/}
				{/*</SlettButton>*/}
			</div>
		</div>
	)
}
