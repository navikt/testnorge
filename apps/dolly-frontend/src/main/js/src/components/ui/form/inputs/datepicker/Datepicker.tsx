import { registerLocale } from 'react-datepicker'
import { addYears, subYears } from 'date-fns'
import locale_nb from 'date-fns/locale/nb'
import { Vis } from '@/components/bestillingsveileder/VisAttributt'
import { fixTimezone, SyntEvent } from '@/components/ui/form/formUtils'
import 'react-datepicker/dist/react-datepicker.css'
import './Datepicker.less'
import { useFormContext } from 'react-hook-form'
import { DatePicker, useDatepicker } from '@navikt/ds-react'
import React, { BaseSyntheticEvent, useCallback, useEffect, useMemo, useState } from 'react'
import styled from 'styled-components'
import _ from 'lodash'
import { TextInput } from '@/components/ui/form/inputs/textInput/TextInput'
import { InputWrapper } from '@/components/ui/form/inputWrapper/InputWrapper'
import { Label } from '@/components/ui/form/inputs/label/Label'
import { formatDate } from '@/utils/DataFormatter'

registerLocale('nb', locale_nb)

const StyledInput = styled(DatePicker.Input)`
	&&& {
		p {
			font-style: italic;
			font-weight: normal;
			color: #ba3a26;
			margin-top: 0.5rem;

			&::before {
				content: none;
			}
		}
	}
`

export const Datepicker = ({
	name,
	placeholder = 'Ikke spesifisert',
	onChange,
	onBlur,
	disabled = false,
	excludeDates,
	minDate,
	maxDate,
}: any) => {
	const formMethods = useFormContext()
	const selectedDate = formMethods.watch(name) ? new Date(formMethods.watch(name)) : undefined

	const [hasError, setHasError] = useState(false)
	const [open, setOpen] = useState(true)
	const handleDateChange = (date) => {
		setOpen(false)
		onChange ? onChange(date) : onBlur(date)
	}

	const datepickerConfig = useMemo(
		() => ({
			fromDate: minDate || subYears(new Date(), 125),
			toDate: maxDate || addYears(new Date(), 5),
			onDateChange: handleDateChange,
			disabled: excludeDates,
			defaultSelected: selectedDate,
			onValidate: (date) => {
				setHasError(!date.isValidDate && !date.isEmpty)
			},
		}),
		[minDate, maxDate, onChange, onBlur, excludeDates, selectedDate],
	)

	const { datepickerProps, inputProps, setSelected } = useDatepicker(datepickerConfig)

	useEffect(() => {
		const [day, month, year] =
			inputProps.value && inputProps.value !== '' && inputProps.value?.split('.')
		const inputDate = (year && new Date(year, month - 1, day)) || undefined
		if (selectedDate && !_.isEqual(inputDate?.toDateString(), selectedDate?.toDateString())) {
			setTimeout(() => {
				setSelected(selectedDate)
			}, 200)
		}
	}, [inputProps.value, selectedDate])

	const handleChange = useCallback(
		(e) => {
			setSelected(undefined)
			inputProps.onChange(e)
			formMethods.trigger(name)
			setOpen(false)
		},
		[inputProps, setSelected],
	)

	useEffect(() => {
		formMethods.trigger(name)
	}, [open])

	return (
		<DatePicker
			{...datepickerProps}
			open={open || undefined}
			onAbort={() => {
				setOpen(false)
			}}
			dropdownCaption={true}
		>
			<StyledInput
				{...inputProps}
				placeholder={placeholder}
				onChange={handleChange}
				size={'small'}
				disabled={disabled}
				label={null}
				error={hasError && 'Ugyldig dato-format'}
			/>
		</DatePicker>
	)
}

export const DollyDatepicker = (props) => {
	const formMethods = useFormContext()
	const existingValue = formMethods.watch(props.name) && new Date(formMethods.watch(props.name))
	const [showDatepicker, setShowDatepicker] = useState(false)
	const [input, setInput] = useState(
		existingValue && _.isDate(existingValue) ? formatDate(existingValue) : existingValue,
	)

	const toggleDatepicker = useCallback(() => {
		setShowDatepicker((prev) => !prev)
	}, [])

	const handleInputBlur = (e: any) => {
		const inputValue = e.target.value
		console.log('inputValue: ', inputValue) //TODO - SLETT MEG
		const [day, month, year] = inputValue && inputValue !== '' ? inputValue.split('.') : []
		const inputDate = (year && new Date(year, month - 1, day)) || inputValue
		formMethods.setValue(props.name, inputDate)
		setInput(_.isDate(inputDate) ? formatDate(inputDate) : inputValue)
		_.isDate(inputDate) && props.onChange(inputDate)
	}

	return (
		<InputWrapper {...props}>
			<Label name={props.name} label={props.label}>
				{(!showDatepicker && (
					<TextInput
						{...props}
						onChange={(e: BaseSyntheticEvent) => setInput(e.target.value)}
						onBlur={handleInputBlur}
						value={input}
						icon={'calendar'}
						datepickerOnclick={toggleDatepicker}
					/>
				)) || <Datepicker {...props} />}
			</Label>
		</InputWrapper>
	)
}

const P_FormDatepicker = ({ ...props }) => {
	const formMethods = useFormContext()
	const value = formMethods.watch(props.name)

	const handleChange = (date: any) => {
		let val = fixTimezone(date)?.toISOString().substring(0, 19) || null

		formMethods.setValue(props.name, val, { shouldTouch: true })
		if (props.afterChange) {
			props.afterChange(date)
		}
		formMethods.trigger(props.name)
	}
	const handleBlur = () => {
		props?.onBlur?.(SyntEvent(props.name, value))
		formMethods.setValue(props.name, value, { shouldTouch: true })
		formMethods.trigger(props.name)
	}
	return <DollyDatepicker value={value} onChange={handleChange} onBlur={handleBlur} {...props} />
}

export const FormDatepicker = ({ visHvisAvhuket = true, ...props }) => {
	const component = <P_FormDatepicker {...props} />
	return visHvisAvhuket ? <Vis attributt={props.name}>{component}</Vis> : component
}
