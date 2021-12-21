import React from 'react'
// @ts-ignore
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
// @ts-ignore
import { AdresseKodeverk, PersoninformasjonKodeverk } from '~/config/kodeverk'
import { FormikProps } from 'formik'
import { FormikDollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'
import { AvansertForm } from '~/components/fagsystem/pdlf/form/partials/avansert/AvansertForm'
import _get from 'lodash/get'
import { FormikDatepicker } from '~/components/ui/form/inputs/datepicker/Datepicker'

export interface StatsborgerskapArray {
	person: {
		Statsborgerskap: Array<StatsborgerskapValues>
	}
}

interface StatsborgerskapValues {
	landkode?: string
	gyldigFraOgMed?: Date
	gyldigTilOgMed?: Date
	bekreftelsesdato?: Date
}

interface StatsborgerskapProps {
	formikBag: FormikProps<{ pdldata: StatsborgerskapArray }>
}

const initialStatsborgerskap = {
	landkode: null,
	gyldigFraOgMed: new Date(),
	gyldigTilOgMed: null,
	bekreftelsesdato: null,
	kilde: 'Dolly',
	master: 'PDL',
	gjeldende: true,
}

export const Statsborgerskap = ({ formikBag }: StatsborgerskapProps) => {
	const StatsborgerskapListe = _get(formikBag.values, 'pdldata.person.statsborgerskap')

	const handleNewEntry = () => {
		formikBag.setFieldValue('pdldata.person.statsborgerskap', [
			...StatsborgerskapListe,
			initialStatsborgerskap,
		])
	}

	const handleRemoveEntry = (idx: number) => {
		StatsborgerskapListe.splice(idx, 1)
		formikBag.setFieldValue('pdldata.person.statsborgerskap', StatsborgerskapListe)
	}

	return (
		<div className="flexbox--flex-wrap">
			<FormikDollyFieldArray
				name={'pdldata.person.statsborgerskap'}
				header="Statsborgerskap"
				newEntry={initialStatsborgerskap}
				canBeEmpty={false}
				handleNewEntry={handleNewEntry}
				handleRemoveEntry={handleRemoveEntry}
			>
				{(path: string, idx: number) => (
					<>
						<FormikSelect
							name={`${path}.landkode`}
							label="Statsborgerskap"
							kodeverk={AdresseKodeverk.StatsborgerskapLand}
							size="large"
							isClearable={false}
						/>
						<FormikDatepicker name={`${path}.gyldigFraOgMed`} label="Statsborgerskap fra" />
						<FormikDatepicker name={`${path}.gyldigTilOgMed`} label="Statsborgerskap til" />
						<FormikDatepicker name={`${path}.bekreftelsesdato`} label="Bekreftelsesdato" />
						<AvansertForm path={path} kanVelgeMaster={false} />
					</>
				)}
			</FormikDollyFieldArray>
		</div>
	)
}
