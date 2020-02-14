import React from 'react'
import { Vis } from '~/components/bestillingsveileder/VisAttributt'
import Panel from '~/components/ui/panel/Panel'
import { panelError, erForste } from '~/components/ui/form/formUtils'
import { FormikTextInput } from '~/components/ui/form/inputs/textInput/TextInput'
import { InntektsinformasjonForm } from './partials/inntektsinformasjonForm'

const inntektstubAttributt = 'inntektstub'

export const InntektstubForm = ({ formikBag }) => (
	<Vis attributt={inntektstubAttributt}>
		{console.log('formikBag :', formikBag)}
		<Panel
			heading="Inntektskomponenten"
			hasErrors={panelError(formikBag, inntektstubAttributt)}
			iconType="sigrun"
			startOpen={() => erForste(formikBag.values, [inntektstubAttributt])}
		>
			<div className="flexbox--flex-wrap">
				<FormikTextInput
					// visHvisAvhuket={false}
					name="inntektstub.antallMaaneder"
					label="Antall måneder"
					type="number"
					// fastfield={false}
				/>
				<FormikTextInput
					// visHvisAvhuket={false}
					name="inntektstub.prosentOekningPerAaar"
					label="Prosentøkning per år"
					type="number"
					// fastfield={false}
				/>

				{/* Array of... */}
				<InntektsinformasjonForm formikBag={formikBag} />
			</div>
		</Panel>
	</Vis>
)
