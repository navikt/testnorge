import { FormikDollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import { initialSpraak } from '@/components/fagsystem/arbeidsplassen/form/initialValues'
import { FormikSelect } from '@/components/ui/form/inputs/select/Select'
import { PersoninformasjonKodeverk } from '@/config/kodeverk'
import * as React from 'react'
import { SelectOptionsManager as Options } from '@/service/SelectOptions'

export const SpraakForm = ({ formikBag }) => {
	return (
		<FormikDollyFieldArray
			name="arbeidsplassenCV.spraak"
			header="Språk"
			// hjelpetekst={infotekst}
			newEntry={initialSpraak}
			nested
		>
			{(spraakPath, idx) => (
				<div key={idx} className="flexbox--flex-wrap">
					{/*// TODO: Må ha riktige data til lista. Sette language*/}
					<FormikSelect
						name={`${spraakPath}.language`}
						label="Språk"
						kodeverk={PersoninformasjonKodeverk.Spraak}
						size="large"
						isClearable={false}
					/>
					<FormikSelect
						name={`${spraakPath}.oralProficiency`}
						label="Muntlig"
						options={Options('spraakNivaa')}
						size="medium"
						isClearable={false}
					/>
					<FormikSelect
						name={`${spraakPath}.writtenProficiency`}
						label="Skriftlig"
						options={Options('spraakNivaa')}
						size="medium"
						isClearable={false}
					/>
				</div>
			)}
		</FormikDollyFieldArray>
	)
}
