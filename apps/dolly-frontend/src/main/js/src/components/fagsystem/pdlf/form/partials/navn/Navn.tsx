import React from 'react'
import { FormikDollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'
import { initialNavn } from '~/components/fagsystem/pdlf/form/initialValues'
import { AvansertForm } from '~/components/fagsystem/pdlf/form/partials/avansert/AvansertForm'
import { FormikCheckbox } from '~/components/ui/form/inputs/checbox/Checkbox'
import { SelectOptionsOppslag } from '~/service/SelectOptionsOppslag'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import _get from 'lodash/get'

export const NavnForm = ({ formikBag, path }) => {
	if (!_get(formikBag?.values, path)) return null
	console.log('formikBag: ', formikBag) //TODO - SLETT MEG
	const navnInfo = SelectOptionsOppslag.hentPersonnavn()
	const fornavnOptions = SelectOptionsOppslag.formatOptions('fornavn', navnInfo)
	const mellomnavnOptions = SelectOptionsOppslag.formatOptions('mellomnavn', navnInfo)
	const etternavnOptions = SelectOptionsOppslag.formatOptions('etternavn', navnInfo)

	const { fornavn, mellomnavn, etternavn } = _get(formikBag?.values, path)

	return (
		<>
			<FormikSelect
				name={`${path}.fornavn`}
				placeholder={fornavn ? fornavn : 'Velg...'}
				label="Fornavn"
				options={fornavnOptions}
			/>
			<FormikSelect
				name={`${path}.mellomnavn`}
				placeholder={mellomnavn ? mellomnavn : 'Velg...'}
				label="Mellomnavn"
				options={mellomnavnOptions}
			/>
			<FormikSelect
				name={`${path}.etternavn`}
				placeholder={etternavn ? etternavn : 'Velg...'}
				label="Etternavn"
				options={etternavnOptions}
			/>
			<FormikCheckbox
				name={`${path}.hasMellomnavn`}
				label="Har tilfeldig mellomnavn"
				checkboxMargin
			/>
			<AvansertForm path={path} kanVelgeMaster={true} />
		</>
	)
}

export const Navn = ({ formikBag }) => {
	return (
		<div className="flexbox--flex-wrap">
			<FormikDollyFieldArray
				name={'pdldata.person.navn'}
				header="Navn"
				newEntry={initialNavn}
				canBeEmpty={false}
			>
				{(path: string, idx: number) => <NavnForm formikBag={formikBag} path={path} />}
			</FormikDollyFieldArray>
		</div>
	)
}
