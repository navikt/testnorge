import React, { PureComponent } from 'react'
import PropTypes from 'prop-types'

export default class Label extends PureComponent {
	static propType = {
		label: PropTypes.string.isRequired,
		required: PropTypes.bool
	}

	static defaultProps = {
		required: true
	}

	render() {
		const { label, required } = this.props

		return (
			<React.Fragment>
				{label} {required && <span style={{ color: 'red' }}>*</span>}
			</React.Fragment>
		)
	}
}
