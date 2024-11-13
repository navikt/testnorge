import ReactDatepicker, { registerLocale } from 'react-datepicker'
import { addYears, subYears } from 'date-fns'
import locale_nb from 'date-fns/locale/nb'
import { TextInput } from '@/components/ui/form/inputs/textInput/TextInput'
import { Label } from '@/components/ui/form/inputs/label/Label'
import { InputWrapper } from '@/components/ui/form/inputWrapper/InputWrapper'
import { Vis } from '@/components/bestillingsveileder/VisAttributt'
import { fixTimezone, SyntEvent } from '@/components/ui/form/formUtils'
import 'react-datepicker/dist/react-datepicker.css'
import { useFormContext } from 'react-hook-form'

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
			customInput={<TextInput name={name} icon="calendar" />}
			excludeDates={excludeDates}
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

const P_FormTimepicker = ({ ...props }) => {
	const formMethods = useFormContext()
	const value = formMethods.watch(props.name)

	const handleChange = (date) => {
		const fixedDate = fixTimezone(date)
		const onChange =
			props.onChange ||
			((event) => {
				formMethods.setValue(props.name, event?.target?.value, { shouldTouch: true })
				formMethods.trigger(props.name)
			})
		if (props.afterChange) props.afterChange(fixedDate)

		return onChange(SyntEvent(props.name, fixedDate))
	}
	const handleBlur = () => props?.onBlur?.(SyntEvent(props.name, value))
	return <DollyTimepicker value={value} onChange={handleChange} onBlur={handleBlur} {...props} />
}

export const FormDateTimepicker = ({ visHvisAvhuket = true, ...props }) => {
	const component = <P_FormTimepicker {...props} />
	return visHvisAvhuket ? <Vis attributt={props.name}>{component}</Vis> : component
}
