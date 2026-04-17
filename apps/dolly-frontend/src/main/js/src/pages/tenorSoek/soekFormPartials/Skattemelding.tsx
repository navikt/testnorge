import { useTenorDomain } from '@/utils/hooks/useTenorSoek'
import { createOptions, getInntektsaarOptions } from '@/pages/tenorSoek/utils'
import { FormSelect } from '@/components/ui/form/inputs/select/Select'
import React from 'react'
import { SoekKategori } from '@/components/ui/soekForm/SoekFormWrapper'

export const Skattemelding = ({ handleChange }: any) => {
	const { domain: skattemeldingstypeOptions, loading: loadingSkattemeldingstype } =
		useTenorDomain('Skattemeldingstype')

	const inntektsaarOptions = getInntektsaarOptions(6)

	return (
		<SoekKategori>
			<FormSelect
				name="skattemelding.inntektsaar"
				options={inntektsaarOptions}
				label="Inntektsår"
				onChange={(val: any) =>
					handleChange(
						val?.value || null,
						'skattemelding.inntektsaar',
						`Inntektsår skattemelding: ${val?.label}`,
					)
				}
			/>
			<FormSelect
				name="skattemelding.skattemeldingstype"
				options={createOptions(skattemeldingstypeOptions?.data)}
				label="Type skattemelding"
				size="large"
				onChange={(val: any) =>
					handleChange(
						val?.value || null,
						'skattemelding.skattemeldingstype',
						`Type skattemelding: ${val?.label}`,
					)
				}
				isLoading={loadingSkattemeldingstype}
			/>
		</SoekKategori>
	)
}
