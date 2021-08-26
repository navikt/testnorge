import React from 'react'
import ReactDatepicker, { registerLocale } from 'react-datepicker'
import { FormikField } from '~/components/ui/form/FormikField'
import { addYears, subYears } from 'date-fns'
import locale_nb from 'date-fns/locale/nb'
import { TextInput } from '~/components/ui/form/inputs/textInput/TextInput'
import { Label } from '~/components/ui/form/inputs/label/Label'
import { InputWrapper } from '~/components/ui/form/inputWrapper/InputWrapper'
import { Vis } from '~/components/bestillingsveileder/VisAttributt'
import { fieldError, SyntEvent } from '~/components/ui/form/formUtils'
import 'react-datepicker/dist/react-datepicker.css'
import './Datepicker.less'

registerLocale('nb', locale_nb)

export const Datepicker = ({
	name,
	value,
	placeholder = 'Ikke spesifisert',
	onChange,
	onBlur,
	disabled = false,
	feil,
	excludeDates,
	minDate,
	maxDate
}) => {
	return (
		<ReactDatepicker
			locale="nb"
			dateFormat="dd.MM.yyyy"
			placeholderText={placeholder}
			selected={(value && new Date(value)) || null}
			onChange={onChange}
			showMonthDropdown
			showYearDropdown
			minDate={minDate || subYears(new Date(), 100)}
			maxDate={maxDate || addYears(new Date(), 5)}
			dropdownMode="select"
			disabled={disabled}
			onBlur={onBlur}
			name={name}
			id={name}
			autoComplete="off"
			customInput={<TextInput icon="calendar" feil={feil} />}
			excludeDates={excludeDates}
		/>
	)
}

export const DollyDatepicker = props => (
	<InputWrapper {...props}>
		<Label name={props.name} label={props.label} feil={props.feil}>
			<Datepicker {...props} />
		</Label>
	</InputWrapper>
)

const P_FormikDatepicker = ({ fastfield, ...props }) => (
	<FormikField name={props.name} fastfield={fastfield}>
		{({ field, form, meta }) => {
			const handleChange = date => {
				form.setFieldTouched(props.name) // Need to trigger touched manually for Datepicker

				if (props.afterChange) props.afterChange(date)

				return field.onChange(SyntEvent(field.name, date))
			}
			const handleBlur = () => field.onBlur(SyntEvent(field.name, field.value))

			return (
				<DollyDatepicker
					value={field.value}
					onChange={handleChange}
					onBlur={handleBlur}
					feil={fieldError(meta)}
					{...props}
				/>
			)
		}}
	</FormikField>
)

export const FormikDatepicker = ({ visHvisAvhuket = true, ...props }) => {
	const component = <P_FormikDatepicker {...props} />
	return visHvisAvhuket ? <Vis attributt={props.name}>{component}</Vis> : component
}
