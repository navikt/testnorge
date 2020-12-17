import * as React from 'react'
import { Node } from './Node'
import { Enhet } from './types'
import './Enhetstre.less'

type EnhetstreProps = {
	enheter: Enhet[]
	selectedEnhet: number
	onNodeClick: Function
	level?: number
}

export const Enhetstre = (props: EnhetstreProps) => {
	const hasChildren = (enhet: Enhet) => {
		return enhet.underenheter && enhet.underenheter.length > 0
	}

	const isSelected = (currentEnhet: number, selected: number) => {
		return currentEnhet === selected
	}

	const level = props.level || 0

	return (
		<div className="enhetstre-container">
			{props.enheter.map((enhet, i) => {
				return (
					<div key={level + i} className="enheter">
						<Node
							enhet={enhet}
							hasChildren={hasChildren(enhet)}
							isSelected={isSelected(enhet.id, props.selectedEnhet)}
							onNodeClick={props.onNodeClick}
						/>
						{hasChildren(enhet) && (
							<Enhetstre
								enheter={enhet.underenheter}
								selectedEnhet={props.selectedEnhet}
								onNodeClick={props.onNodeClick}
								level={props.enheter.length}
							/>
						)}
					</div>
				)
			})}
		</div>
	)
}
