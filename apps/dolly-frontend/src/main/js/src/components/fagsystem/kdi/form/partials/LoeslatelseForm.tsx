import React from 'react'
import { FormSelect } from '@/components/ui/form/inputs/select/Select'
import { SelectOptionsManager as Options } from '@/service/SelectOptions'
import { FormDatepicker } from '@/components/ui/form/inputs/datepicker/Datepicker'
import { FormCheckbox } from '@/components/ui/form/inputs/checbox/Checkbox'
import { KdiMeldingFieldsProps } from '@/components/fagsystem/kdi/form/partials/types'

export const LoeslatelseForm = ({
	path,
	erEksisterendeMelding,
	fengselOptions,
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
				label="Løslatelsestidspunkt"
				format={'DD.MM.YYYY HH:mm:ss'}
				disabled={erEksisterendeMelding}
				// date={rapporteringsdate}
			/>
			<FormCheckbox
				name={`${path}.erOverfoertTilUtlandskfengsel`}
				label="Er overført til utenlandsk fengsel"
				size="small"
				disabled={erEksisterendeMelding}
				checkboxMargin
			/>
			<FormCheckbox
				name={`${path}.erOverfoertTilVaretektMedElektroniskKontroll`}
				label="Er overført til varetekt med elektronisk kontroll"
				size="small"
				disabled={erEksisterendeMelding}
				checkboxMargin
			/>
			{/*	TODO: Skal kun kunne være true om kategori er Varetekt*/}
		</>
	)
}
