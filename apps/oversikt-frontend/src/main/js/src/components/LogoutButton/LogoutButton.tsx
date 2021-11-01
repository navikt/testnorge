import React from 'react'
import SVG from 'react-inlinesvg'
// @ts-ignore
import Logout from './icons/logout.svg'

import './LogoutButton.less'

interface ButtonProps extends React.ButtonHTMLAttributes<HTMLButtonElement> {
	onClick: () => void
	iconSize?: number
	loading?: boolean
}

export default function LogoutButton({ onClick, loading = false, disabled }: ButtonProps) {
	const handleClick = (event: React.MouseEvent<HTMLButtonElement>) => {
		event.stopPropagation()
		onClick()
	}

	return (
		<button
			type="button"
			className="logout-button"
			onClick={handleClick}
			disabled={disabled || loading}
			title="Logg ut"
		>
			<SVG src={Logout} className="svg-icon" />
		</button>
	)
}
