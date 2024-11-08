import { registerLocale } from 'react-datepicker'
import { addYears, isDate, subYears } from 'date-fns'
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
import moment from 'moment'

registerLocale('nb', locale_nb)

const VALID_DATE_FORMATS = [
	'DDMMYYYY',
	'DDMMYY',
	'YYYY-MM-DD',
	'YYYY/MM/DD',
	'DD-MM-YYYY',
	'DD-MM-YY',
	'DD.MM.YYYY',
	'DD.MM.YY',
	'DD/MM/YYYY',
	'DD/MM/YY',
	moment.ISO_8601,
]

export const DollyDatepicker = (props) => {
	const formMethods = useFormContext()
	const existingValue = formMethods.watch(props.name)
	const [showDatepicker, setShowDatepicker] = useState(false)
	const [input, setInput] = useState(existingValue)

	useEffect(() => {
		setInput(
			existingValue && ValidateDateFormat(existingValue)
				? formatDate(existingValue)
				: existingValue,
		)
	}, [])

	const ValidateDateFormat = (date) => {
		const valid = moment(date, VALID_DATE_FORMATS, true).isValid()
		if (valid) {
			formMethods.clearErrors(props.name)
		} else {
			formMethods.setError(props.name, {
				type: 'invalid-date-format',
				message: 'Ugyldig dato-format',
			})
		}
		return valid
	}

	const formatYear = (year: string) => {
		if (year.length === 2) {
			return Number(`${year < '36' ? '20' : '19'}${year}`)
		}
		return Number(year)
	}

	const handleInputBlur = (e: BaseSyntheticEvent) => {
		const value = e.target.value
		const splitChars = /[-./]/
		let [dayOrYear, month, yearOrDay] = splitChars.test(value)
			? value.split(splitChars)
			: [value.slice(0, 2), value.slice(2, 4), value.slice(4)] //Split dd mm yy(yy)

		if (dayOrYear > 31) {
			;[dayOrYear, yearOrDay] = [yearOrDay, dayOrYear] //Swap day and year
		}

		const inputDate = yearOrDay ? new Date(formatYear(yearOrDay), month - 1, dayOrYear) : value
		const fixedDate = isDate(value) ? value : fixTimezone(inputDate)

		formMethods.setValue(props.name, fixedDate)
		setInput(ValidateDateFormat(fixedDate) ? formatDate(fixedDate) : value)
		props.onChange(fixedDate)
		if (moment(fixedDate).isBetween(props.minDate, props.maxDate)) {
			formMethods.clearErrors(props.name)
		} else
			formMethods.setError(props.name, {
				type: 'invalid-date-range',
				message: 'Ugyldig dato',
			})
	}

	const DateInput = (
		<TextInput
			{...props}
			onChange={(e: BaseSyntheticEvent) => {
				const fixedDate = fixTimezone(e.target.value)
				setInput(fixedDate)
				formMethods.setValue(props.name, fixedDate)
				ValidateDateFormat(fixedDate)
			}}
			onBlur={handleInputBlur}
			value={input}
			icon={'calendar'}
			datepickerOnclick={() => setShowDatepicker((prev) => !prev)}
		/>
	)

	return (
		<InputWrapper {...props}>
			<Label name={props.name} label={props.label}>
				{(showDatepicker && (
					<div className="navds-date__wrapper">
						<div className="navds-popover navds-date__popover">
							<DatePicker
								fromDate={props.minDate || subYears(new Date(), 125)}
								toDate={props.maxDate || addYears(new Date(), 5)}
								disabled={props.excludeDates}
								onSelect={(val) => {
									const fixedDate = fixTimezone(val)
									formMethods.setValue(props.name, fixedDate)
									setInput(formatDate(fixedDate))
									formMethods.trigger(props.name)
									setShowDatepicker(false)
								}}
								// onClose={() => setShowDatepicker(false)}
								// open={true}
								dropdownCaption
							>
								{DateInput}
							</DatePicker>
						</div>
					</div>
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
