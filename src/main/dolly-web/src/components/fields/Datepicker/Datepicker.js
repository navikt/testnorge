import React, { Component } from 'react'
import NavDatoInput from 'nav-datovelger'
import cn from 'classnames'
import _get from 'lodash/get'

// import 'nav-datovelger/dist/datovelger/styles/datovelger.css'
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
				<label className="skjemaelement__label" htmlFor={name}>
					{label}
				</label>
				<NavDatoInput.Datovelger
					id={name}
					dato={value}
					input={{
						placeholder: 'eks: 01.01.2000'
					}}
					{...restProps}
				/>
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
	//NAV-Datepicker sender "undefined" når ingen verdi er satt, men formik støtter ikke undefined, så setter tom string istedet.
	return (
		<Datepicker
			name={field.name}
			value={field.value}
			onChange={dato => {
				form.setFieldValue(field.name, dato || '')
				form.setFieldTouched(field.name, true)
			}}
			error={_get(form.touched, field.name) && _get(form.errors, field.name)}
			{...restProps}
		/>
	)
}
