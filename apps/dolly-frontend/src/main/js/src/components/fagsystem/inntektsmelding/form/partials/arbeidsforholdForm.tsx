import * as React from 'react'
import InntektsmeldingSelect from '@/components/fagsystem/inntektsmelding/form/partials/InntektsmeldingSelect'
import { FormTextInput } from '@/components/ui/form/inputs/textInput/TextInput'
import { FormDatepicker } from '@/components/ui/form/inputs/datepicker/Datepicker'
import { FormDollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import { Kodeverk, Ytelser } from '@/components/fagsystem/inntektsmelding/InntektsmeldingTypes'

interface ArbeidsforholdForm {
	path: string
	ytelse: string
}

const initialPeriode = { fom: '', tom: '' }

export default ({ path, ytelse }: ArbeidsforholdForm) => (
	<div className="flexbox--flex-wrap">
		<FormTextInput name={`${path}.arbeidsforholdId`} label="Arbeidsforhold-ID" type="number" />
		<FormTextInput name={`${path}.beregnetInntekt.beloep`} label="Beløp" type="number" />
		<InntektsmeldingSelect
			path={`${path}.beregnetInntekt.aarsakVedEndring`}
			label="Årsak ved endring"
			kodeverk={Kodeverk.AarsakVedEndring}
		/>
		<FormDatepicker name={`${path}.foersteFravaersdag`} label="Første fraværsdag" />
		{/* Gjelder for sykepoenger, svangerskapspenger, pleie, omsorg og opplæring*/}
		{ytelse !== Ytelser.Foreldrepenger && (
			<FormDollyFieldArray
				name={`${path}.avtaltFerieListe`}
				header="Avtalt ferieliste"
				newEntry={initialPeriode}
			>
				{(path: string) => (
					<>
						<FormDatepicker name={`${path}.fom`} label="Fra og med dato" />
						<FormDatepicker name={`${path}.tom`} label="Til og med dato" />
					</>
				)}
			</FormDollyFieldArray>
		)}
	</div>
)
