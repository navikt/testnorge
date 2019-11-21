import React, { Component } from 'react'
import PropTypes from 'prop-types'
import _mapValues from 'lodash/mapValues'
import LinkButton from '~/components/ui/button/LinkButton/LinkButton'
import { FormikCheckbox, DollyCheckbox } from '~/components/ui/form/inputs/checbox/Checkbox'
import Checkbox from '~/components/fields/Checkbox/Checkbox'

import './MiljoVelger.less'

export default class MiljoVelger extends Component {
	static propTypes = {
		heading: PropTypes.string
	}

	render() {
		const { heading, arrayHelpers, environments } = this.props
		if (!environments) return null

		const order = ['U', 'T', 'Q']
		return (
			<div className="miljo-velger">
				<h2>{heading}</h2>
				{order.map(type => this.renderEnvCategory(environments[type], type))}
				{this.renderError(arrayHelpers)}
			</div>
		)
	}

	isChecked = id => this.props.arrayValues.includes(id)
	add = id => this.props.arrayHelpers.push(id)
	remove = id => this.props.arrayHelpers.remove(this.props.arrayValues.indexOf(id))

	onClickHandler = e => {
		const { id } = e.target
		console.log('id :', id)
		console.log('this.isChecked(id) :', this.isChecked(id))
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
		if (!envs) return null
		const allDisabled = envs.some(f => f.disabled)
		return (
			<fieldset key={type} name={`Liste over ${type}-mijøer`}>
				<h3>{type}-miljø</h3>
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
		<DollyCheckbox
			key={id}
			id={id}
			disabled={disabled}
			label={label}
			checked={this.props.arrayValues.includes(id)}
			onClick={this.onClickHandler}
			onChange={() => {}}
		/>
	)

	renderError = ({ name, form }) => {
		console.log('name :', name)
		console.log('form :', form)
		if (form.touched[name] && form.errors[name]) {
			return (
				<span className="miljo-velger_error" style={{ color: 'red' }}>
					{form.errors[name]}
				</span>
			)
		}
		return false
	}
}
