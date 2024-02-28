import { SelectOptionsManager as Options } from '@/service/SelectOptions'
import { FormikSelect } from '@/components/ui/form/inputs/select/Select'
import React from 'react'
import { SoekKategori } from '@/components/ui/soekForm/SoekForm'

export const FolkeregisteretStatsborgerskap = ({ handleChange }: any) => {
	return (
		<SoekKategori>
			<FormikSelect
				name="harNorskStatsborgerskap"
				options={Options('boolean')}
				label="Har norsk statsborgerskap"
				onChange={(val: boolean) => handleChange(val?.value, 'harNorskStatsborgerskap')}
			/>
			<FormikSelect
				name="harFlereStatsborgerskap"
				options={Options('boolean')}
				label="Har flere statsborgerskap"
				onChange={(val: boolean) => handleChange(val?.value, 'harFlereStatsborgerskap')}
			/>
		</SoekKategori>
	)
}
