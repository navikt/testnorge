import React from 'react'
import _get from 'lodash/get'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { AlertStripeInfo } from 'nav-frontend-alertstriper'
import { DollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'
import { SelectOptionsManager as Options } from '~/service/SelectOptions'

export const Barn = ({ formikBag, lagOptions, identInfo, hovedIdent }) => {
	const valgbareIdenter = lagOptions(
		Object.keys(identInfo).filter(ident => !(ident === hovedIdent))
	)

	if (valgbareIdenter.length < 1) {
		return <AlertStripeInfo>Det finnes ikke flere ledige personer i gruppa</AlertStripeInfo>
	}

	return (
		<div className="bestilling-detaljer">
			<DollyFieldArray name="tpsf.relasjoner.barn" title="Barn" newEntry={initialBarn}>
				{(path, idx) => (
					<React.Fragment key={idx}>
						<FormikSelect
							name={`${path}.ident`}
							label="Fnr/dnr/bost"
							options={valgbareIdenter}
							isClearable={false}
						/>
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
				)}
			</DollyFieldArray>
		</div>
	)
}

const muligeForeldre = props => {
	return props.values.tpsf.relasjoner.partner.map((partner, idx) => {
		return { value: partner.ident, label: `Partner ${idx + 1}` }
	})
}

const initialBarn = {
	ident: '',
	partnerIdent: '',
	borHos: 'MEG'
}
