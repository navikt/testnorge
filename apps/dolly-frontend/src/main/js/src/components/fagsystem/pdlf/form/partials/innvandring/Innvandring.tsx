import React from 'react'
// @ts-ignore
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
// @ts-ignore
import { AdresseKodeverk, PersoninformasjonKodeverk } from '~/config/kodeverk'
import { FormikProps } from 'formik'
import { FormikDollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'
import { AvansertForm } from '~/components/fagsystem/pdlf/form/partials/avansert/AvansertForm'
import _get from 'lodash/get'
import { FormikTextInput } from '~/components/ui/form/inputs/textInput/TextInput'
import { FormikDatepicker } from '~/components/ui/form/inputs/datepicker/Datepicker'

export interface InnvandringArray {
	person: {
		innvandretFraLand: Array<InnvandringValues>
	}
}

interface InnvandringValues {
	fraflyttingsland?: string
	fraflyttingsstedIUtlandet?: string
	innflyttingsdato?: Date
}

interface InnvandringProps {
	formikBag: FormikProps<{ pdldata: InnvandringArray }>
}

const initialInnvandring = {
	fraflyttingsland: '',
	fraflyttingsstedIUtlandet: '',
	innflyttingsdato: new Date(),
	master: 'FREG',
	kilde: 'Dolly',
}

export const Innvandring = ({ formikBag }: InnvandringProps) => {
	const innvandringListe = _get(formikBag.values, 'pdldata.person.innflytting')

	const handleNewEntry = () => {
		formikBag.setFieldValue('pdldata.person.innflytting', [...innvandringListe, initialInnvandring])
	}

	return (
		<div className="flexbox--flex-wrap">
			<FormikDollyFieldArray
				name={'pdldata.person.innflytting'}
				header="Innvandring"
				newEntry={initialInnvandring}
				canBeEmpty={false}
				handleNewEntry={handleNewEntry}
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
