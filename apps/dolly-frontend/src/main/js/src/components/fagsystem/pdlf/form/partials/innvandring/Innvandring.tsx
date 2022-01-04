import React from 'react'
// @ts-ignore
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { FormikDollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'
import { AvansertForm } from '~/components/fagsystem/pdlf/form/partials/avansert/AvansertForm'
import { FormikTextInput } from '~/components/ui/form/inputs/textInput/TextInput'
import { FormikDatepicker } from '~/components/ui/form/inputs/datepicker/Datepicker'
import { initialInnvandring } from '~/components/fagsystem/pdlf/form/initialValues'
import { AdresseKodeverk } from '~/config/kodeverk'

export const Innvandring = () => {
	return (
		<div className="flexbox--flex-wrap">
			<FormikDollyFieldArray
				name={'pdldata.person.innflytting'}
				header="Innvandring"
				newEntry={initialInnvandring}
				canBeEmpty={false}
			>
				{(path: string, idx: number) => (
					<>
						<FormikSelect
							name={`${path}.fraflyttingsland`}
							label="Innvandret fra"
							kodeverk={AdresseKodeverk.InnvandretUtvandretLand}
							size="large"
							isClearable={false}
						/>
						<FormikTextInput name={`${path}.fraflyttingsstedIUtlandet`} label="Fraflyttingssted" />
						<FormikDatepicker name={`${path}.innflyttingsdato`} label="Innflyttingsdato" />
						<AvansertForm path={path} kanVelgeMaster={false} />
					</>
				)}
			</FormikDollyFieldArray>
		</div>
	)
}
