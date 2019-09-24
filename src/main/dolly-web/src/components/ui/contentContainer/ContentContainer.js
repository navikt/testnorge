import React, { PureComponent } from 'react'
import PropTypes from 'prop-types'

import './ContentContainer.less'

export default class ContentContainer extends PureComponent {
	static propTypes = {
		children: PropTypes.node
	}

	render() {
		return <div className={this.props.className || 'content-container'}>{this.props.children}</div>
	}
}
