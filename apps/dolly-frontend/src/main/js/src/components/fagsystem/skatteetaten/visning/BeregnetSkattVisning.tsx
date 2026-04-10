import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import { codeToNorskLabel, oversettBoolean } from '@/utils/DataFormatter'
import React from 'react'

export const BeregnetSkatt = ({ beregnetSkatt }: any) => {
	return (
		<>
			<TitleValue title="Inntektsår" value={beregnetSkatt?.inntektsaar} />
			<TitleValue title="Type oppgjør" value={codeToNorskLabel(beregnetSkatt?.typeOppgjoer)} />
			<TitleValue
				title="Har pensjonsgivende inntekt"
				value={oversettBoolean(beregnetSkatt?.pensjonsgivendeInntekt)}
			/>
		</>
	)
}
