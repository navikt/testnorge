import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import { codeToNorskLabel, oversettBoolean } from '@/utils/DataFormatter'
import React from 'react'
import Panel from '@/components/ui/panel/Panel'
import { DollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'

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

export const BeregnetSkattVisning = ({ beregnetSkattListe }: any) => {
	if (!beregnetSkattListe || beregnetSkattListe.length < 1) {
		return null
	}

	return (
		<Panel heading="Beregnet skatt">
			<div className="person-visning_content">
				<DollyFieldArray data={beregnetSkattListe} nested>
					{(beregnetSkatt: any) => {
						return <BeregnetSkatt beregnetSkatt={beregnetSkatt} />
					}}
				</DollyFieldArray>
			</div>
		</Panel>
	)
}
