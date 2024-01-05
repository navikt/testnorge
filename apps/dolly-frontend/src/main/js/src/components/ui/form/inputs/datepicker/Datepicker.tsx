import ReactDatepicker, { registerLocale } from 'react-datepicker'
import { addYears, subYears } from 'date-fns'
import locale_nb from 'date-fns/locale/nb'
import { TextInput } from '@/components/ui/form/inputs/textInput/TextInput'
import { Label } from '@/components/ui/form/inputs/label/Label'
import { InputWrapper } from '@/components/ui/form/inputWrapper/InputWrapper'
import { Vis } from '@/components/bestillingsveileder/VisAttributt'
import { fixTimezone, SyntEvent } from '@/components/ui/form/formUtils'
import 'react-datepicker/dist/react-datepicker.css'
import './Datepicker.less'
import { useFormContext } from 'react-hook-form'

registerLocale('nb', locale_nb)

function addHours(date, amount) {
	date.setHours(amount, 0, 0)
	return date
}

export const Datepicker = ({
	name,
	value,
	placeholder = 'Ikke spesifisert',
	onChange,
	onBlur,
	disabled = false,
	excludeDates,
	minDate,
	maxDate,
	...props
}) => (
	<ReactDatepicker
		locale="nb"
		dateFormat="dd.MM.yyyy"
		placeholderText={placeholder}
		selected={(value && new Date(value)) || null}
		onChange={onChange}
		showMonthDropdown
		showYearDropdown
		minDate={minDate || subYears(new Date(), 125)}
		maxDate={maxDate || addYears(new Date(), 5)}
		dropdownMode="select"
		disabled={disabled}
		onBlur={onBlur}
		name={name}
		id={name}
		autoComplete="off"
		customInput={<TextInput icon="calendar" isDatepicker={true} />}
		excludeDates={excludeDates}
		{...props}
	/>
)

export const DollyDatepicker = (props) => (
	<InputWrapper {...props}>
		<Label name={props.name} label={props.label}>
			<Datepicker {...props} />
		</Label>
	</InputWrapper>
)

const P_FormikDatepicker = ({ addHour = false, ...props }) => {
	const formMethods = useFormContext()
	const value = formMethods.watch(props.name)
	const handleChange = (date) => {
		if (props.afterChange) props.afterChange(date)
		let val = fixTimezone(date)?.toISOString().substring(0, 19) || null
		if (addHour) {
			val = addHours(new Date(fixTimezone(date)), 3)
				.toISOString()
				.substring(0, 19)
		}
		formMethods.setValue(props.name, val)
		formMethods.trigger()
	}
	const handleBlur = () => props?.onBlur?.(SyntEvent(props.name, value))
	return <DollyDatepicker value={value} onChange={handleChange} onBlur={handleBlur} {...props} />
}

export const FormikDatepicker = ({ visHvisAvhuket = true, ...props }) => {
	const component = <P_FormikDatepicker {...props} />
	return visHvisAvhuket ? <Vis attributt={props.name}>{component}</Vis> : component
}
