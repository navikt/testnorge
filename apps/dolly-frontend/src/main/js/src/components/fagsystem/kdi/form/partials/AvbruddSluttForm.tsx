import React from 'react'
import { FormSelect } from '@/components/ui/form/inputs/select/Select'
import { SelectOptionsManager as Options } from '@/service/SelectOptions'
import { FormDatepicker } from '@/components/ui/form/inputs/datepicker/Datepicker'
import { AnnulleringForm } from '@/components/fagsystem/kdi/form/partials/AnnulleringForm'
import { KdiMeldingFieldsProps } from '@/components/fagsystem/kdi/form/partials/types'

export const AvbruddSluttForm = ({ formMethods, path }: KdiMeldingFieldsProps) => {
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
				options={Options('fengsel')} //TODO: Kan hentes fra nytt endepunkt
				size="large"
				isClearable={false}
			/>
			<FormDatepicker
				name={`${path}.tidspunkt`}
				label="Tidspunkt for slutt på straffeavbrudd"
				format={'DD.MM.YYYY HH:mm:ss'}
				// size="large"
				// date={rapporteringsdate}
			/>
			{meldingId && <AnnulleringForm meldingId={meldingId} />}
		</>
	)
}
