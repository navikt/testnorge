import { registerLocale } from 'react-datepicker'
import { addYears, subYears } from 'date-fns'
import locale_nb from 'date-fns/locale/nb'
import { Vis } from '@/components/bestillingsveileder/VisAttributt'
import { fixTimezone, SyntEvent } from '@/components/ui/form/formUtils'
import 'react-datepicker/dist/react-datepicker.css'
import './Datepicker.less'
import { useFormContext } from 'react-hook-form'
import { DatePicker } from '@navikt/ds-react'
import React, { BaseSyntheticEvent, useCallback, useEffect, useState } from 'react'
import { TextInput } from '@/components/ui/form/inputs/textInput/TextInput'
import { InputWrapper } from '@/components/ui/form/inputWrapper/InputWrapper'
import { Label } from '@/components/ui/form/inputs/label/Label'
import { formatDate } from '@/utils/DataFormatter'
import dayjs from 'dayjs'
import customParseFormat from 'dayjs/plugin/customParseFormat'
import { convertInputToDate } from '@/components/ui/form/DateFormatUtils'

registerLocale('nb', locale_nb)
dayjs.extend(customParseFormat)

export const DollyDatepicker = (props) => {
	const formMethods = useFormContext()
	const existingValue = formMethods.watch(props.name)
	const [showDatepicker, setShowDatepicker] = useState(false)
	const [input, setInput] = useState(existingValue ? formatDate(existingValue) : '')
	const [error, setError] = useState(existingValue ? formatDate(existingValue) : '')

	useEffect(() => {
		const date = convertInputToDate(existingValue)
		setInput(date?.isValid?.() ? formatDate(date) : existingValue)
		validateDate(date)
	}, [])

	useEffect(() => {
		if (error) {
			formMethods.setError(props.name, {
				type: 'invalid-date-format',
				message: 'Ugyldig dato-format',
			})
		} else {
			formMethods.clearErrors(props.name)
		}
	}, [error])

	const validateDate = (date) => {
		if (!date || date.isValid?.()) {
			setError(null)
			formMethods.clearErrors(props.name)
		} else if (date.isAfter?.(props.maxDate) || date.isBefore?.(props.minDate)) {
			formMethods.setError(props.name, {
				type: 'invalid-date',
				message: 'Dato utenfor gyldig periode',
			})
		} else {
			setError('Ugyldig dato-format')
			formMethods.setError(props.name, {
				type: 'invalid-date-format',
				message: 'Ugyldig dato-format',
			})
		}
	}
	const handleInputBlur = (e: BaseSyntheticEvent) => {
		const value = e.target.value
		let date = convertInputToDate(value)
		if (!date.isValid?.()) {
			date = value
		}

		formMethods.setValue(props.name, date, { shouldTouch: true })
		setInput(formatDate(date))
		formMethods.trigger(props.name).then(() => {
			validateDate(date)
		})
	}

	const DateInput = (
		<TextInput
			{...props}
			onChange={(e: BaseSyntheticEvent) => {
				const value = e.target.value
				const date = convertInputToDate(value)
				formMethods.setValue(props.name, date, { shouldTouch: true })
				setInput(value)
				formMethods.trigger(props.name).then(() => {
					validateDate(date)
				})
			}}
			onBlur={handleInputBlur}
			isDisabled={props.disabled}
			value={input}
			icon={'calendar'}
			datepickerOnclick={() => {
				if (!props.disabled) {
					setShowDatepicker((prev) => !prev)
				}
			}}
		/>
	)

	return (
		<InputWrapper {...props}>
			<Label name={props.name} label={props.label}>
				{(showDatepicker && (
					<DatePicker
						fromDate={props.minDate || subYears(new Date(), 125)}
						toDate={props.maxDate || addYears(new Date(), 5)}
						disabled={props.excludeDates}
						onSelect={(val) => {
							const date = convertInputToDate(val)
							validateDate(date)
							props.onChange?.(date)
							setInput(formatDate(date))
							setShowDatepicker(false)
						}}
						onClose={() => setShowDatepicker(false)}
						open={true}
						dropdownCaption
					>
						{DateInput}
					</DatePicker>
				)) ||
					DateInput}
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
			props.onChange?.(val)
			props.afterChange?.(date)
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
