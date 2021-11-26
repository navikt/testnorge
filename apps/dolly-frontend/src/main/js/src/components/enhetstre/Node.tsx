import * as React from 'react'
import './Enhetstre.less'
import { OrgTree } from '~/components/enhetstre/OrgTree'

type NodeProps<T> = {
	enhet: OrgTree<T>
	hasChildren: boolean
	isSelected: boolean
	onNodeClick: (id: string) => void
}

const nodeClassName = (hasChildren: boolean, isSelected: boolean) => {
	if (hasChildren) {
		if (isSelected) {
			return 'rectangle-corner-selected'
		} else {
			return 'rectangle-corner'
		}
	} else if (isSelected) {
		return 'rectangle-selected'
	} else {
		return 'rectangle'
	}
}

export function Node<T>(props: NodeProps<T>) {
	const className = nodeClassName(props.hasChildren, props.isSelected)

	return (
		<div onClick={() => props.onNodeClick(props.enhet.getId())} className={className}>
			{props.enhet.getName()}
		</div>
	)
}
