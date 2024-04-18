import { registerLocale } from 'react-datepicker'
import { addYears, isDate, subYears } from 'date-fns'
import locale_nb from 'date-fns/locale/nb'
import { Label } from '@/components/ui/form/inputs/label/Label'
import { InputWrapper } from '@/components/ui/form/inputWrapper/InputWrapper'
import { Vis } from '@/components/bestillingsveileder/VisAttributt'
import { fixTimezone, SyntEvent } from '@/components/ui/form/formUtils'
import 'react-datepicker/dist/react-datepicker.css'
import './Datepicker.less'
import { useFormContext } from 'react-hook-form'
import { DatePicker, useDatepicker } from '@navikt/ds-react'
import _ from 'lodash'
import { formatDate } from '@/utils/DataFormatter'

registerLocale('nb', locale_nb)

function addHours(date, amount) {
	date.setHours(amount)
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
}) => {
	const formMethods = useFormContext()

	const getSelectedDay = () => {
		const selected = formMethods.watch(name)
		if (_.isNil(selected) || (!isDate(selected) && _.isEmpty(selected))) {
			return undefined
		} else if (isDate(selected)) {
			return fixTimezone(selected)
		} else {
			return fixTimezone(new Date(selected))
		}
	}

	const { datepickerProps, inputProps } = useDatepicker({
		fromDate: minDate || subYears(new Date(), 125),
		toDate: maxDate || addYears(new Date(), 5),
		onDateChange: onChange || onBlur,
		disabled: excludeDates,
		defaultSelected: getSelectedDay(),
	})
	const selectedDay = getSelectedDay()

	return (
		<DatePicker {...datepickerProps} dropdownCaption={true} selected={selectedDay}>
			<DatePicker.Input
				{...inputProps}
				value={selectedDay ? formatDate(selectedDay) : ''}
				placeholder={placeholder}
				size={'small'}
				disabled={disabled}
				label={null}
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

const P_FormDatepicker = ({ addHour = true, ...props }) => {
	const formMethods = useFormContext()
	const value = formMethods.watch(props.name)
	const handleChange = (date) => {
		if (props.afterChange) {
			props.afterChange(date)
		}
		let val = fixTimezone(date)?.toISOString().substring(0, 19) || null
		if (addHour) {
			val = addHours(new Date(fixTimezone(date)), 3)
				.toISOString()
				.substring(0, 19)
		}
		formMethods.setValue(props.name, val, { shouldTouch: true })
		formMethods.trigger()
	}
	const handleBlur = () => {
		props?.onBlur?.(SyntEvent(props.name, value))
		formMethods.setValue(props.name, value, { shouldTouch: true })
		formMethods.trigger()
	}
	return <DollyDatepicker value={value} onChange={handleChange} onBlur={handleBlur} {...props} />
}

export const FormDatepicker = ({ visHvisAvhuket = true, ...props }) => {
	const component = <P_FormDatepicker {...props} />
	return visHvisAvhuket ? <Vis attributt={props.name}>{component}</Vis> : component
}
