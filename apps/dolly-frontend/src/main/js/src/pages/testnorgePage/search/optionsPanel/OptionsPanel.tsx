import React from 'react'
import { useToggle } from 'react-use'
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

export const OptionsPanel: React.FC<OptionsPanelProps> = ({
	startOpen = false,
	heading,
	content,
	children,
	selectionColor = 'blue',
	numSelected = 0,
}: OptionsPanelProps) => {
	const [isOpen, toggleOpen] = useToggle(startOpen)
	const renderContent = children ? children : content

	return (
		<div className="options-panel">
			<div className="options-panel-heading" onClick={toggleOpen}>
				<h2>{heading}</h2>
				{numSelected > 0 && (
					<div className={`options-panel-heading_${selectionColor}-circle`}>
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
