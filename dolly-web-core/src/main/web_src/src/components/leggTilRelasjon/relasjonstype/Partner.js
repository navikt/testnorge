import React from 'react'
import _get from 'lodash/get'
import { AlertStripeInfo } from 'nav-frontend-alertstriper'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { FormikCheckbox } from '~/components/ui/form/inputs/checbox/Checkbox'
import { FormikDollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'
import { Sivilstand } from '~/components/fagsystem/tpsf/form/familierelasjoner/partials/Sivilstand'

export const Partner = ({ lagOptions, identInfo, hovedIdent }) => {
	const valgbareIdenter = lagOptions(muligePartnere(identInfo, hovedIdent), identInfo)
	const harAlleredePartner = harPartner(identInfo[hovedIdent])

	if (harAlleredePartner) {
		return (
			<AlertStripeInfo>
				Personen har allerede en partner, og du får derfor ikke legge til flere.
			</AlertStripeInfo>
		)
	} else if (valgbareIdenter.length < 1) {
		return (
			<AlertStripeInfo>
				Det finnes ingen ledige personer i gruppa som personen kan bli partner med.
			</AlertStripeInfo>
		)
	}
	return (
		<div className="bestilling-detaljer">
			<FormikDollyFieldArray
				name="tpsf.relasjoner.partner"
				title="Partner"
				newEntry={initialPartner}
			>
				{(path, idx) => (
					<React.Fragment key={idx}>
						{/* Endres når styling av bredde er på plass */}
						<div style={{ minWidth: '350px' }}>
							<FormikSelect
								name={`${path}.ident`}
								label="Fnr/dnr/bost"
								options={valgbareIdenter}
								isClearable={false}
								size="grow"
							/>
						</div>
						<FormikCheckbox name={`${path}.harFellesAdresse`} label="Bor sammen" />
						<Sivilstand basePath={`${path}.sivilstander`} />
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
			sivilstandRegdato: ''
		}
	],
	harFellesAdresse: true
}

const harPartner = identInfo => {
	return (
		identInfo.relasjoner &&
		identInfo.relasjoner.some(relasjon => {
			return relasjon.relasjonTypeNavn === 'PARTNER'
		})
	)
}

const muligePartnere = (identInfo, hovedIdent) => {
	// Per nå kan ikke bruker legge til en partner som allerede har en annen partner
	return Object.keys(identInfo).filter(ident => {
		if (ident === hovedIdent) return false
		return !harPartner(identInfo[ident])
	})
}
