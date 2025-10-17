import { FormSelect } from '@/components/ui/form/inputs/select/Select'
import React, { SyntheticEvent } from 'react'
import { SelectOptionsManager as Options } from '@/service/SelectOptions'
import { usePersonerTyper } from '@/utils/hooks/useDollySearch'
import { SoekKategori } from '@/components/ui/soekForm/SoekFormWrapper'

export const Fagsystemer = ({ handleChangeList }: any) => {
	const { typer, loading: loadingTyper } = usePersonerTyper()

	return (
		<SoekKategori>
			<div className="flexbox--full-width">
				<FormSelect
					name="registreRequest"
					placeholder={loadingTyper ? 'Laster fagsystemer ...' : 'Velg fagsystemer ...'}
					title="Fagsystemer"
					options={typer}
					isMulti={true}
					size="grow"
					onChange={(val: SyntheticEvent) => {
						handleChangeList(val, 'registreRequest', 'Fagsystem')
					}}
				/>
			</div>
			<div className="flexbox--full-width">
				<FormSelect
					name="miljoer"
					placeholder="Velg miljøer ..."
					title="Miljøer"
					options={Options('miljoer')}
					isMulti={true}
					size="large"
					onChange={(val: SyntheticEvent) => {
						handleChangeList(val, 'miljoer', 'Miljø')
					}}
				/>
			</div>
		</SoekKategori>
	)
}
