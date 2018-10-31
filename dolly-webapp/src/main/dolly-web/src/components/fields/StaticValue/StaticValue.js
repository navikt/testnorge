import React, { PureComponent } from 'react'
import PropTypes from 'prop-types'

import './StaticValue.less'

export default class StaticValue extends PureComponent {
	static propTypes = {
		header: PropTypes.string.isRequired,
		value: PropTypes.string.isRequired,
		format: PropTypes.func,
		headerType: PropTypes.oneOf(['h1', 'h2', 'h3', 'h4'])
	}

	static defaultProps = {
		headerType: 'h2'
	}

	render() {
		const { header, value, format, headerType } = this.props
		let _value = value
		if (format) _value = format(value)

		return (
			<div className="static-value">
				{React.createElement(headerType, null, [header])}
				<span>{_value}</span>
			</div>
		)
	}
}
