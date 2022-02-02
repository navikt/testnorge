import React from 'react'
import _get from 'lodash/get'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { AlertStripeInfo } from 'nav-frontend-alertstriper'
import { FormikDollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'
import { SelectOptionsManager as Options } from '~/service/SelectOptions'
import _has from 'lodash/has'

export const Barn = ({ formikBag, lagOptions, identInfo, hovedIdent }) => {
	const valgbareIdenter = lagOptions(
		Object.keys(identInfo).filter((ident) => ident !== hovedIdent),
		identInfo
	)

	if (valgbareIdenter.length < 1) {
		return (
			<AlertStripeInfo>
				Det finnes ingen ledige personer i gruppa som personen kan bli forelder til
			</AlertStripeInfo>
		)
	}

	return (
		<div className="bestilling-detaljer">
			<FormikDollyFieldArray
				name="pdldata.person.forelderBarnRelasjon"
				header="Barn"
				newEntry={initialBarn}
			>
				{(path, idx) => {
					console.log('formikBag: ', formikBag.values) //TODO - SLETT MEG
					const erBarn = _has(formikBag, `${path}[${idx}].partnerErIkkeForelder`)
					return (
						<React.Fragment key={idx}>
							{/* Endres når det er bestemt hvordan bredde styles */}
							<div style={{ minWidth: '350px' }}>
								<FormikSelect
									name={`${path}.ident`}
									label="Fnr/dnr/bost"
									options={valgbareIdenter}
									isClearable={false}
									size="grow"
								/>
							</div>
							<FormikSelect
								name={`${path}.borHos`}
								label="Bor hos"
								options={Options('barnBorHos')}
								isClearable={false}
							/>

							{muligeForeldre(formikBag).length > 0 && (
								<FormikSelect
									name={`${path}.partnerIdent`}
									label="Forelder 2"
									options={muligeForeldre(formikBag)}
								/>
							)}
						</React.Fragment>
					)
				}}
			</FormikDollyFieldArray>
		</div>
	)
}

const muligeForeldre = (props) => {
	const partnere = _get(props, 'values.tpsf.relasjoner.partnere', [])
	return partnere.map((partner, idx) => {
		return { value: partner.ident, label: `Partner ${idx + 1}` }
	})
}

const initialBarn = {
	ident: '',
	partnerIdent: '',
	borHos: 'MEG',
}
