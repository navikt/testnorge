import React from 'react'
import { Vis } from '~/components/bestillingsveileder/VisAttributt'
import Panel from '~/components/ui/panel/Panel'
import { erForsteEllerTest, panelError } from '~/components/ui/form/formUtils'
import { validation } from './validation'
import { ArbeidsforholdToggle } from './partials/arbeidsforholdToggle'
import { useFormikContext } from 'formik'

export const aaregAttributt = 'aareg'

export const AaregForm = () => {
	const formikBag = useFormikContext()
	return (
		<Vis attributt={aaregAttributt}>
			<Panel
				heading="Arbeidsforhold (Aareg)"
				hasErrors={panelError(formikBag, aaregAttributt)}
				iconType="arbeid"
				startOpen={erForsteEllerTest(formikBag.values, [aaregAttributt])}
			>
				<ArbeidsforholdToggle />
			</Panel>
		</Vis>
	)
}

AaregForm.validation = validation
