import { Table, Tabs } from '@navikt/ds-react'
import { Tjenestepensjonsavtale } from '@/pages/tenorSoek/soekFormPartials/Tjenestepensjonsavtale'
import { Header } from '@/components/ui/soekForm/SoekFormWrapper'

export const PensjonTab = ({ handleChange, getValues, emptyCategory }: any) => {
	return (
		<Tabs.Panel value="pensjon" style={{ width: '100%' }}>
			<Table size="small">
				<Table.Body>
					<Table.ExpandableRow
						content={<Tjenestepensjonsavtale handleChange={handleChange} getValue={getValues} />}
						defaultOpen={true}
					>
						<Table.HeaderCell>
							<Header
								title="Tjenestepensjonsavtale"
								paths={[
									'tjenestepensjonsavtale.pensjonsinnretningOrgnr',
									'tjenestepensjonsavtale.periode',
								]}
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
