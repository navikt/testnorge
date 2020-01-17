import React from 'react'
import * as Yup from 'yup'
import { Formik, Form } from 'formik'
import _get from 'lodash/get'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { AlertStripeInfo } from 'nav-frontend-alertstriper'
import { FormikCheckbox } from '~/components/ui/form/inputs/checbox/Checkbox'
import { FieldArrayAddButton } from '~/components/ui/form/fieldArray/DollyFieldArray'
import { DollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'
import { SelectOptionsManager as Options } from '~/service/SelectOptions'

export const Barn = ({ formikBag, lagOptions, identInfo, hovedIdent }) => {
	const valgbareIdenter = lagOptions(muligeBarn(identInfo, hovedIdent))

	if (valgbareIdenter.length < 1) {
		return (
			<AlertStripeInfo>
				Det finnes ikke flere ledige personer i gruppa som kan bli barnet til personen.
			</AlertStripeInfo>
		)
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

const muligeBarn = (identInfo, hovedIdent) => {
	//Denne må testes nå vi får hovedperson med foreldre
	//Hvis det kun blir adopsjon -> trenger vi sjekke MOR og FAR?
	return Object.keys(identInfo).filter(ident => {
		if (ident === hovedIdent) return false
		if (!identInfo[ident].relasjoner) return true

		const counter = identInfo[ident].relasjoner.reduce((acc, relasjon) => {
			if (relasjon.relasjonTypeNavn === 'MOR' || relasjon.relasjonTypeNavn === 'FAR') {
				// ADOPSJON?
				acc++
			}
			return acc
		}, 0)
		return counter < 2
	})
}

const initialBarn = {
	ident: '',
	partnerIdent: '',
	borHos: 'MEG'
}
