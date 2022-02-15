import React from 'react'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { FormikTextInput } from '~/components/ui/form/inputs/textInput/TextInput'
import { FormikDatepicker } from '~/components/ui/form/inputs/datepicker/Datepicker'
import { FormikDollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'
import { initialPermisjon } from '../initialValues'
import { ArbeidKodeverk } from '~/config/kodeverk'

const infotekst = 'Start- og sluttdato må være innenfor perioden til arbeidsforholdet'

export const PermisjonForm = ({ path }) => {
	return (
		<FormikDollyFieldArray
			name={path}
			header="Permisjon"
			hjelpetekst={infotekst}
			newEntry={initialPermisjon}
			nested
		>
			{(partialPath, idx) => (
				<React.Fragment key={idx}>
					<FormikSelect
						name={`${partialPath}.permisjon`}
						label="Permisjonstype"
						kodeverk={ArbeidKodeverk.PermisjonsOgPermitteringsBeskrivelse}
						isClearable={false}
						size="large"
					/>
					<FormikDatepicker name={`${partialPath}.permisjonsPeriode.fom`} label="Permisjon fra" />
					<FormikDatepicker name={`${partialPath}.permisjonsPeriode.tom`} label="Permisjon til" />
					<FormikTextInput
						name={`${partialPath}.permisjonsprosent`}
						label="Permisjonsprosent"
						type="number"
					/>
				</React.Fragment>
			)}
		</FormikDollyFieldArray>
	)
}
