import React from 'react'
import { FormTextInput } from '@/components/ui/form/inputs/textInput/TextInput'
import { FormDatepicker } from '@/components/ui/form/inputs/datepicker/Datepicker'
import { KdiMeldingFieldsProps } from '@/components/fagsystem/kdi/form/partials/types'
import { PubliseringstidspunktField } from '@/components/fagsystem/kdi/form/partials/PubliseringstidspunktField'

export const ForventetLoeslatelseForm = ({
	path,
	erEksisterendeMelding,
}: KdiMeldingFieldsProps) => {
	return (
		<>
			<PubliseringstidspunktField path={path} erEksisterendeMelding={erEksisterendeMelding} />
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
