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
	...props
}: ExpandButtonProps): JSX.Element {
	const iconType = expanded ? 'designsystem-chevron-up' : 'designsystem-chevron-down'
	const label: string = expanded ? 'Lukk' : 'Åpne'
	return (
		<Button
			className={'expandButton'}
			iconSize={14}
			kind={iconType}
			onClick={onClick}
			disabled={disabled}
			aria-label={label}
			{...props}
		>
			{label}
		</Button>
	)
}
