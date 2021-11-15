import React, { useState } from 'react'
import cn from 'classnames'
// @ts-ignore
import ExpandButton from '~/components/ui/button/ExpandButton'

type NumberingProps = {
	idx: number
}

interface ExpandableBlokkProps<T> {
	idx: number
	children: React.ReactNode
	data: T
	getHeader: (data: T) => string
}

const Numbering = ({ idx }: NumberingProps) => <span className="dfa-blokk-number">{idx + 1}</span>

export default function ExpandableBlokk<T>({
	getHeader,
	idx,
	children,
	data,
}: ExpandableBlokkProps<T>) {
	const [isExpanded, setIsExpanded] = useState(false)
	const headerClass = cn('dfa-blokk_header', { clickable: true })

	return (
		<div className="dfa-blokk">
			<div className={headerClass} onClick={() => setIsExpanded(!isExpanded)}>
				<Numbering idx={idx} />
				<h2>{getHeader(data)} </h2>
				<ExpandButton expanded={isExpanded} onClick={() => setIsExpanded(!isExpanded)} />
			</div>
			{isExpanded && <div className="dfa-blokk_content">{children}</div>}
		</div>
	)
}
