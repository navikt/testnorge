import { FormSelect } from '@/components/ui/form/inputs/select/Select'
import { FormTextInput } from '@/components/ui/form/inputs/textInput/TextInput'
import { FormDatepicker } from '@/components/ui/form/inputs/datepicker/Datepicker'
import { FormDollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import { initialPermisjon } from '../initialValues'
import { ArbeidKodeverk } from '@/config/kodeverk'
import React from 'react'

const infotekst = 'Start- og sluttdato må være innenfor perioden til arbeidsforholdet'

export const PermisjonForm = ({ path }) => {
	return (
		<FormDollyFieldArray
			name={path}
			header="Permisjon"
			hjelpetekst={infotekst}
			newEntry={initialPermisjon}
			nested
		>
			{(partialPath, idx) => (
				<React.Fragment key={idx}>
					<FormSelect
						name={`${partialPath}.permisjon`}
						label="Permisjonstype"
						kodeverk={ArbeidKodeverk.PermisjonsOgPermitteringsBeskrivelse}
						isClearable={false}
						size="large"
					/>
					<FormDatepicker name={`${partialPath}.permisjonsPeriode.fom`} label="Permisjon fra" />
					<FormDatepicker name={`${partialPath}.permisjonsPeriode.tom`} label="Permisjon til" />
					<FormTextInput
						name={`${partialPath}.permisjonsprosent`}
						label="Permisjonsprosent"
						type="number"
					/>
				</React.Fragment>
			)}
		</FormDollyFieldArray>
	)
}
