import * as React from 'react'
import Button from './Button'

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
	return <Button iconSize={14} kind={iconType} onClick={onClick} disabled={disabled} />
}
