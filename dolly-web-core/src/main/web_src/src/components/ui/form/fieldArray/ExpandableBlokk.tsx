import React, { useState } from 'react'
import cn from 'classnames'
import ExpandButton from '~/components/ui/button/ExpandButton'

type Numbering = {
	idx: number
}

interface ExpandableBlokk {
	header: string
	idx: number
	children: any
}

const Numbering = ({ idx }: Numbering) => <span className="dfa-blokk-number">{idx + 1}</span>

export default function ExpandableBlokk({ header, idx, children }: ExpandableBlokk) {
	const [isExpanded, setIsExpanded] = useState(false)

	const headerClass = cn('dfa-blokk_header', { clickable: true })

	return (
		<div className="dfa-blokk">
			<div className={headerClass} onClick={() => setIsExpanded(!isExpanded)}>
				<Numbering idx={idx} />
				<h2>{header}</h2>
				<ExpandButton expanded={isExpanded} onClick={() => setIsExpanded(!isExpanded)} />
			</div>
			{isExpanded && <div className="dfa-blokk_content">{children}</div>}
		</div>
	)
}
