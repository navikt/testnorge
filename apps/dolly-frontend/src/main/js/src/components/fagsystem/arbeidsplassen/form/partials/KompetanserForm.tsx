import { FormikDollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import { initialKompetanser } from '@/components/fagsystem/arbeidsplassen/form/initialValues'
import * as React from 'react'
import { FormikSelect } from '@/components/ui/form/inputs/select/Select'
import { ArbeidKodeverk } from '@/config/kodeverk'

export const KompetanserForm = ({ formikBag }) => {
	return (
		<FormikDollyFieldArray
			name="arbeidsplassenCV.kompetanser"
			header="Kompetanser"
			// hjelpetekst={infotekst}
			newEntry={initialKompetanser}
			buttonText="Kompetanse"
			nested
		>
			{(kompetansePath, idx) => (
				// TODO: Må ha riktige data til lista. Sette title
				<FormikSelect
					name={`${kompetansePath}.title`}
					label="Kompetanse/ferdighet/verktøy"
					kodeverk={ArbeidKodeverk.Yrker}
					size="xxlarge"
					isClearable={false}
				/>
			)}
		</FormikDollyFieldArray>
	)
}
