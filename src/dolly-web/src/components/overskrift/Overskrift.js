import React, { PureComponent } from 'react'
import PropTypes from 'prop-types'
import cn from 'classnames'
import IconButton from '~/components/fields/IconButton/IconButton'

import './Overskrift.less'

export default class Overskrift extends PureComponent {
	static propTypes = {
		type: PropTypes.oneOf(['h1', 'h2', 'h3']),
		label: PropTypes.string.isRequired,
		actions: PropTypes.arrayOf(
			PropTypes.shape({
				onClick: PropTypes.func,
				icon: PropTypes.string
			})
		)
	}

	static defaultProps = {
		type: 'h1',
		actions: []
	}

	render() {
		const { type, label, actions, className, ...restProps } = this.props

		const cssClass = cn('overskrift', className)
		restProps.className = cssClass

		return React.createElement(type, restProps, [
			label,
			actions.map((o, idx) => <IconButton key={idx} onClick={o.onClick} kind={o.icon} />)
		])
	}
}
