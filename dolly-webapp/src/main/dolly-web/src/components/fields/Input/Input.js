import React, { PureComponent } from 'react'
import PropTypes from 'prop-types'
import cn from 'classnames'
import { Input } from 'nav-frontend-skjema'
import _get from 'lodash/get'

import './Input.less'

export default class DollyInput extends PureComponent {
	static propTypes = {
		className: PropTypes.string,
		labelOffscreen: PropTypes.bool
	}

	render() {
		const { className, labelOffscreen, ...restProps } = this.props

		const cssClass = cn(className, {
			'label-offscreen': labelOffscreen
		})

		return <Input placeholder="Ikke spesifisert" className={cssClass} {...restProps} />
	}
}

export const FormikInput = props => {
	const { field, form, error, type, ...restProps } = props
	// prevent undefined value, dette for å unngå at komponenten blir "controlled"
	const initialValue = field.value || ''

	const onChangeHandler = e => {
		const value = e.target.value
		if (type === 'number') {
			e.target.value = value ? value : 0
			return field.onChange(e)
		}
		form.setFieldTouched(field.name, true)
		return field.onChange(e)
	}
	return (
		<DollyInput
			name={field.name}
			value={initialValue}
			onChange={field.onChange}
			onChange={onChangeHandler}
			//onBlur={field.onBlur}
			onBlur={() => form.setFieldTouched(field.name, true)}
			feil={
				_get(form.touched, field.name) && _get(form.errors, field.name)
					? { feilmelding: _get(form.errors, field.name) }
					: null
			}
			type={type}
			{...restProps}
		/>
	)
}
