import React from 'react'
import { Vis } from '~/components/bestillingsveileder/VisAttributt'
import Panel from '~/components/ui/panel/Panel'
import { panelError, erForste } from '~/components/ui/form/formUtils'
import { validation } from './validation'
// import { FormikTextInput } from '~/components/ui/form/inputs/textInput/TextInput'
import { InntektsinformasjonForm } from './partials/inntektsinformasjonForm'

const inntektstubAttributt = 'inntektstub'

export const InntektstubForm = ({ formikBag }) => (
	<Vis attributt={inntektstubAttributt}>
		<Panel
			heading="Inntektskomponenten (A-ordningen)"
			hasErrors={panelError(formikBag, inntektstubAttributt)}
			iconType="inntektstub"
			startOpen={() => erForste(formikBag.values, [inntektstubAttributt])}
		>
			<div className="flexbox--flex-wrap">
				{/* <FormikTextInput
					name="inntektstub.prosentOekningPerAaar"
					label="Prosentøkning per år"
					type="number"
				/> */}

				<InntektsinformasjonForm formikBag={formikBag} />
			</div>
		</Panel>
	</Vis>
)

InntektstubForm.validation = validation
