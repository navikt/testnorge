import React, { Suspense, useContext, useEffect } from 'react'
import { harAvhukedeAttributter } from '@/components/bestillingsveileder/utils'
import { MiljoVelger } from '@/components/miljoVelger/MiljoVelger'
import { MalForm } from './MalForm'
import { OppsummeringKommentarForm } from '@/components/bestillingsveileder/stegVelger/steg/steg3/OppsummeringKommentarForm'
import { BestillingsveilederContext } from '@/components/bestillingsveileder/BestillingsveilederContext'
import { MalFormOrganisasjon } from '@/pages/organisasjoner/MalFormOrganisasjon'
import { useCurrentBruker } from '@/utils/hooks/useBruker'
import Loading from '@/components/ui/loading/Loading'
import { useFormContext } from 'react-hook-form'
import { useOrganisasjonMiljoe } from '@/utils/hooks/useOrganisasjonTilgang'
import { useDollyEnvironments } from '@/utils/hooks/useEnvironments'
import { filterMiljoe, gyldigeDollyMiljoer } from '@/components/miljoVelger/MiljoVelgerUtils'

const Bestillingskriterier = React.lazy(
	() => import('@/components/bestilling/sammendrag/kriterier/Bestillingskriterier'),
)

const Steg3 = ({ loadingBestilling }: { loadingBestilling: boolean }) => {
	const opts = useContext(BestillingsveilederContext)
	const formMethods = useFormContext()

	const { currentBruker } = useCurrentBruker()

	const { organisasjonMiljoe, loading } = useOrganisasjonMiljoe()
	const tilgjengeligeMiljoer = organisasjonMiljoe?.miljoe
	// const tilgjengeligeMiljoer = 'q1' //TODO: Tilgjengelig miljoe for BankID-bruker

	const { dollyEnvironments } = useDollyEnvironments()
	const gyldigeEnvironments = gyldigeDollyMiljoer(dollyEnvironments)

	// const tilgjengeligeMiljoerArray = bankIdBruker
	// 	? tilgjengeligeMiljoer
	// 		? tilgjengeligeMiljoer.split(',')
	// 		: ['q1']
	// 	: null
	//TODO: Rydd opp i navngivning - miljoer/environments

	const importTestnorge = opts?.is?.importTestnorge

	const erOrganisasjon = formMethods.getValues('organisasjon')

	const sivilstand = formMethods.watch('pdldata.person.sivilstand')
	const harRelatertPersonVedSivilstand = sivilstand?.some((item) => item.relatertVedSivilstand)

	const nyIdent = formMethods.watch('pdldata.person.nyident')
	const harEksisterendeNyIdent = nyIdent?.some((item) => item.eksisterendeIdent)

	const forelderBarnRelasjon = formMethods.watch('pdldata.person.forelderBarnRelasjon')
	const harRelatertPersonBarn = forelderBarnRelasjon?.some((item) => item.relatertPerson)

	const fagsystemMiljoer = () => {
		const values = formMethods.getValues()
		// if (loading) {
		// 	return []
		// } else
		if (
			values.dokarkiv ||
			values.instdata ||
			values.arenaforvalter ||
			(values.pensjonforvalter && values.sykemelding)
		) {
			return ['q1', 'q2']
		} else if (values.pensjonforvalter) {
			return ['q2']
		} else if (values.sykemelding) {
			return ['q1']
		}
		return []
	}

	const defaultEnvironments = filterMiljoe(
		gyldigeEnvironments,
		fagsystemMiljoer(),
		tilgjengeligeMiljoer,
	)
	console.log('defaultEnvironments: ', defaultEnvironments) //TODO - SLETT MEG

	useEffect(() => {
		formMethods.setValue('environments', defaultEnvironments)
		if (harRelatertPersonVedSivilstand || harEksisterendeNyIdent || harRelatertPersonBarn) {
			formMethods.setValue('malBestillingNavn', undefined)
		}
		formMethods.trigger('environments')
	}, [])

	const visMiljoeVelger = formMethods.watch('environments')
	// console.log("visMiljoeVelger: ", visMiljoeVelger) //TODO - SLETT MEG

	if (loadingBestilling) {
		return <Loading label={'Oppretter bestilling ...'} />
	}

	return (
		<div>
			{harAvhukedeAttributter(formMethods.getValues()) && (
				<div className="oppsummering">
					<Suspense fallback={<Loading label={'Laster bestillingskriterier ...'} />}>
						<Bestillingskriterier bestilling={formMethods.getValues()} />
					</Suspense>
				</div>
			)}
			{visMiljoeVelger && (
				<MiljoVelger
					bestillingsdata={formMethods.getValues()}
					heading="Hvilke miljøer vil du opprette i?"
					currentBruker={currentBruker}
					// bankIdBruker={bankIdBruker}
					// orgTilgang={organisasjonMiljoe}
					// alleredeValgtMiljoe={alleredeValgtMiljoe()}
					// tilgjengeligeMiljoer={tilgjengeligeMiljoerArray}
				/>
			)}
			{!erOrganisasjon &&
				!importTestnorge &&
				!harRelatertPersonVedSivilstand &&
				!harEksisterendeNyIdent &&
				!harRelatertPersonBarn && (
					<MalForm
						formMethods={formMethods}
						brukerId={currentBruker?.brukerId}
						opprettetFraMal={opts?.mal?.malNavn}
					/>
				)}
			{erOrganisasjon && (
				<MalFormOrganisasjon
					brukerId={currentBruker?.brukerId}
					formMethods={formMethods}
					opprettetFraMal={opts?.mal?.malNavn}
				/>
			)}
			{!erOrganisasjon && <OppsummeringKommentarForm formMethods={formMethods} />}
		</div>
	)
}

export default Steg3
