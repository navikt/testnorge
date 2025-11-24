import { FormDollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import {
	initialUtdanning,
	initialUtdanningVerdier,
} from '@/components/fagsystem/arbeidsplassen/form/initialValues'
import { FormTextInput } from '@/components/ui/form/inputs/textInput/TextInput'
import { FormCheckbox } from '@/components/ui/form/inputs/checbox/Checkbox'
import * as React from 'react'
import { Fritekstfelt } from '@/components/fagsystem/arbeidsplassen/form/styles'
import { FormSelect } from '@/components/ui/form/inputs/select/Select'
import { SelectOptionsManager as Options } from '@/service/SelectOptions'
import { Vis } from '@/components/bestillingsveileder/VisAttributt'
import { EraseFillButtons } from '@/components/fagsystem/arbeidsplassen/form/partials/EraseFillButtons'
import { FormDatepicker } from '@/components/ui/form/inputs/datepicker/Datepicker'
import { useFormContext } from 'react-hook-form'

export const UtdanningForm = () => {
	const formMethods = useFormContext()
	const utdanningListePath = 'arbeidsplassenCV.utdanning'

	return (
		<Vis attributt={utdanningListePath}>
			<FormDollyFieldArray
				name={utdanningListePath}
				header="Utdanninger"
				newEntry={initialUtdanningVerdier}
				buttonText="Utdanning"
				nested
			>
				{(utdanningPath, idx) => {
					const fieldPath = formMethods.watch(`${utdanningPath}.field`)
					const institutionPath = formMethods.watch(`${utdanningPath}.institution`)
					const beskrivelsePath = formMethods.watch(`${utdanningPath}.description`)
					return (
						<>
							<div key={idx} className="flexbox--flex-wrap">
								<FormSelect
									name={`${utdanningPath}.nuskode`}
									label="Utdanningsnivå"
									options={Options('nusKoder')}
									size="large"
									isClearable={false}
								/>
								<FormTextInput
									name={`${utdanningPath}.field`}
									label="Grad og utdanningsretning"
									size="medium"
									key={`field_${fieldPath}`}
								/>
								<FormTextInput
									name={`${utdanningPath}.institution`}
									label="Skole/studiested"
									size="medium"
									key={`institution_${institutionPath}`}
								/>
								<Fritekstfelt
									label="Beskrivelse"
									placeholder="Beskrivelse av utdanning"
									defaultValue={formMethods.getValues(`${utdanningPath}.description`)}
									onBlur={(beskrivelse) =>
										formMethods.setValue(`${utdanningPath}.description`, beskrivelse?.target?.value)
									}
									size="small"
									key={`description_${beskrivelsePath}`}
									resize
								/>
								<FormDatepicker name={`${utdanningPath}.startDate`} label="Startdato" />
								<FormDatepicker
									name={`${utdanningPath}.endDate`}
									label="Sluttdato"
									disabled={formMethods.getValues(`${utdanningPath}.ongoing`)}
								/>
								<FormCheckbox
									id={`${utdanningPath}.ongoing`}
									name={`${utdanningPath}.ongoing`}
									label="Pågående utdanning"
									wrapperSize="inherit"
									disabled={formMethods.getValues(`${utdanningPath}.endDate`)}
									checkboxMargin
								/>
							</div>
							<EraseFillButtons
								formMethods={formMethods}
								path={utdanningPath}
								initialErase={initialUtdanning}
								initialFill={initialUtdanningVerdier}
							/>
						</>
					)
				}}
			</FormDollyFieldArray>
		</Vis>
	)
}
