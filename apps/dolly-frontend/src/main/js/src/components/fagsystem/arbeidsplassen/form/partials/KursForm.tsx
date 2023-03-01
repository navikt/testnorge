import { FormikDollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import { initialKurs } from '@/components/fagsystem/arbeidsplassen/form/initialValues'
import { FormikSelect } from '@/components/ui/form/inputs/select/Select'
import { FormikTextInput } from '@/components/ui/form/inputs/textInput/TextInput'
import { FormikDatepicker } from '@/components/ui/form/inputs/datepicker/Datepicker'
import * as React from 'react'
import { SelectOptionsManager as Options } from '@/service/SelectOptions'
import _get from 'lodash/get'
import Formatters from '@/utils/DataFormatter'

export const KursForm = ({ formikBag }) => {
	return (
		<FormikDollyFieldArray
			name="arbeidsplassenCV.kurs"
			header="Kurs"
			// hjelpetekst={infotekst}
			newEntry={initialKurs}
			nested
		>
			{(kursPath, idx) => {
				const durationUnit = _get(formikBag.values, `${kursPath}.durationUnit`)
				return (
					<div key={idx} className="flexbox--flex-wrap">
						<FormikTextInput name={`${kursPath}.title`} label="Kursnavn" size="xlarge" />
						<FormikTextInput name={`${kursPath}.issuer`} label="Kursholder" size="xlarge" />
						<FormikDatepicker name={`${kursPath}.date`} label="Fullført" />
						<FormikSelect
							name={`${kursPath}.durationUnit`}
							label="Kurslengde"
							options={Options('kursLengde')}
							size="small"
						/>
						<FormikTextInput
							name={`${kursPath}.duration`}
							label={`Antall ${
								durationUnit && durationUnit !== 'UKJENT'
									? Formatters.showLabel('kursLengde', durationUnit)
									: ''
							}`}
							size="small"
							type="number"
						/>
					</div>
				)
			}}
		</FormikDollyFieldArray>
	)
}
