import React from 'react'
import * as Yup from 'yup'
import { FieldArray } from 'formik'
import { FormikDatepicker } from '~/components/ui/form/inputs/datepicker/Datepicker'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { FormikTextInput } from '~/components/ui/form/inputs/textInput/TextInput'
import { SelectOptionsManager as Options } from '~/service/SelectOptions'
import Panel from '~/components/ui/panel/Panel'
import { panelError } from '~/components/ui/form/formUtils'
import MiljoVelgerConnector from '~/components/miljoVelger/MiljoVelgerConnector'

export const MiljoeVelgerForm = ({ formikBag }) => {
	//TODO: Test om miljø fortsatt er huket av om vi går tilbake til steg 2 og frem igjen
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

MiljoeVelgerForm.validation = { environments: Yup.array().required('Velg minst ett miljø') }
