import * as React from 'react'
import './Enhetstre.less'
import { OrgTree } from '~/components/enhetstre/OrgTree'

type NodeProps<T> = {
	enhet: OrgTree<T>
	hasChildren: boolean
	isSelected: boolean
	onNodeClick: (id: string) => void
}

export function Node<T>(props: NodeProps<T>) {
	const className = props.hasChildren
		? props.isSelected
			? 'rectangle-corner-selected'
			: 'rectangle-corner'
		: props.isSelected
		? 'rectangle-selected'
		: 'rectangle'

	return (
		<div onClick={() => props.onNodeClick(props.enhet.getId())} className={className}>
			{props.enhet.getName()}
		</div>
	)
}
