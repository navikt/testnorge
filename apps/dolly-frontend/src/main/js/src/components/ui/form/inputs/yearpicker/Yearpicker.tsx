import React from 'react'
import { FormikProps } from 'formik'
import _get from 'lodash/get'
import { InputWrapper } from '~/components/ui/form/inputWrapper/InputWrapper'
import { Label } from '~/components/ui/form/inputs/label/Label'
import ReactDatepicker from 'react-datepicker'
import { TextInput } from '~/components/ui/form/inputs/textInput/TextInput'

interface YearpickerProps {
	formikBag: FormikProps<any>
	name: string
	label: string
	date: Date
	handleDateChange: (dato: string, type: string) => void
	minDate?: Date
	maxDate?: Date
}

export const Yearpicker = ({
	formikBag,
	name,
	label,
	date,
	handleDateChange,
	maxDate = null,
	disabled = false,
}: YearpickerProps) => {
	const getFeilmelding = (formikProps: FormikProps<any>, formikPath: string) => {
		const feilmelding = _get(formikProps.errors, formikPath)
		return feilmelding ? { feilmelding: feilmelding } : null
	}

	return (
		<InputWrapper>
			<Label name={name} label={label} feil={getFeilmelding(formikBag, name)}>
				<ReactDatepicker
					className={'skjemaelement__input'}
					dateFormat="yyyy"
					selected={date}
					onChange={handleDateChange}
					placeholderText={'Ikke spesifisert'}
					showYearPicker
					customInput={<TextInput icon="calendar" feil={getFeilmelding(formikBag, name)} />}
					maxDate={maxDate}
					disabled={disabled}
				/>
			</Label>
		</InputWrapper>
	)
}
