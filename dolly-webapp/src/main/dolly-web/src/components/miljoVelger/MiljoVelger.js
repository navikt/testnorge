import React, { Component } from 'react'
import PropTypes from 'prop-types'
import _mapValues from 'lodash/mapValues'
import LinkButton from '~/components/button/LinkButton/LinkButton'
import Checkbox from '~/components/fields/Checkbox/Checkbox'
import { EnvironmentManager } from '~/service/Kodeverk'

import './MiljoVelger.less'

export default class MiljoVelger extends Component {
	static propTypes = {
		heading: PropTypes.string
	}

	constructor(props) {
		super(props)
		this.Environments = new EnvironmentManager().getEnvironmentsSortedByType()
	}

	isChecked = id => this.props.arrayValues.includes(id)
	add = id => this.props.arrayHelpers.push(id)
	remove = id => this.props.arrayHelpers.remove(this.props.arrayValues.indexOf(id))

	onClickHandler = e => {
		const { id } = e.target
		return this.isChecked(id) ? this.remove(id) : this.add(id)
	}

	velgAlle = (e, type, envs) => {
		const self = this
		envs.forEach(env =>
			setTimeout(function() {
				return !self.isChecked(env.id) && self.add(env.id)
			}, 0)
		)
	}

	fjernAlle = (e, type, envs) => {
		const self = this
		envs.forEach(env =>
			setTimeout(function() {
				return self.isChecked(env.id) && self.remove(env.id)
			}, 0)
		)
	}

	renderEnvCategory = (envs, type) => {
		const allDisabled = envs.some(f => f.disabled)
		return (
			<fieldset key={type} name={`Liste over ${type}-mijøer`}>
				<h3>{type} miljøer</h3>
				<div className="miljo-velger_checkboxes">{envs.map(env => this.renderCheckbox(env))}</div>
				{!allDisabled && (
					<div className="miljo-velger_buttons">
						<LinkButton text="Velg alle" onClick={e => this.velgAlle(e, type, envs)} />
						<LinkButton text="Fjern alle" onClick={e => this.fjernAlle(e, type, envs)} />
					</div>
				)}
			</fieldset>
		)
	}

	renderCheckbox = ({ id, label, disabled }) => (
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
				<h2>{heading}</h2>
				{Object.keys(this.Environments).map(type =>
					this.renderEnvCategory(this.Environments[type], type)
				)}
				{this.renderError(arrayHelpers)}
			</div>
		)
	}
}
