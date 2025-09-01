import { SelectOptionsManager as Options } from '@/service/SelectOptions'
import { FormSelect } from '@/components/ui/form/inputs/select/Select'
import React from 'react'
import { SoekKategori } from '@/components/ui/soekForm/SoekFormWrapper'

export const FolkeregisteretStatsborgerskap = ({ handleChange }: any) => {
	return (
		<SoekKategori>
			<FormSelect
				name="harNorskStatsborgerskap"
				options={Options('boolean')}
				label="Har norsk statsborgerskap"
				onChange={(val: any) => handleChange(val?.value, 'harNorskStatsborgerskap')}
			/>
			<FormSelect
				name="harFlereStatsborgerskap"
				options={Options('boolean')}
				label="Har flere statsborgerskap"
				onChange={(val: any) => handleChange(val?.value, 'harFlereStatsborgerskap')}
			/>
		</SoekKategori>
	)
}
