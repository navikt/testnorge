import { FormikDollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import {
	initialAnnenErfaring,
	initialAnnenErfaringVerdier,
} from '@/components/fagsystem/arbeidsplassen/form/initialValues'
import { FormikTextInput } from '@/components/ui/form/inputs/textInput/TextInput'
import { Fritekstfelt } from '@/components/fagsystem/arbeidsplassen/form/styles'
import _get from 'lodash/get'
import { FormikDatepicker } from '@/components/ui/form/inputs/datepicker/Datepicker'
import { FormikCheckbox } from '@/components/ui/form/inputs/checbox/Checkbox'
import * as React from 'react'
import { Vis } from '@/components/bestillingsveileder/VisAttributt'
import { EraseFillButtons } from '@/components/fagsystem/arbeidsplassen/form/partials/EraseFillButtons'

export const AnnenErfaringForm = ({ formMethods }) => {
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
								key={`role_${_get(formMethods.getValues(), `${annenErfaringPath}.role`)}`}
							/>
							<Fritekstfelt
								label="Beskrivelse"
								placeholder="Beskrivelse av annen erfaring"
								defaultValue={_get(formMethods.getValues(), `${annenErfaringPath}.description`)}
								onBlur={(beskrivelse) =>
									formMethods.setValue(
										`${annenErfaringPath}.description`,
										beskrivelse?.target?.value,
									)
								}
								size="small"
								key={`description_${_get(
									formMethods.getValues(),
									`${annenErfaringPath}.description`,
								)}`}
								resize
							/>
							<FormikDatepicker name={`${annenErfaringPath}.fromDate`} label="Startdato" />
							<FormikDatepicker
								name={`${annenErfaringPath}.toDate`}
								label="Sluttdato"
								disabled={_get(formMethods.getValues(), `${annenErfaringPath}.ongoing`)}
							/>
							<FormikCheckbox
								id={`${annenErfaringPath}.ongoing`}
								name={`${annenErfaringPath}.ongoing`}
								label="Pågående"
								wrapperSize="inherit"
								isDisabled={_get(formMethods.getValues(), `${annenErfaringPath}.toDate`)}
								checkboxMargin
							/>
						</div>
						<EraseFillButtons
							formMethods={formMethods}
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
