import React from 'react'
// @ts-ignore
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { FormikTextInput } from '~/components/ui/form/inputs/textInput/TextInput'
// @ts-ignore
import { PersoninformasjonKodeverk } from '~/config/kodeverk'
import { FormikProps } from 'formik'
import { FormikDollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'
import { AvansertForm } from '~/components/fagsystem/pdlf/form/partials/avansert/AvansertForm'
import _get from 'lodash/get'

export interface InnvandringArray {
	person: {
		innvandretFraLand: Array<InnvandringValues>
	}
}

interface InnvandringValues {
	fraflyttingsland?: string
	fraflyttingsstedIUtlandet?: string
}

interface InnvandringProps {
	formikBag: FormikProps<{ pdldata: InnvandringArray }>
}

const initialInnvandring = {
	fraflyttingsland: '',
	fraflyttingsstedIUtlandet: '',
	master: 'PDL',
	kilde: 'Dolly',
}

export const Innvandring = ({ formikBag }: InnvandringProps) => {
	const innvandringListe = _get(formikBag.values, 'pdldata.person.innvandretFraLand')

	if (!innvandringListe) return null

	const handleNewEntry = () => {
		formikBag.setFieldValue('pdldata.person.telefonnummer', [
			...innvandringListe,
			initialInnvandring,
		])
	}

	return (
		<div className="flexbox--flex-wrap">
			<FormikDollyFieldArray
				name={'pdldata.person.telefonnummer'}
				header="Telefonnummer"
				newEntry={initialInnvandring}
				canBeEmpty={false}
				handleNewEntry={handleNewEntry}
			>
				{(path: string, idx: number) => (
					<>
						<FormikSelect
							name={`${path}.landskode`}
							label="Landkode"
							kodeverk={PersoninformasjonKodeverk.Retningsnumre}
							size="large"
							isClearable={false}
						/>
						<FormikTextInput
							name={`${path}.nummer`}
							label="Telefonnummer"
							/*@ts-ignore*/
							size="large"
						/>
						<AvansertForm path={path} kanVelgeMaster={false} />
					</>
				)}
			</FormikDollyFieldArray>
		</div>
	)
}
