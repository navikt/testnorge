import * as React from 'react'
// @ts-ignore
import cn from 'classnames'
// @ts-ignore
import Icon from '~/components/ui/icon/Icon'
// @ts-ignore
import Loading from '~/components/ui/loading/Loading'
import './Button.less'

interface Button extends React.ButtonHTMLAttributes<HTMLButtonElement> {
	kind?: string
	onClick: () => void
	iconSize?: number
	loading?: boolean
}

export default function Button({
	kind,
	onClick,
	children,
	className,
	iconSize = 16,
	loading = false,
	type = 'button',
	disabled,
	...rest
}: Button) {
	const handleClick = (event: React.MouseEvent<HTMLButtonElement>) => {
		event.stopPropagation()
		onClick()
	}

	const cssClass = cn('dolly-button', className)

	const renderIcon = loading ? (
		<Loading onlySpinner size={iconSize} />
	) : kind ? (
		// @ts-ignore
		<Icon size={iconSize} kind={kind} />
	) : null

	return (
		<button
			type={type}
			className={cssClass}
			onClick={handleClick}
			disabled={disabled || loading}
			{...rest}
		>
			{renderIcon}
			{children && <span>{children}</span>}
		</button>
	)
}
