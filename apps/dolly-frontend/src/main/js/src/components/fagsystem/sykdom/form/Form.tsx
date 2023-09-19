import { Vis } from '@/components/bestillingsveileder/VisAttributt'
import Panel from '@/components/ui/panel/Panel'
import { erForsteEllerTest, panelError } from '@/components/ui/form/formUtils'
import { Sykemelding } from './partials/Sykemelding'
import { validation } from './validation'
import { FormikProps } from 'formik'
import React from 'react'
import StyledAlert from '@/components/ui/alert/StyledAlert'

interface SykdomFormProps {
	formikBag: FormikProps<{}>
}

export const sykdomAttributt = 'sykemelding'

export const SykdomForm = ({ formikBag }: SykdomFormProps) => (
	// @ts-ignore
	<Vis attributt={sykdomAttributt}>
		<Panel
			heading="Sykemelding"
			hasErrors={panelError(formikBag, sykdomAttributt)}
			iconType="sykdom"
			startOpen={erForsteEllerTest(formikBag.values, [sykdomAttributt])}
			informasjonstekst="Om du velger å generere en sykemelding automatisk, vil du få en syntetisk sykemelding hvor alle verdier blir satt for deg."
		>
			{formikBag.values.sykemelding != null &&
				formikBag.values.sykemelding.hasOwnProperty('syntSykemelding') && (
					<StyledAlert variant={'info'} size={'small'}>
						Syntetisk sykemelding behandler en stor mengde data for å opprette realistiske
						sykemeldinger og kan derfor medføre litt lenger bestillingstid.
					</StyledAlert>
				)}
			<Sykemelding formikBag={formikBag} />
		</Panel>
	</Vis>
)

SykdomForm.validation = validation
