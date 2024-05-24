import React from 'react'
import { DollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import { TabsVisning } from '@/pages/tenorSoek/resultatVisning/TabsVisning'
import SubOverskriftExpandable from '@/components/ui/subOverskrift/SubOverskriftExpandable'
import { Inntekt } from '@/components/fagsystem/skatteetaten/visning/InntektVisning'

export const InntektVisning = ({ data }: any) => {
	if (!data || data.length < 1) {
		return null
	}

	return (
		<SubOverskriftExpandable label={`Inntekt A-ordningen (${data.length})`} iconKind="inntektstub">
			<div>
				<DollyFieldArray data={data} nested>
					{(inntekt: any) => {
						return (
							<TabsVisning kildedata={inntekt.tenorMetadata?.kildedata}>
								<Inntekt inntekt={inntekt} />
							</TabsVisning>
						)
					}}
				</DollyFieldArray>
			</div>
		</SubOverskriftExpandable>
	)
}
