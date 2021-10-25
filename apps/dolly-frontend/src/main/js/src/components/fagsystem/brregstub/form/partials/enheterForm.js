import React from 'react'
import _get from 'lodash/get'
import { FormikDollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'
import { FormikDatepicker } from '~/components/ui/form/inputs/datepicker/Datepicker'
import { DollySelect, FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { OrganisasjonLoader } from '~/components/organisasjonSelect'
import { SelectOptionsOppslag } from '~/service/SelectOptionsOppslag'
import { PersonrollerForm } from './personrollerForm'

const initialValues = {
	rolle: '',
	registreringsdato: new Date(),
	foretaksNavn: {
		navn1: '',
	},
	orgNr: '',
	personroller: [],
}

export const EnheterForm = ({ formikBag }) => {
	const roller = SelectOptionsOppslag.hentRollerFraBrregstub()
	const rollerOptions = SelectOptionsOppslag.formatOptions('roller', roller)

	const setEnhetsinfo = (org, path) => {
		formikBag.setFieldValue(`${path}.orgNr`, org.value)
		formikBag.setFieldValue(`${path}.foretaksNavn.navn1`, org.navn)
		if (org.forretningsAdresse) {
			formikBag.setFieldValue(`${path}.forretningsAdresse`, {
				adresse1: org.forretningsAdresse.adresse,
				kommunenr: org.forretningsAdresse.kommunenr,
				landKode: org.forretningsAdresse.landkode,
				postnr: org.forretningsAdresse.postnr,
				poststed: org.forretningsAdresse.poststed,
			})
		}
		if (org.postadresse) {
			formikBag.setFieldValue(`${path}.postAdresse`, {
				adresse1: org.forretningsAdresse.adresse,
				kommunenr: org.forretningsAdresse.kommunenr,
				landKode: org.forretningsAdresse.landkode,
				postnr: org.forretningsAdresse.postnr,
				poststed: org.forretningsAdresse.poststed,
			})
		}
	}

	return (
		<FormikDollyFieldArray
			name="brregstub.enheter"
			header="Enhet"
			newEntry={initialValues}
			canBeEmpty={false}
		>
			{(path) => (
				<>
					<FormikSelect
						name={`${path}.rolle`}
						label="Rolle"
						options={rollerOptions}
						isLoading={roller.loading}
						size="large"
						isClearable={false}
						fastfield={false}
					/>
					<FormikDatepicker name={`${path}.registreringsdato`} label="Registreringsdato" />
					<OrganisasjonLoader
						render={(data) => (
							<DollySelect
								name={`${path}.orgNr`}
								label="Organisasjonsnummer"
								options={data}
								size="xlarge"
								onChange={(org) => setEnhetsinfo(org, path)}
								value={_get(formikBag.values, `${path}.orgNr`)}
								feil={
									_get(formikBag.values, `${path}.orgNr`) === '' && {
										feilmelding: 'Feltet er påkrevd',
									}
								}
								isClearable={false}
							/>
						)}
					/>
					<PersonrollerForm formikBag={formikBag} path={path} />
				</>
			)}
		</FormikDollyFieldArray>
	)
}
