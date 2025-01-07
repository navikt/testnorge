import { FormDollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import {
	initialAndreGodkjenninger,
	initialAndreGodkjenningerVerdier,
} from '@/components/fagsystem/arbeidsplassen/form/initialValues'
import { FormSelect } from '@/components/ui/form/inputs/select/Select'
import { FormTextInput } from '@/components/ui/form/inputs/textInput/TextInput'
import { FormDatepicker } from '@/components/ui/form/inputs/datepicker/Datepicker'
import * as React from 'react'
import { SelectOptionsManager as Options } from '@/service/SelectOptions'
import { Vis } from '@/components/bestillingsveileder/VisAttributt'
import { EraseFillButtons } from '@/components/fagsystem/arbeidsplassen/form/partials/EraseFillButtons'
import * as _ from 'lodash-es'

export const AndreGodkjenningerForm = ({ formMethods }) => {
	const andreGodkjenningerListePath = 'arbeidsplassenCV.andreGodkjenninger'

	return (
		<Vis attributt={andreGodkjenningerListePath}>
			<FormDollyFieldArray
				name={andreGodkjenningerListePath}
				header="Andre godkjenninger"
				newEntry={initialAndreGodkjenningerVerdier}
				buttonText="Annen godkjenning"
				nested
			>
				{(annenGodkjenningPath, idx) => (
					<>
						<div key={idx} className="flexbox--flex-wrap">
							<FormSelect
								name={`${annenGodkjenningPath}.certificateName`}
								label="Annen godkjenning"
								options={Options('annenGodkjenning')}
								size="xxlarge"
								isClearable={false}
							/>
							<FormTextInput
								name={`${annenGodkjenningPath}.issuer`}
								label="Utsteder"
								size="large"
								key={`issuer_${_.get(formMethods.getValues(), `${annenGodkjenningPath}.issuer`)}`}
							/>
							<FormDatepicker name={`${annenGodkjenningPath}.fromDate`} label="Fullført" />
							<FormDatepicker name={`${annenGodkjenningPath}.toDate`} label="Utløper" />
						</div>
						<EraseFillButtons
							formMethods={formMethods}
							path={annenGodkjenningPath}
							initialErase={initialAndreGodkjenninger}
							initialFill={initialAndreGodkjenningerVerdier}
						/>
					</>
				)}
			</FormDollyFieldArray>
		</Vis>
	)
}
