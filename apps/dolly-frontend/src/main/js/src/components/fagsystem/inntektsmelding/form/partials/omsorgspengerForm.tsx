import * as React from 'react'
import { get as _get } from 'lodash'
import { FormikTextInput } from '~/components/ui/form/inputs/textInput/TextInput'
import { FormikDatepicker } from '~/components/ui/form/inputs/datepicker/Datepicker'
import { FormikDollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'
import { FormikCheckbox } from '~/components/ui/form/inputs/checbox/Checkbox'

interface OmsorgspengerForm {
	path: string
}

const initialDelvisFravaer = {
	dato: '',
	timer: ''
}

const initialFravaersperioder = {
	fom: '',
	tom: ''
}

export default ({ path }: OmsorgspengerForm) => {
	return (
		<div className="flexbox--flex-wrap">
			<FormikDollyFieldArray
				name={`${path}.delvisFravaersListe`}
				header="Delvis fravær"
				newEntry={initialDelvisFravaer}
			>
				{(path: string) => (
					<>
						<FormikDatepicker name={`${path}.dato`} label="Dato" />
						<FormikTextInput name={`${path}.timer`} label="Antall timer" type="number" />
					</>
				)}
			</FormikDollyFieldArray>
			<FormikDollyFieldArray
				name={`${path}.fravaersPerioder`}
				header="Fraværsperioder"
				newEntry={initialFravaersperioder}
			>
				{(path: string) => (
					<>
						<FormikDatepicker name={`${path}.fom`} label="Fra og med dato" />
						<FormikDatepicker name={`${path}.tom`} label="Til og med dato" />
					</>
				)}
			</FormikDollyFieldArray>
			<FormikCheckbox
				name={`${path}.harUtbetaltPliktigeDager`}
				label="Har utbetalt pliktige dager"
				checkboxMargin
				size="medium"
			/>
		</div>
	)
}
