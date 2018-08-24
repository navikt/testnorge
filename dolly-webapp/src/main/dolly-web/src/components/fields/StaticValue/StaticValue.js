import React, { PureComponent } from 'react'
import PropTypes from 'prop-types'

import './StaticValue.less'

export default class StaticValue extends PureComponent {
	static propTypes = {
		header: PropTypes.string.isRequired,
		value: PropTypes.string.isRequired
	}

	render() {
		const { header, value } = this.props
		return (
			<div className="static-value">
				<h5>{header}</h5>
				<span>{value}</span>
			</div>
		)
	}
}
