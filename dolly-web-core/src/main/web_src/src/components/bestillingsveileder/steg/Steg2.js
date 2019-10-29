import React from 'react'
import Overskrift from '~/components/ui/overskrift/Overskrift'
import { Formik } from 'formik'
import {
	TpsfForm,
	initialValues as tpsfInit,
	validation
} from '~/components/fagsystem/tpsf/form/Form'
import * as Yup from 'yup'

export const Steg2 = props => {
	const handleSubmit = () => {
		console.log('submit values')
	}

	const initialValues = Object.assign({}, tpsfInit)

	const validationListe = Yup.object(validation)

	return (
		<div>
			<Overskrift label="Velg verdier" />

			<Formik
				onSubmit={handleSubmit}
				initialValues={initialValues}
				validationSchema={validationListe}
			>
				{formikProps => (
					<div>
						<TpsfForm formikProps={formikProps} />
					</div>
				)}
			</Formik>
		</div>
	)
}
