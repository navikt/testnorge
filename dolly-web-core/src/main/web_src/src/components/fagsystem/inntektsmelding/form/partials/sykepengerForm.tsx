import * as React from 'react'
import { get as _get } from 'lodash'
import { FormikTextInput } from '~/components/ui/form/inputs/textInput/TextInput'
import { FormikDatepicker } from '~/components/ui/form/inputs/datepicker/Datepicker'
import { FormikDollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { FormikCheckbox } from '~/components/ui/form/inputs/checbox/Checkbox'

interface SykepengerForm {
	path: string
}

const initialArbeidsperiode = {
	fom: '',
	tom: ''
}

const eksempelOptions = [{ value: 'test', label: 'test' }]
export default ({ path }: SykepengerForm) => {
	return (
		<>
			<FormikTextInput name={`${path}.bruttoUtbetalt`} label="Brutto utbetalt" type="number" />
			<FormikSelect
				name={`${path}.begrunnelseForReduksjonEllerIkkeUtbetalt`}
				label="Begrunnelse for reduksjon eller ikke utbetalt"
				options={eksempelOptions}
				size="medium"
				isClearable={false}
			/>
			<FormikDollyFieldArray
				name={`${path}.arbeidsgiverperiodeListe`}
				header="Arbeidsgiverperioder"
				newEntry={initialArbeidsperiode}
				nested
			>
				{(path: string) => (
					<>
						<FormikDatepicker name={`${path}.fom`} label="Fra og med dato" />
						<FormikDatepicker name={`${path}.tom`} label="Til og med dato" />
					</>
				)}
			</FormikDollyFieldArray>
		</>
	)
}
