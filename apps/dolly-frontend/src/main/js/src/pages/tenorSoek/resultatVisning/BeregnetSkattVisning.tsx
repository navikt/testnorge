import SubOverskriftExpandable from '@/components/ui/subOverskrift/SubOverskriftExpandable'
import { DollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import { TabsVisning } from '@/pages/tenorSoek/resultatVisning/TabsVisning'
import React from 'react'
import { BeregnetSkatt } from '@/components/fagsystem/skatteetaten/visning/BeregnetSkattVisning'

export const BeregnetSkattVisning = ({ data }: any) => {
	if (!data || data.length < 1) {
		return null
	}

	return (
		<SubOverskriftExpandable label={`Beregnet skatt (${data.length})`} iconKind="bankkonto">
			<div>
				<DollyFieldArray data={data} nested>
					{(beregnetSkatt: any) => {
						return (
							<TabsVisning kildedata={beregnetSkatt.tenorMetadata?.kildedata}>
								<BeregnetSkatt beregnetSkatt={beregnetSkatt} />
							</TabsVisning>
						)
					}}
				</DollyFieldArray>
			</div>
		</SubOverskriftExpandable>
	)
}
