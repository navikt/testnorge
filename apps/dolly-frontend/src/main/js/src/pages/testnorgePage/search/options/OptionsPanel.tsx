import React from 'react'
import { useToggle } from 'react-use'
import cn from 'classnames'
import Icon from '~/components/ui/icon/Icon'
import ExpandButton from '~/components/ui/button/ExpandButton'

import './OptionsPanel.less'

type OptionsPanelProps = {
	startOpen?: boolean
	heading: string
	content?: React.ReactNode
	children?: React.ReactNode
	iconType?: string
}

export const OptionsPanel = ({
	startOpen = false,
	heading,
	content,
	children,
	iconType,
}: OptionsPanelProps) => {
	const [isOpen, toggleOpen] = useToggle(startOpen)

	const panelClass = cn('options-panel', {
		'options-panel-open': isOpen,
	})

	const renderContent = children ? children : content

	return (
		<div className={panelClass}>
			<div className="options-panel-heading" onClick={toggleOpen}>
				<h2>{heading}</h2>
				{iconType && <Icon size={45} kind={iconType} className="header-icon" />}
				<span className="options-panel-heading_buttons">
					<ExpandButton expanded={isOpen} onClick={toggleOpen} />
				</span>
			</div>
			{isOpen && <div className="options-panel-content">{renderContent}</div>}
		</div>
	)
}
