import React from 'react'
import _get from 'lodash/get'
import { FormikTextInput } from '~/components/ui/form/inputs/textInput/TextInput'
import { FormikDatepicker } from '~/components/ui/form/inputs/datepicker/Datepicker'
import { FormikDollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'
import { initialTimeloennet } from '../initialValues'

const infotekst =
	'Start- og sluttdato må både være innenfor samme kalendermåned i samme år og perioden til arbeidsforholdet'

export const TimeloennetForm = ({ path }) => (
	<FormikDollyFieldArray
		name={path}
		header="Timer med timelønnet"
		hjelpetekst={infotekst}
		newEntry={initialTimeloennet}
		nested
	>
		{(path, idx) => (
			<div key={idx} className="flexbox">
				<FormikTextInput
					name={`${path}.antallTimer`}
					label="Antall timer for timelønnet"
					type="number"
				/>
				<FormikDatepicker name={`${path}.periode.fom`} label="Periode fra" />
				<FormikDatepicker name={`${path}.periode.tom`} label="Periode til" />
			</div>
		)}
	</FormikDollyFieldArray>
)
