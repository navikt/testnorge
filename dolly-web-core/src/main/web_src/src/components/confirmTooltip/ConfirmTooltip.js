import React, { Component } from 'react'
import PropTypes from 'prop-types'
import Tooltip from 'rc-tooltip'
import Icon from '~/components/icon/Icon'
import Button from '~/components/button/Button'

import 'rc-tooltip/assets/bootstrap_white.css'
import './ConfirmTooltip.less'

export default class ConfirmTooltip extends Component {
	static propTypes = {
		message: PropTypes.string,
		children: PropTypes.node
	}

	static defaultProps = {
		message: 'Slette?'
	}

	state = {
		visible: false
	}

	render() {
		const { message, children, label, className } = this.props
		const content = (
			<div className="tooltip-content" onClick={this.stopPropagation}>
				<div>{message}</div>
				<Button onClick={this._confirmHandler}>JA</Button>
				<Button onClick={this._closeHandler}>NEI</Button>
			</div>
		)
		const arrow = <div className="rc-tooltip-arrow-inner" />

		return (
			<Tooltip
				visible={this.state.visible}
				placement="right"
				overlay={content}
				arrowContent={arrow}
				trigger={['click']}
				onVisibleChange={this._onVisibleChangeHandler}
			>
				{children ? (
					children
				) : (
					<Button className={className} kind="trashcan">
						{label}
					</Button>
				)}
			</Tooltip>
		)
	}

	_onVisibleChangeHandler = visible => {
		this.setState({ visible })
	}

	_closeHandler = () => {
		this.setState({ visible: false })
	}

	_confirmHandler = () => {
		this._closeHandler()
		this.props.onClick()
	}
}
