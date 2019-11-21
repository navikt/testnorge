import React, { useState } from 'react'
import cn from 'classnames'
import Column from './TableColumn'
import ExpandButton from '~/components/ui/button/ExpandButton'
import Checkbox from '~/components/fields/Checkbox/Checkbox'
import { testpersonIBruk } from '~/ducks/gruppe'

export default function TableRow({ children, expandComponent, navLink, ident }) {
	const [isExpanded, setIsExpanded] = useState(false)

	const onRowClick = event => {
		if (expandComponent) return setIsExpanded(!isExpanded)

		if (navLink) return navLink()

		if (ident) return testpersonIBruk(gruppeId)
	}

	const rowClass = cn('dot-body-row', {
		expanded: isExpanded
	})

	const columnsClass = cn('dot-body-row-columns', {
		clickable: Boolean(expandComponent || navLink || iBruk)
	})

	return (
		<div tabIndex={0} className={rowClass}>
			<div className={columnsClass} onClick={onRowClick}>
				{children}
				<div>
					<Checkbox label={''} id={'brukt'} onClick={() => testpersonIBruk(gruppeId)} />
				</div>
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
