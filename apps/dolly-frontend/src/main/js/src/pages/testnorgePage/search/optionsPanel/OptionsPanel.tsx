import { useToggle } from 'react-use'
import ExpandButton from '@/components/ui/button/ExpandButton/ExpandButton'
import './OptionsPanel.less'
import React from 'react'
import { runningE2ETest } from '@/service/services/Request'

type OptionsPanelProps = {
	startOpen?: boolean
	heading: string
	content?: React.ReactNode
	children?: React.ReactNode
	numSelected?: number
	disabled?: boolean
}

export const OptionsPanel: React.FC<OptionsPanelProps> = ({
	startOpen = runningE2ETest(),
	heading,
	content,
	children,
	numSelected = 0,
	disabled = false,
}: OptionsPanelProps) => {
	const [isOpen, toggleOpen] = useToggle(startOpen)
	const renderContent = children ? children : content

	const handleClick = () => {
		if (!disabled) {
			toggleOpen()
		}
	}

	const headerCn = disabled ? 'options-panel__header--disabled' : 'options-panel__header'
	return (
		<div className="options-panel">
			<div className={headerCn} onClick={handleClick}>
				<h2>{heading}</h2>
				{numSelected > 0 && !disabled && (
					<div className="options-panel__header__circle">
						<p className="circle-text">{numSelected}</p>
					</div>
				)}
				<span className={`${headerCn}__button`}>
					<ExpandButton expanded={isOpen && !disabled} onClick={handleClick} disabled={disabled} />
				</span>
			</div>
			{isOpen && !disabled && <div className="options-panel__content">{renderContent}</div>}
		</div>
	)
}
