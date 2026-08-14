import { Table, Tabs } from '@navikt/ds-react'
import { InntektAordningen } from '@/pages/tenorSoek/soekFormPartials/InntektAordningen'
import { Header } from '@/components/ui/soekForm/SoekFormWrapper'
import { Arbeidsforhold } from '@/pages/tenorSoek/soekFormPartials/Arbeidsforhold'
import { arbeidInntektPaths } from '@/pages/tenorSoek/soekFormTabs/soekFormPaths'

export const ArbeidInntektTab = ({
	handleChange,
	handleChangeList,
	getValues,
	emptyCategory,
	watch,
}: any) => {
	return (
		<Tabs.Panel value="arbeidinntekt" style={{ width: '100%' }}>
			<Table size="small">
				<Table.Body>
					<Table.ExpandableRow
						content={<Arbeidsforhold handleChange={handleChange} />}
						defaultOpen={true}
					>
						<Table.HeaderCell>
							<Header
								title="Arbeidsforhold"
								paths={arbeidInntektPaths.arbeidsforhold}
								getValues={getValues}
								emptyCategory={emptyCategory}
							/>
						</Table.HeaderCell>
					</Table.ExpandableRow>
					<Table.ExpandableRow
						content={
							<InntektAordningen
								handleChange={handleChange}
								handleChangeList={handleChangeList}
								getValue={watch}
							/>
						}
					>
						<Table.HeaderCell>
							<Header
								title="Inntekt A-ordningen"
								paths={arbeidInntektPaths.inntektAordningen}
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
