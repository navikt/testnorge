import React, { useState } from 'react'
import { FormikProps } from 'formik'
import { FormikTextInput } from '~/components/ui/form/inputs/textInput/TextInput'
import { InntektstubVirksomhetToggle } from './inntektstubVirksomhetToggle'
import InntektsinformasjonLister from './inntektsinformasjonLister/inntektsinformasjonLister'
import InntektsendringForm from './inntektsendringForm'
import _get from 'lodash/get'
import { Monthpicker } from '~/components/ui/form/inputs/monthpicker/Monthpicker'

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
			</div>
			<InntektstubVirksomhetToggle formikBag={formikBag} path={path} />
			<InntektsinformasjonLister formikBag={formikBag} path={path} />
			<InntektsendringForm formikBag={formikBag} path={path} />
		</div>
	)
}
