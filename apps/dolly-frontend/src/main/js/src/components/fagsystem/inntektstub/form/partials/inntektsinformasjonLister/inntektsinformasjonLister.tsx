import React from 'react'
import { FormikProps } from 'formik'
import { InntektForm } from './inntektForm'
import { FradragForm } from './fradragForm'
import { ForskuddstrekkForm } from './forskuddstrekkForm'
import { ArbeidsforholdForm } from './arbeidsforholdForm'

interface InntektsinformasjonLister {
	path: string
	formikBag: FormikProps<{}>
}

export default ({ path, formikBag }: InntektsinformasjonLister) => {
	return (
		<>
			<InntektForm formikBag={formikBag} inntektsinformasjonPath={path} />
			<FradragForm formikBag={formikBag} inntektsinformasjonPath={path} />
			<ForskuddstrekkForm formikBag={formikBag} inntektsinformasjonPath={path} />
			<ArbeidsforholdForm formikBag={formikBag} inntektsinformasjonPath={path} />
		</>
	)
}
