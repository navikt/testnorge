import React from 'react'
import * as Yup from 'yup'
import { ifPresent, requiredDate, requiredNumber, requiredString } from '~/utils/YupValidations'
import { Vis } from '~/components/bestillingsveileder/VisAttributt'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { SelectOptionsOppslag } from '~/service/SelectOptionsOppslag'
import Panel from '~/components/ui/panel/Panel'
import { erForste, panelError } from '~/components/ui/form/formUtils'
import { EnheterForm } from './partials/enheterForm'

const brregAttributt = 'brregstub'

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
					<FormikSelect
						name="brregstub.understatuser"
						label="Understatuser"
						options={understatuserOptions}
						isLoading={understatuser.loading}
						isMulti={true}
						size="grow"
						isClearable={false}
						fastfield={false}
					/>
					<EnheterForm formikBag={formikBag} />
				</div>
			</Panel>
		</Vis>
	)
}

BrregstubForm.validation = {
	brregstub: ifPresent(
		'$brregstub',
		Yup.object({
			understatuser: Yup.array().of(Yup.number()).required('Velg minst én understatus'),
			enheter: Yup.array().of(
				Yup.object({
					rolle: requiredString,
					registreringsdato: requiredDate,
					orgNr: requiredNumber,
					personroller: Yup.array().of(
						Yup.object({
							egenskap: requiredString,
							fratraadt: Yup.boolean(),
							registreringsdato: Yup.date(),
						})
					),
				})
			),
		})
	),
}
