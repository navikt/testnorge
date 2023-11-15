import { FormikDollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import {
	initialUtdanning,
	initialUtdanningVerdier,
} from '@/components/fagsystem/arbeidsplassen/form/initialValues'
import { FormikTextInput } from '@/components/ui/form/inputs/textInput/TextInput'
import { FormikCheckbox } from '@/components/ui/form/inputs/checbox/Checkbox'
import { FormikDatepicker } from '@/components/ui/form/inputs/datepicker/Datepicker'
import * as React from 'react'
import { Fritekstfelt } from '@/components/fagsystem/arbeidsplassen/form/styles'
import _get from 'lodash/get'
import { FormikSelect } from '@/components/ui/form/inputs/select/Select'
import { SelectOptionsManager as Options } from '@/service/SelectOptions'
import { Vis } from '@/components/bestillingsveileder/VisAttributt'
import { EraseFillButtons } from '@/components/fagsystem/arbeidsplassen/form/partials/EraseFillButtons'

export const UtdanningForm = ({ formMethods }) => {
	const utdanningListePath = 'arbeidsplassenCV.utdanning'

	return (
		<Vis attributt={utdanningListePath}>
			<FormikDollyFieldArray
				name={utdanningListePath}
				header="Utdanninger"
				newEntry={initialUtdanningVerdier}
				buttonText="Utdanning"
				nested
			>
				{(utdanningPath, idx) => (
					<>
						<div key={idx} className="flexbox--flex-wrap">
							<FormikSelect
								name={`${utdanningPath}.nuskode`}
								label="Utdanningsnivå"
								options={Options('nusKoder')}
								size="large"
								isClearable={false}
							/>
							<FormikTextInput
								name={`${utdanningPath}.field`}
								label="Grad og utdanningsretning"
								size="medium"
								key={`field_${_get(formikBag.values, `${utdanningPath}.field`)}`}
							/>
							<FormikTextInput
								name={`${utdanningPath}.institution`}
								label="Skole/studiested"
								size="medium"
								key={`institution_${_get(formikBag.values, `${utdanningPath}.institution`)}`}
							/>
							<Fritekstfelt
								label="Beskrivelse"
								placeholder="Beskrivelse av utdanning"
								defaultValue={_get(formikBag.values, `${utdanningPath}.description`)}
								onBlur={(beskrivelse) =>
									formikBag.setFieldValue(
										`${utdanningPath}.description`,
										beskrivelse?.target?.value,
									)
								}
								size="small"
								key={`description_${_get(formikBag.values, `${utdanningPath}.description`)}`}
								resize
							/>
							<FormikDatepicker name={`${utdanningPath}.startDate`} label="Startdato" />
							<FormikDatepicker
								name={`${utdanningPath}.endDate`}
								label="Sluttdato"
								disabled={_get(formikBag.values, `${utdanningPath}.ongoing`)}
								fastfield={false}
							/>
							<FormikCheckbox
								id={`${utdanningPath}.ongoing`}
								name={`${utdanningPath}.ongoing`}
								label="Pågående utdanning"
								wrapperSize="inherit"
								isDisabled={_get(formikBag.values, `${utdanningPath}.endDate`)}
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
				)}
			</FormikDollyFieldArray>
		</Vis>
	)
}
