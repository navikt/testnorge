import React, { useContext, useEffect } from 'react'
import * as Yup from 'yup'
import { harAvhukedeAttributter } from '~/components/bestillingsveileder/utils'
import Bestillingskriterier from '~/components/bestilling/sammendrag/kriterier/Bestillingskriterier'
import { MiljoVelger } from '~/components/miljoVelger/MiljoVelger'
import { MalForm } from './MalForm'
import { BestillingInfoboks } from './BestillingInfoboks'
import { IdentVelger } from './IdentVelger'
import { VelgGruppe } from '~/components/bestillingsveileder/stegVelger/steg/steg3/VelgGruppe'
import { OppsummeringKommentarForm } from '~/components/bestillingsveileder/stegVelger/steg/steg3/OppsummeringKommentarForm'
import { BestillingsveilederContext } from '~/components/bestillingsveileder/Bestillingsveileder'
import _get from 'lodash/get'

export const Steg3 = ({ formikBag, brukertype, brukerId }) => {
	const opts = useContext(BestillingsveilederContext)
	const importTestnorge = opts.is.importTestnorge
	const erNyIdent = !opts.personFoerLeggTil && !importTestnorge
	const erOrganisasjon = formikBag.values.hasOwnProperty('organisasjon')
	const bankIdBruker = brukertype === 'BANKID'

	const sivilstand = _get(formikBag.values, 'pdldata.person.sivilstand')
	const harRelatertPersonVedSivilstand = sivilstand.some((item) => item.relatertVedSivilstand)

	const alleredeValgtMiljoe = () => {
		if (bankIdBruker || (formikBag.values && formikBag.values.sykemelding)) {
			return ['q1']
		}
		return []
	}

	useEffect(() => {
		if (importTestnorge) {
			if (harAvhukedeAttributter(formikBag.values)) {
				formikBag.setFieldValue('environments', alleredeValgtMiljoe())
			}
			formikBag.setFieldValue('gruppeId', opts.gruppe?.id)
		} else {
			formikBag.setFieldValue('environments', alleredeValgtMiljoe())
		}
		if (harRelatertPersonVedSivilstand) {
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
					alleredeValgtMiljoe={alleredeValgtMiljoe()}
				/>
			)}
			{importTestnorge && !opts.gruppe && <VelgGruppe formikBag={formikBag} />}
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
			{!erOrganisasjon && !importTestnorge && !harRelatertPersonVedSivilstand && (
				<MalForm formikBag={formikBag} brukerId={brukerId} opprettetFraMal={opts?.mal?.malNavn} />
			)}
			{!erOrganisasjon && !importTestnorge && <OppsummeringKommentarForm formikBag={formikBag} />}
		</div>
	)
}

Steg3.label = 'Oppsummering'

Steg3.validation = Yup.object(
	Object.assign({}, MiljoVelger.validation, MalForm.validation, VelgGruppe.validation)
)
