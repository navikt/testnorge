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
			<FormSelect
				name="harNordenStatsborgerskap"
				options={Options('boolean')}
				label="Har statsborgerskap i Norden"
				onChange={(val: any) =>
					handleChange(
						val?.value,
						'harNordenStatsborgerskap',
						`Har statsborgerskap i Norden: ${oversettBoolean(val?.value)}`,
					)
				}
			/>
			<FormSelect
				name="harEuEoesStatsborgerskap"
				options={Options('boolean')}
				label="Har statsborgerskap i EU/EØS"
				onChange={(val: any) =>
					handleChange(
						val?.value,
						'harEuEoesStatsborgerskap',
						`Har statsborgerskap i EU/EØS: ${oversettBoolean(val?.value)}`,
					)
				}
			/>
			<FormSelect
				name="harTredjelandStatsborgerskap"
				options={Options('boolean')}
				label="Har st.borgerskap i tredjeland"
				onChange={(val: any) =>
					handleChange(
						val?.value,
						'harTredjelandStatsborgerskap',
						`Har statsborgerskap i tredjeland: ${oversettBoolean(val?.value)}`,
					)
				}
			/>
			<FormSelect
				name="harUtgaattStatsborgerskap"
				options={Options('boolean')}
				label="Har utgått statsborgerskap"
				onChange={(val: any) =>
					handleChange(
						val?.value,
						'harUtgaattStatsborgerskap',
						`Har utgått statsborgerskap: ${oversettBoolean(val?.value)}`,
					)
				}
			/>
			<FormSelect
				name="harStatsborgerskapHistorikk"
				options={Options('boolean')}
				label="Har statsborgerskapshistorikk"
				onChange={(val: any) =>
					handleChange(
						val?.value,
						'harStatsborgerskapHistorikk',
						`Har statsborgerskapshistorikk: ${oversettBoolean(val?.value)}`,
					)
				}
			/>
		</SoekKategori>
	)
}
