import { FormSelect } from '@/components/ui/form/inputs/select/Select'
import React from 'react'
import { SoekKategori } from '@/components/ui/soekForm/SoekFormWrapper'
import { SelectOptionsManager as Options } from '@/service/SelectOptions'
import { oversettBoolean } from '@/utils/DataFormatter'

export const BeregnetSkatt = ({ handleChange }: any) => {
	const getInntektsaarOptions = () => {
		const inntektsaarListe = []
		const currentAar = new Date().getFullYear()
		for (let aar = currentAar - 5; aar < currentAar; aar++) {
			inntektsaarListe.push({ value: aar.toString(), label: aar.toString() })
		}
		return inntektsaarListe
	}

	const inntektsaarOptions = getInntektsaarOptions()

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
						`Inntektsår beregnet skatt: ${val?.label}`,
					)
				}
			/>
			<FormSelect
				name="beregnetSkatt.oppgjoerstype"
				options={Options('oppgjoerstype')}
				label="Type oppgjør"
				size="large"
				onChange={(val: any) =>
					handleChange(
						val?.value || null,
						'beregnetSkatt.oppgjoerstype',
						`Type oppgjør: ${val?.label}`,
					)
				}
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
