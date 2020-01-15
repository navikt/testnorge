import React, { PureComponent } from 'react'
import PropTypes from 'prop-types'
import cn from 'classnames'
import Icon from '~/components/ui/icon/Icon'
import Loading from '~/components/ui/loading/Loading'
import './Button.less'

export default class Button extends PureComponent {
	static propTypes = {
		kind: PropTypes.string,
		onClick: PropTypes.func
	}

	static defaultProps = {
		kind: null,
		onClick: () => {} // Default noop func
	}

	onClickHandler = event => {
		event.stopPropagation()
		return this.props.onClick()
	}

	render() {
		const {
			kind,
			onClick,
			iconSize = 16,
			loading,
			children,
			className,
			type = 'button',
			...rest
		} = this.props

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
				onClick={this.onClickHandler}
				disabled={rest.disabled || loading}
				{...rest}
			>
				{renderIcon}
				{children && <span>{children}</span>}
			</button>
		)
	}
}
