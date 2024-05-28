import { DollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import { TabsVisning } from '@/pages/tenorSoek/resultatVisning/TabsVisning'
import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import React from 'react'
import SubOverskriftExpandable from '@/components/ui/subOverskrift/SubOverskriftExpandable'
import { oversettBoolean } from '@/utils/DataFormatter'

export const OrganisasjonTestinnsendingSkattVisning = ({
	data: testinnsendingSkattEnhetData,
}: any) => {
	if (!testinnsendingSkattEnhetData || testinnsendingSkattEnhetData.length < 1) {
		return null
	}

	return (
		<SubOverskriftExpandable
			label={`Testinnsending skatt (${testinnsendingSkattEnhetData.length})`}
			iconKind="pengesekk"
			isExpanded={false}
		>
			<div>
				<DollyFieldArray data={testinnsendingSkattEnhetData} header={null} nested>
					{(testinnsendingSkatt: any) => {
						return (
							<TabsVisning kildedata={testinnsendingSkatt.tenorMetadata?.kildedata}>
								<TitleValue
									title="Har selskapsmelding fastsatt"
									value={oversettBoolean(testinnsendingSkatt.harSelskapsmeldingFastsatt)}
								/>
								<TitleValue
									title="Har skattemelding fastsatt"
									value={oversettBoolean(testinnsendingSkatt.harSkattemeldingFastsatt)}
								/>
								<TitleValue title="Identifikator" value={testinnsendingSkatt.identifikator} />
								<TitleValue
									title="Har selskapsmelding utkast"
									value={oversettBoolean(testinnsendingSkatt.harSelskapsmeldingUtkast)}
								/>
								<TitleValue
									title="Har skattemelding utkast"
									value={testinnsendingSkatt.harSkattemeldingUtkast}
								/>
								<TitleValue title="Inntektsaar" value={testinnsendingSkatt.inntektsaar} />
								<TitleValue
									title="Ny Oppfoering"
									value={oversettBoolean(testinnsendingSkatt.nyOppfoering)}
								/>
								<TitleValue title="Partsnummer" value={testinnsendingSkatt.partsnummer} />
							</TabsVisning>
						)
					}}
				</DollyFieldArray>
			</div>
		</SubOverskriftExpandable>
	)
}
