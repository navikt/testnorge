import React, { PureComponent } from 'react'
import PropTypes from 'prop-types'
import Icon from '~/components/icon/Icon'

import './IconButton.less'

export default class IconButton extends PureComponent {
	static propTypes = {
		kind: PropTypes.string,
		onClick: PropTypes.func
	}

	static defaultProps = {
		kind: '',
		onClick: () => {} // Default noop func
	}

	onClickHandler = event => {
		event.stopPropagation()
		return this.props.onClick()
	}

	render() {
		const { kind } = this.props

		return (
			<button className="iconbutton" onClick={this.onClickHandler}>
				<Icon kind={kind} />
			</button>
		)
	}
}
