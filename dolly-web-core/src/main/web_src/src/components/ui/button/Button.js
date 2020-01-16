import React from 'react'
import cn from 'classnames'
import Icon from '~/components/ui/icon/Icon'
import Loading from '~/components/ui/loading/Loading'
import './Button.less'

export default function Button(props) {
	const {
		kind,
		onClick,
		children,
		className,
		iconSize = 16,
		loading = false,
		type = 'button',
		...rest
	} = props

	const handleClick = event => {
		event.stopPropagation()
		return onClick()
	}

	const cssClass = cn('dolly-button', className)

	const renderIcon = loading ? (
		<Loading onlySpinner size={iconSize} />
	) : kind ? (
		<Icon size={iconSize} kind={kind} />
	) : null

	return (
		<button
			type={type}
			className={cssClass}
			onClick={handleClick}
			disabled={rest.disabled || loading}
			{...rest}
		>
			{renderIcon}
			{children && <span>{children}</span>}
		</button>
	)
}
