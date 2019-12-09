import React from 'react'
import _get from 'lodash/get'
import { FormikTextInput } from '~/components/ui/form/inputs/textInput/TextInput'
import { FormikDatepicker } from '~/components/ui/form/inputs/datepicker/Datepicker'
import { FieldArrayRemoveButton } from '~/components/ui/form/formUtils'
import HjelpeTekst from 'nav-frontend-hjelpetekst'

export const TimeloennetForm = ({ name, formikBag, fjern }) => {
	const timeloennArray = _get(formikBag.values, name, [])
	const harTimeloenn = timeloennArray.length > 0

	return (
		<div>
			{harTimeloenn && (
				<div className="flexbox--align-center">
					<h4>Antall timer for timelønnet</h4>
					<HjelpeTekst>
						Start- og sluttdato må både være innenfor samme kalendermåned i samme år og perioden til
						arbeidsforholdet
					</HjelpeTekst>
				</div>
			)}
			{timeloennArray.map((periode, idx) => (
				<div key={idx} className="flexbox">
					<h5 className="nummer">{`#${idx + 1}`}</h5>
					<FormikTextInput
						name={`${name}[${idx}].antallTimer`}
						label="Antall timer"
						type="number"
					/>
					<FormikDatepicker name={`${name}[${idx}].periode.fom`} label="Periode fra" />
					<FormikDatepicker name={`${name}[${idx}].periode.tom`} label="Periode til" />
					<FieldArrayRemoveButton onClick={() => fjern(idx, name, timeloennArray)} />
				</div>
			))}
		</div>
	)
}
