import React from 'react'
import { FormSelect } from '@/components/ui/form/inputs/select/Select'
import { SelectOptionsManager as Options } from '@/service/SelectOptions'
import { FormDatepicker } from '@/components/ui/form/inputs/datepicker/Datepicker'
import { KdiMeldingFieldsProps } from '@/components/fagsystem/kdi/form/partials/types'
import { PubliseringstidspunktField } from '@/components/fagsystem/kdi/form/partials/PubliseringstidspunktField'

export const AvbruddStartForm = ({
	path,
	erEksisterendeMelding,
	fengselOptions,
	onSort,
	sortVersjon,
}: KdiMeldingFieldsProps) => {
	return (
		<>
			<PubliseringstidspunktField
				path={path}
				erEksisterendeMelding={erEksisterendeMelding}
				onSort={onSort}
				sortVersjon={sortVersjon}
			/>
			<FormSelect
				name={`${path}.kategori`}
				label="Kategori"
				options={Options('kdiKategori')}
				isClearable={false}
				isDisabled={erEksisterendeMelding}
			/>
			<FormSelect
				name={`${path}.organisasjonsnummer`}
				label="Organisasjonsnummer"
				options={fengselOptions}
				size="xlarge"
				isClearable={false}
				isDisabled={erEksisterendeMelding}
			/>
			<FormDatepicker
				name={`${path}.tidspunkt`}
				label="Start straffeavbrudd"
				format={'DD.MM.YYYY HH:mm:ss'}
				disabled={erEksisterendeMelding}
			/>
			<FormDatepicker
				name={`${path}.forventetAvbruddSluttTidspunkt`}
				label="Forv. slutt straffeavbrudd"
				format={'DD.MM.YYYY HH:mm:ss'}
				disabled={erEksisterendeMelding}
			/>
		</>
	)
}
