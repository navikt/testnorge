import { Vis } from '@/components/bestillingsveileder/VisAttributt'
import Panel from '@/components/ui/panel/Panel'
import { erForsteEllerTest, usePanelError } from '@/components/ui/form/formUtils'
import { validation } from './validation'
import React from 'react'
import { useFormContext } from 'react-hook-form'
import { Sykemelding } from '@/components/fagsystem/sykdom/form/partials/Sykemelding'
import { sykemeldingAttributt } from './constants'

export { sykemeldingAttributt, validation as sykdomValidation }

const SykdomForm = () => {
	const formMethods = useFormContext()
	const sykemelding = formMethods.watch(sykemeldingAttributt)

	return (
		// @ts-ignore
		<Vis attributt={[sykemeldingAttributt]}>
			<Panel
				heading="Sykemelding"
				hasErrors={usePanelError([sykemeldingAttributt])}
				iconType="sykdom"
				startOpen={erForsteEllerTest(formMethods.getValues(), [sykemeldingAttributt])}
			>
				{sykemelding && <Sykemelding />}
			</Panel>
		</Vis>
	)
}

SykdomForm.validation = validation

export default SykdomForm
