import React, { lazy, Suspense, useContext } from 'react'
import { useFormContext } from 'react-hook-form'
import { InntektstubForm } from '@/components/fagsystem/inntektstub/form/Form'
import { InntektsmeldingForm } from '@/components/fagsystem/inntektsmelding/form/Form'
import { AaregForm } from '@/components/fagsystem/aareg/form/Form'
import { BrregstubForm } from '@/components/fagsystem/brregstub/form/Form'
import { ArenaForm } from '@/components/fagsystem/arena/form/Form'
import { InstForm } from '@/components/fagsystem/inst/form/Form'
import { UdistubForm } from '@/components/fagsystem/udistub/form/Form'
import { PensjonForm } from '@/components/fagsystem/pensjon/form/Form'
import { MedlForm } from '@/components/fagsystem/medl/form/MedlForm'

import { OrganisasjonForm } from '@/components/fagsystem/organisasjoner/form/Form'
import { TjenestepensjonForm, tpPath } from '@/components/fagsystem/tjenestepensjon/form/Form'
import {
	AlderspensjonForm,
	alderspensjonPath,
} from '@/components/fagsystem/alderspensjon/form/Form'
import { ArbeidsplassenForm } from '@/components/fagsystem/arbeidsplassen/form/Form'
import { UforetrygdForm, uforetrygdPath } from '@/components/fagsystem/uforetrygd/form/Form'
import {
	sigrunstubPensjonsgivendeAttributt,
	SigrunstubPensjonsgivendeForm,
} from '@/components/fagsystem/sigrunstubPensjonsgivende/form/Form'
import { KrrstubForm } from '@/components/fagsystem/krrstub/form/KrrForm'
import { SkattekortForm } from '@/components/fagsystem/skattekort/form/Form'
import { avtalePath, PensjonsavtaleForm } from '@/components/fagsystem/pensjonsavtale/form/Form'
import { FullmaktForm } from '@/components/fagsystem/fullmakt/form/FullmaktForm'
import { AfpOffentligForm, afpOffentligPath } from '@/components/fagsystem/afpOffentlig/form/Form'
import { YrkesskaderForm } from '@/components/fagsystem/yrkesskader/form/Form'
import Loading from '@/components/ui/loading/Loading'
import { ArbeidssoekerregisteretForm } from '@/components/fagsystem/arbeidssoekerregisteret/form/Form'
import {
	BestillingsveilederContext,
	BestillingsveilederContextType,
} from '@/components/bestillingsveileder/BestillingsveilederContext'
import { harAvhukedeAttributter } from '@/components/bestillingsveileder/utils'
import { Alert } from '@navikt/ds-react'
import { useGruppeById } from '@/utils/hooks/useGruppe'
import {
	sigrunstubSummertSkattegrunnlagAttributt,
	SigrunstubSummertSkattegrunnlagForm,
} from '@/components/fagsystem/sigrunstubSummertSkattegrunnlag/form/Form'
import { NavAnsatt } from '@/components/fagsystem/nom/form/NavAnsattForm'
import {
	AlderspensjonNyUttaksgradForm,
	alderspensjonNyUttaksgradPath,
} from '@/components/fagsystem/alderspensjon/form/AlderspensjonNyUttaksgradForm'

const HistarkForm = lazy(() => import('@/components/fagsystem/histark/form/HistarkForm'))
const DokarkivForm = lazy(() => import('@/components/fagsystem/dokarkiv/form/DokarkivForm'))
const SykdomForm = lazy(() => import('@/components/fagsystem/sykdom/form/Form'))
const PdlfForm = lazy(() => import('@/components/fagsystem/pdlf/form/Form'))

const gruppeNavn = (gruppe: any) => <span style={{ fontWeight: 'bold' }}>{gruppe?.navn}</span>

const Steg2: React.FC = () => {
	const { getValues, watch } = useFormContext()
	const gruppeId = watch('gruppeId')
	const opts = useContext(BestillingsveilederContext) as BestillingsveilederContextType
	const { gruppe } = useGruppeById(gruppeId)

	const getEmptyMessage = (leggTil, importTestnorge) => {
		if (leggTil) {
			return 'Du har ikke lagt til flere egenskaper. Vennligst gå tilbake og velg nye egenskaper.'
		} else if (importTestnorge) {
			return (
				<span>
					Du har ikke lagt til egenskaper. Dolly vil importere valgt Test-Norge person(er) til
					{gruppeId === null && <> gruppe du velger i første steg.</>}
					{gruppeId !== null && <> gruppen {gruppeNavn(gruppe)}.</>}
				</span>
			)
		}
		return 'Du har ikke valgt noen egenskaper. Dolly oppretter personer med tilfeldige verdier.'
	}

	const leggTil = opts.is.leggTil
	const importTestnorge = opts.is.importTestnorge

	if (!harAvhukedeAttributter(getValues())) {
		return <Alert variant={'info'}>{getEmptyMessage(leggTil, importTestnorge)}</Alert>
	}

	return (
		<div>
			<PdlfForm />
			{getValues('fullmakt') && <FullmaktForm />}
			{(getValues('nomdata') || getValues('skjerming')) && <NavAnsatt />}
			{getValues('aareg') && <AaregForm />}
			{getValues(sigrunstubPensjonsgivendeAttributt) && <SigrunstubPensjonsgivendeForm />}
			{getValues(sigrunstubSummertSkattegrunnlagAttributt) && (
				<SigrunstubSummertSkattegrunnlagForm />
			)}
			{getValues('inntektstub') && <InntektstubForm />}
			{getValues('inntektsmelding') && <InntektsmeldingForm />}
			{getValues('skattekort') && <SkattekortForm />}
			{getValues('arbeidssoekerregisteret') && <ArbeidssoekerregisteretForm />}
			{getValues('arbeidsplassenCV') && <ArbeidsplassenForm />}
			{getValues('pensjonforvalter') && <PensjonForm />}
			{getValues(avtalePath) && <PensjonsavtaleForm />}
			{getValues(tpPath) && <TjenestepensjonForm />}
			{getValues(alderspensjonPath) && <AlderspensjonForm />}
			{getValues(alderspensjonNyUttaksgradPath) && <AlderspensjonNyUttaksgradForm />}
			{getValues(uforetrygdPath) && <UforetrygdForm />}
			{getValues(afpOffentligPath) && <AfpOffentligForm />}
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
