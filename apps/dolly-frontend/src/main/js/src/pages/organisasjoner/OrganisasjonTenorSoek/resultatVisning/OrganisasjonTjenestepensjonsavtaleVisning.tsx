import { DollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import { TabsVisning } from '@/pages/tenorSoek/resultatVisning/TabsVisning'
import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import React from 'react'
import SubOverskriftExpandable from '@/components/ui/subOverskrift/SubOverskriftExpandable'
import { formatTenorDate, oversettBoolean } from '@/utils/DataFormatter'

export const OrganisasjonTjenestepensjonsavtaleVisning = ({
	data: tjenestepensjonsavtaleData,
}: any) => {
	if (!tjenestepensjonsavtaleData || tjenestepensjonsavtaleData.length < 1) {
		return null
	}

	return (
		<SubOverskriftExpandable
			label={`Tjenestepensjonsavtale (${tjenestepensjonsavtaleData.length})`}
			iconKind="pensjon"
			isExpanded={false}
		>
			<div>
				<DollyFieldArray data={tjenestepensjonsavtaleData} header={null} nested>
					{(tjenestepensjon: any) => {
						return (
							<TabsVisning kildedata={tjenestepensjon.tenorMetadata?.kildedata}>
								<TitleValue
									title="Opplysningspliktig orgnr"
									value={tjenestepensjon.opplysningspliktigOrgnr}
								/>
								<TitleValue
									title="Tjenestepensjonsinnretning orgnr"
									value={tjenestepensjon.tjenestepensjonsinnretningOrgnr}
								/>
								<TitleValue
									title="Ny oppfoering"
									value={oversettBoolean(tjenestepensjon.nyOppfoering)}
								/>
								<TitleValue title="Periode" value={formatTenorDate(tjenestepensjon.periode)} />
								<TitleValue title="Avtalereferanse" value={tjenestepensjon.avtalereferanse} />
							</TabsVisning>
						)
					}}
				</DollyFieldArray>
			</div>
		</SubOverskriftExpandable>
	)
}
