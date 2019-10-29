import React from 'react'
import ReactDatepicker, { registerLocale } from 'react-datepicker'
import { useField } from 'formik'
import { addHours, subYears, addYears, isDate } from 'date-fns'
import locale_nb from 'date-fns/locale/nb'
import { TextInput } from '~/components/ui/form/inputs/textInput/TextInput'
import { Label } from '~/components/ui/form/inputs/label/Label'
import { InputWrapper } from '~/components/ui/form/inputWrapper/InputWrapper'
import { fieldError, SyntEvent } from '~/components/ui/form/formUtils'
registerLocale('nb', locale_nb)

import 'react-datepicker/dist/react-datepicker.css'
import './Datepicker.less'

export const Datepicker = ({
	name,
	value,
	placeholder = 'Ikke spesifisert',
	onChange,
	onBlur,
	disabled = false
}) => {
	const preSave = selectedDate => {
		// Vi bryr oss ikke om klokkeslett. Legger til 3 timer for å slippe å ta hensyn til tidsforskjeller
		const modDateTime = isDate(selectedDate) ? addHours(selectedDate, 3) : ''
		return onChange(modDateTime)
	}
	return (
		<ReactDatepicker
			locale="nb"
			dateFormat="dd.MM.yyyy"
			placeholderText={placeholder}
			selected={(value && new Date(value)) || null}
			onChange={preSave}
			showMonthDropdown
			showYearDropdown
			minDate={subYears(new Date(), 100)}
			maxDate={addYears(new Date(), 5)}
			dropdownMode="select"
			disabled={disabled}
			onBlur={onBlur}
			name={name}
			id={name}
			customInput={<TextInput icon="calendar" />}
		/>
	)
}

export const DollyDatepicker = props => (
	<Label name={props.name} label={props.label} feil={props.feil}>
		<Datepicker {...props} />
	</Label>
)

export const FormikDatepicker = props => {
	const [field, meta] = useField(props)

	const handleChange = date => field.onChange(SyntEvent(field.name, date))
	const handleBlur = () => field.onBlur(SyntEvent(field.name, field.value))

	return (
		<InputWrapper {...props}>
			<DollyDatepicker
				value={field.value}
				onChange={handleChange}
				onBlur={handleBlur}
				feil={fieldError(meta)}
				{...props}
			/>
		</InputWrapper>
	)
}
