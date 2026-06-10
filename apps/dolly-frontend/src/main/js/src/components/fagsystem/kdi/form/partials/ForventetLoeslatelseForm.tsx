import React from 'react'
import { FormTextInput } from '@/components/ui/form/inputs/textInput/TextInput'
import { FormDatepicker } from '@/components/ui/form/inputs/datepicker/Datepicker'
import { KdiMeldingFieldsProps } from '@/components/fagsystem/kdi/form/partials/types'

export const ForventetLoeslatelseForm = ({
	path,
	erEksisterendeMelding,
}: KdiMeldingFieldsProps) => {
	return (
		<>
			<FormDatepicker
				name={`${path}.publiseringstidspunkt`}
				label="Publiseringstidspunkt"
				format={'DD.MM.YYYY HH:mm:ss'}
				disabled={erEksisterendeMelding}
				// date={rapporteringsdate}
			/>
			<FormTextInput
				name={`${path}.innmeldingHendelseId`}
				label="Innmelding hendelse-ID"
				size="large"
				isDisabled={erEksisterendeMelding}
			/>
			<FormDatepicker
				name={`${path}.tidspunkt`}
				label="Forventet løslatt tidspunkt"
				format={'DD.MM.YYYY HH:mm:ss'}
				size="large"
				disabled={erEksisterendeMelding}
				// date={rapporteringsdate}
			/>
		</>
	)
}
