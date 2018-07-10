import React, { PureComponent } from 'react'
import PropTypes from 'prop-types'
import { getIn } from 'formik'

import './FormErrors.less'

export default class FormErrors extends PureComponent {
	static propTypes = {
		errors: PropTypes.object,
		touched: PropTypes.object
	}
	render() {
		const { errors, touched } = this.props

		const actualErrors = []

		for (let error in errors) {
			if (getIn(touched, error, false)) actualErrors.push(errors[error])
		}

		if (actualErrors.length <= 0) return false

		return (
			<div className="form-errors">
				<ul>{actualErrors.map((e, idx) => <li key={idx}>{e}</li>)}</ul>
			</div>
		)
	}
}
