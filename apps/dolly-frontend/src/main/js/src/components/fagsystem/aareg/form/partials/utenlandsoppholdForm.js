import React from 'react'
import { AdresseKodeverk } from '~/config/kodeverk'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { FormikDatepicker } from '~/components/ui/form/inputs/datepicker/Datepicker'
import { FormikDollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'
import { initialUtenlandsopphold } from '../initialValues'

const infotekst =
	'Start- og sluttdato må både være innenfor samme kalendermåned i samme år og perioden til arbeidsforholdet'

export const UtenlandsoppholdForm = ({ path }) => {
	return (
		<FormikDollyFieldArray
			name={path}
			header="Utenlandsopphold"
			hjelpetekst={infotekst}
			newEntry={initialUtenlandsopphold}
			nested
		>
			{(partialPath, idx) => (
				<div key={idx} className="flexbox">
					<FormikSelect
						name={`${partialPath}.land`}
						label="Land"
						kodeverk={AdresseKodeverk.ArbeidOgInntektLand}
						isClearable={false}
						size="large"
					/>
					<FormikDatepicker name={`${partialPath}.periode.fom`} label="Opphold fra" />
					<FormikDatepicker name={`${partialPath}.periode.tom`} label="Opphold til" />
				</div>
			)}
		</FormikDollyFieldArray>
	)
}
