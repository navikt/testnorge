import React, { useState } from 'react'
import '@/pages/gruppe/PersonVisning/PersonMiljoeinfo/DataVisning.less'
// @ts-ignore
import Tooltip from 'rc-tooltip'
import 'rc-tooltip/assets/bootstrap_white.css'
import { Detaljer } from '@/components/fagsystem/organisasjoner/visning/Detaljer'
import { Enhetstre, OrgTree } from '@/components/enhetstre'
import SubOverskrift from '@/components/ui/subOverskrift/SubOverskrift'
import DollyTooltip from '@/components/ui/button/DollyTooltip'
import { useOrganisasjonMiljoe } from '@/utils/hooks/useOrganisasjonTilgang'

// @ts-ignore
const getOrganisasjonInfo = (organisasjon, selectedId, setSelectedId) => {
	const orgTree = new OrgTree(organisasjon[1], '0')
	return (
		<div className="boks">
			<SubOverskrift label="Organisasjonsoversikt" iconKind="organisasjon" />
			<Enhetstre enheter={[orgTree]} selectedEnhet={selectedId} onNodeClick={setSelectedId} />
			<Detaljer data={[orgTree.find(selectedId)]} />
		</div>
	)
}

// @ts-ignore
export const OrganisasjonDataVisning = ({ data }) => {
	const [selectedId, setSelectedId] = useState('0')

	const { organisasjonMiljoe } = useOrganisasjonMiljoe()
	const tilgjengeligMiljoe = organisasjonMiljoe?.miljoe

	if (!data) {
		return null
	}

	const organisasjonListe = Object.entries(data).sort()

	return (
		<div className="flexbox--flex-wrap">
			{organisasjonListe.map((organisasjoner, idx) => {
				const miljoe = organisasjoner[0]
				if (tilgjengeligMiljoe && tilgjengeligMiljoe !== miljoe) return null
				return (
					<DollyTooltip
						useExternalTooltip={true}
						content={getOrganisasjonInfo(organisasjoner, selectedId, setSelectedId)}
						align={{
							offset: [0, -10],
						}}
						key={idx}
						overlayStyle={{ opacity: 1 }}
					>
						<div className="miljoe-knapp">{miljoe.toUpperCase()}</div>
					</DollyTooltip>
				)
			})}
		</div>
	)
}
