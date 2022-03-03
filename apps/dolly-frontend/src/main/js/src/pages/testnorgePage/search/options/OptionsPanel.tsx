import React from 'react'
import { useToggle } from 'react-use'
import cn from 'classnames'
import ExpandButton from '~/components/ui/button/ExpandButton'
import './OptionsPanel.less'

type OptionsPanelProps = {
	startOpen?: boolean
	heading: string
	content?: React.ReactNode
	children?: React.ReactNode
	selectionColor?: string
	numSelected?: number
}

export const OptionsPanel = ({
	startOpen = false,
	heading,
	content,
	children,
	selectionColor,
	numSelected,
}: OptionsPanelProps) => {
	const [isOpen, toggleOpen] = useToggle(startOpen)

	const panelClass = cn('options-panel', {
		'options-panel-open': isOpen,
	})

	const renderContent = children ? children : content
	const circleClassName = selectionColor
		? 'options-panel-heading_' + selectionColor + '-circle'
		: 'circle'

	return (
		<div className={panelClass}>
			<div className="options-panel-heading" onClick={toggleOpen}>
				<h2>{heading}</h2>
				{selectionColor && numSelected && (
					<div className={circleClassName}>
						<p className="circle-text">{numSelected}</p>
					</div>
				)}
				<span className="options-panel-heading_buttons">
					<ExpandButton expanded={isOpen} onClick={toggleOpen} />
				</span>
			</div>
			{isOpen && <div className="options-panel-content">{renderContent}</div>}
		</div>
	)
}
