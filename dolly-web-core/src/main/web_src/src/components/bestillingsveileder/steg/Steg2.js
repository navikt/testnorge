import React from 'react'
import Overskrift from '~/components/ui/overskrift/Overskrift'
import * as Yup from 'yup'
import { TpsfForm } from '~/components/fagsystem/tpsf/form/Form'
import { KrrstubForm } from '~/components/fagsystem/krrstub/form/Form'

export const Steg2 = ({ formikBag }) => {
	return (
		<div>
			<Overskrift label="Velg verdier" />

			<TpsfForm formikBag={formikBag} />
			<KrrstubForm formikBag={formikBag} />
		</div>
	)
}

Steg2.label = 'Velg verdier'
Steg2.initialValues = Object.assign(
	{},
	{
		...TpsfForm.initialValues,
		...KrrstubForm.initialValues
	}
)
Steg2.validation = Yup.object({
	...TpsfForm.validation,
	...KrrstubForm.validation
})
