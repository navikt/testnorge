import * as React from 'react'
import { FormDatepicker } from '@/components/ui/form/inputs/datepicker/Datepicker'
import { FormTextInput } from '@/components/ui/form/inputs/textInput/TextInput'
import { FormDollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import InntektsmeldingSelect from '@/components/fagsystem/inntektsmelding/form/partials/InntektsmeldingSelect'
import { Kodeverk } from '@/components/fagsystem/inntektsmelding/InntektsmeldingTypes'

interface NaturalytelseForm {
	path: string
}

const initialNaturalytelse = {
	beloepPrMnd: '',
	fom: '',
	naturalytelseType: '',
}

export default ({ path }: NaturalytelseForm) => (
	<>
		<FormDollyFieldArray
			name={`${path}.opphoerAvNaturalytelseListe`}
			header="Opphør av naturalytelse"
			newEntry={initialNaturalytelse}
		>
			{(path: string) => (
				<>
					<FormTextInput name={`${path}.beloepPrMnd`} label="Beløp per måned" type="number" />
					<FormDatepicker name={`${path}.fom`} label="Fra og med dato" />
					<InntektsmeldingSelect
						path={`${path}.naturalytelseType`}
						label="Årsak til innsending"
						kodeverk={Kodeverk.NaturalYtelse}
						size="xlarge"
					/>
				</>
			)}
		</FormDollyFieldArray>
		<FormDollyFieldArray
			name={`${path}.gjenopptakelseNaturalytelseListe`}
			header="Gjenopptakelse av naturalytelse"
			newEntry={initialNaturalytelse}
		>
			{(path: string) => (
				<>
					<FormTextInput name={`${path}.beloepPrMnd`} label="Beløp per måned" type="number" />
					<FormDatepicker name={`${path}.fom`} label="Fra og med dato" />
					<InntektsmeldingSelect
						path={`${path}.naturalytelseType`}
						label="Årsak til innsending"
						kodeverk={Kodeverk.NaturalYtelse}
						size="xlarge"
					/>
				</>
			)}
		</FormDollyFieldArray>
	</>
)
