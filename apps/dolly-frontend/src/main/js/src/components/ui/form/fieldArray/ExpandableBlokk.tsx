import React, { useState } from 'react'
import cn from 'classnames'
// @ts-ignore
import ExpandButton from '@/components/ui/button/ExpandButton/ExpandButton'
import { TestComponentSelectors } from '#/mocks/Selectors'

type NumberingProps = {
	idx: number
}

interface ExpandableBlokkProps<T> {
	idx: number
	children: React.ReactNode
	data: T
	getHeader: (data: T) => string
	whiteBackground?: boolean
}

const Numbering = ({ idx }: NumberingProps) => <span className="dfa-blokk-number">{idx + 1}</span>

export default function ExpandableBlokk<T>({
	getHeader,
	idx,
	children,
	data,
	whiteBackground,
}: ExpandableBlokkProps<T>) {
	const [isExpanded, setIsExpanded] = useState(false)
	const className = whiteBackground ? 'dfa-blokk-white' : 'dfa-blokk'
	const headerClass = cn(`${className}_header`, { clickable: true })

	return (
		<div className={className}>
			<div className={headerClass} onClick={() => setIsExpanded(!isExpanded)}>
				<Numbering idx={idx} />
				<h2>{getHeader(data)} </h2>
				<ExpandButton
					data-testid={TestComponentSelectors.BUTTON_OPEN_EXPANDABLE}
					expanded={isExpanded}
					onClick={() => setIsExpanded(!isExpanded)}
				/>
			</div>
			{isExpanded && <div className={`${className}_content`}>{children}</div>}
		</div>
	)
}
