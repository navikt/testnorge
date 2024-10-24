import { registerLocale } from 'react-datepicker'
import { addYears, subYears } from 'date-fns'
import locale_nb from 'date-fns/locale/nb'
import { Label } from '@/components/ui/form/inputs/label/Label'
import { InputWrapper } from '@/components/ui/form/inputWrapper/InputWrapper'
import { Vis } from '@/components/bestillingsveileder/VisAttributt'
import { fixTimezone, SyntEvent } from '@/components/ui/form/formUtils'
import 'react-datepicker/dist/react-datepicker.css'
import './Datepicker.less'
import { useFormContext } from 'react-hook-form'
import { DatePicker, useDatepicker } from '@navikt/ds-react'
import { useEffect, useState } from 'react'
import styled from 'styled-components'
import _ from 'lodash'

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
}) => {
	const formMethods = useFormContext()
	const selectedDate = formMethods.watch(name) ? new Date(formMethods.watch(name)) : undefined

	const [hasError, setHasError] = useState(false)

	const { datepickerProps, inputProps, setSelected } = useDatepicker({
		fromDate: minDate || subYears(new Date(), 125),
		toDate: maxDate || addYears(new Date(), 5),
		onDateChange: onChange || onBlur,
		disabled: excludeDates,
		defaultSelected: selectedDate,
		onValidate: (date) => {
			setHasError(!date.isValidDate && !date.isEmpty)
		},
	})

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

	return (
		<DatePicker {...datepickerProps} dropdownCaption={true}>
			<StyledInput
				{...inputProps}
				placeholder={placeholder}
				onChange={(e) => {
					setSelected(undefined)
					inputProps.onChange(e)
					formMethods.trigger(name)
				}}
				size={'small'}
				disabled={disabled}
				label={null}
				error={hasError && 'Ugyldig dato-format'}
			/>
		</DatePicker>
	)
}

export const DollyDatepicker = (props) => (
	<InputWrapper {...props}>
		<Label name={props.name} label={props.label}>
			<Datepicker {...props} />
		</Label>
	</InputWrapper>
)

const P_FormDatepicker = ({ ...props }) => {
	const formMethods = useFormContext()
	const value = formMethods.watch(props.name)
	const handleChange = (date) => {
		if (props.afterChange) {
			props.afterChange(date)
		}
		let val = fixTimezone(date)?.toISOString().substring(0, 19) || null
		formMethods.setValue(props.name, val, { shouldTouch: true })
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
