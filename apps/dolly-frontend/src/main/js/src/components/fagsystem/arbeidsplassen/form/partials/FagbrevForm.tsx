import { FormikDollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import { initialFagbrev } from '@/components/fagsystem/arbeidsplassen/form/initialValues'
import * as React from 'react'
import { FormikSelect } from '@/components/ui/form/inputs/select/Select'
import { ArbeidKodeverk } from '@/config/kodeverk'

export const FagbrevForm = ({ formikBag }) => {
	return (
		<div style={{ width: '100%' }}>
			<hr />
			<FormikDollyFieldArray
				name="arbeidsplassenCV.fagbrev"
				header="Fagbrev"
				// hjelpetekst={infotekst}
				newEntry={initialFagbrev}
				nested
			>
				{(fagbrevPath, idx) => (
					// TODO: Må ha riktige data til lista. Sette title og type
					<FormikSelect
						name={`${fagbrevPath}.title`}
						label="Fagdokumentasjon"
						kodeverk={ArbeidKodeverk.Yrker}
						size="xxlarge"
						isClearable={false}
					/>
				)}
			</FormikDollyFieldArray>
		</div>
	)
}
