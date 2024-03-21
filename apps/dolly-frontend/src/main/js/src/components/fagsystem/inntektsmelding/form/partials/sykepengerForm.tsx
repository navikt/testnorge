import * as React from 'react'
import InntektsmeldingSelect from '@/components/fagsystem/inntektsmelding/form/partials/InntektsmeldingSelect'
import { FormTextInput } from '@/components/ui/form/inputs/textInput/TextInput'
import { FormDatepicker } from '@/components/ui/form/inputs/datepicker/Datepicker'
import { FormDollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import { Kodeverk } from '@/components/fagsystem/inntektsmelding/InntektsmeldingTypes'

interface SykepengerForm {
	path: string
}

const initialArbeidsperiode = {
	fom: '',
	tom: '',
}

export default ({ path }: SykepengerForm) => (
	<div className="flexbox--flex-wrap">
		<FormTextInput name={`${path}.bruttoUtbetalt`} label="Brutto utbetalt" type="number" />
		<InntektsmeldingSelect
			path={`${path}.begrunnelseForReduksjonEllerIkkeUtbetalt`}
			label="Begrunnelse for reduksjon eller ikke utbetalt"
			kodeverk={Kodeverk.Begrunnelse}
			size="large"
		/>
		<FormDollyFieldArray
			name={`${path}.arbeidsgiverperiodeListe`}
			header="Arbeidsgiverperioder"
			newEntry={initialArbeidsperiode}
		>
			{(newPath: string) => (
				<>
					<FormDatepicker name={`${newPath}.fom`} label="Fra og med dato" />
					<FormDatepicker name={`${newPath}.tom`} label="Til og med dato" />
				</>
			)}
		</FormDollyFieldArray>
	</div>
)
