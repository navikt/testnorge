import React, { useState } from 'react'
import * as Yup from 'yup'
import { FieldArray } from 'formik'
import { FormikDatepicker } from '~/components/ui/form/inputs/datepicker/Datepicker'
import { DollySelect } from '~/components/ui/form/inputs/select/Select'
import { FormikTextInput } from '~/components/ui/form/inputs/textInput/TextInput'
import { SelectOptionsManager as Options } from '~/service/SelectOptions'
import Panel from '~/components/ui/panel/Panel'
import { panelError } from '~/components/ui/form/formUtils'
import MiljoVelgerConnector from '~/components/miljoVelger/MiljoVelgerConnector'

export const MalForm = ({ formikBag }) => {
	const [nyMal, setNyMal] = useState(Boolean(formikBag.values.malBestillingNavn))
	//TODO: Sjekke om malnavn allerede finnes
	return (
		<div className="input-mal-field">
			<h3>Mal</h3>
			<div className="flexbox">
				<DollySelect
					name="nyMal"
					label="Lagrer som mal"
					value={nyMal}
					options={Options('boolean')}
					isClearable={false}
					onChange={e => setNyMal(e.value)}
				/>
				{nyMal && <FormikTextInput name="malBestillingNavn" label="Malnavn" />}
			</div>
		</div>
	)
}

MalForm.validation = { nyttMalnavn: Yup.string().required('Velg et navn') }
