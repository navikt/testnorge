import React from 'react'
import Icon from '~/components/ui/icon/Icon'
import './SubOverskrift.less'

export default function SubOverskrift({ iconKind, label }) {
	if (!label) return null
	return (
		<div className="sub-overskrift">
			{iconKind && <Icon size={18} kind={iconKind} />}
			<h3>{label}</h3>
		</div>
	)
}
