import { Table, Tabs } from '@navikt/ds-react'
import { EnhetsregisteretForetaksregisteret } from '@/pages/tenorSoek/soekFormPartials/EnhetsregisteretForetaksregisteret'
import { Header } from '@/components/ui/soekForm/SoekFormWrapper'

export const VirksomhetTab = ({ handleChangeList, getValues, emptyCategory }: any) => {
	return (
		<Tabs.Panel value="virksomhet" style={{ width: '100%' }}>
			<Table size="small">
				<Table.Body>
					<Table.ExpandableRow
						content={<EnhetsregisteretForetaksregisteret handleChangeList={handleChangeList} />}
						defaultOpen={true}
					>
						<Table.HeaderCell>
							<Header
								title="Enhetsregisteret og Foretaksregisteret"
								paths={['roller']}
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
