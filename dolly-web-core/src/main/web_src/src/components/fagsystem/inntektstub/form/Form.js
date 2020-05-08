import React from 'react'
import { Vis } from '~/components/bestillingsveileder/VisAttributt'
import Panel from '~/components/ui/panel/Panel'
import { panelError, erForste } from '~/components/ui/form/formUtils'
import { validation } from './validation'
import Inntektsinformasjon from './partials/inntektsinformasjon'

const inntektstubAttributt = 'inntektstub'

export const InntektstubForm = ({ formikBag }) => (
	<Vis attributt={inntektstubAttributt}>
		<Panel
			heading="A-ordningen (Inntektskomponenten)"
			hasErrors={panelError(formikBag, inntektstubAttributt)}
			iconType="inntektstub"
			startOpen={() => erForste(formikBag.values, [inntektstubAttributt])}
		>
			<Inntektsinformasjon formikBag={formikBag} />
		</Panel>
	</Vis>
)

InntektstubForm.validation = validation
