import React from 'react'
import { InputWrapper } from '~/components/ui/form/inputWrapper/InputWrapper'
import { FormikField } from '~/components/ui/form/FormikField'
import { SyntEvent } from '~/components/ui/form/formUtils'
import { Checkbox as NavCheckbox, Switch } from '@navikt/ds-react'

import './Checkbox.less'
import styled from 'styled-components'

const StyledAttributeCheckbox = styled(NavCheckbox)`
	&&& {
		.skjemaelement__label {
			text-transform: none;
		}
	}
`

const StyledSwitch = styled(Switch)`
	&&& {
		margin-left: 3px;
	}
`
const StyledCheckbox = styled(NavCheckbox)`
	&&& {
		.skjemaelement__label {
			font-size: 0.75em;
		}
	}
`

export const Checkbox = ({ id, attributtCheckbox = false, ...restProps }) =>
	attributtCheckbox ? (
		<StyledAttributeCheckbox id={id || restProps.name} {...restProps} />
	) : (
		<StyledCheckbox id={id || restProps.name} {...restProps} />
	)

export const DollyCheckbox = ({
	isSwitch = false,
	wrapperSize = 'grow',
	label = '',
	checked = false,
	...props
}) => (
	<InputWrapper size={wrapperSize}>
		{isSwitch ? (
			<StyledSwitch {...props}>{label}</StyledSwitch>
		) : (
			<Checkbox {...props}>{label}</Checkbox>
		)}
	</InputWrapper>
)

export const FormikCheckbox = ({
	afterChange = null,
	size = 'small',
	fastfield = false,
	...props
}) => (
	<FormikField name={props.name} fastfield={fastfield}>
		{({ field, _form, _meta }) => {
			const handleChange = (event) => {
				field.onChange(SyntEvent(field.name, event.target.checked))
				if (afterChange) afterChange(event.target.checked)
			}

			return <DollyCheckbox size={size} checked={field.value} onChange={handleChange} {...props} />
		}}
	</FormikField>
)
