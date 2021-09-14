import * as React from 'react'
import { FormikDatepicker } from '~/components/ui/form/inputs/datepicker/Datepicker'
import { FormikDollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'

interface PleiepengerForm {
	path: string
}

const initialPleiepengerperiode = {
	fom: '',
	tom: '',
}

export default ({ path }: PleiepengerForm) => (
	<FormikDollyFieldArray
		name={path}
		header="Pleiepengerperioder"
		newEntry={initialPleiepengerperiode}
		nested
	>
		{(path: string) => (
			<>
				<FormikDatepicker name={`${path}.fom`} label="Fra og med dato" />
				<FormikDatepicker name={`${path}.tom`} label="Til og med dato" />
			</>
		)}
	</FormikDollyFieldArray>
)
