import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import React from 'react'
import { arrayToString, codeToNorskLabel, oversettBoolean } from '@/utils/DataFormatter'

export const Inntekt = ({ inntekt }: any) => {
	return (
		<>
			<TitleValue title="Periode" value={inntekt.periode} />
			<TitleValue title="Opplysningspliktig" value={inntekt.opplysningspliktig} />
			<TitleValue
				title="Inntektstype"
				value={arrayToString(inntekt.inntektstype?.map((type: string) => codeToNorskLabel(type)))}
			/>
			<TitleValue
				title="Beskrivelse"
				value={arrayToString(inntekt.beskrivelse?.map((beskr: string) => codeToNorskLabel(beskr)))}
			/>
			<TitleValue
				title="Forskuddstrekk"
				value={arrayToString(
					inntekt.forskuddstrekk?.map((trekk: string) => codeToNorskLabel(trekk)),
				)}
			/>
			<TitleValue title="Har historikk" value={oversettBoolean(inntekt.harHistorikk)} />
		</>
	)
}
