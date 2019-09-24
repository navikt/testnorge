import React from 'react'
import { useToggle } from 'react-use'
import ExpandButton from '~/components/ui/button/ExpandButton'
import StaticValue from '~/components/fields/StaticValue/StaticValue'

import './GruppeDetaljer.less'

export default function GruppeDetaljer({ gruppe }) {
	const [isExpanded, toggleExpanded] = useToggle(false)

	return (
		<div className="gruppe-detaljer">
			<div className="gd-blokker">
				<StaticValue header="EIER" value={gruppe.opprettetAvNavIdent} />
				<StaticValue header="TEAM" value={gruppe.team.navn} />
				<StaticValue
					header="ANTALL OPPRETTEDE TESTPERSONER"
					value={String(gruppe.testidenter ? gruppe.testidenter.length : 0)}
				/>
				<StaticValue header="SIST ENDRET" value={gruppe.datoEndret} />
				{isExpanded && <StaticValue header="HENSIKT" value={gruppe.hensikt} />}
			</div>
			<div className="gruppe-detaljer-chevron">
				<ExpandButton onClick={toggleExpanded} expanded={isExpanded} />
			</div>
		</div>
	)
}
