import SubOverskrift from '@/components/ui/subOverskrift/SubOverskrift'
import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import { arrayToString, oversettBoolean } from '@/utils/DataFormatter'
import React from 'react'
import { DollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import { TabsVisning } from '@/pages/tenorSoek/resultatVisning/TabsVisning'

export const InntektVisning = ({ data }) => {
	if (!data || data.length < 1) {
		return null
	}

	return (
		<>
			<SubOverskrift label={`Inntekt A-ordningen (${data.length})`} iconKind="inntektstub" />
			<div>
				<DollyFieldArray data={data} header="" nested>
					{(inntekt, idx: number) => {
						return (
							<TabsVisning kildedata={inntekt.tenorMetadata?.kildedata}>
								<TitleValue title="Periode" value={inntekt.periode} />
								<TitleValue title="Opplysningspliktig" value={inntekt.opplysningspliktig} />
								<TitleValue title="Inntektstype" value={arrayToString(inntekt.inntektstype)} />
								<TitleValue title="Beskrivelse" value={arrayToString(inntekt.beskrivelse)} />
								<TitleValue title="Forskuddstrekk" value={arrayToString(inntekt.forskuddstrekk)} />
								<TitleValue title="Har historikk" value={oversettBoolean(inntekt.harHistorikk)} />
								{/* Relasjoner:*/}
							</TabsVisning>
						)
					}}
				</DollyFieldArray>
			</div>
		</>
	)
}
