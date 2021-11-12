import React from 'react'
import { Vis } from '~/components/bestillingsveileder/VisAttributt'
import Panel from '~/components/ui/panel/Panel'
import { panelError, erForste } from '~/components/ui/form/formUtils'
import { validation } from './validation'
import { ArbeidsforholdToggle } from './partials/arbeidsforholdToggle'

const aaregAttributt = 'aareg'

export const AaregForm = ({ formikBag }) => (
	<Vis attributt={aaregAttributt}>
		<Panel
			heading="Arbeidsforhold (Aareg)"
			hasErrors={panelError(formikBag, aaregAttributt)}
			iconType="arbeid"
			startOpen={() => erForste(formikBag.values, [aaregAttributt])}
		>
			<ArbeidsforholdToggle formikBag={formikBag} />
		</Panel>
	</Vis>
)

AaregForm.validation = validation
