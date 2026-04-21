import SubOverskriftExpandable from '@/components/ui/subOverskrift/SubOverskriftExpandable'
import { DollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import { TabsVisning } from '@/pages/tenorSoek/resultatVisning/TabsVisning'
import React from 'react'
import { SummertSkattegrunnlag } from '@/components/fagsystem/skatteetaten/visning/SummertSkattegrunnlagVisning'

export const SummertSkattegrunnlagVisning = ({ data }: any) => {
	if (!data || data.length < 1) {
		return null
	}

	return (
		<SubOverskriftExpandable label={`Summert skattegrunnlag (${data.length})`} iconKind="bankkonto">
			<div>
				<DollyFieldArray data={data} nested>
					{(summertSkattegrunnlag: any) => {
						return (
							<TabsVisning kildedata={summertSkattegrunnlag.tenorMetadata?.kildedata}>
								<SummertSkattegrunnlag summertSkattegrunnlag={summertSkattegrunnlag} />
							</TabsVisning>
						)
					}}
				</DollyFieldArray>
			</div>
		</SubOverskriftExpandable>
	)
}
