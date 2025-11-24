import React, { useEffect, useState } from 'react'
import cn from 'classnames'
import Column from './TableColumn'
import ExpandButton from '@/components/ui/button/ExpandButton/ExpandButton'
import { useDispatch } from 'react-redux'
import { resetNavigering } from '@/ducks/finnPerson'

export default function TableRow({
	children,
	icon,
	expandComponent,
	navLink,
	expandPerson = false,
	expandBestilling = false,
}) {
	const [isExpanded, setIsExpanded] = useState(false)
	const dispatch = useDispatch()

	useEffect(() => {
		setIsExpanded(expandPerson || expandBestilling)
	}, [expandPerson, expandBestilling])

	const onRowClick = () => {
		if (isExpanded) dispatch(resetNavigering())
		if (expandComponent) return setIsExpanded(!isExpanded)

		if (navLink) return navLink()
	}

	const rowWrapperClass = cn('dot-body-row-wrapper', {
		expanded: isExpanded,
	})

	const rowClass = cn('dot-body-row', {
		clickable: Boolean(expandComponent || navLink),
	})

	return (
		<div tabIndex={0} className={rowWrapperClass}>
			<div className={rowClass} onClick={onRowClick}>
				{icon}
				<div className="dot-body-row-columns">
					{children}
					{expandComponent && (
						<Column width="10" className="dot-body-row-actioncolumn">
							<ExpandButton expanded={isExpanded} onClick={onRowClick} />
						</Column>
					)}
				</div>
			</div>
			{isExpanded && <div className="dot-expandcomponent">{expandComponent}</div>}
		</div>
	)
}
