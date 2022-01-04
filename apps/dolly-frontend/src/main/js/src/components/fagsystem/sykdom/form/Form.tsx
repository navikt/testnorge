import React from 'react'
import { Vis } from '~/components/bestillingsveileder/VisAttributt'
import Panel from '~/components/ui/panel/Panel'
import { panelError, erForste } from '~/components/ui/form/formUtils'
import { Sykemelding } from './partials/Sykemelding'
import { validation } from './validation'
import { AlertStripeInfo } from 'nav-frontend-alertstriper'
import { FormikProps } from 'formik'
import { AlertAaregRequired } from '~/components/ui/brukerAlert/AlertAaregRequired'

interface SykdomFormProps {
	formikBag: FormikProps<{}>
}

const sykdomAttributt = 'sykemelding'

export const SykdomForm = ({ formikBag }: SykdomFormProps) => (
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
				<AlertAaregRequired meldingSkjema="Sykemeldingen" />
			)}
			{formikBag.values.sykemelding != null &&
				formikBag.values.sykemelding.hasOwnProperty('syntSykemelding') && (
					<AlertStripeInfo style={{ marginBottom: '20px' }}>
						Syntetisk sykemelding behandler en stor mengde data for å opprette realistiske
						sykemeldinger og kan derfor medføre litt lenger bestillingstid.
					</AlertStripeInfo>
				)}
			<Sykemelding formikBag={formikBag} />
		</Panel>
	</Vis>
)

SykdomForm.validation = validation
