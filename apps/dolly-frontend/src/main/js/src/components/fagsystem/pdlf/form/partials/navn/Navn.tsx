import { FormikDollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import { initialNavn } from '@/components/fagsystem/pdlf/form/initialValues'
import { AvansertForm } from '@/components/fagsystem/pdlf/form/partials/avansert/AvansertForm'
import { FormikCheckbox } from '@/components/ui/form/inputs/checbox/Checkbox'
import { SelectOptionsOppslag } from '@/service/SelectOptionsOppslag'
import { FormikSelect } from '@/components/ui/form/inputs/select/Select'
import * as _ from 'lodash-es'
import { FormikProps } from 'formik'

type NavnTypes = {
	formikBag: FormikProps<{}>
	path?: string
}

export const NavnForm = ({ formikBag, path }: NavnTypes) => {
	if (!_.get(formikBag?.values, path)) {
		return null
	}

	const navnInfo = SelectOptionsOppslag.hentPersonnavn()
	const fornavnOptions = SelectOptionsOppslag.formatOptions('fornavn', navnInfo)
	const mellomnavnOptions = SelectOptionsOppslag.formatOptions('mellomnavn', navnInfo)
	const etternavnOptions = SelectOptionsOppslag.formatOptions('etternavn', navnInfo)

	const { fornavn, mellomnavn, etternavn } = _.get(formikBag?.values, path)

	return (
		<>
			<FormikSelect
				name={`${path}.fornavn`}
				placeholder={fornavn || 'Velg...'}
				label="Fornavn"
				options={fornavnOptions}
			/>
			<FormikSelect
				name={`${path}.mellomnavn`}
				placeholder={mellomnavn || 'Velg...'}
				label="Mellomnavn"
				options={mellomnavnOptions}
				isDisabled={_.get(formikBag?.values, `${path}.hasMellomnavn`)}
			/>
			<FormikSelect
				name={`${path}.etternavn`}
				placeholder={etternavn || 'Velg...'}
				label="Etternavn"
				options={etternavnOptions}
			/>
			<FormikCheckbox
				name={`${path}.hasMellomnavn`}
				label="Har tilfeldig mellomnavn"
				checkboxMargin
				isDisabled={mellomnavn !== null}
			/>
			<AvansertForm path={path} kanVelgeMaster={true} />
		</>
	)
}

export const Navn = ({ formikBag }: NavnTypes) => {
	return (
		<div className="flexbox--flex-wrap">
			<FormikDollyFieldArray
				name={'pdldata.person.navn'}
				header="Navn"
				newEntry={initialNavn}
				canBeEmpty={false}
			>
				{(path: string) => <NavnForm formikBag={formikBag} path={path} />}
			</FormikDollyFieldArray>
		</div>
	)
}
