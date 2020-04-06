import * as React from 'react'
import { get as _get } from 'lodash'
import _has from 'lodash/has'
import Button from '~/components/ui/button/Button'
import { FormikTextInput } from '~/components/ui/form/inputs/textInput/TextInput'
import { FormikDatepicker } from '~/components/ui/form/inputs/datepicker/Datepicker'
import { FormikDollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'

interface RefusjonForm {
	path: string
	formikBag: any
}

const initialEndringIRefusjon = {
	fom: '',
	tom: ''
}

export default ({ path, formikBag }: RefusjonForm) => {
	return (
		<>
			<FormikTextInput
				name={`${path}.refusjonsbeloepPrMnd`}
				label="Samlet månedlig refusjonsbeløp"
				type="number"
				size="medium"
			/>
			<FormikDatepicker name={`${path}.refusjonsopphoersdato`} label="Opphørsdato for refusjon" />
			{/* Endring i refusjon gjelder sykepenger, foreldrepenger, svangerskapspenger, pleiepenger, opplæring */}
			{/* <FormikDollyFieldArray
				name={`${path}.endringIRefusjonListe`}
				header="Endring i refusjon"
				newEntry={initialEndringIRefusjon}
				nested
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
			</FormikDollyFieldArray> */}
		</>
	)
}
