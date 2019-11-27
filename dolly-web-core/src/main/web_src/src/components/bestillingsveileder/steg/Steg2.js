import React from 'react'
import * as Yup from 'yup'
import _values from 'lodash/values'
import { AlertStripeInfo } from 'nav-frontend-alertstriper'
import { TpsfForm } from '~/components/fagsystem/tpsf/form/Form'
import { KrrstubForm } from '~/components/fagsystem/krrstub/form/Form'
import { SigrunstubForm } from '~/components/fagsystem/sigrunstub/form/Form'
import { PdlfForm } from '~/components/fagsystem/pdlf/Form'
import { ArenaForm } from '~/components/fagsystem/arena/form/Form'
import { InstForm } from '~/components/fagsystem/inst/Form'
import { UdistubForm } from '~/components/fagsystem/udistub/form/Form'

export const Steg2 = ({ formikBag, attributter }) => {
	const avhukedeAttributter = _values(attributter).some(a => a)

	return (
		<div>
			{avhukedeAttributter ? (
				<div>
					<TpsfForm formikBag={formikBag} />
					<InstForm formikBag={formikBag} />
					<KrrstubForm formikBag={formikBag} />
					<SigrunstubForm formikBag={formikBag} />
					<ArenaForm formikBag={formikBag} />
					<UdistubForm formikBag={formikBag} />
				</div>
			) : (
				<AlertStripeInfo>
					Du har ikke valgt noen attributter. Dolly oppretter testpersoner med tilfeldige verdier.
				</AlertStripeInfo>
			)}
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
			...ArenaForm.initialValues(attrs),
			...UdistubForm.initialValues(attrs),
			...PdlfForm.initialValues(attrs)
		}
	)
}
Steg2.validation = Yup.object({
	...TpsfForm.validation,
	...KrrstubForm.validation,
	...InstForm.validation,
	...ArenaForm.validation,
	...UdistubForm.validation,
	...PdlfForm.validation
})
