import React, { useState } from 'react'
import { Enhetstre, OrgTree } from '~/components/enhetstre'
import { Detaljer } from './Detaljer'
import { TidligereBestillinger } from '~/pages/gruppe/PersonVisning/TidligereBestillinger/TidligereBestillinger'
import SubOverskrift from '~/components/ui/subOverskrift/SubOverskrift'
import { EnhetBestilling, EnhetData } from '../types'
import { BestillingSammendragModal } from '~/components/bestilling/sammendrag/SammendragModal'
import { DollyApi } from '~/service/Api'
import { OrganisasjonMiljoeinfo } from '~/components/fagsystem/organisasjoner/visning/miljoevisning/OrganisasjonMiljoeinfo'

type OrganisasjonVisning = {
	data: EnhetData
	bestillinger: Array<EnhetBestilling>
}

export const OrganisasjonVisning = ({ data, bestillinger }: OrganisasjonVisning) => {
	if (!data) return null

	const [selectedId, setSelectedId] = useState('0')

	const orgTree = new OrgTree(data.orgInfo, '0')

	const slettOrganisasjon = () => {
		DollyApi.deleteOrganisasjonOrgnummer(data.organisasjonsnummer).then(() => {
			window.location.reload()
		})
	}

	return (
		<div>
			<SubOverskrift label="Organisasjonsoversikt" iconKind="organisasjon" />
			<Enhetstre enheter={[orgTree]} selectedEnhet={selectedId} onNodeClick={setSelectedId} />
			<Detaljer data={[orgTree.find(selectedId)]} />
			{/* @ts-ignore */}
			<TidligereBestillinger ids={data.bestillingId} />
			<OrganisasjonMiljoeinfo orgnummer={data.organisasjonsnummer} />
			<div className="flexbox--align-center--justify-end info-block">
				<BestillingSammendragModal
					bestilling={bestillinger.filter(bestilling => bestilling.id === data.bestillingId[0])[0]}
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
