import { FormikDollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import {
	initialAndreGodkjenninger,
	initialAndreGodkjenningerVerdier,
} from '@/components/fagsystem/arbeidsplassen/form/initialValues'
import { FormikSelect } from '@/components/ui/form/inputs/select/Select'
import { DollyTextInput, FormikTextInput } from '@/components/ui/form/inputs/textInput/TextInput'
import { FormikDatepicker } from '@/components/ui/form/inputs/datepicker/Datepicker'
import * as React from 'react'
import { SelectOptionsManager as Options } from '@/service/SelectOptions'
import { Vis } from '@/components/bestillingsveileder/VisAttributt'
import { EraseFillButtons } from '@/components/fagsystem/arbeidsplassen/form/partials/EraseFillButtons'
import _get from 'lodash/get'

export const AndreGodkjenningerForm = ({ formikBag }) => {
	const andreGodkjenningerListePath = 'arbeidsplassenCV.andreGodkjenninger'

	return (
		<Vis attributt={andreGodkjenningerListePath}>
			<FormikDollyFieldArray
				name={andreGodkjenningerListePath}
				header="Andre godkjenninger"
				newEntry={initialAndreGodkjenningerVerdier}
				buttonText="Annen godkjenning"
				nested
			>
				{(annenGodkjenningPath, idx) => (
					<>
						<div key={idx} className="flexbox--flex-wrap">
							<FormikSelect
								name={`${annenGodkjenningPath}.certificateName`}
								label="Annen godkjenning"
								options={Options('annenGodkjenning')}
								size="xxlarge"
								isClearable={false}
							/>
							<FormikTextInput
								name={`${annenGodkjenningPath}.issuer`}
								label="Utsteder"
								size="large"
								key={`issuer_${_get(formikBag.values, `${annenGodkjenningPath}.issuer`)}`}
							/>
							<FormikDatepicker name={`${annenGodkjenningPath}.fromDate`} label="Fullført" />
							<FormikDatepicker name={`${annenGodkjenningPath}.toDate`} label="Utløper" />
						</div>
						<EraseFillButtons
							formikBag={formikBag}
							path={annenGodkjenningPath}
							initialErase={initialAndreGodkjenninger}
							initialFill={initialAndreGodkjenningerVerdier}
						/>
					</>
				)}
			</FormikDollyFieldArray>
		</Vis>
	)
}
