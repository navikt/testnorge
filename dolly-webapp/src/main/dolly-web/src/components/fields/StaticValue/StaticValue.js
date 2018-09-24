import React, { PureComponent } from 'react'
import PropTypes from 'prop-types'

import './StaticValue.less'

export default class StaticValue extends PureComponent {
	static propTypes = {
		header: PropTypes.string.isRequired,
		value: PropTypes.string.isRequired,
		format: PropTypes.func
	}

	render() {
		const { header, value, format } = this.props
		let _value = value
		if (format) _value = format(value)

		return (
			<div className="static-value">
				{header} 
				<span>{_value}</span>
			</div>
		)
	}
}
