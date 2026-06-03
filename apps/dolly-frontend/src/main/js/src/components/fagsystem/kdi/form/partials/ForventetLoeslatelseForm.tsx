import { initialForventetLoeslatelse } from '@/components/fagsystem/kdi/initialValues'
import React from 'react'
import { FormDollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import { FormTextInput } from '@/components/ui/form/inputs/textInput/TextInput'
import { FormDatepicker } from '@/components/ui/form/inputs/datepicker/Datepicker'
import { AnnulleringForm } from '@/components/fagsystem/kdi/form/partials/AnnulleringForm'

export const ForventetLoeslatelseForm = ({ formMethods }) => {
	return (
		<FormDollyFieldArray
			name="instdataKdi.forventetLoeslatelse"
			header="Forventet løslatelse"
			newEntry={initialForventetLoeslatelse}
			nested
		>
			{(path, idx) => {
				// const hendelseId = formMethods.getValues(path)
				const meldingId = formMethods.getValues(`${path}.meldingId`)

				return (
					<React.Fragment key={idx}>
						{/*{hendelseId && (*/}
						{/*	<FormTextInput*/}
						{/*		name={`${path}.hendelseId`}*/}
						{/*		label="Hendelse-ID"*/}
						{/*		size="large"*/}
						{/*		isDisabled*/}
						{/*	/>*/}
						{/*)}*/}
						<FormDatepicker
							name={`${path}.publiseringstidspunkt`}
							label="Publiseringstidspunkt"
							format={'DD.MM.YYYY HH:mm:ss'}
							// date={rapporteringsdate}
						/>
						<FormTextInput
							name={`${path}.innmeldingHendelseId`}
							label="Innmelding hendelse-ID"
							size="large"
						/>
						<FormDatepicker
							name={`${path}.tidspunkt`}
							label="Forventet løslatt tidspunkt"
							format={'DD.MM.YYYY HH:mm:ss'}
							size="large"
							// date={rapporteringsdate}
						/>
						{meldingId && <AnnulleringForm meldingId={meldingId} />}
					</React.Fragment>
				)
			}}
		</FormDollyFieldArray>
	)
}
