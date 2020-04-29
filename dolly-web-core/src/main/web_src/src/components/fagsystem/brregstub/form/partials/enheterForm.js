import React from 'react'
import _get from 'lodash/get'
import { FormikDollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'
import { FormikDatepicker } from '~/components/ui/form/inputs/datepicker/Datepicker'
import { FormikTextInput } from '~/components/ui/form/inputs/textInput/TextInput'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { DollySelect } from '~/components/ui/form/inputs/select/Select'
import { OrganisasjonLoader } from '~/components/organisasjonSelect'
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

	const setEnhetsinfo = (org, path) => {
		formikBag.setFieldValue(`${path}.orgNr`, org.value)
		if (org.forretningsAdresse) {
			console.log('org.forretningsAdresse :>> ', org.forretningsAdresse)
			formikBag.setFieldValue(`${path}.forretningsAdresse.adresse1`, org.forretningsAdresse.adresse)
			formikBag.setFieldValue(
				`${path}.forretningsAdresse.kommunenr`,
				org.forretningsAdresse.kommunenr
			)
			formikBag.setFieldValue(
				`${path}.forretningsAdresse.landKode`,
				org.forretningsAdresse.landkode
			)
			formikBag.setFieldValue(`${path}.forretningsAdresse.postnr`, org.forretningsAdresse.postnr)
			formikBag.setFieldValue(
				`${path}.forretningsAdresse.poststed`,
				org.forretningsAdresse.poststed
			)
		}
		if (org.postadresse) {
			formikBag.setFieldValue(`${path}.postAdresse.adresse1`, org.postadresse.adresse)
			formikBag.setFieldValue(`${path}.postAdresse.kommunenr`, org.postadresse.kommunenr)
			formikBag.setFieldValue(`${path}.postAdresse.landKode`, org.postadresse.landkode)
			formikBag.setFieldValue(`${path}.postAdresse.postnr`, org.postadresse.postnr)
			formikBag.setFieldValue(`${path}.postAdresse.poststed`, org.postadresse.poststed)
		}
	}

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
					<OrganisasjonLoader
						render={data => (
							<DollySelect
								name={`${path}.orgNr`}
								label="Organisasjonsnummer"
								options={data}
								size="xlarge"
								onChange={org => setEnhetsinfo(org, path)}
								value={_get(formikBag.values, `${path}.orgNr`)}
								feil={
									_get(formikBag.values, `${path}.orgNr`) === '' && {
										feilmelding: 'Feltet er påkrevd'
									}
								}
								isClearable={false}
							/>
						)}
					/>
				</>
			)}
		</FormikDollyFieldArray>
	)
}
