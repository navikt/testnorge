import { Vis } from '@/components/bestillingsveileder/VisAttributt'
import Panel from '@/components/ui/panel/Panel'
import { erForsteEllerTest, panelError } from '@/components/ui/form/formUtils'
import { validation } from './validation'
import React from 'react'
import { useFormContext } from 'react-hook-form'
import { DetaljertSykemelding } from '@/components/fagsystem/sykdom/form/partials/DetaljertSykemelding'

export const sykdomAttributt = 'sykemelding'

export const SykdomForm = () => {
	const formMethods = useFormContext()
	return (
		// @ts-ignore
		<Vis attributt={sykdomAttributt}>
			<Panel
				heading="Sykemelding"
				hasErrors={panelError(sykdomAttributt)}
				iconType="sykdom"
				startOpen={erForsteEllerTest(formMethods.getValues(), [sykdomAttributt])}
			>
				<DetaljertSykemelding formMethods={formMethods} />
			</Panel>
		</Vis>
	)
}

SykdomForm.validation = validation
