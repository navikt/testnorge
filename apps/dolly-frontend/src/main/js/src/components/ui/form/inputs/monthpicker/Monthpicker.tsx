import React from 'react'
import ReactDatepicker from 'react-datepicker'
import _get from 'lodash/get'
import { FormikProps } from 'formik'
import { InputWrapper } from '~/components/ui/form/inputWrapper/InputWrapper'
import { Label } from '~/components/ui/form/inputs/label/Label'
import { TextInput } from '~/components/ui/form/inputs/textInput/TextInput'

interface MonthpickerProps {
	formikBag: FormikProps<any>
	name: string
	label: string
	date: Date
	handleDateChange: (dato: string, type: string) => void
	minDate?: Date
	maxDate?: Date
}

export const Monthpicker = ({
	formikBag,
	name,
	label,
	date,
	handleDateChange,
	minDate = null,
	maxDate = null,
}: MonthpickerProps) => {
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
					onChange={handleDateChange}
					placeholderText={'yyyy-MM'}
					showMonthYearPicker
					customInput={<TextInput icon="calendar" feil={getFeilmelding(formikBag, name)} />}
					dropdownMode="select"
					autoComplete="off"
					minDate={minDate}
					maxDate={maxDate}
				/>
			</Label>
		</InputWrapper>
	)
}
