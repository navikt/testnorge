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

	onVisibleChangeHandler = visible => {
		this.setState({ visible })
	}

	closeHandler = () => {
		this.setState({ visible: false })
	}

	confirmHandler = () => {
		this.closeHandler()
		this.props.onClick()
	}

	render() {
		const { message, children, label, className } = this.props
		const content = (
			<div className="tooltip-content" onClick={this.stopPropagation}>
				<div>{message}</div>
				<Button onClick={this.confirmHandler}>JA</Button>
				<Button onClick={this.closeHandler}>NEI</Button>
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
				onVisibleChange={this.onVisibleChangeHandler}
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
}
