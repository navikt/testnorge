import { SelectOptionsManager as Options } from '@/service/SelectOptions'
import { FormikSelect } from '@/components/ui/form/inputs/select/Select'
import React from 'react'
import { SoekKategori } from '@/components/ui/soekForm/SoekForm'

export const FolkeregisteretStatsborgerskap = ({
	formikBag,
	handleChangeBoolean,
	getValue,
}: any) => {
	return (
		<SoekKategori>
			<FormikSelect
				name="harNorskStatsborgerskap"
				options={Options('boolean')}
				size="small"
				label="Har norsk statsborgerskap"
				onChange={(val: boolean) => handleChangeBoolean(val?.value, 'harNorskStatsborgerskap')}
				value={getValue('harNorskStatsborgerskap')}
			/>
			<FormikSelect
				name="harFlereStatsborgerskap"
				options={Options('boolean')}
				size="small"
				label="Har flere statsborgerskap"
				onChange={(val: boolean) => handleChangeBoolean(val?.value, 'harFlereStatsborgerskap')}
				value={getValue('harFlereStatsborgerskap')}
			/>
		</SoekKategori>
	)
}
