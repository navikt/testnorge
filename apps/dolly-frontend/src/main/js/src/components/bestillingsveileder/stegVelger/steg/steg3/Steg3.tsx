import React, { Suspense, useContext, useEffect } from 'react'
import { harAvhukedeAttributter } from '@/components/bestillingsveileder/utils'
import { MiljoVelger } from '@/components/miljoVelger/MiljoVelger'
import { MalForm } from './MalForm'
import { OppsummeringKommentarForm } from '@/components/bestillingsveileder/stegVelger/steg/steg3/OppsummeringKommentarForm'
import {
	BestillingsveilederContext,
	BestillingsveilederContextType,
} from '@/components/bestillingsveileder/BestillingsveilederContext'
import { MalFormOrganisasjon } from '@/pages/organisasjoner/MalFormOrganisasjon'
import { useCurrentBruker } from '@/utils/hooks/useBruker'
import Loading from '@/components/ui/loading/Loading'
import { useFormContext } from 'react-hook-form'
import { useOrganisasjonMiljoe } from '@/utils/hooks/useOrganisasjonTilgang'

const Bestillingskriterier = React.lazy(
	() => import('@/components/bestilling/sammendrag/kriterier/Bestillingskriterier'),
)

const Steg3 = ({ loadingBestilling }: { loadingBestilling: boolean }) => {
	const opts = useContext(BestillingsveilederContext) as BestillingsveilederContextType
	const formMethods = useFormContext()
	const { currentBruker } = useCurrentBruker()

	const { organisasjonMiljoe, loading } = useOrganisasjonMiljoe()
	const tilgjengeligMiljoe = organisasjonMiljoe?.miljoe

	const importTestnorge = opts.is.importTestnorge

	const erOrganisasjon = formMethods.getValues('organisasjon')

	const bankIdBruker = currentBruker?.brukertype === 'BANKID'

	const sivilstand = formMethods.watch('pdldata.person.sivilstand')
	const harRelatertPersonVedSivilstand = sivilstand?.some((item) => item.relatertVedSivilstand)

	const nyIdent = formMethods.watch('pdldata.person.nyident')
	const harEksisterendeNyIdent = nyIdent?.some((item) => item.eksisterendeIdent)

	const forelderBarnRelasjon = formMethods.watch('pdldata.person.forelderBarnRelasjon')
	const harRelatertPersonBarn = forelderBarnRelasjon?.some((item) => item.relatertPerson)

	const alleredeValgtMiljoe = () => {
		if (loading) {
			return []
		} else if (bankIdBruker) {
			return tilgjengeligMiljoe ? tilgjengeligMiljoe.split(',') : ['q1']
		}
		return []
	}

	const erQ1EllerQ2MiljoeAvhengig = (values: any) => {
		if (!values) {
			return false
		}
		return values.dokarkiv || values.instdata || values.arenaforvalter || values.pensjonforvalter
	}

	useEffect(() => {
		if (importTestnorge) {
			if (harAvhukedeAttributter(formMethods.getValues())) {
				formMethods.setValue('environments', alleredeValgtMiljoe())
			}
		} else if (bankIdBruker) {
			formMethods.setValue('environments', alleredeValgtMiljoe())
		} else if (erQ1EllerQ2MiljoeAvhengig(formMethods.getValues())) {
			formMethods.setValue('environments', ['q1', 'q2'])
		} else if (formMethods.getValues()?.sykemelding?.detaljertSykemelding) {
			formMethods.setValue('environments', ['q1'])
		} else if (!formMethods.getValues()?.environments) {
			formMethods.setValue('environments', [])
		}
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
						<Bestillingskriterier bestilling={formMethods.getValues()} />
					</Suspense>
				</div>
			)}
			{visMiljoeVelger && (
				<MiljoVelger
					bestillingsdata={formMethods.getValues()}
					heading="Hvilke miljøer vil du opprette i?"
					bankIdBruker={bankIdBruker}
					orgTilgang={organisasjonMiljoe}
					alleredeValgtMiljoe={alleredeValgtMiljoe()}
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
