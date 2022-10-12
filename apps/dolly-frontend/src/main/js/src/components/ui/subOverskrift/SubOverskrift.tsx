import React from 'react'
import Icon from '~/components/ui/icon/Icon'
import './SubOverskrift.less'

type Props = {
	iconKind?: string
	label?: string
	isWarning?: boolean
}

export default function SubOverskrift({ iconKind, label, isWarning = false }: Props) {
	if (!label) {
		return null
	}
	return (
		<div className={`sub-overskrift${isWarning ? ' warning' : ''}`}>
			{iconKind && <Icon size={18} kind={iconKind} />}
			<h3>{label}</h3>
		</div>
	)
}
