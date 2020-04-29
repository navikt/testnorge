import React from 'react'
import _get from 'lodash/get'
import { FormikDollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'
import { FormikDatepicker } from '~/components/ui/form/inputs/datepicker/Datepicker'
import { FormikTextInput } from '~/components/ui/form/inputs/textInput/TextInput'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { SelectOptionsOppslag } from '~/service/SelectOptionsOppslag'

const initialValues = {
	rollebeskrivelse: '',
	registreringsdato: new Date(),
	foretaksNavn: {
		navn1: ''
	},
	orgNr: ''
}

export const EnheterForm = ({ formikBag }) => {
	const roller = SelectOptionsOppslag.hentRollerFraBrregstub()
	const rollerOptions = SelectOptionsOppslag.formatOptions('roller', roller)

	return (
		<FormikDollyFieldArray name="bregstub.enheter" header="Enhet" newEntry={initialValues}>
			{path => (
				<>
					<FormikSelect
						name={`${path}.rollebeskrivelse`}
						label="Rollebeskrivelse"
						options={rollerOptions}
						isLoading={roller.loading}
						size="large"
						isClearable={false}
						fastfield={false}
					/>
					<FormikDatepicker name={`${path}.registreringsdato`} label="Registreringsdato" />
					<FormikTextInput name={`${path}.foretaksNavn.navn1`} label="Foretaksnavn" size="large" />
					<FormikTextInput name={`${path}.orgNr`} label="Organisasjonsnummer" type="number" />
				</>
			)}
		</FormikDollyFieldArray>
	)
}
