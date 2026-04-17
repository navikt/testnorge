import { FormSelect } from '@/components/ui/form/inputs/select/Select'
import React from 'react'
import { SoekKategori } from '@/components/ui/soekForm/SoekFormWrapper'
import { SelectOptionsManager as Options } from '@/service/SelectOptions'
import { oversettBoolean } from '@/utils/DataFormatter'
import { useTenorDomain } from '@/utils/hooks/useTenorSoek'
import { createOptions, getInntektsaarOptions } from '@/pages/tenorSoek/utils'

export const BeregnetSkatt = ({ handleChange }: any) => {
	const { domain: typeOppgjoerOptions, loading: loadingTypeOppgjoer } =
		useTenorDomain('Oppgjoerstype')

	const inntektsaarOptions = getInntektsaarOptions(7)

	return (
		<SoekKategori>
			<FormSelect
				name="beregnetSkatt.inntektsaar"
				options={inntektsaarOptions}
				label="Inntektsår"
				onChange={(val: any) =>
					handleChange(
						val?.value || null,
						'beregnetSkatt.inntektsaar',
						`Beregnet skatt inntektsår: ${val?.label}`,
					)
				}
			/>
			<FormSelect
				name="beregnetSkatt.oppgjoerstype"
				options={createOptions(typeOppgjoerOptions?.data)}
				label="Type oppgjør"
				size="large"
				onChange={(val: any) =>
					handleChange(
						val?.value || null,
						'beregnetSkatt.oppgjoerstype',
						`Beregnet skatt type oppgjør: ${val?.label}`,
					)
				}
				isLoading={loadingTypeOppgjoer}
			/>
			<FormSelect
				name="beregnetSkatt.pensjonsgivendeInntekt"
				options={Options('boolean')}
				size="small"
				label="Har pensjonsgivende inntekt"
				onChange={(val: any) =>
					handleChange(
						val?.value,
						'beregnetSkatt.pensjonsgivendeInntekt',
						`Har pensjonsgivende inntekt: ${oversettBoolean(val?.value)}`,
					)
				}
			/>
		</SoekKategori>
	)
}
