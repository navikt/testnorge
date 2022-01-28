import * as React from 'react'
import { FormikTextInput } from '~/components/ui/form/inputs/textInput/TextInput'
import { FormikDatepicker } from '~/components/ui/form/inputs/datepicker/Datepicker'
import { FormikDollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'
import { Ytelser } from '~/components/fagsystem/inntektsmelding/InntektsmeldingTypes'

interface RefusjonForm {
	path: string
	ytelse: string
}

const initialEndringIRefusjon = {
	refusjonsbeloepPrMnd: null as string,
	endringsdato: new Date(),
}

export default ({ path, ytelse }: RefusjonForm) => (
	<div className="flexbox--flex-wrap">
		<FormikTextInput
			name={`${path}.refusjonsbeloepPrMnd`}
			label="Samlet månedlig refusjonsbeløp"
			type="number"
			size="medium"
		/>
		<FormikDatepicker name={`${path}.refusjonsopphoersdato`} label="Opphørsdato for refusjon" />
		{/* Endring i refusjon gjelder sykepenger, foreldrepenger, svangerskapspenger, pleiepenger, opplæring */}
		{ytelse !== Ytelser.Omsorgspenger && (
			<FormikDollyFieldArray
				name={`${path}.endringIRefusjonListe`}
				header="Endring i refusjon"
				newEntry={initialEndringIRefusjon}
			>
				{(path: string) => (
					<>
						<FormikDatepicker name={`${path}.endringsdato`} label="Endringsdato" />
						<FormikTextInput
							name={`${path}.refusjonsbeloepPrMnd`}
							label="Nytt refusjonsbeløp per måned"
							type="number"
						/>
					</>
				)}
			</FormikDollyFieldArray>
		)}
	</div>
)
