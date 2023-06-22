import { FormikDollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import {
	initialKurs,
	initialKursVerdier,
} from '@/components/fagsystem/arbeidsplassen/form/initialValues'
import { FormikSelect } from '@/components/ui/form/inputs/select/Select'
import { FormikTextInput } from '@/components/ui/form/inputs/textInput/TextInput'
import { FormikDatepicker } from '@/components/ui/form/inputs/datepicker/Datepicker'
import * as React from 'react'
import { SelectOptionsManager as Options } from '@/service/SelectOptions'
import _get from 'lodash/get'
import { showLabel } from '@/utils/DataFormatter'
import { Vis } from '@/components/bestillingsveileder/VisAttributt'
import { EraseFillButtons } from '@/components/fagsystem/arbeidsplassen/form/partials/EraseFillButtons'

export const KursForm = ({ formikBag }) => {
	const kursListePath = 'arbeidsplassenCV.kurs'

	return (
		<Vis attributt={kursListePath}>
			<FormikDollyFieldArray
				name={kursListePath}
				header="Kurs"
				newEntry={initialKursVerdier}
				nested
			>
				{(kursPath, idx) => {
					const durationUnit = _get(formikBag.values, `${kursPath}.durationUnit`)
					return (
						<>
							<div key={idx} className="flexbox--flex-wrap">
								<FormikTextInput
									name={`${kursPath}.title`}
									label="Kursnavn"
									size="xlarge"
									key={`title_${_get(formikBag.values, `${kursPath}.title`)}`}
								/>
								<FormikTextInput
									name={`${kursPath}.issuer`}
									label="Kursholder"
									size="xlarge"
									key={`issuer_${_get(formikBag.values, `${kursPath}.issuer`)}`}
								/>
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
											? showLabel('kursLengde', durationUnit)
											: ''
									}`}
									size="small"
									type="number"
									key={`duration_${_get(formikBag.values, `${kursPath}.duration`)}`}
								/>
							</div>
							<EraseFillButtons
								formikBag={formikBag}
								path={kursPath}
								initialFill={initialKursVerdier}
								initialErase={initialKurs}
							/>
						</>
					)
				}}
			</FormikDollyFieldArray>
		</Vis>
	)
}
