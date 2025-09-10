import { SelectOptionsManager as Options } from '@/service/SelectOptions'
import { FormSelect } from '@/components/ui/form/inputs/select/Select'
import React from 'react'
import { SoekKategori } from '@/components/ui/soekForm/SoekForm'
import { oversettBoolean } from '@/utils/DataFormatter'

export const FolkeregisteretStatsborgerskap = ({ handleChange }: any) => {
	return (
		<SoekKategori>
			<FormSelect
				name="harNorskStatsborgerskap"
				options={Options('boolean')}
				label="Har norsk statsborgerskap"
				onChange={(val: any) =>
					handleChange(
						val?.value,
						'harNorskStatsborgerskap',
						`Har norsk statsborgerskap: ${oversettBoolean(val?.value)}`,
					)
				}
			/>
			<FormSelect
				name="harFlereStatsborgerskap"
				options={Options('boolean')}
				label="Har flere statsborgerskap"
				onChange={(val: any) =>
					handleChange(
						val?.value,
						'harFlereStatsborgerskap',
						`Har flere statsborgerskap: ${oversettBoolean(val?.value)}`,
					)
				}
			/>
		</SoekKategori>
	)
}
