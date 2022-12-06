import ReactDatepicker from 'react-datepicker'
import _get from 'lodash/get'
import { FormikProps, useFormikContext } from 'formik'
import { Label } from '@/components/ui/form/inputs/label/Label'
import { TextInput } from '@/components/ui/form/inputs/textInput/TextInput'
import { InputWrapper } from '@/components/ui/form/inputWrapper/InputWrapper'

interface MonthpickerProps {
	name: string
	label: string
	date?: Date
	handleDateChange?: (dato: string, type: string) => void
	onChange?: (date: Date) => void
	isClearable?: boolean
	minDate?: Date
	maxDate?: Date
}

export const Monthpicker = ({
	name,
	label,
	date = null,
	handleDateChange,
	onChange,
	isClearable = false,
	minDate = null,
	maxDate = null,
	...props
}: MonthpickerProps) => {
	const formikBag = useFormikContext()
	const getFeilmelding = (formikProps: FormikProps<any>, formikPath: string) => {
		const feilmelding = _get(formikProps.errors, formikPath)
		return feilmelding ? { feilmelding: feilmelding } : null
	}
	const formattedDate = date instanceof Date || date === null ? date : new Date(date)

	return (
		<InputWrapper size={'medium'}>
			<Label name={name} label={label} feil={getFeilmelding(formikBag, name)} containerClass={null}>
				<ReactDatepicker
					className={'skjemaelement__input'}
					locale="nb"
					dateFormat="yyyy-MM"
					selected={formattedDate}
					onChange={onChange ? onChange : handleDateChange}
					placeholderText={'yyyy-mm'}
					showMonthYearPicker
					customInput={<TextInput icon="calendar" feil={getFeilmelding(formikBag, name)} />}
					dropdownMode="select"
					autoComplete="off"
					minDate={minDate}
					maxDate={maxDate}
					{...props}
				/>
			</Label>
		</InputWrapper>
	)
}
