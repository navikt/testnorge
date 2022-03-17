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

export const Steg3 = ({ formikBag, brukertype, brukerId }) => {
	const opts = useContext(BestillingsveilederContext)
	const importTestnorge = opts.is.importTestnorge
	const erNyIdent = !opts.personFoerLeggTil && !importTestnorge
	const erOrganisasjon = formikBag.values.hasOwnProperty('organisasjon')
	const bankIdBruker = brukertype === 'BANKID'

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
			formikBag.setFieldValue('gruppeId', '')
		} else {
			formikBag.setFieldValue('environments', alleredeValgtMiljoe())
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
			{importTestnorge && <VelgGruppe formikBag={formikBag} />}
			{!erOrganisasjon && !importTestnorge && <MalForm formikBag={formikBag} brukerId={brukerId} />}
			{!erOrganisasjon && !importTestnorge && <OppsummeringKommentarForm formikBag={formikBag} />}
		</div>
	)
}

Steg3.label = 'Oppsummering'

Steg3.validation = Yup.object(
	Object.assign({}, MiljoVelger.validation, MalForm.validation, VelgGruppe.validation)
)
