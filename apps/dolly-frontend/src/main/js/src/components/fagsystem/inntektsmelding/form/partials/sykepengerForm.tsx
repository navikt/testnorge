import * as React from 'react'
import InntektsmeldingSelect from './InntektsmeldingSelect'
import { FormikTextInput } from '~/components/ui/form/inputs/textInput/TextInput'
import { FormikDatepicker } from '~/components/ui/form/inputs/datepicker/Datepicker'
import { FormikDollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'
import { Kodeverk } from '~/components/fagsystem/inntektsmelding/InntektsmeldingTypes'

interface SykepengerForm {
	path: string
}

const initialArbeidsperiode = {
	fom: '',
	tom: ''
}

export default ({ path }: SykepengerForm) => (
	<div className="flexbox--flex-wrap">
		<FormikTextInput name={`${path}.bruttoUtbetalt`} label="Brutto utbetalt" type="number" />
		<InntektsmeldingSelect
			path={`${path}.begrunnelseForReduksjonEllerIkkeUtbetalt`}
			label="Begrunnelse for reduksjon eller ikke utbetalt"
			kodeverk={Kodeverk.Begrunnelse}
			size="large"
		/>
		<FormikDollyFieldArray
			name={`${path}.arbeidsgiverperiodeListe`}
			header="Arbeidsgiverperioder"
			newEntry={initialArbeidsperiode}
		>
			{(newPath: string) => (
				<>
					<FormikDatepicker name={`${newPath}.fom`} label="Fra og med dato" />
					<FormikDatepicker name={`${newPath}.tom`} label="Til og med dato" />
				</>
			)}
		</FormikDollyFieldArray>
	</div>
)
