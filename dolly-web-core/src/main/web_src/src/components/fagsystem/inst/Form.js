import React from 'react'
import { FieldArray } from 'formik'
import * as Yup from 'yup'
import { Vis, pathAttrs } from '~/components/bestillingsveileder/VisAttributt'
import { panelError } from '~/components/ui/form/formUtils'
import Panel from '~/components/ui/panel/Panel'
import { FormikDatepicker } from '~/components/ui/form/inputs/datepicker/Datepicker'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { FieldArrayAddButton, FieldArrayRemoveButton } from '~/components/ui/form/formUtils'
import { SelectOptionsManager as Options } from '~/service/SelectOptions'

export const InstForm = ({ formikBag }) => (
	<Vis attributt={pathAttrs.kategori.instdata}>
		<Panel heading="Institusjonsopphold" hasErrors={panelError(formikBag)}>
			<FieldArray
				name="instdata"
				render={arrayHelpers => {
					const initialValues = formikBag.initialValues.instdata[0]
					return (
						<div>
							{formikBag.values.instdata.map((curr, idx) => (
								<div className="flexbox" key={idx}>
									<FormikSelect
										name={`instdata[${idx}].institusjonstype`}
										label="Institusjonstype"
										options={Options('institusjonstype')}
									/>
									<FormikDatepicker name={`instdata[${idx}].startdato`} label="Startdato" />
									<FormikDatepicker name={`instdata[${idx}].faktiskSluttdato`} label="Sluttdato" />
									<FieldArrayRemoveButton onClick={e => arrayHelpers.remove(idx)} />
								</div>
							))}

							<FieldArrayAddButton
								title="Institusjonsopphold"
								onClick={e => arrayHelpers.push(initialValues)}
							/>
						</div>
					)
				}}
			/>
		</Panel>
	</Vis>
)

InstForm.initialValues = attrs => {
	const initial = {
		instdata: [
			{
				institusjonstype: '',
				startdato: '',
				faktiskSluttdato: ''
			}
		]
	}
	return attrs.instdata ? initial : {}
}

InstForm.validation = {
	instdata: Yup.array().of(
		Yup.object({
			institusjonstype: Yup.string().required('Velg institusjonstype'),
			startdato: Yup.string()
				.typeError('Formatet må være DD.MM.YYYY.')
				.required(),
			faktiskSluttdato: Yup.string().typeError('Formatet må være DD.MM.YYYY.')
		})
	)
}
