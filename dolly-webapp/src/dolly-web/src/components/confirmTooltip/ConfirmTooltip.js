import React, { Component } from 'react'
import PropTypes from 'prop-types'
import Tooltip from 'rc-tooltip'
import Icon from '~/components/icon/Icon'
import LinkButton from '~/components/button/LinkButton/LinkButton'
import IconButton from '~/components/button/IconButton/IconButton'

import 'rc-tooltip/assets/bootstrap_white.css'

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

	closeHandler = e => {
		this.setState({ visible: false })
	}

	render() {
		const { message, children } = this.props
		const content = (
			<div className="tooltip-content" onClick={this.stopPropagation}>
				<div>{message}</div>
				<LinkButton onClick={this.closeHandler}>JA</LinkButton>
				<LinkButton onClick={this.closeHandler}>NEI</LinkButton>
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
				{children ? children : <IconButton kind="trashcan" />}
			</Tooltip>
		)
	}
}
