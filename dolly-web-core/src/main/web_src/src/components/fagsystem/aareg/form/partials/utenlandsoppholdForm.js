import React from 'react'
import _get from 'lodash/get'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { FormikDatepicker } from '~/components/ui/form/inputs/datepicker/Datepicker'
import { DollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'
import { initialUtenlandsopphold } from '../initialValues'

const infotekst =
	'Start- og sluttdato må både være innenfor samme kalendermåned i samme år og perioden til arbeidsforholdet'

export const UtenlandsoppholdForm = ({ path }) => (
	<DollyFieldArray
		name={path}
		title="Utenlandsopphold"
		hjelpetekst={infotekst}
		newEntry={initialUtenlandsopphold}
	>
		{(path, idx) => (
			<div key={idx} className="flexbox">
				<FormikSelect
					name={`${path}.land`}
					label="Land"
					kodeverk="LandkoderISO2"
					isClearable={false}
				/>
				<FormikDatepicker name={`${path}.periode.fom`} label="Opphold fra" />
				<FormikDatepicker name={`${path}.periode.tom`} label="Opphold til" />
			</div>
		)}
	</DollyFieldArray>
)
