import React from 'react'
import _get from 'lodash/get'
import { FormikTextInput } from '~/components/ui/form/inputs/textInput/TextInput'
import { FormikDatepicker } from '~/components/ui/form/inputs/datepicker/Datepicker'
import { FieldArrayRemoveButton } from '~/components/ui/form/formUtils'

export const TimeloennetForm = ({ name, formikBag, fjern }) => {
	const timeloennArray = _get(formikBag.values, name, [])
	const harTimeloenn = timeloennArray.length > 0

	return (
		<div>
			{harTimeloenn && <h4>Antall timer for timelønnet</h4>}
			{timeloennArray.map((periode, idx) => (
				<div key={idx} className="flexbox">
					<h5 className="nummer">{`#${idx + 1}`}</h5>
					{/* Skal være number +- ting */}
					<FormikTextInput name={`${name}[${idx}].antallTimer`} label="Antall timer" />
					<FormikDatepicker name={`${name}[${idx}].periode.fom`} label="Periode fra" />
					<FormikDatepicker name={`${name}[${idx}].periode.tom`} label="Periode til" />
					<FieldArrayRemoveButton onClick={() => fjern(idx, name, timeloennArray)} />
				</div>
			))}
		</div>
	)
}
