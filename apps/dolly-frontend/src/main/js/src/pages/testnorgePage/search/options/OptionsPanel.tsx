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
	circleColor?: string
	circleCount?: number
}

export const OptionsPanel = ({
	startOpen = false,
	heading,
	content,
	children,
	circleColor,
	circleCount,
}: OptionsPanelProps) => {
	const [isOpen, toggleOpen] = useToggle(startOpen)

	const panelClass = cn('options-panel', {
		'options-panel-open': isOpen,
	})

	const renderContent = children ? children : content
	const circleClassName = circleColor
		? 'options-panel-heading_' + circleColor + '-circle'
		: 'circle'

	return (
		<div className={panelClass}>
			<div className="options-panel-heading" onClick={toggleOpen}>
				<h2>{heading}</h2>
				{circleColor && circleCount && (
					<div className={circleClassName}>
						<p className="circle-text">{circleCount}</p>
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
