import React from 'react'
import { FormikTextInput } from '~/components/ui/form/inputs/textInput/TextInput'
import { FormikDatepicker } from '~/components/ui/form/inputs/datepicker/Datepicker'
import { FormikDollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'
import { initialTimeloennet } from '../initialValues'

const infotekst =
	'Start- og sluttdato må både være innenfor samme kalendermåned i samme år og perioden til arbeidsforholdet'

export const TimeloennetForm = ({ path }) => {
	return (
		<FormikDollyFieldArray
			name={path}
			header="Timer med timelønnet"
			hjelpetekst={infotekst}
			newEntry={initialTimeloennet}
			nested
		>
			{(partialPath, idx) => (
				<div key={idx} className="flexbox">
					<FormikTextInput
						name={`${partialPath}.antallTimer`}
						label="Antall timer for timelønnet"
						type="number"
					/>
					<FormikDatepicker name={`${partialPath}.periode.fom`} label="Periode fra" />
					<FormikDatepicker name={`${partialPath}.periode.tom`} label="Periode til" />
				</div>
			)}
		</FormikDollyFieldArray>
	)
}
