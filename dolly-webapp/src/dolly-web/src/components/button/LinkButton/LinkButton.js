import React, { PureComponent } from 'react'
import PropTypes from 'prop-types'

export default class LinkButton extends PureComponent {
	static propTypes = {
		children: PropTypes.node.isRequired,
		onClick: PropTypes.func.isRequired,
		fontSize: PropTypes.number
	}

	onClickHandler = e => {
		e.stopPropagation()
		this.props.onClick()
	}

	render() {
		const textStyle = {
			fontSize: this.props.fontSize || 14
		}

		return (
			<button className="iconbutton" onClick={this.onClickHandler}>
				<div style={textStyle}>{this.props.children}</div>
			</button>
		)
	}
}
