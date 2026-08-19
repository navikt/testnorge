import { Table, Tabs } from '@navikt/ds-react'
import { Skattemelding } from '@/pages/tenorSoek/soekFormPartials/Skattemelding'
import { Header } from '@/components/ui/soekForm/SoekFormWrapper'
import { BeregnetSkatt } from '@/pages/tenorSoek/soekFormPartials/BeregnetSkatt'
import { SummertSkattegrunnlag } from '@/pages/tenorSoek/soekFormPartials/SummertSkattegrunnlag'
import { skattPaths } from '@/pages/tenorSoek/soekFormTabs/soekFormPaths'

export const SkattTab = ({ handleChange, getValues, emptyCategory }: any) => {
	return (
		<Tabs.Panel value="skatt" style={{ width: '100%' }}>
			<Table size="small">
				<Table.Body>
					<Table.ExpandableRow
						content={<BeregnetSkatt handleChange={handleChange} />}
						defaultOpen={true}
					>
						<Table.HeaderCell>
							<Header
								title="Beregnet skatt"
								paths={skattPaths.beregnetSkatt}
								getValues={getValues}
								emptyCategory={emptyCategory}
							/>
						</Table.HeaderCell>
					</Table.ExpandableRow>
					<Table.ExpandableRow content={<SummertSkattegrunnlag handleChange={handleChange} />}>
						<Table.HeaderCell>
							<Header
								title="Summert skattegrunnlag"
								paths={skattPaths.summertSkattegrunnlag}
								getValues={getValues}
								emptyCategory={emptyCategory}
							/>
						</Table.HeaderCell>
					</Table.ExpandableRow>
					<Table.ExpandableRow content={<Skattemelding handleChange={handleChange} />}>
						<Table.HeaderCell>
							<Header
								title="Skattemelding"
								paths={skattPaths.skattemelding}
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
