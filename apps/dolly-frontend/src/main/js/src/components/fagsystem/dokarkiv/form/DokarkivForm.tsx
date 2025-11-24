import React from 'react'
import { Vis } from '@/components/bestillingsveileder/VisAttributt'
import { Kategori } from '@/components/ui/form/kategori/Kategori'
import Panel from '@/components/ui/panel/Panel'
import { erForsteEllerTest, panelError } from '@/components/ui/form/formUtils'
import { useFormContext } from 'react-hook-form'
import { FormDollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import {
	initialDigitalInnsending,
	initialDokarkiv,
} from '@/components/fagsystem/dokarkiv/form/initialValues'
import { Dokument } from '@/components/fagsystem/dokarkiv/form/partials/Dokument'

export type Vedlegg = {
	id: string
	name: string
	dokNavn: string
	mimetype: string
	size: number
	content: {
		base64: string
	}
}

const dokarkivAttributt = 'dokarkiv'

const DokarkivForm = () => {
	const formMethods = useFormContext()
	const digitalInnsending = formMethods.watch('dokarkiv[0].avsenderMottaker')

	return (
		// @ts-ignore
		<Vis attributt={dokarkivAttributt}>
			<Panel
				heading="Dokumenter (Joark)"
				hasErrors={panelError(dokarkivAttributt)}
				iconType="dokarkiv"
				// @ts-ignore
				startOpen={erForsteEllerTest(formMethods.getValues(), [dokarkivAttributt])}
			>
				<Kategori
					title={`Oppretting av ${digitalInnsending ? 'digitalt' : 'skannet '} dokument`}
					vis={dokarkivAttributt}
				>
					<FormDollyFieldArray
						name="dokarkiv"
						header="Dokument"
						newEntry={digitalInnsending ? initialDigitalInnsending : initialDokarkiv}
						canBeEmpty={false}
					>
						{(path: string) => (
							<Dokument
								path={path}
								formMethods={formMethods}
								digitalInnsending={digitalInnsending}
							/>
						)}
					</FormDollyFieldArray>
				</Kategori>
			</Panel>
		</Vis>
	)
}

export default DokarkivForm
