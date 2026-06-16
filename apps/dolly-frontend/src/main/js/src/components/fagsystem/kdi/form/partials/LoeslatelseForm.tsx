import React from 'react'
import { FormSelect } from '@/components/ui/form/inputs/select/Select'
import { SelectOptionsManager as Options } from '@/service/SelectOptions'
import { FormDatepicker } from '@/components/ui/form/inputs/datepicker/Datepicker'
import { FormCheckbox } from '@/components/ui/form/inputs/checbox/Checkbox'
import { KdiMeldingFieldsProps } from '@/components/fagsystem/kdi/form/partials/types'
import { PubliseringstidspunktField } from '@/components/fagsystem/kdi/form/partials/PubliseringstidspunktField'
import { Option } from '@/service/SelectOptionsOppslag'

export const LoeslatelseForm = ({
	path,
	formMethods,
	erEksisterendeMelding,
	fengselOptions,
	onSort,
	sortVersjon,
}: KdiMeldingFieldsProps) => {
	const kategori = formMethods?.watch(`${path}.kategori`)

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
				// TODO: Denne gjoer at feilmeldinger henger igjen i form
				onChange={(val: Option) => {
					formMethods?.setValue(`${path}.kategori`, val.value)
					if (val.value !== 'Varetekt') {
						formMethods?.setValue(`${path}.erOverfoertTilVaretektMedElektroniskKontroll`, false)
					}
				}}
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
				disabled={erEksisterendeMelding || kategori !== 'Varetekt'}
				checkboxMargin
			/>
		</>
	)
}
