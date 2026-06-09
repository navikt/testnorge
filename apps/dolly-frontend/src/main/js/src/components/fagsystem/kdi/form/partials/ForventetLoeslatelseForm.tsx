import React from 'react'
import { FormTextInput } from '@/components/ui/form/inputs/textInput/TextInput'
import { FormDatepicker } from '@/components/ui/form/inputs/datepicker/Datepicker'
import { RedigeringAnnulleringForm } from './RedigeringAnnulleringForm'
import { KdiMeldingFieldsProps } from '@/components/fagsystem/kdi/form/partials/types'

export const ForventetLoeslatelseForm = ({ formMethods, path }: KdiMeldingFieldsProps) => {
	const meldingId = formMethods.getValues(`${path}.meldingId`)

	return (
		<>
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
			{meldingId && <RedigeringAnnulleringForm meldingId={meldingId} />}
		</>
	)
}
