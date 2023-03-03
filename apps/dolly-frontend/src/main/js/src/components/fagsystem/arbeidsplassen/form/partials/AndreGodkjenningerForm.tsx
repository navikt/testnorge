import { FormikDollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import { initialAndreGodkjenninger } from '@/components/fagsystem/arbeidsplassen/form/initialValues'
import { FormikSelect } from '@/components/ui/form/inputs/select/Select'
import { ArbeidKodeverk } from '@/config/kodeverk'
import { FormikTextInput } from '@/components/ui/form/inputs/textInput/TextInput'
import { FormikDatepicker } from '@/components/ui/form/inputs/datepicker/Datepicker'
import * as React from 'react'

export const AndreGodkjenningerForm = ({ formikBag }) => {
	return (
		<div style={{ width: '100%' }}>
			<hr />
			<FormikDollyFieldArray
				name="arbeidsplassenCV.andreGodkjenninger"
				header="Andre godkjenninger"
				// hjelpetekst={infotekst}
				newEntry={initialAndreGodkjenninger}
				buttonText="Annen godkjenning"
				nested
			>
				{(annenGodkjenningPath, idx) => (
					<div key={idx} className="flexbox--flex-wrap">
						{/*// TODO: Må ha riktige data til lista. Sette certificateName*/}
						<FormikSelect
							name={`${annenGodkjenningPath}.certificateName`}
							label="Annen godkjenning"
							kodeverk={ArbeidKodeverk.Yrker}
							size="xxlarge"
							isClearable={false}
						/>
						<FormikTextInput
							name={`${annenGodkjenningPath}.issuer`}
							label="Utsteder"
							size="large"
						/>
						<FormikDatepicker name={`${annenGodkjenningPath}.fromDate`} label="Fullført" />
						<FormikDatepicker name={`${annenGodkjenningPath}.toDate`} label="Utløper" />
					</div>
				)}
			</FormikDollyFieldArray>
		</div>
	)
}
