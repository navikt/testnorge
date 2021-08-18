import React, { useState } from 'react'
import { Enhetstre, OrgTree } from '~/components/enhetstre'
import { mapBestillingData } from './BestillingKriterieMapper'
import { EnhetBestilling } from '~/components/fagsystem/organisasjoner/types'

type OrganisasjonKriterier = {
	data: EnhetBestilling
	render: Function
}

export const OrganisasjonKriterier = ({ data, render }: OrganisasjonKriterier) => {
	if (!data) return null

	const [selectedId, setSelectedId] = useState('0')

	const orgTree = new OrgTree(data, '0')

	return (
		<div>
			<h4>Organisasjonsoversikt</h4>
			<Enhetstre enheter={[orgTree]} selectedEnhet={selectedId} onNodeClick={setSelectedId} />
			{render(mapBestillingData({ organisasjon: [orgTree.find(selectedId)] }))}
		</div>
	)
}
