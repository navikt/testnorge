import * as React from 'react'
import Button from './Button'

interface ExpandButtonProps {
	onClick: () => void
	expanded?: boolean
	iconSize?: number
}

export default function ExpandButton({
	expanded = false,
	iconSize = 14,
	onClick,
}: ExpandButtonProps) {
	const iconType = expanded ? 'chevron-up' : 'chevron-down'
	return <Button iconSize={iconSize} kind={iconType} onClick={onClick} />
}
