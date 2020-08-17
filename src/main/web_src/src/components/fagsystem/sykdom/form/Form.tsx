import React from 'react'
import { Vis } from '~/components/bestillingsveileder/VisAttributt'
import Panel from '~/components/ui/panel/Panel'
import { panelError } from '~/components/ui/form/formUtils'
import { erForste } from '~/components/ui/form/formUtils'
import { SykemeldingForm } from './partials/Sykemelding'
import { validation } from './validation'
import { AlertStripeInfo } from 'nav-frontend-alertstriper'

const sykdomAttributt = 'sykemelding'

export const SykdomForm = ({ formikBag }) => (
	<Vis attributt={sykdomAttributt}>
		<Panel
			heading="Sykemelding"
			hasErrors={panelError(formikBag, sykdomAttributt)}
			iconType="sykdom"
			startOpen={() => erForste(formikBag.values, sykdomAttributt)}
		>
			{!formikBag.values.hasOwnProperty('aareg') && (
				<>
					<AlertStripeInfo>
						{
							'Personen må ha et arbeidsforhold knyttet til den samme virksomheten som du velger i sykemeldingen. Det kan du legge til ved å gå tilbake til forrige side og huke av for Arbeidsforhold (Aareg).'
						}
					</AlertStripeInfo>
					<br></br>
				</>
			)}
			<SykemeldingForm formikBag={formikBag} />
		</Panel>
	</Vis>
)

SykdomForm.validation = validation
