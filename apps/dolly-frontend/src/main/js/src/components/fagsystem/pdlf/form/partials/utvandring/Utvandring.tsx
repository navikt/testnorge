import React from 'react'
// @ts-ignore
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { FormikDollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'
import { AvansertForm } from '~/components/fagsystem/pdlf/form/partials/avansert/AvansertForm'
import { FormikTextInput } from '~/components/ui/form/inputs/textInput/TextInput'
import { FormikDatepicker } from '~/components/ui/form/inputs/datepicker/Datepicker'
import { initialUtvandring } from '~/components/fagsystem/pdlf/form/initialValues'
import { AdresseKodeverk } from '~/config/kodeverk'

export const Utvandring = () => {
	return (
		<div className="flexbox--flex-wrap">
			<FormikDollyFieldArray
				name={'pdldata.person.utflytting'}
				header="Utvandring"
				newEntry={initialUtvandring}
				canBeEmpty={false}
			>
				{(path: string, idx: number) => (
					<>
						<FormikSelect
							name={`${path}.tilflyttingsland`}
							label="Utvandret til"
							kodeverk={AdresseKodeverk.InnvandretUtvandretLand}
							size="large"
							isClearable={false}
						/>
						<FormikTextInput name={`${path}.tilflyttingsstedIUtlandet`} label="Tilflyttingssted" />
						<FormikDatepicker name={`${path}.utflyttingsdato`} label="Utflyttingsdato" />
						<AvansertForm path={path} kanVelgeMaster={false} />
					</>
				)}
			</FormikDollyFieldArray>
		</div>
	)
}
