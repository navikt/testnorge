import React, { useState } from 'react'
import ExpandButton from '~/components/ui/button/ExpandButton'

const Numbering = ({ idx }) => <span className="dfa-blokk-number">{idx + 1}</span>

export default function DollyExpandableBlokk({ header, idx, children }) {
	const [isExpanded, setIsExpanded] = useState(false)

	return (
		<div className="dfa-blokk">
			<div className="dfa-blokk_header">
				<Numbering idx={idx} />
				<h2>{header}</h2>
				<ExpandButton expanded={isExpanded} onClick={() => setIsExpanded(!isExpanded)} />
			</div>
			{isExpanded && <div className="dfa-blokk_content">{children}</div>}
		</div>
	)
}
