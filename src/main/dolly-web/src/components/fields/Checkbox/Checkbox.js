import React, { PureComponent } from 'react'
import PropTypes from 'prop-types'
import cn from 'classnames'
import { Checkbox } from 'nav-frontend-skjema'

import './Checkbox.less'

export default class DollyCheckbox extends PureComponent {
	static propTypes = {
		id: PropTypes.string.isRequired,
		label: PropTypes.string
	}

	static defaultProps = {
		label: ''
	}

	render() {
		const { id, label, className, disabled, ...restProps } = this.props

		const cssClass = cn('dolly-checkbox', className, {
			nonlabel: label === ''
		})

		const disabledCss = 'dolly-checkbox_disabled'
		return (
			<Checkbox
				disabled={disabled}
				id={id}
				className={disabled ? cn(cssClass, disabledCss) : cssClass}
				label={label}
				{...restProps}
			/>
		)
	}
}
