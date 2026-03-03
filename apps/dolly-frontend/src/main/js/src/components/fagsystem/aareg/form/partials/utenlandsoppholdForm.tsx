import { AdresseKodeverk } from '@/config/kodeverk'
import { FormDatepicker } from '@/components/ui/form/inputs/datepicker/Datepicker'
import { FormDollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import { initialUtenlandsopphold } from '../initialValues'
import { LandVelger } from '@/components/landVelger/LandVelger'
import React from 'react'

const infotekst =
	'Start- og sluttdato må både være innenfor samme kalendermåned i samme år og perioden til arbeidsforholdet'

export const UtenlandsoppholdForm = ({ path, formMethods }) => {
	return (
		<FormDollyFieldArray
			name={path}
			header="Utenlandsopphold"
			hjelpetekst={infotekst}
			newEntry={initialUtenlandsopphold}
			nested
		>
			{(partialPath, idx) => (
				<div key={idx} className="flexbox">
					<LandVelger
						formMethods={formMethods}
						path={`${partialPath}.land`}
						checkboxName={`${partialPath}.ukjentLand`}
						ukjentLandKode="???"
						label="Land"
						kodeverk={AdresseKodeverk.ArbeidOgInntektLand}
						size="medium"
					/>
					<FormDatepicker name={`${partialPath}.periode.fom`} label="Opphold fra" />
					<FormDatepicker name={`${partialPath}.periode.tom`} label="Opphold til" />
				</div>
			)}
		</FormDollyFieldArray>
	)
}
