import { FormikDollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import {
	initialUtdanning,
	initialUtdanningVerdier,
} from '@/components/fagsystem/arbeidsplassen/form/initialValues'
import { DollyTextInput, FormikTextInput } from '@/components/ui/form/inputs/textInput/TextInput'
import { FormikCheckbox } from '@/components/ui/form/inputs/checbox/Checkbox'
import { FormikDatepicker } from '@/components/ui/form/inputs/datepicker/Datepicker'
import * as React from 'react'
import { Fritekstfelt } from '@/components/fagsystem/arbeidsplassen/form/styles'
import _get from 'lodash/get'
import { FormikSelect } from '@/components/ui/form/inputs/select/Select'
import { SelectOptionsManager as Options } from '@/service/SelectOptions'
import { Vis } from '@/components/bestillingsveileder/VisAttributt'
import { EraseFillButtons } from '@/components/fagsystem/arbeidsplassen/form/partials/EraseFillButtons'

export const UtdanningForm = ({ formikBag }) => {
	const utdanningerPath = 'arbeidsplassenCV.utdanning'

	return (
		<Vis attributt={utdanningerPath}>
			<FormikDollyFieldArray
				name={utdanningerPath}
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
							<DollyTextInput
								name={`${utdanningPath}.field`}
								label="Grad og utdanningsretning"
								size="medium"
								// defaultValue={_get(formikBag.values, `${utdanningPath}.field`)}
								value={_get(formikBag.values, `${utdanningPath}.field`)}
								onChange={(i) => formikBag.setFieldValue(`${utdanningPath}.field`, i.target.value)}
							/>
							<DollyTextInput
								name={`${utdanningPath}.institution`}
								label="Skole/studiested"
								size="medium"
								value={_get(formikBag.values, `${utdanningPath}.institution`)}
								onChange={(i) =>
									formikBag.setFieldValue(`${utdanningPath}.institution`, i.target.value)
								}
							/>
							<Fritekstfelt
								label="Beskrivelse"
								placeholder="Beskrivelse av utdanning"
								value={_get(formikBag.values, `${utdanningPath}.description`)}
								onChange={(beskrivelse) =>
									formikBag.setFieldValue(
										`${utdanningPath}.description`,
										beskrivelse?.target?.value
									)
								}
								size="small"
							/>
							<FormikDatepicker name={`${utdanningPath}.startDate`} label="Startdato" />
							<FormikDatepicker
								name={`${utdanningPath}.endDate`}
								label="Sluttdato"
								disabled={_get(formikBag.values, `${utdanningPath}.ongoing`)}
								fastfield={false}
							/>
							<FormikCheckbox
								name={`${utdanningPath}.ongoing`}
								label="Pågående utdanning"
								wrapperSize="inherit"
								isDisabled={_get(formikBag.values, `${utdanningPath}.endDate`)}
								checkboxMargin
							/>
						</div>
						<EraseFillButtons
							formikBag={formikBag}
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
