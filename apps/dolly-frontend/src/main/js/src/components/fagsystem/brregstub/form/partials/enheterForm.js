import React from 'react'
import _get from 'lodash/get'
import { FormikDollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'
import { FormikDatepicker } from '~/components/ui/form/inputs/datepicker/Datepicker'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { SelectOptionsOppslag } from '~/service/SelectOptionsOppslag'
import { PersonrollerForm } from './personrollerForm'
import { OrgnrToggle } from '~/components/fagsystem/brregstub/form/partials/orgnrToggle'

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
		const currentValues = _get(formikBag.values, path)
		currentValues['orgNr'] = org.value
		currentValues['foretaksNavn'] = { navn1: org.navn }
		if (org.forretningsAdresse) {
			currentValues['forretningsAdresse'] = {
				adresse1: org.forretningsAdresse.adresselinje1,
				kommunenr: org.forretningsAdresse.kommunenr,
				landKode: org.forretningsAdresse.landkode,
				postnr: org.forretningsAdresse.postnr,
				poststed: org.forretningsAdresse.poststed,
			}
		} else if (currentValues.hasOwnProperty('forretningsAdresse')) {
			delete currentValues['forretningsAdresse']
		}

		if (org.postadresse) {
			currentValues['postAdresse'] = {
				adresse1: org.postadresse.adresselinje1,
				kommunenr: org.postadresse.kommunenr,
				landKode: org.postadresse.landkode,
				postnr: org.postadresse.postnr,
				poststed: org.postadresse.poststed,
			}
		} else if (currentValues.hasOwnProperty('postAdresse')) {
			delete currentValues['postAdresse']
		}

		formikBag.setFieldValue(path, currentValues)
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
					<OrgnrToggle path={path} formikBag={formikBag} setEnhetsinfo={setEnhetsinfo} />
					<PersonrollerForm formikBag={formikBag} path={path} />
				</>
			)}
		</FormikDollyFieldArray>
	)
}
