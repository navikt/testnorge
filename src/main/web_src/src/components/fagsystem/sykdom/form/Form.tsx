import React from 'react'
import { Vis } from '~/components/bestillingsveileder/VisAttributt'
import Panel from '~/components/ui/panel/Panel'
import { panelError } from '~/components/ui/form/formUtils'
import { erForste } from '~/components/ui/form/formUtils'
import { SykemeldingForm } from './partials/Sykemelding'
import { validation } from './validation'

const sykdomAttributt = 'sykemelding'

export const SykdomForm = ({ formikBag }) => (
	<Vis attributt={sykdomAttributt}>
		<Panel
			heading="Sykemelding"
			hasErrors={panelError(formikBag, sykdomAttributt)}
			iconType="sykdom"
			startOpen={() => erForste(formikBag.values, sykdomAttributt)}
		>
			<SykemeldingForm formikBag={formikBag} />
		</Panel>
	</Vis>
)

SykdomForm.validation = validation
