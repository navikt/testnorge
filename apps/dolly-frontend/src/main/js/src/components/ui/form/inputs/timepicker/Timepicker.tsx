import ReactDatepicker, { registerLocale } from 'react-datepicker'
import { FormikField } from '@/components/ui/form/FormikField'
import { addYears, subYears } from 'date-fns'
import locale_nb from 'date-fns/locale/nb'
import { TextInput } from '@/components/ui/form/inputs/textInput/TextInput'
import { Label } from '@/components/ui/form/inputs/label/Label'
import { InputWrapper } from '@/components/ui/form/inputWrapper/InputWrapper'
import { Vis } from '@/components/bestillingsveileder/VisAttributt'
import { fieldError, fixTimezone, SyntEvent } from '@/components/ui/form/formUtils'
import 'react-datepicker/dist/react-datepicker.css'

registerLocale('nb', locale_nb)

const displayTimeZone = (date: Date) => {
	if (!date) {
		return null
	}
	const tzoffset = new Date().getTimezoneOffset() * 60000 //offset in milliseconds
	return new Date(date.getTime() + tzoffset)
}
export const TimePicker = ({
	name,
	value,
	placeholder = 'Ikke spesifisert',
	onChange,
	onBlur,
	disabled = false,
	feil,
	excludeDates,
	minDate,
	maxDate,
}) => {
	const displayTime = value && displayTimeZone(new Date(value))
	return (
		<ReactDatepicker
			locale="nb"
			dateFormat="dd.MM.yyyy HH:mm"
			placeholderText={placeholder}
			selected={displayTime}
			onChange={onChange}
			showMonthDropdown
			showYearDropdown
			showTimeInput
			timeInputLabel={'Tid'}
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

export const DollyTimepicker = (props) => (
	<InputWrapper {...props}>
		<Label name={props.name} label={props.label} feil={props.feil}>
			<TimePicker {...props} />
		</Label>
	</InputWrapper>
)

const P_FormikTimepicker = ({ fastfield, ...props }) => (
	<FormikField name={props.name} fastfield={fastfield}>
		{({ field, form, meta }) => {
			const handleChange = (date) => {
				const fixedDate = fixTimezone(date)
				form.setFieldTouched(props.name) // Need to trigger touched manually for Datepicker

				if (props.afterChange) props.afterChange(fixedDate)

				return field.onChange(SyntEvent(field.name, fixedDate))
			}
			const handleBlur = () => field.onBlur(SyntEvent(field.name, field.value))

			return (
				<DollyTimepicker
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

export const FormikDateTimepicker = ({ visHvisAvhuket = true, ...props }) => {
	const component = <P_FormikTimepicker {...props} />
	return visHvisAvhuket ? <Vis attributt={props.name}>{component}</Vis> : component
}
