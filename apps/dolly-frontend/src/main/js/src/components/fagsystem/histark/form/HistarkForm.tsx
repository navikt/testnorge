import React from 'react'
import { Vis } from '@/components/bestillingsveileder/VisAttributt'
import { Kategori } from '@/components/ui/form/kategori/Kategori'
import Panel from '@/components/ui/panel/Panel'
import { erForsteEllerTest, panelError } from '@/components/ui/form/formUtils'
import { FormDollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import { useFormContext } from 'react-hook-form'
import { initialHistark } from '@/components/fagsystem/histark/form/initialValues'
import { HistarkDokument } from '@/components/fagsystem/histark/form/HistarkDokument'

const histarkAttributt = 'histark'

const HistarkForm = () => {
	const formMethods = useFormContext()
	if (!formMethods.getValues(histarkAttributt)) {
		return null
	}

	return (
		// @ts-ignore
		<Vis attributt={histarkAttributt}>
			<Panel
				heading="Dokumenter (Histark)"
				hasErrors={panelError(histarkAttributt)}
				iconType="dokarkiv"
				// @ts-ignore
				startOpen={erForsteEllerTest(formMethods.getValues(), [histarkAttributt])}
			>
				<Kategori title={`Oppretting av saksmappe for histark`} vis={histarkAttributt}>
					<FormDollyFieldArray
						name="histark.dokumenter"
						header="Dokument"
						newEntry={initialHistark}
						canBeEmpty={false}
					>
						{(path: string) => <HistarkDokument path={path} formMethods={formMethods} />}
					</FormDollyFieldArray>
				</Kategori>
			</Panel>
		</Vis>
	)
}

export default HistarkForm
