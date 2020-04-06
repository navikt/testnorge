import * as React from 'react'
import { get as _get } from 'lodash'
import { FormikTextInput } from '~/components/ui/form/inputs/textInput/TextInput'
import { FormikDatepicker } from '~/components/ui/form/inputs/datepicker/Datepicker'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { FormikDollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'

interface ArbeidsforholdForm {
	path: string
	ytelse: string
}

const initialPeriode = { fom: '', tom: '' }

const eksempelOptions = [{ value: 'test', label: 'Test' }]

export default ({ path, ytelse }: ArbeidsforholdForm) => {
	return (
		<>
			<FormikTextInput name={`${path}.beregnetInntekt.beloep`} label="Beløp" type="number" />
			{/* Ferie: Gjelder for sykepoenger, svangerskapspenger, pleie, omsorg og opplæring. Gjør mer elegant! */}
			{ytelse !== 'FORELDREPENGER' && (
				<FormikDollyFieldArray
					name={`${path}.avtaltFerieListe`}
					header="Avtalt ferieliste"
					newEntry={initialPeriode}
					// nested
				>
					{(path: string) => (
						<>
							<FormikDatepicker name={`${path}.fom`} label="Fra og med dato" />
							<FormikDatepicker name={`${path}.tom`} label="Til og med dato" />
						</>
					)}
				</FormikDollyFieldArray>
			)}
			{/* 
			<FormikSelect
				name={`${path}.beregnetInntekt.aarsakVedEndring`}
				label="Årsak ved endring"
				options={eksempelOptions}
			/>

			<FormikDatepicker name={`${path}.foersteFravaersdag`} label="Første fraværsdag" /> */}
		</>
	)
}
