import React, { Component } from 'react'
import Datovelger from 'nav-datovelger'
import cn from 'classnames'

import 'nav-datovelger/dist/datovelger/styles/datovelger.css'
import './Datepicker.less'

export default class Datepicker extends Component {
	state = {
		dato: null
	}

	render() {
		const { name, value, label, error, ...restProps } = this.props

		// classname = skjemaelement for å følge samme styling som andre input comps fra NAV
		return (
			<div className={cn({ error }, 'skjemaelement')}>
				<label className="skjemaelement__label" htmlFor={label.toLowerCase()}>
					{label}
				</label>
				<Datovelger id={name} dato={value} {...restProps} />
				{error && (
					<div role="alert" aria-live="assertive">
						<div className="skjemaelement__feilmelding">{error}</div>
					</div>
				)}
			</div>
		)
	}
}

export const FormikDatepicker = props => {
	const { field, form, ...restProps } = props

	//NAV-Datepicker har ikke onBlur, så ved onChange er den både touched og changed
	return (
		<Datepicker
			name={field.name}
			value={field.value}
			onChange={dato => {
				form.setFieldValue(field.name, dato)
				form.setFieldTouched(field.name, true)
			}}
			error={form.touched[field.name] && form.errors[field.name]}
			{...restProps}
		/>
	)
}
