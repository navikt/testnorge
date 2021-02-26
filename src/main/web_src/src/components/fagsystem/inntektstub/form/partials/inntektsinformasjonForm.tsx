import React, { useState } from 'react'
import { FormikProps } from 'formik'
import { FormikTextInput, TextInput } from '~/components/ui/form/inputs/textInput/TextInput'
import { InntektstubVirksomhetToggle } from './inntektstubVirksomhetToggle'
import InntektsinformasjonLister from './inntektsinformasjonLister/inntektsinformasjonLister'
import InntektsendringForm from './inntektsendringForm'
import ReactDatepicker from 'react-datepicker'
import { Label } from '~/components/ui/form/inputs/label/Label'
import { InputWrapper } from '~/components/ui/form/inputWrapper/InputWrapper'
import _get from 'lodash/get'

interface InntektsinformasjonForm {
	path: string
	formikBag: FormikProps<{}>
}

export default ({ path, formikBag }: InntektsinformasjonForm) => {
	const [date, setDate] = useState(
		_get(formikBag.values, `${path}.sisteAarMaaned`) != ''
			? Date.parse(_get(formikBag.values, `${path}.sisteAarMaaned`))
			: null
	)

	const handleDateChange = (selectedDate: Date) => {
		setDate(selectedDate)
		formikBag.setFieldValue(
			`${path}.sisteAarMaaned`,
			selectedDate ? selectedDate.toISOString().substr(0, 7) : undefined
		)
	}

	const getFeilmelding = (formikProps: FormikProps<{}>, formikPath: string) => {
		const feilmelding = _get(formikProps.errors, formikPath)
		return feilmelding ? { feilmelding: feilmelding } : null
	}

	return (
		<div key={path}>
			<div className="flexbox--flex-wrap">
				<InputWrapper>
					<Label
						name={`${path}.sisteAarMaaned`}
						label={'År/måned'}
						feil={getFeilmelding(formikBag, `${path}.sisteAarMaaned`)}
					>
						<ReactDatepicker
							className={'skjemaelement__input'}
							locale="nb"
							dateFormat="yyyy-MM"
							selected={date}
							onChange={handleDateChange}
							placeholderText={'yyyy-MM'}
							showMonthYearPicker
							customInput={
								<TextInput
									icon="calendar"
									feil={getFeilmelding(formikBag, `${path}.sisteAarMaaned`)}
								/>
							}
							dropdownMode="select"
							autoComplete="off"
						/>
					</Label>
				</InputWrapper>
				<FormikTextInput
					name={`${path}.antallMaaneder`}
					label="Generer antall måneder"
					type="number"
				/>
			</div>
			<InntektstubVirksomhetToggle path={path} formikBag={formikBag} />
			<InntektsinformasjonLister formikBag={formikBag} path={path} />
			<InntektsendringForm formikBag={formikBag} path={path} />
		</div>
	)
}
