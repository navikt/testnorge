import React from 'react'
import { FormikDollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'
import { initialNavn } from '~/components/fagsystem/pdlf/form/initialValues'
import { AvansertForm } from '~/components/fagsystem/pdlf/form/partials/avansert/AvansertForm'
import { FormikCheckbox } from '~/components/ui/form/inputs/checbox/Checkbox'

export const Navn = () => {
	return (
		<div className="flexbox--flex-wrap">
			<FormikDollyFieldArray
				name={'pdldata.person.navn'}
				header="Navn"
				newEntry={initialNavn}
				canBeEmpty={false}
			>
				{(path: string, idx: number) => (
					<>
						<FormikCheckbox name={`${path}.hasMellomnavn`} label="Har mellomnavn" />
						<AvansertForm path={path} kanVelgeMaster={true} />
					</>
				)}
			</FormikDollyFieldArray>
		</div>
	)
}
