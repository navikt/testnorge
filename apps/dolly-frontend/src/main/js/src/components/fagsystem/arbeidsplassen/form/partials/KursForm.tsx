import { FormDollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import {
	initialKurs,
	initialKursVerdier,
} from '@/components/fagsystem/arbeidsplassen/form/initialValues'
import { FormSelect } from '@/components/ui/form/inputs/select/Select'
import { FormTextInput } from '@/components/ui/form/inputs/textInput/TextInput'
import { FormDatepicker } from '@/components/ui/form/inputs/datepicker/Datepicker'
import * as React from 'react'
import { SelectOptionsManager as Options } from '@/service/SelectOptions'
import * as _ from 'lodash-es'
import { showLabel } from '@/utils/DataFormatter'
import { Vis } from '@/components/bestillingsveileder/VisAttributt'
import { EraseFillButtons } from '@/components/fagsystem/arbeidsplassen/form/partials/EraseFillButtons'

export const KursForm = ({ formMethods }) => {
	const kursListePath = 'arbeidsplassenCV.kurs'

	return (
		<Vis attributt={kursListePath}>
			<FormDollyFieldArray name={kursListePath} header="Kurs" newEntry={initialKursVerdier} nested>
				{(kursPath, idx) => {
					const durationUnit = _.get(formMethods.getValues(), `${kursPath}.durationUnit`)
					return (
						<>
							<div key={idx} className="flexbox--flex-wrap">
								<FormTextInput
									name={`${kursPath}.title`}
									label="Kursnavn"
									size="xlarge"
									key={`title_${_.get(formMethods.getValues(), `${kursPath}.title`)}`}
								/>
								<FormTextInput
									name={`${kursPath}.issuer`}
									label="Kursholder"
									size="xlarge"
									key={`issuer_${_.get(formMethods.getValues(), `${kursPath}.issuer`)}`}
								/>
								<FormDatepicker name={`${kursPath}.date`} label="Fullført" />
								<FormSelect
									name={`${kursPath}.durationUnit`}
									label="Kurslengde"
									options={Options('kursLengde')}
									size="small"
								/>
								<FormTextInput
									name={`${kursPath}.duration`}
									label={`Antall ${
										durationUnit && durationUnit !== 'UKJENT'
											? showLabel('kursLengde', durationUnit)
											: ''
									}`}
									size="small"
									type="number"
									key={`duration_${_.get(formMethods.getValues(), `${kursPath}.duration`)}`}
								/>
							</div>
							<EraseFillButtons
								formMethods={formMethods}
								path={kursPath}
								initialFill={initialKursVerdier}
								initialErase={initialKurs}
							/>
						</>
					)
				}}
			</FormDollyFieldArray>
		</Vis>
	)
}
