import * as React from 'react'
import './Enhetstre.less'
import { OrgTree } from '~/components/enhetstre/OrgTree'

type NodeProps<T> = {
	enhet: OrgTree<T>
	hasChildren: boolean
	isSelected: boolean
	onNodeClick: Function
}

export function Node<T>(props: NodeProps<T>) {
	if (props.hasChildren) {
		return (
			<div
				onClick={() => props.onNodeClick(props.enhet.getId())}
				className={props.isSelected ? 'rectangle-corner-selected' : 'rectangle-corner'}
			>
				{props.enhet.getName()}
			</div>
		)
	} else {
		return (
			<div
				onClick={() => props.onNodeClick(props.enhet.getId())}
				className={props.isSelected ? 'rectangle-selected' : 'rectangle'}
			>
				{props.enhet.getName()}
			</div>
		)
	}
}
