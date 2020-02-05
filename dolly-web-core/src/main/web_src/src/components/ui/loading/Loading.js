import React, { PureComponent } from 'react'
import PropTypes from 'prop-types'

import './Loading.less'

export default class Loading extends PureComponent {
	static propTypes = {
		label: PropTypes.string,
		onlySpinner: PropTypes.bool,
		fullpage: PropTypes.bool,
		panel: PropTypes.bool
	}

	static defaultProps = {
		label: 'Laster',
		onlySpinner: false,
		fullpage: false,
		panel: false
	}

	renderSpinner = () => {
		return <div className="loading-spinner" />
	}

	render() {
		const { label, onlySpinner, fullpage, panel, className } = this.props

		if (onlySpinner) return this.renderSpinner()

		const cssClass = className ? className : 'loading-component'

		const component = (
			<div className={cssClass}>
				{label}
				{this.renderSpinner()}
			</div>
		)

		if (panel) return <div className="panel-loading-container">{component}</div>

		if (fullpage) return <div className="fullpage-loading-container">{component}</div>

		return component
	}
}
