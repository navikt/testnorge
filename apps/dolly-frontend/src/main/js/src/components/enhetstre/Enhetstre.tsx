import * as React from 'react'
import { Node } from './Node'
import './Enhetstre.less'
import { Org, OrgTree } from '@/components/enhetstre/OrgTree'

type EnhetstreProps<T extends Org<T>> = {
	enheter: OrgTree<T>[]
	selectedEnhet: string
	onNodeClick: (id: string) => void
	level?: number
}

export function Enhetstre<T extends Org<T>>(props: EnhetstreProps<T>) {
	const level = props.level ?? 0
	return (
		<div className="enhetstre-container">
			{props.enheter.map((enhet) => {
				const children = enhet.getUnderenheter()
				const hasChildren = children.length > 0
				const isSelected = enhet.getId() === props.selectedEnhet
				return (
					<div key={enhet.getId()} className="enheter">
						<Node
							enhet={enhet}
							hasChildren={hasChildren}
							isSelected={isSelected}
							onNodeClick={props.onNodeClick}
						/>
						{hasChildren && (
							<Enhetstre
								enheter={children}
								selectedEnhet={props.selectedEnhet}
								onNodeClick={props.onNodeClick}
								level={level + 1}
							/>
						)}
					</div>
				)
			})}
		</div>
	)
}
