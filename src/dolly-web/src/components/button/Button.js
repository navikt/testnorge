import React, { PureComponent } from 'react'
import PropTypes from 'prop-types'
import Icon from '~/components/icon/Icon'

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
		const { kind, children, type = 'button' } = this.props

		return (
			<button type={type} className="dolly-button" onClick={this.onClickHandler}>
				{children && children}
				{kind && <Icon kind={kind} />}
			</button>
		)
	}
}
