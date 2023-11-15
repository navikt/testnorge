import React, { useContext } from 'react'
import * as Yup from 'yup'
import { harAvhukedeAttributter } from '@/components/bestillingsveileder/utils'
import { BestillingsveilederContext } from '@/components/bestillingsveileder/BestillingsveilederContext'
import { SigrunstubForm } from '@/components/fagsystem/sigrunstub/form/Form'
import { InntektstubForm } from '@/components/fagsystem/inntektstub/form/Form'
import { InntektsmeldingForm } from '@/components/fagsystem/inntektsmelding/form/Form'
import { AaregForm } from '@/components/fagsystem/aareg/form/Form'
import { BrregstubForm } from '@/components/fagsystem/brregstub/form/Form'
import { PdlfForm } from '@/components/fagsystem/pdlf/form/Form'
import { ArenaForm } from '@/components/fagsystem/arena/form/Form'
import { InstForm } from '@/components/fagsystem/inst/form/Form'
import { UdistubForm } from '@/components/fagsystem/udistub/form/Form'
import { PensjonForm } from '@/components/fagsystem/pensjon/form/Form'
import { DokarkivForm } from '@/components/fagsystem/dokarkiv/form/DokarkivForm'
import { MedlForm } from '@/components/fagsystem/medl/form/MedlForm'
import { SykdomForm } from '@/components/fagsystem/sykdom/form/Form'
import { OrganisasjonForm } from '@/components/fagsystem/organisasjoner/form/Form'
import { TjenestepensjonForm } from '@/components/fagsystem/tjenestepensjon/form/Form'
import { ifPresent } from '@/utils/YupValidations'
import { Alert } from '@navikt/ds-react'
import { AlderspensjonForm } from '@/components/fagsystem/alderspensjon/form/Form'
import { SkjermingForm } from '@/components/fagsystem/skjermingsregister/form/SkjermingForm'
import { ArbeidsplassenForm } from '@/components/fagsystem/arbeidsplassen/form/Form'
import { HistarkForm } from '@/components/fagsystem/histark/form/HistarkForm'
import { UforetrygdForm } from '@/components/fagsystem/uforetrygd/form/Form'
import { SigrunstubPensjonsgivendeForm } from '@/components/fagsystem/sigrunstubPensjonsgivende/form/Form'
import { KrrstubForm } from '@/components/fagsystem/krrstub/form/KrrForm'
import { useFormContext } from 'react-hook-form'

const gruppeNavn = (gruppe) => <span style={{ fontWeight: 'bold' }}>{gruppe.navn}</span>

const getEmptyMessage = (leggTil, importTestnorge, gruppe = null) => {
	if (leggTil) {
		return 'Du har ikke lagt til flere egenskaper. Vennligst gå tilbake og velg nye egenskaper.'
	} else if (importTestnorge) {
		return (
			<span>
				Du har ikke lagt til egenskaper. Dolly vil importere valgt Test-Norge person(er) til
				{gruppe === null && <> gruppe du velger i neste steg.</>}
				{gruppe !== null && <> gruppen {gruppeNavn(gruppe)}.</>}
			</span>
		)
	}
	return 'Du har ikke valgt noen egenskaper. Dolly oppretter personer med tilfeldige verdier.'
}

export const Steg2 = () => {
	const opts = useContext(BestillingsveilederContext)
	const formMethods = useFormContext()

	const leggTil = opts.is.leggTil
	const importTestnorge = opts.is.importTestnorge
	const gruppe = opts.gruppe

	if (!harAvhukedeAttributter(formMethods.getValues())) {
		return <Alert variant={'info'}>{getEmptyMessage(leggTil, importTestnorge, gruppe)}</Alert>
	}

	return (
		<div>
			<PdlfForm formMethods={formMethods} />
			<AaregForm />
			<ArbeidsplassenForm formMethods={formMethods} />
			<SigrunstubForm formMethods={formMethods} />
			<SigrunstubPensjonsgivendeForm formMethods={formMethods} />
			<InntektstubForm formMethods={formMethods} />
			<InntektsmeldingForm formMethods={formMethods} />
			<PensjonForm formMethods={formMethods} />
			<TjenestepensjonForm formMethods={formMethods} />
			<AlderspensjonForm formMethods={formMethods} />
			<UforetrygdForm formMethods={formMethods} />
			<ArenaForm formMethods={formMethods} />
			<SykdomForm formMethods={formMethods} />
			<BrregstubForm formMethods={formMethods} />
			<InstForm formMethods={formMethods} />
			<KrrstubForm formMethods={formMethods} />
			<MedlForm formMethods={formMethods} />
			<UdistubForm formMethods={formMethods} />
			<DokarkivForm formMethods={formMethods} />
			<HistarkForm formMethods={formMethods} />
			<OrganisasjonForm formMethods={formMethods} />
		</div>
	)
}

Steg2.label = 'Velg verdier'

Steg2.validation = Yup.object({
	...PdlfForm.validation,
	...AaregForm.validation,
	...ArbeidsplassenForm.validation,
	...SigrunstubForm.validation,
	...SigrunstubPensjonsgivendeForm.validation,
	...InntektstubForm.validation,
	...InntektsmeldingForm.validation,
	...SykdomForm.validation,
	...BrregstubForm.validation,
	...InstForm.validation,
	...KrrstubForm.validation,
	...MedlForm.validation,
	...ArenaForm.validation,
	...UdistubForm.validation,
	...SkjermingForm.validation,
	...DokarkivForm.validation,
	...HistarkForm.validation,
	...OrganisasjonForm.validation,
	pensjonforvalter: ifPresent(
		'$pensjonforvalter',
		Yup.object({
			...PensjonForm.validation,
			...TjenestepensjonForm.validation,
			...AlderspensjonForm.validation,
			...UforetrygdForm.validation,
		}),
	),
})
