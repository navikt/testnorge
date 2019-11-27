import React from 'react'
import * as Yup from 'yup'
import { FieldArray } from 'formik'
import MiljoVelgerConnector from '~/components/miljoVelger/MiljoVelgerConnector'

export const MiljoeVelgerForm = ({ formikBag }) => {
	return (
		<FieldArray
			name="environments"
			render={arrayHelpers => (
				<div className="input-container">
					<MiljoVelgerConnector
						heading="Hvilke testmiljø vil du opprette testpersonene i?"
						arrayHelpers={arrayHelpers}
						arrayValues={formikBag.values.environments}
					/>
				</div>
			)}
		/>
	)
}

MiljoeVelgerForm.validation = {
	environments: Yup.array().of(Yup.string().required('Velg et navn'))
}
