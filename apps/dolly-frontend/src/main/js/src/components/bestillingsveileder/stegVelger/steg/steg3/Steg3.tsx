import React, { Suspense, useContext, useEffect, useState } from 'react'
import * as Yup from 'yup'
import { harAvhukedeAttributter } from '@/components/bestillingsveileder/utils'
import { MiljoVelger } from '@/components/miljoVelger/MiljoVelger'
import { MalForm } from './MalForm'
import { VelgGruppe } from '@/components/bestillingsveileder/stegVelger/steg/steg3/VelgGruppe'
import { OppsummeringKommentarForm } from '@/components/bestillingsveileder/stegVelger/steg/steg3/OppsummeringKommentarForm'
import { BestillingsveilederContext } from '@/components/bestillingsveileder/BestillingsveilederContext'
import * as _ from 'lodash-es'
import { MalFormOrganisasjon } from '@/pages/organisasjoner/MalFormOrganisasjon'
import { useFormikContext } from 'formik'
import { useCurrentBruker, useOrganisasjonTilgang } from '@/utils/hooks/useBruker'
import Loading from '@/components/ui/loading/Loading'
import { Gruppevalg } from '@/components/velgGruppe/VelgGruppeToggle'
import { Bestillingsdata } from '@/components/bestilling/sammendrag/Bestillingsdata'

const Bestillingskriterier = React.lazy(
	() => import('@/components/bestilling/sammendrag/kriterier/Bestillingskriterier'),
)

export const Steg3 = () => {
	const opts = useContext(BestillingsveilederContext)
	const formikBag = useFormikContext()
	const { currentBruker } = useCurrentBruker()

	const [gruppevalg, setGruppevalg] = useState(Gruppevalg.MINE)

	const { organisasjonTilgang } = useOrganisasjonTilgang()
	const tilgjengeligMiljoe = organisasjonTilgang?.miljoe

	const importTestnorge = opts.is.importTestnorge

	const erOrganisasjon = formikBag.values.hasOwnProperty('organisasjon')
	const erQ2MiljoeAvhengig =
		_.get(formikBag.values, 'pdldata.person.fullmakt') ||
		_.get(formikBag.values, 'pdldata.person.falskIdentitet') ||
		_.get(formikBag.values, 'pdldata.person.falskIdentitet') ||
		_.get(formikBag.values, 'pdldata.person.utenlandskIdentifikasjonsnummer') ||
		_.get(formikBag.values, 'pdldata.person.kontaktinformasjonForDoedsbo')

	const bankIdBruker = currentBruker?.brukertype === 'BANKID'

	const sivilstand = _.get(formikBag.values, 'pdldata.person.sivilstand')
	const harRelatertPersonVedSivilstand = sivilstand?.some((item) => item.relatertVedSivilstand)

	const nyIdent = _.get(formikBag.values, 'pdldata.person.nyident')
	const harEksisterendeNyIdent = nyIdent?.some((item) => item.eksisterendeIdent)

	const forelderBarnRelasjon = _.get(formikBag.values, 'pdldata.person.forelderBarnRelasjon')
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
			if (harAvhukedeAttributter(formikBag.values)) {
				formikBag.setFieldValue('environments', alleredeValgtMiljoe())
			}
			formikBag.setFieldValue('gruppeId', opts.gruppe?.id)
		} else if (bankIdBruker) {
			formikBag.setFieldValue('environments', alleredeValgtMiljoe())
		} else if (erQ1EllerQ2MiljoeAvhengig(formikBag?.values)) {
			formikBag.setFieldValue('environments', ['q1', 'q2'])
		} else if (formikBag.values?.sykemelding) {
			formikBag.setFieldValue('environments', ['q1'])
		} else if (erQ2MiljoeAvhengig) {
			formikBag.setFieldValue('environments', alleredeValgtMiljoe())
		} else if (!formikBag.values?.environments) {
			formikBag.setFieldValue('environments', [])
		}
		if (harRelatertPersonVedSivilstand || harEksisterendeNyIdent || harRelatertPersonBarn) {
			formikBag.setFieldValue('malBestillingNavn', undefined)
		}
	}, [])

	const visMiljoeVelger = formikBag.values.hasOwnProperty('environments')
	return (
		<div>
			{harAvhukedeAttributter(formikBag.values) && (
				<div className="oppsummering">
					<Suspense fallback={<Loading label={'Laster bestillingskriterier...'} />}>
						<Bestillingsdata bestilling={formikBag.values} />
						{/*//TODO: Fjernes naar bestillingsdata er klar*/}
						<Bestillingskriterier bestilling={formikBag.values} />
					</Suspense>
				</div>
			)}
			{visMiljoeVelger && (
				<MiljoVelger
					bestillingsdata={formikBag.values}
					heading="Hvilke miljøer vil du opprette i?"
					bankIdBruker={bankIdBruker}
					orgTilgang={organisasjonTilgang}
					alleredeValgtMiljoe={alleredeValgtMiljoe()}
				/>
			)}
			{importTestnorge && !opts.gruppe && (
				<VelgGruppe
					formikBag={formikBag}
					title={'Hvilken gruppe vil du importere til?'}
					gruppevalg={gruppevalg}
					setGruppevalg={setGruppevalg}
				/>
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
						formikBag={formikBag}
						brukerId={currentBruker?.brukerId}
						opprettetFraMal={opts?.mal?.malNavn}
					/>
				)}
			{erOrganisasjon && (
				<MalFormOrganisasjon
					brukerId={currentBruker?.brukerId}
					formikBag={formikBag}
					opprettetFraMal={opts?.mal?.malNavn}
				/>
			)}
			{!erOrganisasjon && !importTestnorge && <OppsummeringKommentarForm formikBag={formikBag} />}
		</div>
	)
}

Steg3.label = 'Oppsummering'

Steg3.validation = Yup.object(
	Object.assign({}, MiljoVelger.validation, MalForm.validation, VelgGruppe.validation),
)
