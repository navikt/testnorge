import React, { useState } from 'react'
import { Enhetstre, OrgTree } from '@/components/enhetstre'
import { useBestillingData } from './BestillingKriterieMapper'
import { EnhetBestilling } from '@/components/fagsystem/organisasjoner/types'

interface OrganisasjonKriterierProps {
	data: EnhetBestilling
	render: (mapped: any) => React.ReactNode
}

export const OrganisasjonKriterier: React.FC<OrganisasjonKriterierProps> = ({ data, render }) => {
	const [selectedId, setSelectedId] = useState('0')
	if (!data) {
		return null
	}
	const orgTree = new OrgTree(data, '0')
	const selected = orgTree.find(selectedId)
	const mapped = useBestillingData({ organisasjon: [selected] })

	return (
		<div>
			<h4>Organisasjonsoversikt</h4>
			<Enhetstre enheter={[orgTree]} selectedEnhet={selectedId} onNodeClick={setSelectedId} />
			{render(mapped)}
		</div>
	)
}
