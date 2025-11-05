import { FormDollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import {
	initialAnnenErfaring,
	initialAnnenErfaringVerdier,
} from '@/components/fagsystem/arbeidsplassen/form/initialValues'
import { DollyTextInput } from '@/components/ui/form/inputs/textInput/TextInput'
import { Fritekstfelt } from '@/components/fagsystem/arbeidsplassen/form/styles'
import * as _ from 'lodash-es'
import { FormDatepicker } from '@/components/ui/form/inputs/datepicker/Datepicker'
import { FormCheckbox } from '@/components/ui/form/inputs/checbox/Checkbox'
import * as React from 'react'
import { Vis } from '@/components/bestillingsveileder/VisAttributt'
import { EraseFillButtons } from '@/components/fagsystem/arbeidsplassen/form/partials/EraseFillButtons'
import { useFormContext } from 'react-hook-form'

export const AnnenErfaringForm = () => {
	const formMethods = useFormContext()
	const annenErfaringListePath = 'arbeidsplassenCV.annenErfaring'

	return (
		<Vis attributt={annenErfaringListePath}>
			<FormDollyFieldArray
				name={annenErfaringListePath}
				header="Andre erfaringer"
				newEntry={initialAnnenErfaringVerdier}
				buttonText="Annen erfaring"
				nested
			>
				{(annenErfaringPath, idx) => (
					<>
						<div key={idx} className="flexbox--flex-wrap">
							<DollyTextInput
								name={`${annenErfaringPath}.role`}
								label="Rolle"
								size="xlarge"
								key={`role_${formMethods.getValues(`${annenErfaringPath}.role`)}`}
							/>
							<Fritekstfelt
								label="Beskrivelse"
								placeholder="Beskrivelse av annen erfaring"
								defaultValue={_.get(formMethods.getValues(), `${annenErfaringPath}.description`)}
								onBlur={(beskrivelse) =>
									formMethods.setValue(
										`${annenErfaringPath}.description`,
										beskrivelse?.target?.value,
									)
								}
								size="small"
								key={`description_${formMethods.getValues(`${annenErfaringPath}.description`)}`}
								resize
							/>
							<FormDatepicker name={`${annenErfaringPath}.fromDate`} label="Startdato" />
							<FormDatepicker
								name={`${annenErfaringPath}.toDate`}
								label="Sluttdato"
								disabled={_.get(formMethods.getValues(), `${annenErfaringPath}.ongoing`)}
							/>
							<FormCheckbox
								id={`${annenErfaringPath}.ongoing`}
								name={`${annenErfaringPath}.ongoing`}
								label="Pågående"
								wrapperSize="inherit"
								disabled={_.get(formMethods.getValues(), `${annenErfaringPath}.toDate`)}
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
			</FormDollyFieldArray>
		</Vis>
	)
}
