import React from 'react'
import { useToggle } from 'react-use'
import ExpandButton from '~/components/ui/button/ExpandButton'
import { TitleValue } from '~/components/ui/titleValue/TitleValue'

import './GruppeDetaljer.less'

export default function GruppeDetaljer({ gruppe, identArray }) {
	const [isExpanded, toggleExpanded] = useToggle(false)
	const antallIBruk = identArray.map(p => p.ibruk).filter(Boolean).length

	return (
		<div className="gruppe-detaljer">
			<div className="gd-blokker">
				<TitleValue title="EIER" value={gruppe.opprettetAvNavIdent} />
				<TitleValue title="ANTALL OPPRETTEDE TESTPERSONER" value={String(identArray.length)} />
				<TitleValue title="SIST ENDRET" value={gruppe.datoEndret} />
				<TitleValue title="Antall i bruk" value={String(antallIBruk)} />
				{isExpanded && <TitleValue title="HENSIKT" value={gruppe.hensikt} />}
			</div>
			<div className="gruppe-detaljer-chevron">
				<ExpandButton onClick={toggleExpanded} expanded={isExpanded} />
			</div>
		</div>
	)
}
