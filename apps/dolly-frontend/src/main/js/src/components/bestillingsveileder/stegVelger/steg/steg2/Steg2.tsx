import React, { lazy, Suspense, useContext, useEffect } from 'react'
import { useFormContext } from 'react-hook-form'
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
import { AlderspensjonForm } from '@/components/fagsystem/alderspensjon/form/Form'
import { ArbeidsplassenForm } from '@/components/fagsystem/arbeidsplassen/form/Form'
import { UforetrygdForm } from '@/components/fagsystem/uforetrygd/form/Form'
import { SigrunstubPensjonsgivendeForm } from '@/components/fagsystem/sigrunstubPensjonsgivende/form/Form'
import { KrrstubForm } from '@/components/fagsystem/krrstub/form/KrrForm'
import { SkattekortForm } from '@/components/fagsystem/skattekort/form/Form'
import { PensjonsavtaleForm } from '@/components/fagsystem/pensjonsavtale/form/Form'
import { FullmaktForm } from '@/components/fagsystem/fullmakt/form/FullmaktForm'
import { AfpOffentligForm } from '@/components/fagsystem/afpOffentlig/form/Form'
import { YrkesskaderForm } from '@/components/fagsystem/yrkesskader/form/Form'
import Loading from '@/components/ui/loading/Loading'
import { PdlfForm } from '@/components/fagsystem/pdlf/form/Form'
import { ArbeidssoekerregisteretForm } from '@/components/fagsystem/arbeidssoekerregisteret/form/Form'
import { BestillingsveilederContext } from '@/components/bestillingsveileder/BestillingsveilederContext'
import { harAvhukedeAttributter } from '@/components/bestillingsveileder/utils'
import { Alert } from '@navikt/ds-react'

const HistarkForm = lazy(() => import('@/components/fagsystem/histark/form/HistarkForm'))
const DokarkivForm = lazy(() => import('@/components/fagsystem/dokarkiv/form/DokarkivForm'))

const Steg2: React.FC = () => {
	const { getValues, setValue } = useFormContext()
	const opts: any = useContext(BestillingsveilederContext)

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

	useEffect(() => {
		if (opts.gruppe?.id) {
			setValue('gruppeId', opts.gruppe?.id)
		}
	}, [])

	const leggTil = opts.is.leggTil
	const importTestnorge = opts.is.importTestnorge
	const gruppe = opts.gruppe

	if (!harAvhukedeAttributter(getValues())) {
		return <Alert variant={'info'}>{getEmptyMessage(leggTil, importTestnorge, gruppe)}</Alert>
	}
	const gruppeNavn = (gruppe: any) => <span style={{ fontWeight: 'bold' }}>{gruppe.navn}</span>

	return (
		<div>
			<PdlfForm />
			{getValues('fullmakt') && <FullmaktForm />}
			{getValues('aareg') && <AaregForm />}
			{getValues('sigrunstub') && <SigrunstubForm />}
			{getValues('sigrunstubPensjonsgivende') && <SigrunstubPensjonsgivendeForm />}
			{getValues('inntektstub') && <InntektstubForm />}
			{getValues('inntektsmelding') && <InntektsmeldingForm />}
			{getValues('skattekort') && <SkattekortForm />}
			{getValues('arbeidssoekerregisteret') && <ArbeidssoekerregisteretForm />}
			{getValues('arbeidsplassenCV') && <ArbeidsplassenForm />}
			{getValues('pensjonforvalter') && <PensjonForm />}
			{getValues('pensjonsavtale') && <PensjonsavtaleForm />}
			{getValues('tjenestepensjon') && <TjenestepensjonForm />}
			{getValues('alderspensjon') && <AlderspensjonForm />}
			{getValues('uforetrygd') && <UforetrygdForm />}
			{getValues('afpOffentlig') && <AfpOffentligForm />}
			{getValues('arenaforvalter') && <ArenaForm />}
			{getValues('sykemelding') && <SykdomForm />}
			{getValues('yrkesskader') && <YrkesskaderForm />}
			{getValues('brregstub') && <BrregstubForm />}
			{getValues('instdata') && <InstForm />}
			{getValues('krrstub') && <KrrstubForm />}
			{getValues('medl') && <MedlForm />}
			{getValues('udistub') && <UdistubForm />}
			{getValues('organisasjon') && <OrganisasjonForm />}

			<Suspense fallback={<Loading label="Laster komponenter..." />}>
				{getValues('dokarkiv') && <DokarkivForm />}
				{getValues('histark') && <HistarkForm />}
			</Suspense>
		</div>
	)
}

export default Steg2
