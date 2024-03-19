import { FormTextInput } from '@/components/ui/form/inputs/textInput/TextInput'
import { FormDatepicker } from '@/components/ui/form/inputs/datepicker/Datepicker'
import { FormDollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import { initialPermittering } from '../initialValues'
import React from 'react'

type Permittering = {
	path: string
}

const infotekst = 'Start- og sluttdato må være innenfor perioden til arbeidsforholdet'

export const PermitteringForm = ({ path }: Permittering) => {
	return (
		<FormDollyFieldArray
			name={path}
			header="Permittering"
			hjelpetekst={infotekst}
			newEntry={initialPermittering}
			nested
		>
			{(partialPath: string, idx: number) => (
				<React.Fragment key={idx}>
					<FormDatepicker
						name={`${partialPath}.permitteringsPeriode.fom`}
						label="Permittering fra"
					/>
					<FormDatepicker
						name={`${partialPath}.permitteringsPeriode.tom`}
						label="Permittering til"
					/>
					<FormTextInput
						name={`${partialPath}.permitteringsprosent`}
						label="Permitteringsprosent"
						type="number"
					/>
				</React.Fragment>
			)}
		</FormDollyFieldArray>
	)
}
