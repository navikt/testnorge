import SubOverskriftExpandable from '@/components/ui/subOverskrift/SubOverskriftExpandable'
import { DollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import { TabsVisning } from '@/pages/tenorSoek/resultatVisning/TabsVisning'
import React from 'react'
import { Skattemelding } from '@/components/fagsystem/skatteetaten/visning/SkattemeldingVisning'

export const SkattemeldingVisning = ({ data }: any) => {
	if (!data || data.length < 1) {
		return null
	}

	return (
		<SubOverskriftExpandable label={`Skattemelding (${data.length})`} iconKind="bankkonto">
			<div>
				<DollyFieldArray data={data} header={null} nested>
					{(skattemelding: any) => {
						return (
							<TabsVisning kildedata={skattemelding.tenorMetadata?.kildedata}>
								<Skattemelding skattemelding={skattemelding} />
							</TabsVisning>
						)
					}}
				</DollyFieldArray>
			</div>
		</SubOverskriftExpandable>
	)
}
