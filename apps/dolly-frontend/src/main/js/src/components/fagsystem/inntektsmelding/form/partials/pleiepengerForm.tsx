import * as React from 'react'
import { FormDatepicker } from '@/components/ui/form/inputs/datepicker/Datepicker'
import { FormDollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'

interface PleiepengerForm {
	path: string
}

const initialPleiepengerperiode = {
	fom: '',
	tom: '',
}

export default ({ path }: PleiepengerForm) => (
	<FormDollyFieldArray
		name={path}
		header="Pleiepengerperioder"
		newEntry={initialPleiepengerperiode}
		nested
	>
		{(path: string) => (
			<>
				<FormDatepicker name={`${path}.fom`} label="Fra og med dato" />
				<FormDatepicker name={`${path}.tom`} label="Til og med dato" />
			</>
		)}
	</FormDollyFieldArray>
)
