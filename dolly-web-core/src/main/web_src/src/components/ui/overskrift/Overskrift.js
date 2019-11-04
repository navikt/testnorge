import React, { PureComponent } from 'react'
import PropTypes from 'prop-types'
import cn from 'classnames'
import Button from '~/components/ui/button/Button'

import './Overskrift.less'

export default class Overskrift extends PureComponent {
	static propTypes = {
		type: PropTypes.oneOf(['h1', 'h2', 'h3']),
		label: PropTypes.string.isRequired
	}

	static defaultProps = {
		label: 'default label',
		type: 'h1'
	}

	render() {
		const { type, label, className, children, ...restProps } = this.props

		const cssClass = cn('overskrift', className)
		restProps.className = cssClass

		return React.createElement(type, restProps, [label, children])
	}
}
