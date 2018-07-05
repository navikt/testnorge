import React, { PureComponent } from 'react'
import PropTypes from 'prop-types'

import './Loading.less'

export default class Loading extends PureComponent {
	static propTypes = {
		label: PropTypes.string,
		onlySpinner: PropTypes.bool,
		fullpage: PropTypes.bool
	}

	static defaultProps = {
		label: 'Laster',
		onlySpinner: false,
		fullpage: false
	}

	renderSpinner = () => {
		return <div className="loading-spinner" />
	}

	render() {
		const { label, onlySpinner, fullpage } = this.props

		if (onlySpinner) return this.renderSpinner()

		const component = (
			<div className="loading-component">
				{label}
				{this.renderSpinner()}
			</div>
		)

		if (fullpage) return <div className="fullpage-loading-container">{component}</div>

		return component
	}
}
