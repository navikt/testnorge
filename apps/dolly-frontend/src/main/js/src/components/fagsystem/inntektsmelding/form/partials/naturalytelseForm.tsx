import * as React from 'react'
import { FormikDatepicker } from '~/components/ui/form/inputs/datepicker/Datepicker'
import { FormikTextInput } from '~/components/ui/form/inputs/textInput/TextInput'
import { FormikDollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'
import InntektsmeldingSelect from './InntektsmeldingSelect'
import { Kodeverk } from '~/components/fagsystem/inntektsmelding/InntektsmeldingTypes'

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
		<FormikDollyFieldArray
			name={`${path}.opphoerAvNaturalytelseListe`}
			header="Opphør av naturalytelse"
			newEntry={initialNaturalytelse}
		>
			{(path: string) => (
				<>
					<FormikTextInput name={`${path}.beloepPrMnd`} label="Beløp per måned" type="number" />
					<FormikDatepicker name={`${path}.fom`} label="Fra og med dato" />
					<InntektsmeldingSelect
						path={`${path}.naturalytelseType`}
						label="Årsak til innsending"
						kodeverk={Kodeverk.NaturalYtelse}
						size="xlarge"
					/>
				</>
			)}
		</FormikDollyFieldArray>
		<FormikDollyFieldArray
			name={`${path}.gjenopptakelseNaturalytelseListe`}
			header="Gjenopptakelse av naturalytelse"
			newEntry={initialNaturalytelse}
		>
			{(path: string) => (
				<>
					<FormikTextInput name={`${path}.beloepPrMnd`} label="Beløp per måned" type="number" />
					<FormikDatepicker name={`${path}.fom`} label="Fra og med dato" />
					<InntektsmeldingSelect
						path={`${path}.naturalytelseType`}
						label="Årsak til innsending"
						kodeverk={Kodeverk.NaturalYtelse}
						size="xlarge"
					/>
				</>
			)}
		</FormikDollyFieldArray>
	</>
)
