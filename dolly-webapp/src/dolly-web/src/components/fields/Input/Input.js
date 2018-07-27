import React, { PureComponent } from 'react'
import PropTypes from 'prop-types'
import cn from 'classnames'
import { Input } from 'nav-frontend-skjema'

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

		return <Input className={cssClass} {...restProps} />
	}
}

export const FormikInput = props => {
	const { field, form, error, ...restProps } = props

	return (
		<DollyInput
			name={field.name}
			value={field.value}
			onChange={field.onChange}
			onBlur={field.onBlur}
			feil={
				form.touched[field.name] && form.errors[field.name]
					? { feilmelding: form.errors[field.name] }
					: null
			}
			{...restProps}
		/>
	)
}
