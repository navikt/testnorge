import React from 'react'
// @ts-ignore
import { DollySelect, FormikSelect } from '~/components/ui/form/inputs/select/Select'
// @ts-ignore
import { PersoninformasjonKodeverk } from '~/config/kodeverk'
import { FormikProps } from 'formik'
import { FormikDollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'
import { AvansertForm } from '~/components/fagsystem/pdlf/form/partials/avansert/AvansertForm'
import _get from 'lodash/get'

export interface TilrettelagtKommunikasjonArray {
	person: {
		TilrettelagtKommunikasjon: Array<TilrettelagtKommunikasjonValues>
	}
}

interface TilrettelagtKommunikasjonValues {
	doedsdato?: string
}

interface TilrettelagtKommunikasjonProps {
	formikBag: FormikProps<{ pdldata: TilrettelagtKommunikasjonArray }>
}

const initialTilrettelagtKommunikasjon = {
	talespraaktolk: { spraak: null },
	tegnspraaktolk: { spraak: null },
	kilde: 'Dolly',
	master: 'PDL',
	gjeldende: true,
}

export const TilrettelagtKommunikasjon = ({ formikBag }: TilrettelagtKommunikasjonProps) => {
	const TilrettelagtKommunikasjonListe = _get(
		formikBag.values,
		'pdldata.person.tilrettelagtKommunikasjon'
	)

	const handleNewEntry = () => {
		formikBag.setFieldValue('pdldata.person.tilrettelagtKommunikasjon', [
			...TilrettelagtKommunikasjonListe,
			initialTilrettelagtKommunikasjon,
		])
	}

	const handleRemoveEntry = (idx: number) => {
		TilrettelagtKommunikasjonListe.splice(idx, 1)
		formikBag.setFieldValue(
			'pdldata.person.tilrettelagtKommunikasjon',
			TilrettelagtKommunikasjonListe
		)
	}

	return (
		<div className="flexbox--flex-wrap">
			<FormikDollyFieldArray
				name={'pdldata.person.tilrettelagtKommunikasjon'}
				header="Tilrettelagt kommunikasjon"
				newEntry={initialTilrettelagtKommunikasjon}
				canBeEmpty={false}
				handleNewEntry={handleNewEntry}
				handleRemoveEntry={handleRemoveEntry}
			>
				{(path: string, idx: number) => (
					<>
						<FormikSelect name={`${path}.talespraaktolk.spraak`} label="Talespraak tolk" />
						<AvansertForm path={path} kanVelgeMaster={false} />
					</>
				)}
			</FormikDollyFieldArray>
		</div>
	)
}
