import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import { arrayToString, oversettBoolean } from '@/utils/DataFormatter'
import React from 'react'
import { DollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import { TabsVisning } from '@/pages/tenorSoek/resultatVisning/TabsVisning'
import SubOverskriftExpandable from '@/components/ui/subOverskrift/SubOverskriftExpandable'

export const InntektVisning = ({ data }: any) => {
	if (!data || data.length < 1) {
		return null
	}

	return (
		<SubOverskriftExpandable label={`Inntekt A-ordningen (${data.length})`} iconKind="inntektstub">
			<div>
				<DollyFieldArray data={data} header={null} nested>
					{(inntekt: any) => {
						return (
							<TabsVisning kildedata={inntekt.tenorMetadata?.kildedata}>
								<TitleValue title="Periode" value={inntekt.periode} />
								<TitleValue title="Opplysningspliktig" value={inntekt.opplysningspliktig} />
								<TitleValue title="Inntektstype" value={arrayToString(inntekt.inntektstype)} />
								<TitleValue title="Beskrivelse" value={arrayToString(inntekt.beskrivelse)} />
								<TitleValue title="Forskuddstrekk" value={arrayToString(inntekt.forskuddstrekk)} />
								<TitleValue title="Har historikk" value={oversettBoolean(inntekt.harHistorikk)} />
							</TabsVisning>
						)
					}}
				</DollyFieldArray>
			</div>
		</SubOverskriftExpandable>
	)
}
