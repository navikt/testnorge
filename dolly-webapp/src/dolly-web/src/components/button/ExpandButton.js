import React from 'react'
import IconButton from './IconButton/IconButton'

export default function ExpandButton({ expanded = false, onClick }) {
	const iconType = expanded ? 'chevron-up' : 'chevron-down'
	return <IconButton kind={iconType} onClick={onClick} />
}
