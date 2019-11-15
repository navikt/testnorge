import React from 'react'
import Overskrift from '~/components/ui/overskrift/Overskrift'
import { Formik } from 'formik'
import * as Yup from 'yup'
import {
	TpsfForm,
	initialValues as tpsfInit,
	validation as tpsfValidation
} from '~/components/fagsystem/tpsf/form/Form'
import {
	KrrstubForm,
	initialValues as krrstubInit,
	validation as krrstubValidation
} from '~/components/fagsystem/krrstub/form/Form'
import {
	ArenaForm,
	initialValues as arenaInit,
	validation as arenaValidation
} from '~/components/fagsystem/arena/form/Form'
import DisplayFormikState from '~/utils/DisplayFormikState'

export const Steg2 = props => {
	const handleSubmit = () => {
		console.log('submit values')
	}

	const initialValues = Object.assign({}, { ...tpsfInit, ...krrstubInit, ...arenaInit })

	const validationListe = Yup.object({
		...tpsfValidation,
		...krrstubValidation,
		...arenaValidation
	})

	return (
		<div>
			<Overskrift label="Velg verdier" />

			<Formik
				onSubmit={handleSubmit}
				initialValues={initialValues}
				validationSchema={validationListe}
			>
				{formikBag => (
					<div>
						<TpsfForm formikProps={formikBag} />
						<KrrstubForm formikProps={formikBag} />
						<ArenaForm formikBag={formikBag} />
						<DisplayFormikState {...formikBag} />
					</div>
				)}
			</Formik>
		</div>
	)
}
