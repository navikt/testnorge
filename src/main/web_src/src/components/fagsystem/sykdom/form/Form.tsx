import React from 'react'
import { Vis } from '~/components/bestillingsveileder/VisAttributt'
import Panel from '~/components/ui/panel/Panel'
import { panelError } from '~/components/ui/form/formUtils'
import { erForste } from '~/components/ui/form/formUtils'
import { Sykemelding } from './partials/Sykemelding'
import { validation } from './validation'
import { AlertStripeInfo } from 'nav-frontend-alertstriper'
import { FormikProps } from 'formik'

interface SykdomForm {
	formikBag: FormikProps<{}>
}

const sykdomAttributt = 'sykemelding'

export const SykdomForm = ({ formikBag }: SykdomForm) => (
	// @ts-ignore
	<Vis attributt={sykdomAttributt}>
		<Panel
			heading="Sykemelding"
			hasErrors={panelError(formikBag, sykdomAttributt)}
			iconType="sykdom"
			// @ts-ignore
			startOpen={() => erForste(formikBag.values, sykdomAttributt)}
			informasjonstekst="Om du velger å generere en sykemelding automatisk, vil du få en syntetisk sykemelding hvor alle verdier blir satt for deg."
		>
			{!formikBag.values.hasOwnProperty('aareg') && (
				<>
					{/* 
					// @ts-ignore */}
					<AlertStripeInfo style={{ marginBottom: '20px' }}>
						Personen må ha et arbeidsforhold knyttet til den samme virksomheten som du velger i
						sykemeldingen. Det kan du legge til ved å gå tilbake til forrige side og huke av for
						Arbeidsforhold (Aareg).
					</AlertStripeInfo>
				</>
			)}
			<Sykemelding formikBag={formikBag} />
		</Panel>
	</Vis>
)

SykdomForm.validation = validation
