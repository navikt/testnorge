import React from 'react'
import * as Yup from 'yup'
import { Vis } from '~/components/bestillingsveileder/VisAttributt'
import { panelError } from '~/components/ui/form/formUtils'
import { erForste } from '~/components/ui/form/formUtils'
import Panel from '~/components/ui/panel/Panel'
import { FormikTextInput } from '~/components/ui/form/inputs/textInput/TextInput'
import { FormikDatepicker } from '~/components/ui/form/inputs/datepicker/Datepicker'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { FormikDollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'
import { requiredDate, requiredString, requiredNumber, messages } from '~/utils/YupValidations'
import { SelectOptionsOppslag } from '~/service/SelectOptionsOppslag'

const initialValues = {
	dato: '',
	virksomhetsnummer: '',
	beloep: ''
}
const inntektsmeldingAttributt = 'inntektsmelding'
const informasjonstekst = 'Personen må ha et arbeidsforhold knyttet til den valgte virksomheten.'

export const InntektsmeldingForm = ({ formikBag }) => {
	const orgInfo = SelectOptionsOppslag('orgnr')
	const options = SelectOptionsOppslag.formatOptions(orgInfo)

	return (
		<Vis attributt={inntektsmeldingAttributt}>
			<Panel
				heading="Inntektsmelding (fra Altinn)"
				hasErrors={panelError(formikBag, inntektsmeldingAttributt)}
				iconType="inntektsmelding"
				informasjonstekst={informasjonstekst}
				startOpen={() => erForste(formikBag.values, [inntektsmeldingAttributt])}
			>
				<FormikDollyFieldArray
					name="inntektsmelding.inntekter"
					header="Inntekt"
					newEntry={initialValues}
				>
					{(path, idx) => (
						<>
							<FormikTextInput name={`${path}.beloep`} label="Beløp" type="number" />
							<FormikDatepicker name={`${path}.dato`} label="Innsendingstidspunkt" type="month" />
							<FormikSelect
								name={`${path}.virksomhetsnummer`}
								label="Virksomhet (orgnr/id)"
								options={options}
								isClearable={false}
								size="xlarge"
								fastfield={false}
							/>
						</>
					)}
				</FormikDollyFieldArray>
			</Panel>
		</Vis>
	)
}

InntektsmeldingForm.validation = {
	inntektsmelding: Yup.object({
		inntekter: Yup.array().of(
			Yup.object({
				dato: requiredDate,
				virksomhetsnummer: requiredString,
				beloep: requiredNumber.typeError(messages.required)
			})
		)
	})
}
