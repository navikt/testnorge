import { FormikDollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import {
	initialOffentligeGodkjenninger,
	initialOffentligeGodkjenningerVerdier,
} from '@/components/fagsystem/arbeidsplassen/form/initialValues'
import { FormikSelect } from '@/components/ui/form/inputs/select/Select'
import { SelectOptionsManager as Options } from '@/service/SelectOptions'
import { DollyTextInput, FormikTextInput } from '@/components/ui/form/inputs/textInput/TextInput'
import { FormikDatepicker } from '@/components/ui/form/inputs/datepicker/Datepicker'
import * as React from 'react'
import { Vis } from '@/components/bestillingsveileder/VisAttributt'
import { EraseFillButtons } from '@/components/fagsystem/arbeidsplassen/form/partials/EraseFillButtons'
import _get from 'lodash/get'

export const OffentligeGodkjenningerForm = ({ formikBag }) => {
	const offentligeGodkjenningerListePath = 'arbeidsplassenCV.offentligeGodkjenninger'

	return (
		<Vis attributt={offentligeGodkjenningerListePath}>
			<FormikDollyFieldArray
				name={offentligeGodkjenningerListePath}
				header="Offentlige godkjenninger"
				newEntry={initialOffentligeGodkjenningerVerdier}
				buttonText="Offentlig godkjenning"
				nested
			>
				{(offentligGodkjenningPath, idx) => (
					<>
						<div key={idx} className="flexbox--flex-wrap">
							<FormikSelect
								name={`${offentligGodkjenningPath}.title`}
								label="Offentlig godkjenning"
								options={Options('offentligGodkjenning')}
								size="xxlarge"
								isClearable={false}
							/>
							<DollyTextInput
								name={`${offentligGodkjenningPath}.issuer`}
								label="Utsteder"
								size="large"
								value={_get(formikBag.values, `${offentligGodkjenningPath}.issuer`)}
								onChange={(i) =>
									formikBag.setFieldValue(`${offentligGodkjenningPath}.issuer`, i.target.value)
								}
							/>
							<FormikDatepicker name={`${offentligGodkjenningPath}.fromDate`} label="Fullført" />
							<FormikDatepicker name={`${offentligGodkjenningPath}.toDate`} label="Utløper" />
						</div>
						<EraseFillButtons
							formikBag={formikBag}
							path={offentligGodkjenningPath}
							initialErase={initialOffentligeGodkjenninger}
							initialFill={initialOffentligeGodkjenningerVerdier}
						/>
					</>
				)}
			</FormikDollyFieldArray>
		</Vis>
	)
}
