import React from 'react'
import { Vis } from '~/components/bestillingsveileder/VisAttributt'
import { FormikTextInput } from '~/components/ui/form/inputs/textInput/TextInput'
import { InntektsinformasjonForm } from './partials/inntektsinformasjonForm'

export const InntektstubForm = () => (
	<Vis attributt="inntektstub">
		{/* // <Panel> */}
		<div className="flexbox--flex-wrap">
			<FormikTextInput
				visHvisAvhuket={false}
				name="antallMaaneder"
				label="Antall måneder"
				type="number"
				fastfield={false}
			/>
			<FormikTextInput
				visHvisAvhuket={false}
				name="prosentOekningPerAaar	"
				label="Prosentøkning per år"
				type="number"
				fastfield={false}
			/>

			{/* Array of... */}
			<InntektsinformasjonForm />
		</div>
	</Vis>
)
