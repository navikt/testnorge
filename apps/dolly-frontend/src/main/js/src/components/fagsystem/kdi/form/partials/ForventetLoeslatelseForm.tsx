import { initialForventetLoeslatelse } from '@/components/fagsystem/kdi/initialValues'
import React from 'react'
import { FormDollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import { FormTextInput } from '@/components/ui/form/inputs/textInput/TextInput'
import { FormDatepicker } from '@/components/ui/form/inputs/datepicker/Datepicker'
import { AnnulleringForm } from '@/components/fagsystem/kdi/form/partials/AnnulleringForm'

export const ForventetLoeslatelseForm = ({ eksisterendeKdiData }) => {
	return (
		<FormDollyFieldArray
			name="instdataKdi.forventetLoeslatelse"
			header="Forventet løslatelse"
			newEntry={initialForventetLoeslatelse}
			nested
		>
			{(path, idx) => (
				<React.Fragment key={idx}>
					{eksisterendeKdiData && <FormTextInput name={`${path}.hendelseId`} label="Hendelse-ID" />}
					<FormDatepicker
						name={`${path}.publiseringstidspunkt`}
						label="Publiseringstidspunkt"
						format={'DD.MM.YYYY HH:mm:ss'}
						// date={rapporteringsdate}
					/>
					<FormTextInput name={`${path}.innmeldingHendelseId`} label="Innmelding hendelse-ID" />
					<FormDatepicker
						name={`${path}.tidspunkt`}
						label="Forventet løslatt tidspunkt"
						format={'DD.MM.YYYY HH:mm:ss'}
						size="large"
						// date={rapporteringsdate}
					/>
					<AnnulleringForm meldingId={555} />
				</React.Fragment>
			)}
		</FormDollyFieldArray>
	)
}
