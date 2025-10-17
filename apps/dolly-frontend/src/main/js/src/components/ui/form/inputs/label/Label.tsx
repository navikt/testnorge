import cn from 'classnames'
import * as _ from 'lodash-es'
import styled from 'styled-components'
import './Label.less'
import { Hjelpetekst } from '@/components/hjelpetekst/Hjelpetekst'
import { useForm, useFormContext } from 'react-hook-form'
import { useContext } from 'react'
import {
	ShowErrorContext,
	ShowErrorContextType,
} from '@/components/bestillingsveileder/ShowErrorContext'

const StyledLabel = styled.label`
	font-size: 0.75em;
	text-transform: uppercase;
`

type LabelProps = {
	name: string
	fieldName?: string
	label: string
	info?: string
	containerClass?: string
	children: React.ReactNode
	manualError?: string
}

export const Label = ({
	name,
	fieldName,
	label,
	info = null as unknown as string,
	containerClass = null as unknown as string,
	children,
	manualError = null as unknown as string,
}: LabelProps) => {
	const {
		getFieldState,
		formState: { touchedFields, isSubmitted, submitCount },
	} = useFormContext() || useForm()
	const isTouched = _.has(touchedFields, name) || _.has(touchedFields, fieldName)
	const hasSubmitted = isSubmitted || submitCount > 0
	const error =
		getFieldState(`manual.${name}`)?.error ||
		getFieldState(name)?.error ||
		getFieldState(fieldName)?.error
	const errorContext: ShowErrorContextType = useContext(ShowErrorContext)
	const feilmelding = error?.message
	const wrapClass = cn('skjemaelement', containerClass, {
		error: Boolean(
			(!_.isEmpty(feilmelding) && (errorContext?.showError || isTouched || hasSubmitted)) ||
				manualError,
		),
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
			{!_.isEmpty(feilmelding) && (errorContext?.showError || isTouched || hasSubmitted) && (
				<div role="alert" aria-live="assertive">
					<div className="skjemaelement__feilmelding">{feilmelding}</div>
				</div>
			)}
			{manualError && (
				<div role="alert" aria-live="assertive">
					<div className="skjemaelement__feilmelding">{manualError}</div>
				</div>
			)}
		</div>
	)
}
