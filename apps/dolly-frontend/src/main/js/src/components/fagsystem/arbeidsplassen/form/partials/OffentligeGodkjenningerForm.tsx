import { FormDollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import {
	initialOffentligeGodkjenninger,
	initialOffentligeGodkjenningerVerdier,
} from '@/components/fagsystem/arbeidsplassen/form/initialValues'
import { FormSelect } from '@/components/ui/form/inputs/select/Select'
import { SelectOptionsManager as Options } from '@/service/SelectOptions'
import { FormTextInput } from '@/components/ui/form/inputs/textInput/TextInput'
import { FormDatepicker } from '@/components/ui/form/inputs/datepicker/Datepicker'
import * as React from 'react'
import { Vis } from '@/components/bestillingsveileder/VisAttributt'
import { EraseFillButtons } from '@/components/fagsystem/arbeidsplassen/form/partials/EraseFillButtons'
import { useFormContext } from 'react-hook-form'

export const OffentligeGodkjenningerForm = () => {
	const formMethods = useFormContext()
	const offentligeGodkjenningerListePath = 'arbeidsplassenCV.offentligeGodkjenninger'

	return (
		<Vis attributt={offentligeGodkjenningerListePath}>
			<FormDollyFieldArray
				name={offentligeGodkjenningerListePath}
				header="Offentlige godkjenninger"
				newEntry={initialOffentligeGodkjenningerVerdier}
				buttonText="Offentlig godkjenning"
				nested
			>
				{(offentligGodkjenningPath, idx) => (
					<>
						<div key={idx} className="flexbox--flex-wrap">
							<FormSelect
								name={`${offentligGodkjenningPath}.title`}
								label="Offentlig godkjenning"
								options={Options('offentligGodkjenning')}
								size="xxlarge"
								isClearable={false}
							/>
							<FormTextInput
								name={`${offentligGodkjenningPath}.issuer`}
								label="Utsteder"
								size="large"
								key={`issuer_${formMethods.getValues(`${offentligGodkjenningPath}.issuer`)}`}
							/>
							<FormDatepicker name={`${offentligGodkjenningPath}.fromDate`} label="Fullført" />
							<FormDatepicker name={`${offentligGodkjenningPath}.toDate`} label="Utløper" />
						</div>
						<EraseFillButtons
							formMethods={formMethods}
							path={offentligGodkjenningPath}
							initialErase={initialOffentligeGodkjenninger}
							initialFill={initialOffentligeGodkjenningerVerdier}
						/>
					</>
				)}
			</FormDollyFieldArray>
		</Vis>
	)
}
