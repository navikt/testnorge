import React, { PureComponent } from 'react'
import PropTypes from 'prop-types'
import IconButton from '~/components/fields/IconButton/IconButton'

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
		const { type, label, actions } = this.props

		return React.createElement(type, null, [
			label,
			actions.map((o, idx) => <IconButton key={idx} onClick={o.onClick} iconName={o.icon} />)
		])
	}
}
