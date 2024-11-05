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
import { TextInput } from '@/components/ui/form/inputs/textInput/TextInput'
import { InputWrapper } from '@/components/ui/form/inputWrapper/InputWrapper'
import { Label } from '@/components/ui/form/inputs/label/Label'
import { formatDate } from '@/utils/DataFormatter'
import moment from 'moment'

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
	toggleDatepicker,
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

	const { datepickerProps, inputProps } = useDatepicker(datepickerConfig)

	useEffect(() => {
		formMethods.trigger(name)
	}, [open])

	return (
		<DatePicker
			{...datepickerProps}
			open={open || undefined}
			onAbort={() => {
				setOpen(false)
				toggleDatepicker?.()
			}}
			onClose={() => {
				setOpen(false)
				toggleDatepicker?.()
			}}
			onOpenToggle={() => {
				setOpen(false)
				toggleDatepicker?.()
			}}
			dropdownCaption={true}
		>
			<StyledInput
				{...inputProps}
				placeholder={placeholder}
				size={'small'}
				disabled={disabled}
				label={null}
				error={hasError && 'Ugyldig dato-format'}
			/>
		</DatePicker>
	)
}

const isValidDate = (date) => {
	return moment(date, ['DD-MM-YYYY', moment.ISO_8601], true).isValid()
}

export const DollyDatepicker = (props) => {
	const formMethods = useFormContext()
	const existingValue = formMethods.watch(props.name)
	const [showDatepicker, setShowDatepicker] = useState(false)
	const [input, setInput] = useState(
		existingValue && isValidDate(existingValue) ? formatDate(existingValue) : existingValue,
	)
	const toggleDatepicker = useCallback(() => {
		setShowDatepicker((prev) => !prev)
	}, [])

	useEffect(() => {
		setInput(
			existingValue && isValidDate(existingValue) ? formatDate(existingValue) : existingValue,
		)
	}, [showDatepicker, existingValue])

	const formatYear = (year: string) => {
		if (year.length === 2) {
			return Number(`${year < '36' ? '20' : '19'}${year}`)
		}
		return Number(year)
	}

	const handleInputBlur = (e: any) => {
		const [day, month, year] = e.target.value.split(/[-./]/)
		const inputDate = year ? new Date(formatYear(year), month - 1, day) : e.target.value
		const fixedDate = fixTimezone(inputDate)

		formMethods.setValue(props.name, fixedDate)
		setInput(isValidDate(fixedDate) ? formatDate(fixedDate) : e.target.value)
		if (isValidDate(fixedDate)) {
			props.onChange(fixedDate)
		}
	}

	return (
		<InputWrapper {...props}>
			<Label name={props.name} label={props.label}>
				{(!showDatepicker && (
					<TextInput
						{...props}
						name={null}
						onChange={(e: BaseSyntheticEvent) => {
							const fixedDate = fixTimezone(e.target.value)
							setInput(fixedDate)
							formMethods.setValue(props.name, fixedDate)
						}}
						onBlur={handleInputBlur}
						value={input}
						icon={'calendar'}
						datepickerOnclick={toggleDatepicker}
					/>
				)) || <Datepicker {...props} toggleDatepicker={toggleDatepicker} />}
			</Label>
		</InputWrapper>
	)
}

const P_FormDatepicker = ({ ...props }) => {
	const formMethods = useFormContext()
	const value = formMethods.watch(props.name)

	const handleChange = useCallback(
		(date: any) => {
			let val = fixTimezone(date)?.toISOString().substring(0, 19) || null

			formMethods.setValue(props.name, val, { shouldTouch: true })
			if (props.afterChange) {
				props.afterChange(date)
			}
			formMethods.trigger(props.name)
		},
		[formMethods, props.afterChange, props],
	)
	const handleBlur = useCallback(() => {
		props?.onBlur?.(SyntEvent(props.name, value))
		formMethods.setValue(props.name, value, { shouldTouch: true })
		formMethods.trigger(props.name)
	}, [props, formMethods])

	return <DollyDatepicker onChange={handleChange} onBlur={handleBlur} {...props} />
}

export const FormDatepicker = ({ visHvisAvhuket = true, ...props }) => {
	const component = <P_FormDatepicker {...props} />
	return visHvisAvhuket ? <Vis attributt={props.name}>{component}</Vis> : component
}
