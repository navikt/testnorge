import React, { Suspense, useContext, useEffect } from 'react'
import { harAvhukedeAttributter } from '@/components/bestillingsveileder/utils'
import { MiljoVelger } from '@/components/miljoVelger/MiljoVelger'
import { MalForm } from './MalForm'
import { OppsummeringKommentarForm } from '@/components/bestillingsveileder/stegVelger/steg/steg3/OppsummeringKommentarForm'
import { BestillingsveilederContext } from '@/components/bestillingsveileder/BestillingsveilederContext'
import { MalFormOrganisasjon } from '@/pages/organisasjoner/MalFormOrganisasjon'
import { useCurrentBruker } from '@/utils/hooks/useBruker'
import Loading from '@/components/ui/loading/Loading'
import { Bestillingsvisning } from '@/components/bestilling/sammendrag/Bestillingsvisning'
import { useFormContext } from 'react-hook-form'
import { useOrganisasjonMiljoe } from '@/utils/hooks/useOrganisasjonTilgang'
import { useDollyEnvironments } from '@/utils/hooks/useEnvironments'
import { filterMiljoe, gyldigeDollyMiljoer } from '@/components/miljoVelger/MiljoVelgerUtils'

const Steg3 = ({ loadingBestilling }: { loadingBestilling: boolean }) => {
	const opts = useContext(BestillingsveilederContext)
	const formMethods = useFormContext()
	const { currentBruker } = useCurrentBruker()

	const { organisasjonMiljoe } = useOrganisasjonMiljoe()
	const tilgjengeligeMiljoer = organisasjonMiljoe?.miljoe

	const { dollyEnvironments, loading } = useDollyEnvironments()
	const gyldigeEnvironments = gyldigeDollyMiljoer(dollyEnvironments)

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

	useEffect(() => {
		formMethods.setValue('environments', defaultEnvironments)
		if (harRelatertPersonVedSivilstand || harEksisterendeNyIdent || harRelatertPersonBarn) {
			formMethods.setValue('malBestillingNavn', undefined)
		}
		formMethods.trigger('environments')
	}, [])

	const visMiljoeVelger = formMethods.watch('environments')

	if (loadingBestilling) {
		return <Loading label={'Oppretter bestilling ...'} />
	}

	return (
		<div>
			{harAvhukedeAttributter(formMethods.getValues()) && (
				<div className="oppsummering">
					<Suspense fallback={<Loading label={'Laster bestillingskriterier ...'} />}>
						<Bestillingsvisning bestilling={formMethods.getValues()} />
					</Suspense>
				</div>
			)}
			{visMiljoeVelger && (
				<MiljoVelger
					bestillingsdata={formMethods.getValues()}
					heading="Hvilke miljøer vil du opprette i?"
					currentBruker={currentBruker}
					gyldigeMiljoer={gyldigeEnvironments}
					tilgjengeligeMiljoer={tilgjengeligeMiljoer}
					loading={loading}
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
