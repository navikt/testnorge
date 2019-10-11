import React from 'react'
import Button from './Button'

export default function ExpandButton({ expanded = false, onClick }) {
	const iconType = expanded ? 'chevron-up' : 'chevron-down'
	return <Button iconSize={14} kind={iconType} onClick={onClick} />
}
