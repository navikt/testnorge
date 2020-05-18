import React from 'react'
import _get from 'lodash/get'
import { FormikProps } from 'formik'
import { FormikTextInput } from '~/components/ui/form/inputs/textInput/TextInput'
import { InntektstubVirksomhetToggle } from './inntektstubVirksomhetToggle'
import InntektsinformasjonLister from './inntektsinformasjonLister/inntektsinformasjonLister'
import InntektsendringForm from './inntektsendringForm'

interface InntektsinformasjonForm {
	path: string
	formikBag: FormikProps<{}>
}

export default ({ path, formikBag }: InntektsinformasjonForm) => (
	<div key={path}>
		<div className="flexbox--flex-wrap">
			<FormikTextInput name={`${path}.sisteAarMaaned`} label="Måned/år" type="month" />
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
