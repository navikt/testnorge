import { registerLocale } from 'react-datepicker'
import { addYears, subYears } from 'date-fns'
import locale_nb from 'date-fns/locale/nb'
import { Vis } from '@/components/bestillingsveileder/VisAttributt'
import { convertInputToDate, fixTimezone, SyntEvent } from '@/components/ui/form/formUtils'
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

registerLocale('nb', locale_nb)
dayjs.extend(customParseFormat)

export const VALID_DATE_FORMATS = [
	'DDMMYYYY',
	'DDMMYY',
	'DD-MM-YYYY',
	'DD-MM-YY',
	'DD.MM.YYYY',
	'DD.MM.YY',
	'DD/MM/YYYY',
	'DD/MM/YY',
	'YYYY.MM.DD',
	'YYYY-MM-DD',
	'YYYY/MM/DD',
	'YYYY-MM-DDTHH:mm:ss.SSSZ',
	'YYYY-MM-DDTHH:mm:ss',
]

export const DollyDatepicker = (props) => {
	const formMethods = useFormContext()
	const existingValue = formMethods.watch(props.name)
	const [showDatepicker, setShowDatepicker] = useState(false)
	const [input, setInput] = useState(existingValue && formatDate(existingValue))

	useEffect(() => {
		const date = convertInputToDate(existingValue, props.name)
		setInput(date ? formatDate(date) : existingValue)
	}, [])

	const handleInputBlur = (e: BaseSyntheticEvent) => {
		const value = e.target.value
		const date = convertInputToDate(value, props.name)

		formMethods.setValue(props.name, date, { shouldTouch: true })
		setInput(date ? formatDate(date) : value)
		if (!date || (date.isValid() && date.isAfter(props.minDate) && date.isBefore(props.maxDate))) {
			formMethods.clearErrors(props.name)
		} else {
			formMethods.setError(props.name, {
				type: 'invalid-date',
				message: 'Ugyldig dato',
			})
		}
	}

	const DateInput = (
		<TextInput
			{...props}
			onChange={(e: BaseSyntheticEvent) => {
				const value = e.target.value
				const date = convertInputToDate(value, props.name)
				formMethods.setValue(props.name, date, { shouldTouch: true })
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
						fromDate={props.minDate || subYears(new Date(), 125)}
						toDate={props.maxDate || addYears(new Date(), 5)}
						disabled={props.excludeDates}
						onSelect={(val) => {
							const date = convertInputToDate(val, props.name)
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
