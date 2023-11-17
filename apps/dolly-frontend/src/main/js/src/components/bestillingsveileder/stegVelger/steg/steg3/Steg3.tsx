import React, { Suspense, useContext, useEffect } from 'react'
import * as Yup from 'yup'
import { harAvhukedeAttributter } from '@/components/bestillingsveileder/utils'
import { MiljoVelger } from '@/components/miljoVelger/MiljoVelger'
import { MalForm } from './MalForm'
import { VelgGruppe } from '@/components/bestillingsveileder/stegVelger/steg/steg3/VelgGruppe'
import { OppsummeringKommentarForm } from '@/components/bestillingsveileder/stegVelger/steg/steg3/OppsummeringKommentarForm'
import { BestillingsveilederContext } from '@/components/bestillingsveileder/BestillingsveilederContext'
import * as _ from 'lodash-es'
import { MalFormOrganisasjon } from '@/pages/organisasjoner/MalFormOrganisasjon'
import { useCurrentBruker, useOrganisasjonTilgang } from '@/utils/hooks/useBruker'
import Loading from '@/components/ui/loading/Loading'
import { useFormContext } from 'react-hook-form'

const Bestillingskriterier = React.lazy(
	() => import('@/components/bestilling/sammendrag/kriterier/Bestillingskriterier'),
)

export const Steg3 = () => {
	const opts = useContext(BestillingsveilederContext)
	const formMethods = useFormContext()
	const { currentBruker } = useCurrentBruker()

	const { organisasjonTilgang } = useOrganisasjonTilgang()
	const tilgjengeligMiljoe = organisasjonTilgang?.miljoe

	const importTestnorge = opts.is.importTestnorge

	const erOrganisasjon = formMethods.getValues('organisasjon')
	const erQ2MiljoeAvhengig =
		_.get(formMethods.getValues(), 'pdldata.person.fullmakt') ||
		_.get(formMethods.getValues(), 'pdldata.person.falskIdentitet') ||
		_.get(formMethods.getValues(), 'pdldata.person.falskIdentitet') ||
		_.get(formMethods.getValues(), 'pdldata.person.utenlandskIdentifikasjonsnummer') ||
		_.get(formMethods.getValues(), 'pdldata.person.kontaktinformasjonForDoedsbo')

	const bankIdBruker = currentBruker?.brukertype === 'BANKID'

	const sivilstand = _.get(formMethods.getValues(), 'pdldata.person.sivilstand')
	const harRelatertPersonVedSivilstand = sivilstand?.some((item) => item.relatertVedSivilstand)

	const nyIdent = _.get(formMethods.getValues(), 'pdldata.person.nyident')
	const harEksisterendeNyIdent = nyIdent?.some((item) => item.eksisterendeIdent)

	const forelderBarnRelasjon = _.get(formMethods.getValues(), 'pdldata.person.forelderBarnRelasjon')
	const harRelatertPersonBarn = forelderBarnRelasjon?.some((item) => item.relatertPerson)

	const alleredeValgtMiljoe = () => {
		if (bankIdBruker) {
			return tilgjengeligMiljoe ? [tilgjengeligMiljoe] : ['q1']
		}
		return erQ2MiljoeAvhengig ? ['q2'] : []
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
			formMethods.setValue('gruppeId', opts.gruppe?.id)
		} else if (bankIdBruker) {
			formMethods.setValue('environments', alleredeValgtMiljoe())
		} else if (erQ1EllerQ2MiljoeAvhengig(formMethods.getValues())) {
			formMethods.setValue('environments', ['q1', 'q2'])
		} else if (formMethods.getValues()?.sykemelding) {
			formMethods.setValue('environments', ['q1'])
		} else if (erQ2MiljoeAvhengig) {
			formMethods.setValue('environments', alleredeValgtMiljoe())
		} else if (!formMethods.getValues()?.environments) {
			formMethods.setValue('environments', [])
		}
		if (harRelatertPersonVedSivilstand || harEksisterendeNyIdent || harRelatertPersonBarn) {
			formMethods.setValue('malBestillingNavn', undefined)
		}
	}, [])

	const visMiljoeVelger = formMethods.getValues().hasOwnProperty('environments')
	return (
		<div>
			{harAvhukedeAttributter(formMethods.getValues()) && (
				<div className="oppsummering">
					<Suspense fallback={<Loading label={'Laster bestillingskriterier...'} />}>
						<Bestillingskriterier bestilling={formMethods.getValues()} />
					</Suspense>
				</div>
			)}
			{visMiljoeVelger && (
				<MiljoVelger
					bestillingsdata={formMethods.getValues()}
					heading="Hvilke miljøer vil du opprette i?"
					bankIdBruker={bankIdBruker}
					orgTilgang={organisasjonTilgang}
					alleredeValgtMiljoe={alleredeValgtMiljoe()}
				/>
			)}
			{importTestnorge && !opts.gruppe && (
				<VelgGruppe formMethods={formMethods} title={'Hvilken gruppe vil du importere til?'} />
			)}
			{importTestnorge && opts.gruppe && (
				<div className="oppsummering">
					<div className="bestilling-detaljer">
						<h4>Gruppe for import</h4>
						<div className="info-text">
							<div style={{}}>{opts.gruppe.navn}</div>
						</div>
					</div>
				</div>
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
			{!erOrganisasjon && !importTestnorge && (
				<OppsummeringKommentarForm formMethods={formMethods} />
			)}
		</div>
	)
}

Steg3.label = 'Oppsummering'

Steg3.validation = Yup.object(
	Object.assign({}, MiljoVelger.validation, MalForm.validation, VelgGruppe.validation),
)
