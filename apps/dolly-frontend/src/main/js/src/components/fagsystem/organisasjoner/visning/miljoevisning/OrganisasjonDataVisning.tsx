import React, { useState } from 'react'
import '~/pages/gruppe/PersonVisning/PersonMiljoeinfo/DataVisning.less'
// @ts-ignore
import Tooltip from 'rc-tooltip'
import 'rc-tooltip/assets/bootstrap_white.css'
import { Detaljer } from '~/components/fagsystem/organisasjoner/visning/Detaljer'
import { Enhetstre, OrgTree } from '~/components/enhetstre'
import SubOverskrift from '~/components/ui/subOverskrift/SubOverskrift'

// @ts-ignore
export const OrganisasjonDataVisning = ({ data }) => {
	if (!data) return null

	const organisasjonListe = Object.entries(data).sort()

	// @ts-ignore
	const getOrganisasjonInfo = organisasjon => {
		const [selectedId, setSelectedId] = useState('0')
		const orgTree = new OrgTree(organisasjon[1], '0')
		return (
			<div className="boks">
				<SubOverskrift label="Organisasjonsoversikt" iconKind="organisasjon" />
				<Enhetstre enheter={[orgTree]} selectedEnhet={selectedId} onNodeClick={setSelectedId} />
				<Detaljer data={[orgTree.find(selectedId)]} />
			</div>
		)
	}

	return (
		<div className="flexbox--flex-wrap">
			{organisasjonListe.map((organisasjoner, idx) => {
				const miljoe = organisasjoner[0]
				return (
					<Tooltip
						overlay={getOrganisasjonInfo(organisasjoner)}
						placement="top"
						align={{
							offset: ['0', '-10']
						}}
						mouseEnterDelay={0.1}
						mouseLeaveDelay={0.1}
						arrowContent={<div className="rc-tooltip-arrow-inner"></div>}
						key={idx}
						overlayStyle={{ opacity: 1 }}
					>
						<div className="miljoe-knapp">{miljoe.toUpperCase()}</div>
					</Tooltip>
				)
			})}
		</div>
	)
}
