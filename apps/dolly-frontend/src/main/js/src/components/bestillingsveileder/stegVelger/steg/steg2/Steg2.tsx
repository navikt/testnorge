import React, { lazy, Suspense, useContext, useEffect } from 'react'
import { harAvhukedeAttributter } from '@/components/bestillingsveileder/utils'
import { BestillingsveilederContext } from '@/components/bestillingsveileder/BestillingsveilederContext'
import { SigrunstubForm } from '@/components/fagsystem/sigrunstub/form/Form'
import { InntektstubForm } from '@/components/fagsystem/inntektstub/form/Form'
import { InntektsmeldingForm } from '@/components/fagsystem/inntektsmelding/form/Form'
import { AaregForm } from '@/components/fagsystem/aareg/form/Form'
import { BrregstubForm } from '@/components/fagsystem/brregstub/form/Form'
import { ArenaForm } from '@/components/fagsystem/arena/form/Form'
import { InstForm } from '@/components/fagsystem/inst/form/Form'
import { UdistubForm } from '@/components/fagsystem/udistub/form/Form'
import { PensjonForm } from '@/components/fagsystem/pensjon/form/Form'
import { MedlForm } from '@/components/fagsystem/medl/form/MedlForm'
import { SykdomForm } from '@/components/fagsystem/sykdom/form/Form'
import { OrganisasjonForm } from '@/components/fagsystem/organisasjoner/form/Form'
import { TjenestepensjonForm } from '@/components/fagsystem/tjenestepensjon/form/Form'
import { Alert } from '@navikt/ds-react'
import { AlderspensjonForm } from '@/components/fagsystem/alderspensjon/form/Form'
import { ArbeidsplassenForm } from '@/components/fagsystem/arbeidsplassen/form/Form'
import { UforetrygdForm } from '@/components/fagsystem/uforetrygd/form/Form'
import { SigrunstubPensjonsgivendeForm } from '@/components/fagsystem/sigrunstubPensjonsgivende/form/Form'
import { KrrstubForm } from '@/components/fagsystem/krrstub/form/KrrForm'
import { useFormContext } from 'react-hook-form'
import { SkattekortForm } from '@/components/fagsystem/skattekort/form/Form'
import { PensjonsavtaleForm } from '@/components/fagsystem/pensjonsavtale/form/Form'
import { FullmaktForm } from '@/components/fagsystem/fullmakt/form/FullmaktForm'
import { AfpOffentligForm } from '@/components/fagsystem/afpOffentlig/form/Form'
import { YrkesskaderForm } from '@/components/fagsystem/yrkesskader/form/Form'
import Loading from '@/components/ui/loading/Loading'
import { PdlfForm } from '@/components/fagsystem/pdlf/form/Form'

const HistarkForm = lazy(() => import('@/components/fagsystem/histark/form/HistarkForm'))
const DokarkivForm = lazy(() => import('@/components/fagsystem/dokarkiv/form/DokarkivForm'))

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

const Steg2 = () => {
	const opts: any = useContext(BestillingsveilederContext)
	const formMethods = useFormContext()

	useEffect(() => {
		if (opts.gruppe?.id) {
			formMethods.setValue('gruppeId', opts.gruppe?.id)
		}
	}, [])

	const leggTil = opts.is.leggTil
	const importTestnorge = opts.is.importTestnorge
	const gruppe = opts.gruppe

	if (!harAvhukedeAttributter(formMethods.getValues())) {
		return <Alert variant={'info'}>{getEmptyMessage(leggTil, importTestnorge, gruppe)}</Alert>
	}

	return (
		<div>
			<PdlfForm />
			<FullmaktForm />
			<AaregForm />
			<SigrunstubForm />
			<SigrunstubPensjonsgivendeForm />
			<InntektstubForm />
			<InntektsmeldingForm />
			<SkattekortForm />
			<ArbeidsplassenForm />
			<PensjonForm />
			<PensjonsavtaleForm />
			<TjenestepensjonForm />
			<AlderspensjonForm />
			<UforetrygdForm />
			<AfpOffentligForm />
			<ArenaForm />
			<SykdomForm />
			<YrkesskaderForm />
			<BrregstubForm />
			<InstForm />
			<KrrstubForm />
			<MedlForm />
			<UdistubForm />
			<Suspense fallback={<Loading label="Laster komponenter..." />}>
				<DokarkivForm />
				<HistarkForm />
			</Suspense>
			<OrganisasjonForm />
		</div>
	)
}

Steg2.label = 'Velg verdier'

export default Steg2
