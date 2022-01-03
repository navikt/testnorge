import React from 'react'
// @ts-ignore
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
// @ts-ignore
import { PersoninformasjonKodeverk } from '~/config/kodeverk'
import { FormikProps } from 'formik'
import { FormikDollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'
import { AvansertForm } from '~/components/fagsystem/pdlf/form/partials/avansert/AvansertForm'
import _get from 'lodash/get'
import { FormikDatepicker } from '~/components/ui/form/inputs/datepicker/Datepicker'

export interface DoedsfallArray {
	person: {
		doedsfall: Array<DoedsfallValues>
	}
}

interface DoedsfallValues {
	doedsdato?: string
}

interface DoedsfallProps {
	formikBag: FormikProps<{ pdldata: DoedsfallArray }>
}

const initialDoedsfall = {
	doedsdato: new Date(),
	kilde: 'Dolly',
	master: 'PDL',
	gjeldende: true,
}

export const Doedsfall = ({ formikBag }: DoedsfallProps) => {
	const doedsfallListe = _get(formikBag.values, 'pdldata.person.doedsfall')

	const handleNewEntry = () => {
		formikBag.setFieldValue('pdldata.person.doedsfall', [...doedsfallListe, initialDoedsfall])
	}

	const handleRemoveEntry = (idx: number) => {
		doedsfallListe.splice(idx, 1)
		formikBag.setFieldValue('pdldata.person.doedsfall', doedsfallListe)
	}

	return (
		<div className="flexbox--flex-wrap">
			<FormikDollyFieldArray
				name={'pdldata.person.doedsfall'}
				header="Dødsfall"
				newEntry={initialDoedsfall}
				canBeEmpty={false}
				handleNewEntry={handleNewEntry}
				handleRemoveEntry={handleRemoveEntry}
			>
				{(path: string, idx: number) => (
					<>
						<FormikDatepicker name={`${path}.doedsdato`} label="Dødsdato" />
						<AvansertForm path={path} kanVelgeMaster={false} />
					</>
				)}
			</FormikDollyFieldArray>
		</div>
	)
}
