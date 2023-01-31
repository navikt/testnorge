import React, { useContext, useEffect } from 'react'
import * as Yup from 'yup'
import { harAvhukedeAttributter } from '@/components/bestillingsveileder/utils'
import Bestillingskriterier from '@/components/bestilling/sammendrag/kriterier/Bestillingskriterier'
import { MiljoVelger } from '@/components/miljoVelger/MiljoVelger'
import { MalForm } from './MalForm'
import { BestillingInfoboks } from './BestillingInfoboks'
import { IdentVelger } from './IdentVelger'
import { VelgGruppe } from '@/components/bestillingsveileder/stegVelger/steg/steg3/VelgGruppe'
import { OppsummeringKommentarForm } from '@/components/bestillingsveileder/stegVelger/steg/steg3/OppsummeringKommentarForm'
import { BestillingsveilederContext } from '@/components/bestillingsveileder/Bestillingsveileder'
import * as _ from 'lodash-es'
import { MalFormOrganisasjon } from '@/pages/organisasjoner/MalFormOrganisasjon'
import { useFormikContext } from 'formik'
import { useBrukerProfil, useCurrentBruker, useOrganisasjonTilgang } from '@/utils/hooks/useBruker'

export const Steg3 = () => {
	const opts = useContext(BestillingsveilederContext)
	const formikBag = useFormikContext()
	const { currentBruker } = useCurrentBruker()
	const { brukerProfil } = useBrukerProfil()
	const { organisasjonTilgang } = useOrganisasjonTilgang(brukerProfil?.orgnummer)

	// const testOrgTilgang = {
	// 	id: null,
	// 	miljoe: 'q2',
	// 	organisasjonNummer: '811307602',
	// }

	const importTestnorge = opts.is.importTestnorge
	const erNyIdent = !opts.personFoerLeggTil && !importTestnorge
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
			return erQ2MiljoeAvhengig ? ['q1', 'q2'] : ['q1']
		}
		return erQ2MiljoeAvhengig ? ['q2'] : []
	}

	useEffect(() => {
		if (importTestnorge) {
			if (harAvhukedeAttributter(formikBag.values)) {
				formikBag.setFieldValue('environments', alleredeValgtMiljoe())
			}
			formikBag.setFieldValue('gruppeId', opts.gruppe?.id)
		} else if (formikBag.values?.sykemelding || bankIdBruker) {
			formikBag.setFieldValue('environments', ['q1'])
		} else if (!formikBag.values?.environments) {
			formikBag.setFieldValue('environments', [])
		} else if (erQ2MiljoeAvhengig) {
			formikBag.setFieldValue('environments', alleredeValgtMiljoe())
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
					<Bestillingskriterier bestilling={formikBag.values} />
					<BestillingInfoboks bestillingsdata={formikBag.values} />
				</div>
			)}
			{!erOrganisasjon && erNyIdent && <IdentVelger formikBag={formikBag} />}
			{visMiljoeVelger && (
				<MiljoVelger
					bestillingsdata={formikBag.values}
					heading="Hvilke miljøer vil du opprette i?"
					bankIdBruker={bankIdBruker}
					orgTilgang={organisasjonTilgang}
					// orgTilgang={testOrgTilgang}
					alleredeValgtMiljoe={alleredeValgtMiljoe()}
				/>
			)}
			{importTestnorge && !opts.gruppe && (
				<VelgGruppe formikBag={formikBag} title={'Hvilken gruppe vil du importere til?'} />
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
	Object.assign({}, MiljoVelger.validation, MalForm.validation, VelgGruppe.validation)
)
