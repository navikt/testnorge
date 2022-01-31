import React from 'react'
import { FormikDollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'
import { initialNavn } from '~/components/fagsystem/pdlf/form/initialValues'
import { AvansertForm } from '~/components/fagsystem/pdlf/form/partials/avansert/AvansertForm'
import { FormikCheckbox } from '~/components/ui/form/inputs/checbox/Checkbox'
import { SelectOptionsOppslag } from '~/service/SelectOptionsOppslag'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'

export const Navn = () => {
	const navnInfo = SelectOptionsOppslag.hentPersonnavn()
	const fornavnOptions = SelectOptionsOppslag.formatOptions('fornavn', navnInfo)
	const mellomnavnOptions = SelectOptionsOppslag.formatOptions('mellomnavn', navnInfo)
	const etternavnOptions = SelectOptionsOppslag.formatOptions('etternavn', navnInfo)

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
						<FormikSelect name={`${path}.fornavn`} label="Fornavn" options={fornavnOptions} />
						<FormikSelect
							name={`${path}.mellomnavn`}
							label="Mellomnavn"
							options={mellomnavnOptions}
						/>
						<FormikSelect name={`${path}.etternavn`} label="Etternavn" options={etternavnOptions} />
						<FormikCheckbox
							name={`${path}.hasMellomnavn`}
							label="Har tilfeldig mellomnavn"
							checkboxMargin
						/>
						<AvansertForm path={path} kanVelgeMaster={true} />
					</>
				)}
			</FormikDollyFieldArray>
		</div>
	)
}
