import cn from 'classnames'
import * as _ from 'lodash'
import styled from 'styled-components'
import './Label.less'
import { Hjelpetekst } from '@/components/hjelpetekst/Hjelpetekst'
import { useFormContext } from 'react-hook-form'
import { useContext } from 'react'
import {
	ShowErrorContext,
	ShowErrorContextType,
} from '@/components/bestillingsveileder/ShowErrorContext'

const StyledLabel = styled.label`
	font-size: 0.75em;
	text-transform: uppercase;
`

export const Label = ({ name, label, info = null, containerClass = null, children }) => {
	const {
		getFieldState,
		formState: { touchedFields },
	} = useFormContext()
	const { error } = getFieldState(name)
	const isTouched = _.has(touchedFields, name)
	const errorContext: ShowErrorContextType = useContext(ShowErrorContext)
	const feilmelding = error?.message
	const wrapClass = cn('skjemaelement', containerClass, {
		error: Boolean(!_.isEmpty(feilmelding) && (errorContext?.showError || isTouched)),
		'label-offscreen': _.isNil(label),
	})

	return (
		<div className={wrapClass}>
			{info ? (
				<div className="flexbox--align-center--justify-start">
					<StyledLabel htmlFor={name} className="skjemaelement__label">
						{label}
					</StyledLabel>
					<div className="label-hjelpetekst">
						<Hjelpetekst>{info}</Hjelpetekst>
					</div>
				</div>
			) : (
				<StyledLabel htmlFor={name} className="skjemaelement__label">
					{label}
				</StyledLabel>
			)}
			{children}
			{!_.isEmpty(feilmelding) && (errorContext?.showError || isTouched) && (
				<div role="alert" aria-live="assertive">
					<div className="skjemaelement__feilmelding">{feilmelding}</div>
				</div>
			)}
		</div>
	)
}
