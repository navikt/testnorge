import * as React from 'react'
import './Enhetstre.less'
import { Enhet } from '~/components/enhetstre/types'

type NodeProps = {
	enhet: Enhet
	hasChildren: boolean
	isSelected: boolean
	onNodeClick: Function
}

export const Node = (props: NodeProps) => {
	if (props.hasChildren) {
		return (
			<div
				onClick={() => props.onNodeClick(props.enhet.id)}
				className={props.isSelected ? 'rectangle-corner-selected' : 'rectangle-corner'}
			>
				{props.enhet.organisasjonsnavn}
			</div>
		)
	} else {
		return (
			<div
				onClick={() => props.onNodeClick(props.enhet.id)}
				className={props.isSelected ? 'rectangle-selected' : 'rectangle'}
			>
				{props.enhet.organisasjonsnavn}
			</div>
		)
	}
}
