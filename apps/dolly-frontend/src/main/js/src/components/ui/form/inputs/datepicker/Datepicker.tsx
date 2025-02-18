import { addYears, subYears } from 'date-fns'
import { Vis } from '@/components/bestillingsveileder/VisAttributt'
import { SyntEvent } from '@/components/ui/form/formUtils'
import { useFormContext } from 'react-hook-form'
import { DatePicker, useDatepicker } from '@navikt/ds-react'
import React, { BaseSyntheticEvent, useCallback, useEffect, useState } from 'react'
import { TextInput } from '@/components/ui/form/inputs/textInput/TextInput'
import { InputWrapper } from '@/components/ui/form/inputWrapper/InputWrapper'
import { Label } from '@/components/ui/form/inputs/label/Label'
import { formatDate } from '@/utils/DataFormatter'
import { convertInputToDate } from '@/components/ui/form/DateFormatUtils'

export const DollyDatepicker = (props: any) => {
	const {
		excludeDates,
		disabled,
		onChange,
		minDate = subYears(new Date(), 125),
		name,
		label,
		maxDate = addYears(new Date(), 5),
		format,
	} = props
	const dateFormat = format || 'DD.MM.YYYY'
	const formMethods = useFormContext()
	const existingValue = formMethods.watch(name)
	const [showDatepicker, setShowDatepicker] = useState(false)
	const [input, setInput] = useState(existingValue ? formatDate(existingValue, dateFormat) : '')

	const getDatepickerProps = useCallback(() => {
		return useDatepicker({
			fromDate: minDate,
			toDate: maxDate,
			disabled: excludeDates,
		})
	}, [minDate, maxDate, excludeDates, input])

	const { datepickerProps, setSelected } = getDatepickerProps()

	useEffect(() => {
		const date = convertInputToDate(existingValue)
		setInput(date?.isValid?.() ? formatDate(date, dateFormat) : existingValue)
		formMethods.trigger(`manual.${name}`).then(() => {
			validateDate(date)
		})
	}, [])

	const validateDate = (date) => {
		if (date?.isAfter?.(maxDate) || date?.isBefore?.(minDate)) {
			formMethods.setError(`manual.${name}`, {
				type: 'invalid-date',
				message: 'Dato utenfor gyldig periode',
			})
		} else if (!date || date.isValid?.()) {
			formMethods.clearErrors(`manual.${name}`)
			formMethods.clearErrors(name)
		} else {
			formMethods.setError(`manual.${name}`, {
				type: 'invalid-date-format',
				message: 'Ugyldig dato-format',
			})
		}
	}

	const setFormDate = (date) => {
		if (date === '') {
			date = null
		}
		let formDate = date?.isValid?.() ? date?.toDate() : date
		onChange?.(formDate)
		const dateStr = formDate?.toISOString?.().substring?.(0, 19)
		formMethods.setValue(name, dateStr || date, {
			shouldTouch: true,
		})
		formMethods.trigger(`manual.${name}`).then(() => {
			validateDate(date)
		})
	}

	const handleInputBlur = (e: BaseSyntheticEvent) => {
		const value = e.target.value
		let date = convertInputToDate(value, true)
		if (date.isValid?.()) {
			setSelected(date.toDate?.())
		} else {
			date = value
		}

		setFormDate(date)
		setInput(formatDate(date, dateFormat))
		validateDate(date)
	}

	const DateInput = (
		<TextInput
			{...props}
			onChange={(e: BaseSyntheticEvent) => {
				const value = e.target.value
				const date = convertInputToDate(value, true)
				setFormDate(date)
				setInput(value)
			}}
			onBlur={handleInputBlur}
			isDisabled={disabled}
			input={input}
			icon={'calendar'}
			datepickerOnclick={() => {
				if (!disabled) {
					setShowDatepicker((prev) => !prev)
				}
			}}
		/>
	)

	return (
		<InputWrapper {...props}>
			<Label name={name} label={label}>
				{(showDatepicker && (
					<DatePicker
						{...datepickerProps}
						onSelect={(val) => {
							setSelected(val)

							const date = convertInputToDate(val, true)
							setFormDate(date)
							setInput(formatDate(date, dateFormat))
							formMethods.trigger(`manual.${name}`).then(() => {
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
