import * as React from 'react'
import { Node } from './Node'
import './Enhetstre.less'
import { Org, OrgTree } from '~/components/enhetstre/OrgTree'

type EnhetstreProps<T extends Org<T>> = {
	enheter: OrgTree<T>[]
	selectedEnhet: string
	onNodeClick: Function
	level?: number
}

export function Enhetstre<T>(props: EnhetstreProps<T>) {
	const hasChildren = (enhet: OrgTree<T>) => {
		return enhet.getUnderenheter().length > 0
	}

	const isSelected = (currentEnhet: string, selected: string) => {
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
							isSelected={isSelected(enhet.getId(), props.selectedEnhet)}
							onNodeClick={props.onNodeClick}
						/>
						{hasChildren(enhet) && (
							<Enhetstre
								enheter={enhet.getUnderenheter()}
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
