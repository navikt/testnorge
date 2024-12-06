import { FormDollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import { FormDatepicker } from '@/components/ui/form/inputs/datepicker/Datepicker'
import { FormSelect } from '@/components/ui/form/inputs/select/Select'
import { SelectOptionsOppslag } from '@/service/SelectOptionsOppslag'
import { PersonrollerForm } from '@/components/fagsystem/brregstub/form/partials/personrollerForm'
import { OrgnrToggle } from '@/components/fagsystem/brregstub/form/partials/orgnrToggle'
import { SelectOptionsFormat } from '@/service/SelectOptionsFormat'
import React from 'react'

const initialValues = {
	rolle: '',
	registreringsdato: new Date(),
	foretaksNavn: {
		navn1: '',
	},
	orgNr: null,
	personroller: [],
}

export const EnheterForm = ({ formMethods }) => {
	const roller = SelectOptionsOppslag.hentRollerFraBrregstub()
	const rollerOptions = SelectOptionsFormat.formatOptions('roller', roller)

	const setEnhetsinfo = (org, path) => {
		const currentValues = formMethods.watch(path)
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
		formMethods.setValue(path, currentValues)
		formMethods.trigger(path)
	}

	return (
		<FormDollyFieldArray
			name="brregstub.enheter"
			header="Enhet"
			newEntry={initialValues}
			canBeEmpty={false}
		>
			{(path) => (
				<React.Fragment key={path}>
					<FormSelect
						name={`${path}.rolle`}
						label="Rolle"
						options={rollerOptions}
						isLoading={roller.loading}
						size="large"
						isClearable={false}
					/>
					<FormDatepicker name={`${path}.registreringsdato`} label="Registreringsdato" />
					<OrgnrToggle path={path} formMethods={formMethods} setEnhetsinfo={setEnhetsinfo} />
					<PersonrollerForm formMethods={formMethods} path={path} />
				</React.Fragment>
			)}
		</FormDollyFieldArray>
	)
}
