import * as React from 'react'
import Button from '../Button'
import './ExpandButton.less'

interface ExpandButtonProps {
	onClick: () => void
	expanded?: boolean
	disabled?: boolean
}

export default function ExpandButton({
	expanded = false,
	disabled = false,
	onClick,
}: ExpandButtonProps) {
	const iconType = expanded ? 'chevron-up' : 'chevron-down'
	return (
		<Button
			className={'expandButton'}
			iconSize={14}
			kind={iconType}
			onClick={onClick}
			disabled={disabled}
			aria-label={expanded ? 'Lukk' : 'Åpne'}
		>
			{expanded ? 'Lukk' : 'Åpne'}
		</Button>
	)
}
