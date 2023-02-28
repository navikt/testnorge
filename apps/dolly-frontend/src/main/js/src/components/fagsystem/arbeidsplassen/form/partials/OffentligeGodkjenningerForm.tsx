import { FormikDollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import { initialOffentligeGodkjenninger } from '@/components/fagsystem/arbeidsplassen/form/initialValues'
import { FormikSelect } from '@/components/ui/form/inputs/select/Select'
import { ArbeidKodeverk } from '@/config/kodeverk'
import { FormikTextInput } from '@/components/ui/form/inputs/textInput/TextInput'
import { FormikDatepicker } from '@/components/ui/form/inputs/datepicker/Datepicker'
import * as React from 'react'

export const OffentligeGodkjenningerForm = ({ formikBag }) => {
	return (
		<FormikDollyFieldArray
			name="arbeidsplassenCV.offentligeGodkjenninger"
			header="Offentlige godkjenninger"
			// hjelpetekst={infotekst}
			newEntry={initialOffentligeGodkjenninger}
			buttonText="Offentlig godkjenning"
			nested
		>
			{(offentligGodkjenningPath, idx) => (
				<div key={idx} className="flexbox--flex-wrap">
					{/*TODO: Må kanskje sette styrkkode også. Og finn ut hvilke data som er riktig å bruke i lista*/}
					<FormikSelect
						name={`${offentligGodkjenningPath}.title`}
						label="Offentlig godkjenning"
						kodeverk={ArbeidKodeverk.Yrker}
						size="xxlarge"
						isClearable={false}
					/>
					<FormikTextInput
						name={`${offentligGodkjenningPath}.issuer`}
						label="Utsteder"
						size="large"
					/>
					<FormikDatepicker name={`${offentligGodkjenningPath}.fromDate`} label="Fullført" />
					<FormikDatepicker name={`${offentligGodkjenningPath}.toDate`} label="Utløper" />
				</div>
			)}
		</FormikDollyFieldArray>
	)
}
