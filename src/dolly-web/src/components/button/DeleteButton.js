import React, { Component } from 'react'
import Tooltip from 'rc-tooltip'
import Icon from '~/components/icon/Icon'
import LinkButton from './LinkButton/LinkButton'
import IconButton from './IconButton/IconButton'

import 'rc-tooltip/assets/bootstrap_white.css'

export default class DeleteButton extends Component {
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
		const content = (
			<div className="tooltip-content" onClick={this.stopPropagation}>
				<div>Slette?</div>
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
				<IconButton kind="trashcan" onClick={() => {}} />
			</Tooltip>
		)
	}
}
