import React from 'react'
import { FormSelect } from '@/components/ui/form/inputs/select/Select'
import { SelectOptionsManager as Options } from '@/service/SelectOptions'
import { FormDatepicker } from '@/components/ui/form/inputs/datepicker/Datepicker'
import { RedigeringAnnulleringForm } from './RedigeringAnnulleringForm'
import { KdiMeldingFieldsProps } from '@/components/fagsystem/kdi/form/partials/types'

export const InnsettelseForm = ({ formMethods, path, fengselOptions }: KdiMeldingFieldsProps) => {
	// TODO: Naar man sletter melding som har annullering maa annullering med tilsvarende meldingId også slettes
	const meldingId = formMethods.getValues(`${path}.meldingId`)

	return (
		<>
			<FormDatepicker
				name={`${path}.publiseringstidspunkt`}
				label="Publiseringstidspunkt"
				format={'DD.MM.YYYY HH:mm:ss'}
				// date={rapporteringsdate}
			/>
			<FormSelect
				name={`${path}.kategori`}
				label="Kategori"
				options={Options('kdiKategori')}
				isClearable={false}
			/>
			<FormSelect
				name={`${path}.organisasjonsnummer`}
				label="Organisasjonsnummer"
				options={fengselOptions}
				size="xlarge"
				isClearable={false}
			/>
			<FormDatepicker
				name={`${path}.tidspunkt`}
				label="Innsettelsestidspunkt"
				format={'DD.MM.YYYY HH:mm:ss'}
				// date={rapporteringsdate}
			/>
			{meldingId && <RedigeringAnnulleringForm meldingId={meldingId} />}
		</>
	)
}
