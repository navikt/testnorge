import * as React from 'react'
import { FormTextInput } from '@/components/ui/form/inputs/textInput/TextInput'
import { FormDatepicker } from '@/components/ui/form/inputs/datepicker/Datepicker'
import { FormDollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import { Ytelser } from '@/components/fagsystem/inntektsmelding/InntektsmeldingTypes'

interface RefusjonForm {
	path: string
	ytelse: string
}

const initialEndringIRefusjon = {
	refusjonsbeloepPrMnd: null as unknown as string,
	endringsdato: new Date(),
}

export default ({ path, ytelse }: RefusjonForm) => (
	<div className="flexbox--flex-wrap">
		<FormTextInput
			name={`${path}.refusjonsbeloepPrMnd`}
			label="Samlet månedlig refusjonsbeløp"
			type="number"
			size="medium"
		/>
		<FormDatepicker name={`${path}.refusjonsopphoersdato`} label="Opphørsdato for refusjon" />
		{/* Endring i refusjon gjelder sykepenger, foreldrepenger, svangerskapspenger, pleiepenger, opplæring */}
		{ytelse !== Ytelser.Omsorgspenger && (
			<FormDollyFieldArray
				name={`${path}.endringIRefusjonListe`}
				header="Endring i refusjon"
				newEntry={initialEndringIRefusjon}
			>
				{(path: string) => (
					<>
						<FormDatepicker name={`${path}.endringsdato`} label="Endringsdato" />
						<FormTextInput
							name={`${path}.refusjonsbeloepPrMnd`}
							label="Nytt refusjonsbeløp per måned"
							type="number"
						/>
					</>
				)}
			</FormDollyFieldArray>
		)}
	</div>
)
