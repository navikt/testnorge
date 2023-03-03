import { FormikDollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import { initialUtdanning } from '@/components/fagsystem/arbeidsplassen/form/initialValues'
import { FormikTextInput } from '@/components/ui/form/inputs/textInput/TextInput'
import { FormikCheckbox } from '@/components/ui/form/inputs/checbox/Checkbox'
import { FormikDatepicker } from '@/components/ui/form/inputs/datepicker/Datepicker'
import * as React from 'react'
import { Fritekstfelt } from '@/components/fagsystem/arbeidsplassen/form/styles'
import _get from 'lodash/get'
import { FormikSelect } from '@/components/ui/form/inputs/select/Select'
import { SelectOptionsManager as Options } from '@/service/SelectOptions'

export const UtdanningForm = ({ formikBag }) => {
	return (
		<div style={{ width: '100%' }}>
			<hr />
			{/*<hr color="#595959" size="5px" />*/}
			<FormikDollyFieldArray
				name="arbeidsplassenCV.utdanning"
				header="Utdanninger"
				// hjelpetekst={infotekst}
				newEntry={initialUtdanning}
				buttonText="Utdanning"
				nested
			>
				{(utdanningPath, idx) => (
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
						/>
						<FormikTextInput
							name={`${utdanningPath}.institution`}
							label="Skole/studiested"
							size="medium"
						/>
						<Fritekstfelt
							label="Beskrivelse"
							placeholder="Beskrivelse av utdanning"
							value={_get(formikBag.values, `${utdanningPath}.description`)}
							onChange={(beskrivelse) =>
								formikBag.setFieldValue(`${utdanningPath}.description`, beskrivelse?.target?.value)
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
				)}
			</FormikDollyFieldArray>
		</div>
	)
}
