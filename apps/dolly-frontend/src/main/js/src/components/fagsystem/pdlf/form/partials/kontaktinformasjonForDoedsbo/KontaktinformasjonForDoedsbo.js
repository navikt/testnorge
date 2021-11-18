import React from 'react'
import { SelectOptionsManager as Options } from '~/service/SelectOptions'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { FormikDatepicker } from '~/components/ui/form/inputs/datepicker/Datepicker'
import { Kontakt } from './Kontakt'
import { Adresse } from './Adresse'
import { FormikDollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'
import { AvansertForm } from '~/components/fagsystem/pdlf/form/partials/avansert/AvansertForm'
import { initialKontaktinfoForDoedebo } from '~/components/fagsystem/pdlf/form/initialValues'

export const KontaktinformasjonForDoedsbo = ({ formikBag }) => {
	return (
		<FormikDollyFieldArray
			name="pdldata.person.kontaktinformasjonForDoedsbo"
			header="Kontaktinformasjon for dødsbo"
			newEntry={initialKontaktinfoForDoedebo}
			canBeEmpty={false}
		>
			{(path, idx) => {
				return (
					<>
						<FormikSelect
							name={`${path}.skifteform`}
							label="Skifteform"
							options={Options('skifteform')}
							isClearable={false}
						/>
						<FormikDatepicker
							name={`${path}.attestutstedelsesdato`}
							label="Utstedelsesdato skifteattest"
						/>
						<Kontakt formikBag={formikBag} path={path} />
						<Adresse formikBag={formikBag} path={`${path}.adresse`} />
						<AvansertForm path={path} kanVelgeMaster={false} />
					</>
				)
			}}
		</FormikDollyFieldArray>
	)
}
