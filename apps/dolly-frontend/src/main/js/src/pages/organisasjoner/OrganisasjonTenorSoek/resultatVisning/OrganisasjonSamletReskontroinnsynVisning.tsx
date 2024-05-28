import { DollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import { TabsVisning } from '@/pages/tenorSoek/resultatVisning/TabsVisning'
import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import React from 'react'
import SubOverskriftExpandable from '@/components/ui/subOverskrift/SubOverskriftExpandable'
import { oversettBoolean } from '@/utils/DataFormatter'

export const OrganisasjonSamletReskontroinnsynVisning = ({
	data: samletReskontroinnsynData,
}: any) => {
	if (!samletReskontroinnsynData || samletReskontroinnsynData.length < 1) {
		return null
	}

	return (
		<SubOverskriftExpandable
			label={`Samlet reskontroinnsyn (${samletReskontroinnsynData.length})`}
			iconKind="kvittering"
			isExpanded={false}
		>
			<div>
				<DollyFieldArray data={samletReskontroinnsynData} header={null} nested>
					{(samletReskontro: any) => {
						return (
							<TabsVisning kildedata={samletReskontro.tenorMetadata?.kildedata}>
								<TitleValue title="Foedselsnummer" value={samletReskontro.foedselsnummer} />
								<TitleValue title="Orgnr" value={samletReskontro.orgnr} />
								<TitleValue
									title="Har innbetaling"
									value={oversettBoolean(samletReskontro.harInnbetaling)}
								/>
								<TitleValue
									title="Ny oppfoering"
									value={oversettBoolean(samletReskontro.nyOppfoering)}
								/>
								<TitleValue title="Har krav" value={oversettBoolean(samletReskontro.harKrav)} />
							</TabsVisning>
						)
					}}
				</DollyFieldArray>
			</div>
		</SubOverskriftExpandable>
	)
}
