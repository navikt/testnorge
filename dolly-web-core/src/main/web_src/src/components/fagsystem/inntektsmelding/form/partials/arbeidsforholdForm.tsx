import * as React from 'react'
import { get as _get } from 'lodash'
import { FormikProps } from 'formik'
import InntektsmeldingSelect from './InntektsmeldingSelect'
import { FormikTextInput } from '~/components/ui/form/inputs/textInput/TextInput'
import { FormikDatepicker } from '~/components/ui/form/inputs/datepicker/Datepicker'
import { FormikDollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'

interface ArbeidsforholdForm {
	path: string
	ytelse: string
	formikBag: FormikProps<{}>
}

const initialPeriode = { fom: '', tom: '' }

export default ({ path, ytelse, formikBag }: ArbeidsforholdForm) => {
	return (
		<div className="flexbox--flex-wrap">
			<FormikTextInput name={`${path}.beregnetInntekt.beloep`} label="Beløp" type="number" />
			<InntektsmeldingSelect
				path={`${path}.beregnetInntekt.aarsakVedEndring`}
				label="Årsak ved endring"
				kodeverk="AARSAK_VED_ENDRING_TYPE"
				formikBag={formikBag}
			/>
			<FormikDatepicker name={`${path}.foersteFravaersdag`} label="Første fraværsdag" />
			{/* Ferie: Gjelder for sykepoenger, svangerskapspenger, pleie, omsorg og opplæring. TODO Gjør mer elegant! */}
			{ytelse !== 'FORELDREPENGER' && (
				<FormikDollyFieldArray
					name={`${path}.avtaltFerieListe`}
					header="Avtalt ferieliste"
					newEntry={initialPeriode}
				>
					{(path: string) => (
						<>
							<FormikDatepicker name={`${path}.fom`} label="Fra og med dato" />
							<FormikDatepicker name={`${path}.tom`} label="Til og med dato" />
						</>
					)}
				</FormikDollyFieldArray>
			)}
		</div>
	)
}
