import { Table, Tabs } from '@navikt/ds-react'
import { FolkeregisteretIdentifikasjonStatus } from '@/pages/tenorSoek/soekFormPartials/FolkeregisteretIdentifikasjonStatus'
import { Header } from '@/components/ui/soekForm/SoekFormWrapper'
import { FolkeregisteretStatsborgerskap } from '@/pages/tenorSoek/soekFormPartials/FolkeregisteretStatsborgerskap'
import { FolkeregisteretNavn } from '@/pages/tenorSoek/soekFormPartials/FolkeregisteretNavn'
import { FolkeregisteretAdresse } from '@/pages/tenorSoek/soekFormPartials/FolkeregisteretAdresse'
import { FolkeregisteretRelasjoner } from '@/pages/tenorSoek/soekFormPartials/FolkeregisteretRelasjoner'
import { FolkeregisteretHendelser } from '@/pages/tenorSoek/soekFormPartials/FolkeregisteretHendelser'
import { folkeregisteretPaths } from '@/pages/tenorSoek/soekFormTabs/soekFormPaths'

export const FolkeregisteretTab = ({
	handleChange,
	handleChangeList,
	getValues,
	emptyCategory,
}: any) => {
	return (
		<Tabs.Panel value="folkeregisteret" style={{ width: '100%' }}>
			<Table size="small">
				<Table.Body>
					<Table.ExpandableRow
						content={
							<FolkeregisteretIdentifikasjonStatus
								handleChange={handleChange}
								handleChangeList={handleChangeList}
							/>
						}
						defaultOpen={true}
					>
						<Table.HeaderCell>
							<Header
								title="Identifikasjon og status"
								paths={folkeregisteretPaths.identifikasjonOgStatus}
								getValues={getValues}
								emptyCategory={emptyCategory}
							/>
						</Table.HeaderCell>
					</Table.ExpandableRow>
					<Table.ExpandableRow
						content={<FolkeregisteretStatsborgerskap handleChange={handleChange} />}
					>
						<Table.HeaderCell>
							<Header
								title="Statsborgerskap"
								paths={folkeregisteretPaths.statsborgerskap}
								getValues={getValues}
								emptyCategory={emptyCategory}
							/>
						</Table.HeaderCell>
					</Table.ExpandableRow>
					<Table.ExpandableRow content={<FolkeregisteretNavn handleChange={handleChange} />}>
						<Table.HeaderCell>
							<Header
								title="Navn"
								paths={folkeregisteretPaths.navn}
								getValues={getValues}
								emptyCategory={emptyCategory}
							/>
						</Table.HeaderCell>
					</Table.ExpandableRow>
					<Table.ExpandableRow content={<FolkeregisteretAdresse handleChange={handleChange} />}>
						<Table.HeaderCell>
							<Header
								title="Adresser"
								paths={folkeregisteretPaths.adresser}
								getValues={getValues}
								emptyCategory={emptyCategory}
							/>
						</Table.HeaderCell>
					</Table.ExpandableRow>
					<Table.ExpandableRow content={<FolkeregisteretRelasjoner handleChange={handleChange} />}>
						<Table.HeaderCell>
							<Header
								title="Relasjoner"
								paths={folkeregisteretPaths.relasjoner}
								getValues={getValues}
								emptyCategory={emptyCategory}
							/>
						</Table.HeaderCell>
					</Table.ExpandableRow>
					<Table.ExpandableRow content={<FolkeregisteretHendelser handleChange={handleChange} />}>
						<Table.HeaderCell>
							<Header
								title="Hendelser"
								paths={folkeregisteretPaths.hendelser}
								getValues={getValues}
								emptyCategory={emptyCategory}
							/>
						</Table.HeaderCell>
					</Table.ExpandableRow>
				</Table.Body>
			</Table>
		</Tabs.Panel>
	)
}
