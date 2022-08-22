import * as React from 'react'
import InntektsmeldingSelect from './InntektsmeldingSelect'
import { FormikTextInput } from '~/components/ui/form/inputs/textInput/TextInput'
import { FormikDatepicker } from '~/components/ui/form/inputs/datepicker/Datepicker'
import { FormikDollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'
import { Kodeverk, Ytelser } from '~/components/fagsystem/inntektsmelding/InntektsmeldingTypes'

interface ArbeidsforholdForm {
	path: string
	ytelse: string
}

const initialPeriode = { fom: '', tom: '' }

export default ({ path, ytelse }: ArbeidsforholdForm) => (
	<div className="flexbox--flex-wrap">
		<FormikTextInput name={`${path}.arbeidsforholdId`} label="Arbeidsforhold-ID" type="number" />
		<FormikTextInput name={`${path}.beregnetInntekt.beloep`} label="Beløp" type="number" />
		<InntektsmeldingSelect
			path={`${path}.beregnetInntekt.aarsakVedEndring`}
			label="Årsak ved endring"
			kodeverk={Kodeverk.AarsakVedEndring}
		/>
		<FormikDatepicker name={`${path}.foersteFravaersdag`} label="Første fraværsdag" />
		{/* Gjelder for sykepoenger, svangerskapspenger, pleie, omsorg og opplæring*/}
		{ytelse !== Ytelser.Foreldrepenger && (
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
