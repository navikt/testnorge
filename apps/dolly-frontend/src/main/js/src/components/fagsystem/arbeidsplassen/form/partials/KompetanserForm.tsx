import { FormikDollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import { initialKompetanser } from '@/components/fagsystem/arbeidsplassen/form/initialValues'
import * as React from 'react'
import { FormikSelect } from '@/components/ui/form/inputs/select/Select'
import { SelectOptionsManager as Options } from '@/service/SelectOptions'

export const KompetanserForm = ({ formikBag }) => {
	return (
		<div style={{ width: '100%' }}>
			<hr />
			<FormikDollyFieldArray
				name="arbeidsplassenCV.kompetanser"
				header="Kompetanser"
				// hjelpetekst={infotekst}
				newEntry={initialKompetanser}
				buttonText="Kompetanse"
				nested
			>
				{(kompetansePath, idx) => (
					<FormikSelect
						name={`${kompetansePath}.title`}
						label="Kompetanse/ferdighet/verktøy"
						options={Options('kompetanse')}
						size="xxlarge"
						isClearable={false}
					/>
				)}
			</FormikDollyFieldArray>
		</div>
	)
}
