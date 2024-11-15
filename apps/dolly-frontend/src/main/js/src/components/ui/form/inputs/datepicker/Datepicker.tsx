import { registerLocale } from 'react-datepicker'
import { addYears, subYears } from 'date-fns'
import locale_nb from 'date-fns/locale/nb'
import { Vis } from '@/components/bestillingsveileder/VisAttributt'
import { SyntEvent } from '@/components/ui/form/formUtils'
import 'react-datepicker/dist/react-datepicker.css'
import './Datepicker.less'
import { useFormContext } from 'react-hook-form'
import { DatePicker, useDatepicker } from '@navikt/ds-react'
import React, { BaseSyntheticEvent, useCallback, useEffect, useState } from 'react'
import { TextInput } from '@/components/ui/form/inputs/textInput/TextInput'
import { InputWrapper } from '@/components/ui/form/inputWrapper/InputWrapper'
import { Label } from '@/components/ui/form/inputs/label/Label'
import { formatDate } from '@/utils/DataFormatter'
import { convertInputToDate } from '@/components/ui/form/DateFormatUtils'

registerLocale('nb', locale_nb)

export const DollyDatepicker = (props) => {
	const formMethods = useFormContext()
	const existingValue = formMethods.watch(props.name)
	const [showDatepicker, setShowDatepicker] = useState(false)
	const [input, setInput] = useState(existingValue ? formatDate(existingValue) : '')
	const [errorMessage, setErrorMessage] = useState(existingValue ? formatDate(existingValue) : '')

	const getDatepickerProps = useCallback(() => {
		return useDatepicker({
			fromDate: props.minDate || subYears(new Date(), 125),
			toDate: props.maxDate || addYears(new Date(), 5),
			disabled: props.excludeDates,
			inputFormat: 'dd.MM.yyyy',
		})
	}, [props.minDate, props.maxDate, props.excludeDates, input])

	const { datepickerProps, setSelected } = getDatepickerProps()

	useEffect(() => {
		const date = convertInputToDate(existingValue)
		setInput(date?.isValid?.() ? formatDate(date) : existingValue)
		formMethods.trigger(props.name).then(() => {
			validateDate(date)
		})
	}, [])

	useEffect(() => {
		if (errorMessage) {
			formMethods.setError(props.name, {
				type: 'invalid-date',
				message: errorMessage,
			})
		} else {
			formMethods.clearErrors(props.name)
		}
	}, [errorMessage])

	const validateDate = (date) => {
		if (!date || date.isValid?.()) {
			setErrorMessage(null)
			formMethods.clearErrors(props.name)
		} else if (date.isAfter?.(props.maxDate) || date.isBefore?.(props.minDate)) {
			setErrorMessage('Dato utenfor gyldig periode')
			formMethods.setError(props.name, {
				type: 'invalid-date',
				message: 'Dato utenfor gyldig periode',
			})
		} else {
			setErrorMessage('Ugyldig dato-format')
			formMethods.setError(props.name, {
				type: 'invalid-date-format',
				message: 'Ugyldig dato-format',
			})
		}
	}

	const setFormDate = (date) => {
		const formDate = date.isValid?.() ? date.toDate() : date
		props.onChange?.(formDate)
		formMethods.setValue(props.name, formDate, {
			shouldTouch: true,
		})
		formMethods.trigger(props.name).then(() => {
			validateDate(date)
		})
	}

	const handleInputBlur = (e: BaseSyntheticEvent) => {
		const value = e.target.value
		let date = convertInputToDate(value)
		if (date.isValid?.()) {
			setSelected(date.toDate?.())
		} else {
			date = value
		}

		setFormDate(date)
		setInput(formatDate(date))
	}

	const DateInput = (
		<TextInput
			{...props}
			style={{ width: '172px' }}
			onChange={(e: BaseSyntheticEvent) => {
				const value = e.target.value
				const date = convertInputToDate(value)
				setFormDate(date)
				setInput(value)
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
						{...datepickerProps}
						onSelect={(val) => {
							setSelected(val)

							const date = convertInputToDate(val)
							setFormDate(date)
							setInput(formatDate(date))
							formMethods.trigger(props.name).then(() => {
								validateDate(date)
							})
							setShowDatepicker(false)
						}}
						onClose={() => setShowDatepicker(false)}
						open
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
			formMethods.setValue(props.name, date, { shouldTouch: true })
			props.onChange?.(date)
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
