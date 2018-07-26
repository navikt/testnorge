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
				<label className="skjemaelement__label" for={label.toLowerCase()}>
					{label}
				</label>
				<Datovelger id={name} dato={value} {...restProps} />
				{error && (
					<div role="alert" aria-live="assertive">
						<div class="skjemaelement__feilmelding">{error}</div>
					</div>
				)}
			</div>
		)
	}
}

export const FormikDatepicker = props => {
	const { field, form, ...restProps } = props

	return (
		<Datepicker
			name={field.name}
			value={field.value}
			onChange={dato => form.setFieldValue(field.name, dato)}
			onBlur={e => form.setFieldTouched(field.name, true)}
			{...restProps}
		/>
	)
}
