import React, { useEffect, useState } from 'react'
import cn from 'classnames'
import { useFormContext } from 'react-hook-form'
import * as _ from 'lodash-es'
import { ShowErrorContext } from '@/components/bestillingsveileder/ShowErrorContext'
import Icon from '@/components/ui/icon/Icon'
import { Button } from '@navikt/ds-react'
import styled from 'styled-components'

const CalendarButton = styled(Button)`
	position: absolute;
	height: 37px;
	translate: -31px 1px;
	padding: 5px 3px 0 3px;
	margin: 0;

	svg {
		translate: -1px 1px;
	}
`

type DateInputProps = {
	name: string
	fieldName?: string
	className?: string
	placeholder?: string
	isDisabled?: boolean
	autoFocus?: boolean
	value?: string
	defaultValue?: string
	style?: React.CSSProperties
	'data-testid'?: string
	manualError?: string
	datepickerOnclick: React.MouseEventHandler<HTMLButtonElement>
	onChange?: (event: React.ChangeEvent<HTMLInputElement>) => void
	onBlur?: (event: React.FocusEvent<HTMLInputElement>) => void
	onClick?: (event: React.MouseEvent<HTMLInputElement>) => void
	onFocus?: (event: React.FocusEvent<HTMLInputElement>) => void
	onKeyDown?: (event: React.KeyboardEvent<HTMLInputElement>) => void
	afterChange?: (event: React.FocusEvent<HTMLInputElement>) => void
}

export const DateInput = ({
	placeholder = 'DD.MM.ÅÅÅÅ',
	name,
	fieldName,
	className,
	isDisabled,
	datepickerOnclick,
	autoFocus,
	value,
	defaultValue,
	style,
	'data-testid': dataTestId,
	...props
}: DateInputProps) => {
	const { register, formState, watch } = useFormContext() || {}
	const { showError } = React.useContext(ShowErrorContext) || {}

	const [fieldValue, setFieldValue] = useState(
		value ?? defaultValue ?? (name ? watch(name) || '' : ''),
	)

	const { onBlur: registerOnBlur } = name && register ? register(name) : {}

	const error =
		formState?.errors &&
		(_.get(formState.errors, `manual.${name}`) ||
			_.get(formState.errors, name) ||
			(fieldName && _.get(formState.errors, fieldName)))

	const isTouched =
		formState?.touchedFields &&
		((name && _.has(formState.touchedFields, name)) ||
			(fieldName && _.has(formState.touchedFields, fieldName)))

	const shouldShowError = (error && (showError || isTouched)) || !!props.manualError

	useEffect(() => {
		const externalValue = value ?? (name ? watch(name) : undefined)
		if (externalValue !== undefined && externalValue !== fieldValue) {
			setFieldValue(externalValue || '')
		}
	}, [value, watch, name, fieldValue])

	const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
		setFieldValue(e.target.value)
		props.onChange?.(e)
	}

	const handleBlur = (e: React.FocusEvent<HTMLInputElement>) => {
		registerOnBlur?.(e)
		props.onBlur?.(e)
		props.afterChange?.(e)
	}

	const inputClassNames = cn('skjemaelement__input', className, {
		'skjemaelement__input--harFeil': shouldShowError,
	})

	return (
		<>
			<input
				id={name}
				name={name}
				value={fieldValue || ''}
				type="text"
				disabled={isDisabled}
				autoFocus={autoFocus}
				className={inputClassNames}
				placeholder={placeholder}
				onChange={handleChange}
				onBlur={handleBlur}
				onClick={props.onClick}
				onFocus={props.onFocus}
				onKeyDown={props.onKeyDown}
				style={style}
				data-testid={dataTestId}
			/>
			<CalendarButton variant="tertiary" onClick={datepickerOnclick}>
				<Icon kind="calendar" style={{ color: 'black' }} fontSize="1.5rem" />
			</CalendarButton>
		</>
	)
}
