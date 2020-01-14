import React from 'react'
import * as Yup from 'yup'
import { Formik, Form } from 'formik'
import _get from 'lodash/get'
import { AlertStripeInfo } from 'nav-frontend-alertstriper'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { FormikCheckbox } from '~/components/ui/form/inputs/checbox/Checkbox'
import { FieldArrayAddButton } from '~/components/ui/form/fieldArray/DollyFieldArray'
import { DollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'
import { Sivilstand } from '~/components/fagsystem/tpsf/form/familierelasjoner/partials/Sivilstand'

export const Partner = ({ props, valgbareIdenter }) => {
	if (valgbareIdenter.length < 1)
		return (
			<AlertStripeInfo>
				Det finnes ikke flere ledige personer i gruppa som personen kan bli partner med.
			</AlertStripeInfo>
		)

	return (
		<div className="bestilling-detaljer">
			<DollyFieldArray name="tpsf.relasjoner.partner" title="Partner" newEntry={initialPartner}>
				{(path, idx) => (
					<React.Fragment key={idx}>
						<FormikSelect name={`${path}.ident`} label="Fnr/dnr/bost" options={valgbareIdenter} />
						<FormikCheckbox name={`${path}.harFellesAdresse`} label="Bor sammen" />
						<Sivilstand tpsfPath={`${path}.sivilstander`} />
					</React.Fragment>
				)}
			</DollyFieldArray>
			{/* {props.values.relasjoner.partner.map((partner, idx) => {
				return (
					<div key={idx}>
						<h4>Partner {idx + 1}</h4>
						<div>
							<FormikSelect
								name={`relasjoner.partner[${idx}].ident`}
								label="Fnr/dnr/bost"
								options={valgbareIdenter}
							/>
							<FormikCheckbox
								name={`relasjoner.partner[${idx}].harFellesAdresse`}
								label="Bor sammen"
							/>
						</div>
					</div>
				)
			})}
			<FieldArrayAddButton
				title={'Partner'}
				onClick={() => {
					leggTilPartner(`relasjoner.partner`)
				}}
			/> */}
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
