import React, { useState } from 'react'
import cn from 'classnames'
import Icon from '~/components/ui/icon/Icon'
import Column from './TableColumn'
import ExpandButton from '~/components/ui/button/ExpandButton'

export default function TableRow({ children, icon, expandComponent, navLink }) {
	const [isExpanded, setIsExpanded] = useState(false)

	const onRowClick = event => {
		if (expandComponent) return setIsExpanded(!isExpanded)

		if (navLink) return navLink()
	}

	const rowClass = cn('dot-body-row', {
		expanded: isExpanded
	})

	const columnsClass = cn('dot-body-row-columns', {
		'has-icon': Boolean(icon),
		clickable: Boolean(expandComponent || navLink)
	})

	return (
		<div tabIndex={0} className={rowClass}>
			<div className={columnsClass} onClick={onRowClick}>
				{icon && icon}
				{children}
				{expandComponent && (
					<Column width="10" className="dot-body-row-actioncolumn">
						<ExpandButton expanded={isExpanded} onClick={onRowClick} />
					</Column>
				)}
			</div>
			{isExpanded && <div className="dot-body-row-expandcomponent">{expandComponent}</div>}
		</div>
	)
}
