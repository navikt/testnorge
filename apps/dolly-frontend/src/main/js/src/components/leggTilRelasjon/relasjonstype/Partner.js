import React from 'react'
import _get from 'lodash/get'
import { AlertStripeInfo } from 'nav-frontend-alertstriper'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { FormikCheckbox } from '~/components/ui/form/inputs/checbox/Checkbox'
import { FormikDollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'
import { Sivilstand } from '~/components/fagsystem/tpsf/form/familierelasjoner/partials/partnere/sivilstand/Sivilstand'

export const Partner = ({ lagOptions, identInfo, hovedIdent, formikBag }) => {
	const valgbareIdenter = lagOptions(muligePartnere(identInfo, hovedIdent), identInfo)
	const harAlleredePartner = harPartner(identInfo[hovedIdent])

	let visMelding = null

	if (harAlleredePartner)
		visMelding = 'Personen har allerede en partner, og du får derfor ikke legge til flere.'
	if (valgbareIdenter.length < 1)
		visMelding = 'Det finnes ingen ledige personer i gruppa som personen kan bli partner med.'

	if (visMelding) return <AlertStripeInfo>{visMelding}</AlertStripeInfo>

	return (
		<div className="bestilling-detaljer">
			<FormikDollyFieldArray
				name="tpsf.relasjoner.partnere"
				header="Partner"
				newEntry={initialPartner}
			>
				{(path, idx) => (
					<React.Fragment key={idx}>
						<FormikSelect
							name={`${path}.ident`}
							label="Fnr/dnr/npid"
							options={valgbareIdenter}
							isClearable={false}
							size="large"
						/>

						<FormikCheckbox name={`${path}.harFellesAdresse`} label="Bor sammen" checkboxMargin />
						<Sivilstand
							sivilstander={_get(formikBag.values, `${path}.sivilstander`)}
							basePath={`${path}.sivilstander`}
							formikBag={formikBag}
							erSistePartner={formikBag.values.tpsf.relasjoner.partnere.length === idx + 1}
						/>
					</React.Fragment>
				)}
			</FormikDollyFieldArray>
		</div>
	)
}

const initialPartner = {
	ident: '',
	sivilstander: [
		{
			sivilstand: '',
			sivilstandRegdato: '',
		},
	],
	harFellesAdresse: true,
}

const harPartner = (identInfo) =>
	_get(identInfo, 'relasjoner', []).some((r) => r.relasjonTypeNavn === 'PARTNER')

// Per nå kan ikke bruker legge til en partner som allerede har en annen partner
const muligePartnere = (identInfo, hovedIdent) => {
	return Object.keys(identInfo).filter((ident) => {
		if (ident === hovedIdent) return false
		return !harPartner(identInfo[ident])
	})
}
