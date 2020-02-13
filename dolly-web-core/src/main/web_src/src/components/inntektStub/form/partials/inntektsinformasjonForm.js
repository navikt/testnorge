import React from 'react'
import { FormikDatepicker } from '~/components/ui/form/inputs/datepicker/Datepicker'
import { FormikTextInput } from '~/components/ui/form/inputs/textInput/TextInput'
import { InntektForm } from './inntektForm'

export const InntektsinformasjonForm = () => {
	return (
		<React.Fragment>
			{/* <Kategori></Kategori> */}
			<div className="flexbox--flex-wrap">
				{/* måå kanskje ha 2 felter? */}
				<FormikDatepicker name="aarMaaned" label="År/måned" />
				<FormikTextInput
					name="opplysningspliktig"
					label="Opplysningspliktig (orgnr/ident)"
					fastfield={false}
				/>
				<FormikTextInput name="virksomhet" label="Virksomhet (orgnr/ident)" fastfield={false} />
			</div>
			{/* array of... */}
			<InntektForm />
		</React.Fragment>

		// array of...
		// <Fradrag />

		// array of...
		// <Forskuddstrekk />

		// array of...
		// <Arbeidsforhold />
	)
}
