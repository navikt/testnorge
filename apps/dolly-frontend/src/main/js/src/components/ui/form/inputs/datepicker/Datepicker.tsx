import { addYears, subYears } from 'date-fns'
import { Vis } from '@/components/bestillingsveileder/VisAttributt'
import { SyntEvent } from '@/components/ui/form/formUtils'
import { useFormContext } from 'react-hook-form'
import { DatePicker, useDatepicker } from '@navikt/ds-react'
import React, { BaseSyntheticEvent, useCallback, useEffect, useState } from 'react'
import { InputWrapper } from '@/components/ui/form/inputWrapper/InputWrapper'
import { Label } from '@/components/ui/form/inputs/label/Label'
import { formatDate } from '@/utils/DataFormatter'
import { convertInputToDate } from '@/components/ui/form/DateFormatUtils'
import { DateInput } from '@/components/ui/form/inputs/datepicker/DateInput'

type DollyDatepickerProps = {
	name: string
	duplicateName?: string
	label?: string
	excludeDates?: Date[]
	disabled?: boolean
	onChange?: (date: Date | null) => void
	minDate?: Date
	maxDate?: Date
	format?: string
	[key: string]: any
}

export const DollyDatepicker = ({
	excludeDates,
	disabled,
	onChange,
	minDate = subYears(new Date(), 125),
	name,
	duplicateName,
	label,
	maxDate = addYears(new Date(), 5),
	format,
	...props
}: DollyDatepickerProps) => {
	const dateFormat = format || 'DD.MM.YYYY'
	const formMethods = useFormContext()
	const existingValue = formMethods.watch(name)
	const [showDatepicker, setShowDatepicker] = useState(false)
	const [input, setInput] = useState('')

	const { datepickerProps, setSelected } = useDatepicker({
		fromDate: minDate,
		toDate: maxDate,
		disabled: excludeDates,
	})

	useEffect(() => {
		const date = convertInputToDate(existingValue)
		const formattedDate = date?.isValid?.() ? formatDate(date, dateFormat) : ''
		setInput(formattedDate)
		validateDate(date)
	}, [])

	const validateDate = (date: any) => {
		formMethods.clearErrors(`manual.${name}`)
		formMethods.clearErrors(name)

		if (!date) return

		if (!date.isValid?.()) {
			formMethods.setError(`manual.${name}`, {
				type: 'invalid-date-format',
				message: 'Ugyldig dato-format',
			})
		} else if (date.isAfter?.(maxDate) || date.isBefore?.(minDate)) {
			formMethods.setError(`manual.${name}`, {
				type: 'invalid-date',
				message: 'Dato utenfor gyldig periode',
			})
		}
	}

	const setFormDate = (date: any) => {
		if (date === '') date = null

		try {
			if (!date || date.isValid?.()) {
				const formDate = date?.isValid?.() ? date?.toDate() : date
				onChange?.(formDate)

				const dateStr = formDate?.toISOString?.().substring?.(0, 19)
				formMethods.setValue(name, dateStr || date, { shouldTouch: true })
				duplicateName && formMethods.setValue(duplicateName, dateStr || date, { shouldTouch: true })
				validateDate(date)
			}
		} catch (err) {
			console.warn('Ugyldig dato')
		}
	}

	const handleInputChange = (e: BaseSyntheticEvent) => {
		const value = e.target.value
		setInput(value)

		if (value && value.length >= 10) {
			try {
				const date = convertInputToDate(value, true)
				if (date?.isValid?.()) {
					setFormDate(date)
				}
			} catch (err) {}
		}
	}

	const handleInputBlur = (e: BaseSyntheticEvent) => {
		const value = e.target.value

		if (!value) {
			setFormDate(null)
			setInput('')
			return
		}

		try {
			const date = convertInputToDate(value, true)

			if (date?.isValid?.()) {
				setSelected(date.toDate?.())
				setFormDate(date)
				setInput(formatDate(date, dateFormat))
			} else {
				validateDate(date)
			}
		} catch (err) {
			validateDate(null)
		}
	}

	const handleCalendarClick = () => {
		if (!disabled) {
			setShowDatepicker((prev) => !prev)
		}
	}

	const dateInputProps = {
		...props,
		name: props.name || name,
		onChange: handleInputChange,
		onBlur: handleInputBlur,
		isDisabled: disabled,
		value: input,
		datepickerOnclick: handleCalendarClick,
	}

	return (
		<InputWrapper {...props}>
			<Label name={name} label={label}>
				{showDatepicker ? (
					<DatePicker
						{...datepickerProps}
						onSelect={(val: Date | undefined) => {
							setSelected(val)
							const date = convertInputToDate(val, true)
							setFormDate(date)
							setInput(formatDate(date, dateFormat))
							setShowDatepicker(false)
						}}
						onClose={() => setShowDatepicker(false)}
						open
						dropdownCaption
					>
						<DateInput {...dateInputProps} />
					</DatePicker>
				) : (
					<DateInput {...dateInputProps} />
				)}
			</Label>
		</InputWrapper>
	)
}

const P_FormDatepicker = (props: any) => {
	const formMethods = useFormContext()
	const value = formMethods.watch(props.name)

	const handleChange = useCallback(
		(date: any) => {
			formMethods.setValue(props.name, date, { shouldTouch: true })
			props.duplicateName && formMethods.setValue(props.duplicateName, date, { shouldTouch: true })
			props.onChange?.(date)
			formMethods.trigger(props.name)
		},
		[formMethods, props],
	)

	const handleBlur = useCallback(() => {
		props?.onBlur?.(SyntEvent(props.name, value))
		formMethods.setValue(props.name, value, { shouldTouch: true })
		props.duplicateName && formMethods.setValue(props.duplicateName, value, { shouldTouch: true })
		formMethods.trigger(props.name)
	}, [props, formMethods, value])

	return <DollyDatepicker onChange={handleChange} onBlur={handleBlur} {...props} />
}

export const FormDatepicker = ({ visHvisAvhuket = true, ...props }) => {
	const component = <P_FormDatepicker {...props} />
	return visHvisAvhuket ? <Vis attributt={props.name}>{component}</Vis> : component
}
