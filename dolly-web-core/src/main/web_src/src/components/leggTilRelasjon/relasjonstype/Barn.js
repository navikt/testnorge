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

export const Barn = ({ props, valgbareIdenter }) => {
	return (
		<div className="bestilling-detaljer">
			<DollyFieldArray name="tpsf.relasjoner.barn" title="Barn" newEntry={initialBarn}>
				{(path, idx) => (
					<React.Fragment key={idx}>
						<FormikSelect name={`${path}.ident`} label="Fnr/dnr/bost" options={valgbareIdenter} />
						<FormikSelect
							name={`${path}.borHos`}
							label="Bor hos"
							options={Options('barnBorHos')}
							isClearable={false}
						/>
						{_get(props.values, `${path}.borHos`) !== 'MEG' && muligeForeldre(props).length > 0 && (
							<FormikSelect
								name={`${path}.partnerIdent`}
								label="Forelder 2"
								options={muligeForeldre(props)}
								isClearable={false}
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
