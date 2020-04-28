import React from 'react'
import _get from 'lodash/get'
import { FormikDollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'
import { FormikDatepicker } from '~/components/ui/form/inputs/datepicker/Datepicker'
import { FormikTextInput } from '~/components/ui/form/inputs/textInput/TextInput'
import { DollySelect } from '~/components/ui/form/inputs/select/Select'
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
					<DollySelect
						name={`${path}.rollebeskrivelse`}
						label="Rollebeskrivelse"
						options={rollerOptions}
						isLoading={roller.loading}
						onChange={rolle => formikBag.setFieldValue(`${path}.rollebeskrivelse`, rolle.value)}
						value={_get(formikBag.values, `${path}.rollebeskrivelse`)}
						feil={
							_get(formikBag.values, `${path}.rollebeskrivelse`) === '' && {
								feilmelding: 'Feltet er påkrevd'
							}
						}
						size="large"
						isClearable={false}
					/>
					<FormikDatepicker name={`${path}.registreringsdato`} label="Registreringsdato" />
					<FormikTextInput name={`${path}.foretaksNavn.navn1`} label="Foretaksnavn" size="large" />
					<FormikTextInput name={`${path}.orgNr`} label="Organisasjonsnummer" type="number" />
				</>
			)}
		</FormikDollyFieldArray>
	)
}
