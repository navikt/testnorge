import React from 'react'
import * as Yup from 'yup'
import _has from 'lodash/has'
import { AlertStripeInfo } from 'nav-frontend-alertstriper'
import { TpsfForm } from '~/components/fagsystem/tpsf/form/Form'
import { KrrstubForm } from '~/components/fagsystem/krrstub/form/Form'
import { SigrunstubForm } from '~/components/fagsystem/sigrunstub/form/Form'
import { PdlfForm } from '~/components/fagsystem/pdlf/form/Form'
import { ArenaForm } from '~/components/fagsystem/arena/form/Form'
import { InstForm } from '~/components/fagsystem/inst/form/Form'
import { UdistubForm } from '~/components/fagsystem/udistub/form/Form'

export const Steg2 = ({ formikBag }) => {
	const avhukedeAttributter = [
		'tpsf',
		'pdlforvalter',
		'aareg',
		'sigrunstub',
		'instdata',
		'krrstub',
		'arenaforvalter',
		'udistub'
	].some(path => _has(formikBag.values, path))

	if (!avhukedeAttributter) {
		return (
			<AlertStripeInfo>
				Du har ikke valgt noen egenskaper. Dolly oppretter personer med tilfeldige verdier.
			</AlertStripeInfo>
		)
	}

	return (
		<div>
			<TpsfForm formikBag={formikBag} />
			<InstForm formikBag={formikBag} />
			<KrrstubForm formikBag={formikBag} />
			<SigrunstubForm formikBag={formikBag} />
			<ArenaForm formikBag={formikBag} />
			<UdistubForm formikBag={formikBag} />
			<PdlfForm formikBag={formikBag} />
		</div>
	)
}

Steg2.label = 'Velg verdier'

Steg2.validation = Yup.object({
	...TpsfForm.validation,
	...KrrstubForm.validation,
	...InstForm.validation,
	...ArenaForm.validation,
	...UdistubForm.validation,
	...PdlfForm.validation
})
