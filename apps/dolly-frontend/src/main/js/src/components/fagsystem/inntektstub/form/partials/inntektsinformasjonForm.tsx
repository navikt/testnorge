import React, { useState } from 'react'
import { FormikProps } from 'formik'
import { FormikTextInput } from '~/components/ui/form/inputs/textInput/TextInput'
import { InntektstubVirksomhetToggle } from './inntektstubVirksomhetToggle'
import InntektsinformasjonLister from './inntektsinformasjonLister/inntektsinformasjonLister'
import InntektsendringForm from './inntektsendringForm'
import _get from 'lodash/get'
import { Monthpicker } from '~/components/ui/form/inputs/monthpicker/Monthpicker'
import { FormikDatepicker } from '~/components/ui/form/inputs/datepicker/Datepicker'

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

	const [rapporteringsdate, setRapporteringsdato] = useState(
		_get(formikBag.values, `${path}.rapporteringsdato`) != ''
			? Date.parse(_get(formikBag.values, `${path}.rapporteringsdato`))
			: null
	)

	const handleDateChange = (selectedDate: Date) => {
		setDate(selectedDate)
		formikBag.setFieldValue(
			`${path}.sisteAarMaaned`,
			selectedDate && selectedDate.toISOString().substr(0, 7)
		)
	}

	const handleRapporteringDateChange = (selectedDate: Date) => {
		setRapporteringsdato(selectedDate)
		formikBag.setFieldValue(
			`${path}.rapporteringsdato`,
			selectedDate && selectedDate.toISOString().substr(0, 10)
		)
	}

	return (
		<div key={path}>
			<div className="flexbox--flex-wrap">
				<Monthpicker
					formikBag={formikBag}
					name={`${path}.sisteAarMaaned`}
					label="År/måned"
					date={date}
					handleDateChange={handleDateChange}
				/>
				<FormikTextInput
					name={`${path}.antallMaaneder`}
					label="Generer antall måneder"
					type="number"
				/>
				<FormikDatepicker
					formikBag={formikBag}
					name={`${path}.rapporteringsdato`}
					label="Rapporteringsdato"
					date={rapporteringsdate}
					onChange={handleRapporteringDateChange}
				/>
			</div>
			<InntektstubVirksomhetToggle formikBag={formikBag} path={path} />
			<InntektsinformasjonLister formikBag={formikBag} path={path} />
			<InntektsendringForm formikBag={formikBag} path={path} />
		</div>
	)
}
