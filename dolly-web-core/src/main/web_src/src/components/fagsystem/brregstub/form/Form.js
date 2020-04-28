import React from 'react'
import _get from 'lodash/get'
import * as Yup from 'yup'
import { requiredDate, requiredString, requiredNumber, messages } from '~/utils/YupValidations'
import { Vis } from '~/components/bestillingsveileder/VisAttributt'
import { DollySelect } from '~/components/ui/form/inputs/select/Select'
import { SelectOptionsOppslag } from '~/service/SelectOptionsOppslag'
import Panel from '~/components/ui/panel/Panel'
import { panelError } from '~/components/ui/form/formUtils'
import { erForste } from '~/components/ui/form/formUtils'
import { EnheterForm } from './partials/enheterForm'

const brregAttributt = 'bregstub'

export const BrregstubForm = ({ formikBag }) => {
	const understatuser = SelectOptionsOppslag.hentUnderstatusFraBrregstub()
	const understatuserOptions = SelectOptionsOppslag.formatOptions('understatuser', understatuser)

	return (
		<Vis attributt={brregAttributt}>
			<Panel
				heading="Brønnøysundregistrene"
				hasErrors={panelError(formikBag, brregAttributt)}
				iconType="brreg"
				startOpen={() => erForste(formikBag.values, [brregAttributt])}
			>
				<div className="flexbox--flex-wrap">
					<DollySelect
						name="bregstub.understatuser"
						label="Understatus"
						options={understatuserOptions}
						isLoading={understatuser.loading}
						onChange={understatus =>
							formikBag.setFieldValue('bregstub.understatuser', understatus.value)
						}
						value={_get(formikBag.values, 'bregstub.understatuser')}
						size="xxlarge"
						isClearable={false}
					/>
					<EnheterForm formikBag={formikBag} />
				</div>
			</Panel>
		</Vis>
	)
}

BrregstubForm.validation = {
	bregstub: Yup.object({
		understatuser: requiredNumber,
		enheter: Yup.array().of(
			Yup.object({
				rollebeskrivelse: requiredString.typeError(messages.required),
				registreringsdato: requiredDate,
				foretaksNavn: Yup.object({
					navn1: requiredString
				}),
				orgNr: requiredNumber
					.transform((i, j) => (j === '' ? null : i))
					.test(
						'len',
						'Orgnummer må være et tall med 9 sifre',
						val => val && val.toString().length === 9
					)
					.nullable()
			})
		)
	})
}
