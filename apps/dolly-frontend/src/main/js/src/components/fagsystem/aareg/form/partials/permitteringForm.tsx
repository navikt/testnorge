import React from 'react'
import { FormikTextInput } from '~/components/ui/form/inputs/textInput/TextInput'
import { FormikDatepicker } from '~/components/ui/form/inputs/datepicker/Datepicker'
import { FormikDollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'
import { initialPermittering } from '../initialValues'

type Permittering = {
	path: string
}

const infotekst = 'Start- og sluttdato må være innenfor perioden til arbeidsforholdet'

export const PermitteringForm = ({ path }: Permittering) => {
	return (
		<FormikDollyFieldArray
			name={path}
			header="Permittering"
			hjelpetekst={infotekst}
			newEntry={initialPermittering}
			nested
		>
			{(partialPath: string, idx: number) => (
				<React.Fragment key={idx}>
					<FormikDatepicker
						name={`${partialPath}.permitteringsPeriode.fom`}
						label="Permittering fra"
					/>
					<FormikDatepicker
						name={`${partialPath}.permitteringsPeriode.tom`}
						label="Permittering til"
					/>
					<FormikTextInput
						name={`${partialPath}.permitteringsprosent`}
						label="Permitteringsprosent"
						type="number"
					/>
				</React.Fragment>
			)}
		</FormikDollyFieldArray>
	)
}
