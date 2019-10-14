import React, { PureComponent } from 'react'
import PropTypes from 'prop-types'
import cn from 'classnames'

import './StaticValue.less'

export default class StaticValue extends PureComponent {
	static propTypes = {
		header: PropTypes.string.isRequired,
		format: PropTypes.func,
		headerType: PropTypes.oneOf(['h1', 'h2', 'h3', 'h4', 'label']),
		optionalClassName: PropTypes.string
	}

	static defaultProps = {
		headerType: 'h2'
	}

	render() {
		const { header, value, format, headerType, optionalClassName, size } = this.props
		let _value =
			value === true ? 'JA' : value ? value : value === false ? 'NEI' : 'Ikke spesifisert'
		if (format) _value = format(value)
		const fixedSize = size ? cn('static-value', 'static-value_' + size) : 'static-value'
		return (
			<div className={optionalClassName || cn(fixedSize, 'static-value-headers')}>
				{React.createElement(headerType, null, [header])}
				<span>{_value}</span>
			</div>
		)
	}
}
