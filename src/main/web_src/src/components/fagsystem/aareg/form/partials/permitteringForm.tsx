import React from 'react'
import _get from 'lodash/get'
import { FormikTextInput } from '~/components/ui/form/inputs/textInput/TextInput'
import { FormikDatepicker } from '~/components/ui/form/inputs/datepicker/Datepicker'
import { FormikDollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'
import { initialPermittering } from '../initialValues'

type Permittering = {
	path: string
}

const infotekst = 'Start- og sluttdato må være innenfor perioden til arbeidsforholdet'

export const PermitteringForm = ({ path }: Permittering) => (
	<FormikDollyFieldArray
		name={path}
		header="Permittering"
		infotekst={infotekst}
		newEntry={initialPermittering}
		nested
	>
		{(path, idx) => (
			<React.Fragment key={idx}>
				<FormikDatepicker name={`${path}.permitteringsPeriode.fom`} label="Permittering fra" />
				<FormikDatepicker name={`${path}.permitteringsPeriode.tom`} label="Permittering til" />
				<FormikTextInput
					name={`${path}.permitteringsprosent`}
					label="Permitteringsprosent"
					type="number"
				/>
			</React.Fragment>
		)}
	</FormikDollyFieldArray>
)
