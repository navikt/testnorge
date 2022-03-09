import React, { useContext } from 'react'
import * as Yup from 'yup'
import { AlertStripeInfo } from 'nav-frontend-alertstriper'
import { harAvhukedeAttributter } from '~/components/bestillingsveileder/utils'
import { BestillingsveilederContext } from '~/components/bestillingsveileder/Bestillingsveileder'
import { TpsfForm } from '~/components/fagsystem/tpsf/form/Form'
import { KrrstubForm } from '~/components/fagsystem/krrstub/form/Form'
import { SigrunstubForm } from '~/components/fagsystem/sigrunstub/form/Form'
import { InntektstubForm } from '~/components/fagsystem/inntektstub/form/Form'
import { InntektsmeldingForm } from '~/components/fagsystem/inntektsmelding/form/Form'
import { AaregForm } from '~/components/fagsystem/aareg/form/Form'
import { BrregstubForm } from '~/components/fagsystem/brregstub/form/Form'
import { PdlfForm } from '~/components/fagsystem/pdlf/form/Form'
import { ArenaForm } from '~/components/fagsystem/arena/form/Form'
import { InstForm } from '~/components/fagsystem/inst/form/Form'
import { UdistubForm } from '~/components/fagsystem/udistub/form/Form'
import { PensjonForm } from '~/components/fagsystem/pensjon/form/Form'
import { DokarkivForm } from '~/components/fagsystem/dokarkiv/form/scanning/DokarkivForm'
import { SykdomForm } from '~/components/fagsystem/sykdom/form/Form'
import { OrganisasjonForm } from '~/components/fagsystem/organisasjoner/form/Form'
import { NomForm } from '~/components/fagsystem/nom/form/Form'

const getEmptyMessage = (leggTil, importTestnorge) => {
	if (leggTil) {
		return 'Du har ikke lagt til flere egenskaper. Dolly vil opprette den samme personen i miljøene du velger i neste steg.'
	} else if (importTestnorge) {
		return (
			'Du har ikke lagt til egenskaper. Dolly vil importere valgt Testnorge person(er) til ' +
			'gruppe du velger i neste steg.'
		)
	}
	return 'Du har ikke valgt noen egenskaper. Dolly oppretter personer med tilfeldige verdier.'
}

export const Steg2 = ({ formikBag }) => {
	const opts = useContext(BestillingsveilederContext)
	const leggTil = opts.is.leggTil
	const importTestnorge = opts.is.importTestnorge

	if (!harAvhukedeAttributter(formikBag.values)) {
		return <AlertStripeInfo>{getEmptyMessage(leggTil, importTestnorge)}</AlertStripeInfo>
	}

	return (
		<div>
			<TpsfForm formikBag={formikBag} />
			<PdlfForm formikBag={formikBag} />
			<AaregForm formikBag={formikBag} />
			<SigrunstubForm formikBag={formikBag} />
			<PensjonForm formikBag={formikBag} />
			<InntektstubForm formikBag={formikBag} />
			<InntektsmeldingForm formikBag={formikBag} />
			<SykdomForm formikBag={formikBag} />
			<BrregstubForm formikBag={formikBag} />
			<InstForm formikBag={formikBag} />
			<KrrstubForm formikBag={formikBag} />
			<NomForm formikBag={formikBag} />
			<ArenaForm formikBag={formikBag} />
			<UdistubForm formikBag={formikBag} />
			<DokarkivForm formikBag={formikBag} />
			<OrganisasjonForm formikBag={formikBag} />
		</div>
	)
}

Steg2.label = 'Velg verdier'

Steg2.validation = Yup.object({
	...TpsfForm.validation,
	...PdlfForm.validation,
	...AaregForm.validation,
	...SigrunstubForm.validation,
	...PensjonForm.validation,
	...InntektstubForm.validation,
	...InntektsmeldingForm.validation,
	...SykdomForm.validation,
	...BrregstubForm.validation,
	...InstForm.validation,
	...KrrstubForm.validation,
	...ArenaForm.validation,
	...UdistubForm.validation,
	...DokarkivForm.validation,
	...OrganisasjonForm.validation,
})
