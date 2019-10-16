import React from 'react'
import Icon from '~/components/ui/icon/Icon'
import './Header.less'

export default function SammendragHeader({ iconKind, label }) {
	if (!label) return false
	return (
		<div className="sammendrag-header">
			{iconKind && <Icon size={18} kind={iconKind} />}
			<h3>{label}</h3>
		</div>
	)
}
