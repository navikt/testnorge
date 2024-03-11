import * as React from 'react'
import { FormTextInput } from '@/components/ui/form/inputs/textInput/TextInput'
import { FormDatepicker } from '@/components/ui/form/inputs/datepicker/Datepicker'
import { FormDollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import { FormCheckbox } from '@/components/ui/form/inputs/checbox/Checkbox'

interface OmsorgspengerForm {
	path: string
}

const initialDelvisFravaer = {
	dato: '',
	timer: '',
}

const initialFravaersperioder = {
	fom: '',
	tom: '',
}

export default ({ path }: OmsorgspengerForm) => {
	return (
		<div className="flexbox--flex-wrap">
			<FormDollyFieldArray
				name={`${path}.delvisFravaersListe`}
				header="Delvis fravær"
				newEntry={initialDelvisFravaer}
			>
				{(path: string) => (
					<>
						<FormDatepicker name={`${path}.dato`} label="Dato" />
						<FormTextInput name={`${path}.timer`} label="Antall timer" type="number" />
					</>
				)}
			</FormDollyFieldArray>
			<FormDollyFieldArray
				name={`${path}.fravaersPerioder`}
				header="Fraværsperioder"
				newEntry={initialFravaersperioder}
			>
				{(path: string) => (
					<>
						<FormDatepicker name={`${path}.fom`} label="Fra og med dato" />
						<FormDatepicker name={`${path}.tom`} label="Til og med dato" />
					</>
				)}
			</FormDollyFieldArray>
			<FormCheckbox
				name={`${path}.harUtbetaltPliktigeDager`}
				label="Har utbetalt pliktige dager"
				size="medium"
				checkboxMargin
			/>
		</div>
	)
}
