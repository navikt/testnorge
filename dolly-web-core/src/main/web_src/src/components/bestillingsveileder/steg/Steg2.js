import React from 'react'
import Overskrift from '~/components/ui/overskrift/Overskrift'
import * as Yup from 'yup'
import { TpsfForm } from '~/components/fagsystem/tpsf/form/Form'
import { KrrstubForm } from '~/components/fagsystem/krrstub/form/Form'
import { SigrunstubForm } from '~/components/fagsystem/sigrunstub/form/Form'
import { ArenaForm } from '~/components/fagsystem/arena/form/Form'
import { InstForm } from '~/components/fagsystem/inst/Form'

export const Steg2 = ({ formikBag }) => {
	return (
		<div>
			<Overskrift label="Velg verdier" />

			<TpsfForm formikBag={formikBag} />
			<InstForm formikBag={formikBag} />
			<KrrstubForm formikBag={formikBag} />
			<SigrunstubForm formikBag={formikBag} />
			<ArenaForm formikBag={formikBag} />
		</div>
	)
}

Steg2.label = 'Velg verdier'
Steg2.initialValues = attrs => {
	return Object.assign(
		{},
		{
			...TpsfForm.initialValues(attrs),
			...KrrstubForm.initialValues(attrs),
			...SigrunstubForm.initialValues(attrs),
			...InstForm.initialValues(attrs),
			...ArenaForm.initialValues(attrs)
		}
	)
}
Steg2.validation = Yup.object({
	...TpsfForm.validation,
	...KrrstubForm.validation,
	...InstForm.validation,
	...ArenaForm.validation
})
