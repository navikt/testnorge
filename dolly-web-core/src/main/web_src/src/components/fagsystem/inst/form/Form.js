import React from 'react'
import * as Yup from 'yup'
import { Vis } from '~/components/bestillingsveileder/VisAttributt'
import { panelError } from '~/components/ui/form/formUtils'
import { erForste } from '~/components/ui/form/formUtils'
import Panel from '~/components/ui/panel/Panel'
import { FormikDatepicker } from '~/components/ui/form/inputs/datepicker/Datepicker'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { FormikDollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'
import { SelectOptionsManager as Options } from '~/service/SelectOptions'
import { requiredDate, requiredString } from '~/utils/YupValidations'

const initialValues = {
	institusjonstype: '',
	startdato: '',
	faktiskSluttdato: ''
}
const instAttributt = 'instdata'

export const InstForm = ({ formikBag }) => (
	<Vis attributt={instAttributt}>
		<Panel
			heading="Institusjonsopphold"
			hasErrors={panelError(formikBag, instAttributt)}
			iconType="institusjon"
			startOpen={() => erForste(formikBag.values, [instAttributt])}
		>
			<FormikDollyFieldArray name="instdata" header="Opphold" newEntry={initialValues}>
				{(path, idx) => (
					<React.Fragment key={idx}>
						<FormikSelect
							name={`${path}.institusjonstype`}
							label="Institusjonstype"
							options={Options('institusjonstype')}
							isClearable={false}
						/>
						<FormikDatepicker name={`${path}.startdato`} label="Startdato" />
						<FormikDatepicker name={`${path}.faktiskSluttdato`} label="Sluttdato" />
					</React.Fragment>
				)}
			</FormikDollyFieldArray>
		</Panel>
	</Vis>
)

InstForm.validation = {
	instdata: Yup.array().of(
		Yup.object({
			institusjonstype: requiredString,
			startdato: requiredDate
		})
	)
}
