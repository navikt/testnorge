import React from 'react'
import { useToggle } from 'react-use'
import ExpandButton from '~/components/ui/button/ExpandButton'

import './AdvancedOptions.less'

interface AdvancedOptionsProps {
	children?: React.ReactNode
	startOpen?: boolean
}

export const AdvancedOptions = ({ children, startOpen = false }: AdvancedOptionsProps) => {
	const [isOpen, toggleOpen] = useToggle(startOpen)

	return (
		<div className="advanced-panel">
			<div className="flexbox--full-width">
				<div className="advanced-panel-heading" onClick={toggleOpen}>
					<h3>{'AVANSERTE VALG'}</h3>
					<span className="advanced-panel-heading_buttons">
						<ExpandButton expanded={isOpen} onClick={toggleOpen} iconSize={12} />
					</span>
				</div>
				{isOpen && <div className="advanced-panel-content">{children}</div>}
			</div>
		</div>
	)
}
