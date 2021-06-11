import React from 'react'
import ReactDatepicker from 'react-datepicker'
import _get from 'lodash/get'
import { FormikProps } from 'formik'
import { InputWrapper } from '~/components/ui/form/inputWrapper/InputWrapper'
import { Label } from '~/components/ui/form/inputs/label/Label'
import { TextInput } from '~/components/ui/form/inputs/textInput/TextInput'

interface Monthpicker {
	formikBag: FormikProps<{}>
	name: string
	label: string
	date: string
	handleDateChange: Function
}

export const Monthpicker = ({ formikBag, name, label, date, handleDateChange }: Monthpicker) => {
	const getFeilmelding = (formikProps: FormikProps<{}>, formikPath: string) => {
		const feilmelding = _get(formikProps.errors, formikPath)
		return feilmelding ? { feilmelding: feilmelding } : null
	}

	return (
		<InputWrapper size={'medium'}>
			{/* @ts-ignore */}
			<Label name={name} label={label} feil={getFeilmelding(formikBag, name)}>
				<ReactDatepicker
					className={'skjemaelement__input'}
					locale="nb"
					dateFormat="yyyy-MM"
					selected={date}
					onChange={handleDateChange}
					placeholderText={'yyyy-MM'}
					showMonthYearPicker
					// @ts-ignore
					customInput={<TextInput icon="calendar" feil={getFeilmelding(formikBag, name)} />}
					dropdownMode="select"
					autoComplete="off"
				/>
			</Label>
		</InputWrapper>
	)
}
