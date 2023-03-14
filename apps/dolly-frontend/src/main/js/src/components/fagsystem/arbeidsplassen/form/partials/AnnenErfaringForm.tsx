import { FormikDollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import {
	initialAnnenErfaring,
	initialAnnenErfaringVerdier,
} from '@/components/fagsystem/arbeidsplassen/form/initialValues'
import { DollyTextInput, FormikTextInput } from '@/components/ui/form/inputs/textInput/TextInput'
import { Fritekstfelt } from '@/components/fagsystem/arbeidsplassen/form/styles'
import _get from 'lodash/get'
import { FormikDatepicker } from '@/components/ui/form/inputs/datepicker/Datepicker'
import { FormikCheckbox } from '@/components/ui/form/inputs/checbox/Checkbox'
import * as React from 'react'
import { Vis } from '@/components/bestillingsveileder/VisAttributt'
import { EraseFillButtons } from '@/components/fagsystem/arbeidsplassen/form/partials/EraseFillButtons'

export const AnnenErfaringForm = ({ formikBag }) => {
	const annenErfaringListePath = 'arbeidsplassenCV.annenErfaring'

	return (
		<Vis attributt={annenErfaringListePath}>
			<FormikDollyFieldArray
				name={annenErfaringListePath}
				header="Andre erfaringer"
				newEntry={initialAnnenErfaringVerdier}
				buttonText="Annen erfaring"
				nested
			>
				{(annenErfaringPath, idx) => (
					<>
						<div key={idx} className="flexbox--flex-wrap">
							<FormikTextInput
								name={`${annenErfaringPath}.role`}
								label="Rolle"
								size="xlarge"
								key={`role_${_get(formikBag.values, `${annenErfaringPath}.role`)}`}
							/>
							<Fritekstfelt
								label="Beskrivelse"
								placeholder="Beskrivelse av annen erfaring"
								value={_get(formikBag.values, `${annenErfaringPath}.description`)}
								onChange={(beskrivelse) =>
									formikBag.setFieldValue(
										`${annenErfaringPath}.description`,
										beskrivelse?.target?.value
									)
								}
								size="small"
							/>
							<FormikDatepicker name={`${annenErfaringPath}.fromDate`} label="Startdato" />
							<FormikDatepicker
								name={`${annenErfaringPath}.toDate`}
								label="Sluttdato"
								disabled={_get(formikBag.values, `${annenErfaringPath}.ongoing`)}
								fastfield={false}
							/>
							<FormikCheckbox
								name={`${annenErfaringPath}.ongoing`}
								label="Pågående"
								wrapperSize="inherit"
								isDisabled={_get(formikBag.values, `${annenErfaringPath}.toDate`)}
								checkboxMargin
							/>
						</div>
						<EraseFillButtons
							formikBag={formikBag}
							path={annenErfaringPath}
							initialErase={initialAnnenErfaring}
							initialFill={initialAnnenErfaringVerdier}
						/>
					</>
				)}
			</FormikDollyFieldArray>
		</Vis>
	)
}
