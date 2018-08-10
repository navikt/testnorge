import React, { Component } from 'react'
import PropTypes from 'prop-types'
import Panel from '~/components/panel/Panel'
import Checkbox from '~/components/fields/Checkbox/Checkbox'
import { EnvironmentManager } from '~/service/Kodeverk'

import './MiljoVelger.less'

export default class MiljoVelger extends Component {
	static propTypes = {
		heading: PropTypes.string
	}

	constructor(props) {
		super(props)
		this.Environments = new EnvironmentManager().getAllEnvironments()
	}

	onClickHandler = e => {
		const { arrayHelpers, arrayValues } = this.props
		const indexOf = arrayValues.indexOf(e.target.id)

		if (e.target.checked && indexOf === -1) return arrayHelpers.push(e.target.id)

		return arrayHelpers.remove(indexOf)
	}

	createCheckbox = ({ id, label, disabled }) => (
		<Checkbox
			key={id}
			id={id}
			disabled={disabled}
			label={label}
			checked={this.props.arrayValues.includes(id)}
			onClick={this.onClickHandler}
		/>
	)

	renderError = ({ name, form }) => {
		if (form.touched[name] && form.errors[name]) {
			return <span style={{ color: 'red' }}>{form.errors[name]}</span>
		}
		return false
	}

	render() {
		const { heading, arrayHelpers } = this.props

		return (
			<div className="miljo-velger">
				<h3>{heading}</h3>
				<div className="miljo-velger_content">
					<div>{this.Environments.map(env => this.createCheckbox(env))}</div>
				</div>
				{this.renderError(arrayHelpers)}
			</div>
		)
	}
}
