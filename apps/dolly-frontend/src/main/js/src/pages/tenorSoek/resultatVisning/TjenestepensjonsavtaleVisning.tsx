import React from 'react'
import SubOverskriftExpandable from '@/components/ui/subOverskrift/SubOverskriftExpandable'
import { TabsVisning } from '@/pages/tenorSoek/resultatVisning/TabsVisning'
import { DollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import { Tjenestepensjonsavtale } from '@/components/fagsystem/skatteetaten/visning/TjenestepensjonsavtaleVisning'

export const TjenestepensjonsavtaleVisning = ({ data }: any) => {
	if (!data) {
		return null
	}

	return (
		<SubOverskriftExpandable
			label={`Tjenestepensjonsavtale (${data.length})`}
			iconKind="pensjon"
			isExpanded={true}
		>
			<div>
				<DollyFieldArray data={data} nested>
					{(tjenestepensjonsavtale: any) => {
						return (
							<TabsVisning kildedata={tjenestepensjonsavtale.tenorMetadata?.kildedata}>
								<Tjenestepensjonsavtale tjenestepensjonsavtale={tjenestepensjonsavtale} />
							</TabsVisning>
						)
					}}
				</DollyFieldArray>
			</div>
		</SubOverskriftExpandable>
	)
}
