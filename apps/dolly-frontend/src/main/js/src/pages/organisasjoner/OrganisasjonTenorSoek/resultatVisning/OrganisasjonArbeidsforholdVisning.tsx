import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import React from 'react'
import { TabsVisning } from '@/pages/tenorSoek/resultatVisning/TabsVisning'
import SubOverskriftExpandable from '@/components/ui/subOverskrift/SubOverskriftExpandable'
import { TenorOrganisasjon } from '@/pages/organisasjoner/OrganisasjonTenorSoek/resultatVisning/OrganisasjonTenorVisning'
import { oversettBoolean } from '@/utils/DataFormatter'

export const OrganisasjonArbeidsforholdVisning = ({ data }: { data: TenorOrganisasjon }) => {
	if (!data) {
		return null
	}

	console.log('data: ', data) //TODO - SLETT MEG

	return (
		<>
			<SubOverskriftExpandable label="Arbeidsforhold" iconKind="arbeid" isExpanded={true}>
				<TabsVisning kildedata={data.tenorMetadata?.kildedata}>
					<TitleValue title="Har ansatte" value={oversettBoolean(data.harAnsatte)} />
					<TitleValue title="Antall ansatte" value={data.antallAnsatte} />
					<TitleValue title="Konkurs" value={oversettBoolean(data.konkurs)} />
				</TabsVisning>
			</SubOverskriftExpandable>
		</>
	)
}
