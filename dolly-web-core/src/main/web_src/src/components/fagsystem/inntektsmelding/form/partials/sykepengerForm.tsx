import * as React from 'react'
import { get as _get } from 'lodash'
import InntektsmeldingSelect from './InntektsmeldingSelect'
import { FormikTextInput } from '~/components/ui/form/inputs/textInput/TextInput'
import { FormikDatepicker } from '~/components/ui/form/inputs/datepicker/Datepicker'
import { FormikDollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'

interface SykepengerForm {
	path: string
}

const initialArbeidsperiode = {
	fom: '',
	tom: ''
}

export default ({ path }: SykepengerForm) => {
	return (
		<div className="flexbox--flex-wrap">
			<FormikTextInput name={`${path}.bruttoUtbetalt`} label="Brutto utbetalt" type="number" />
			<InntektsmeldingSelect
				path={`${path}.begrunnelseForReduksjonEllerIkkeUtbetalt`}
				label="Begrunnelse for reduksjon eller ikke utbetalt"
				kodeverk="BEGRUNNELSE_TYPE"
				size="large"
			/>
			<FormikDollyFieldArray
				name={`${path}.arbeidsgiverperiodeListe`}
				header="Arbeidsgiverperioder"
				newEntry={initialArbeidsperiode}
			>
				{(path: string) => (
					<>
						<FormikDatepicker name={`${path}.fom`} label="Fra og med dato" />
						<FormikDatepicker name={`${path}.tom`} label="Til og med dato" />
					</>
				)}
			</FormikDollyFieldArray>
		</div>
	)
}
