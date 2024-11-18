import ReactDatepicker, { registerLocale } from 'react-datepicker'
import { addYears, subYears } from 'date-fns'
import locale_nb from 'date-fns/locale/nb'
import { TextInput } from '@/components/ui/form/inputs/textInput/TextInput'
import { Label } from '@/components/ui/form/inputs/label/Label'
import { InputWrapper } from '@/components/ui/form/inputWrapper/InputWrapper'
import { Vis } from '@/components/bestillingsveileder/VisAttributt'
import 'react-datepicker/dist/react-datepicker.css'
import { useFormContext } from 'react-hook-form'
import { BaseSyntheticEvent, useEffect, useState } from 'react'
import { convertInputToDate } from '@/components/ui/form/DateFormatUtils'
import { formatDate } from '@/utils/DataFormatter'

registerLocale('nb', locale_nb)

export const TimePicker = ({
	name,
	dateFormat = 'dd.MM.yyyy HH:mm',
	placeholder = 'Ikke spesifisert',
	onChange,
	disabled = false,
	excludeDates,
	minDate,
	maxDate,
}) => {
	const formMethods = useFormContext()
	const existingValue = formMethods.watch(name)
	const [showDatepicker, setShowDatepicker] = useState(false)
	const [selected, setSelected] = useState(existingValue)
	const [input, setInput] = useState(formatDate(existingValue, 'DD.MM.YYYY HH:mm'))
	const [errorMessage, setErrorMessage] = useState('')

	const validateDate = (date) => {
		if (!date || date.isValid?.()) {
			setErrorMessage('')
			formMethods.clearErrors(name)
		} else if (date.isAfter?.(maxDate) || date.isBefore?.(minDate)) {
			setErrorMessage('Dato utenfor gyldig periode')
			formMethods.setError(name, {
				type: 'invalid-date',
				message: 'Dato utenfor gyldig periode',
			})
		} else {
			setErrorMessage('Ugyldig dato-format')
			formMethods.setError(name, {
				type: 'invalid-date-format',
				message: 'Ugyldig dato-format',
			})
		}
	}

	useEffect(() => {
		if (errorMessage) {
			formMethods.setError(name, {
				type: 'invalid-date',
				message: errorMessage,
			})
		} else {
			formMethods.clearErrors(name)
		}
	}, [errorMessage])

	const setFormDate = (date) => {
		const formDate = date.isValid?.() ? date.toDate() : date
		onChange?.(formDate)
		formMethods.setValue(name, formDate, {
			shouldTouch: true,
		})
		formMethods.trigger(name).then(() => {
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
		setInput(formatDate(date, 'DD.MM.YYYY HH:mm'))
	}
	console.log('input: ', input) //TODO - SLETT MEG

	return (
		<ReactDatepicker
			open={showDatepicker}
			locale="nb"
			selected={selected}
			dateFormat={dateFormat}
			placeholderText={placeholder}
			onClickOutside={() => {
				setTimeout(() => {
					setShowDatepicker(false)
				}, 150)
			}}
			onChangeRaw={(e: any) => {
				const date = e.target.value
				if (!date) {
					return
				}
				const formattedDate = convertInputToDate(date)
				if (formattedDate?.isValid?.()) {
					setSelected(formattedDate.toDate())
				}

				setFormDate(formattedDate)
				// setShowDatepicker(false)
			}}
			onChange={(date) => {
				const formattedDate = convertInputToDate(date)
				setSelected(date)
				setFormDate(formattedDate)
				// setShowDatepicker(false)
			}}
			showMonthDropdown
			showYearDropdown
			showTimeSelect
			timeInputLabel={'Tid'}
			minDate={minDate || subYears(new Date(), 100)}
			maxDate={maxDate || addYears(new Date(), 5)}
			dropdownMode="select"
			disabled={disabled}
			name={name}
			id={name}
			autoComplete="off"
			excludeDates={excludeDates}
			customInput={
				<TextInput
					name={name}
					input={input}
					afterChange={handleInputBlur}
					datepickerOnclick={() => {
						if (!disabled) {
							setShowDatepicker((prev) => !prev)
						}
					}}
					icon={'calendar'}
				/>
			}
		/>
	)
}

export const DollyTimepicker = (props) => (
	<InputWrapper {...props}>
		<Label name={props.name} label={props.label}>
			<TimePicker {...props} />
		</Label>
	</InputWrapper>
)

export const FormDateTimepicker = ({ visHvisAvhuket = true, ...props }) => {
	const component = <DollyTimepicker {...props} />
	return visHvisAvhuket ? <Vis attributt={props.name}>{component}</Vis> : component
}
