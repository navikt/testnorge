import { Table, Tabs } from '@navikt/ds-react'
import { Skattemelding } from '../soekFormPartials/Skattemelding'
import { Header } from '@/components/ui/soekForm/SoekFormWrapper'
import { BeregnetSkatt } from '@/pages/tenorSoek/soekFormPartials/BeregnetSkatt'
import { SummertSkattegrunnlag } from '@/pages/tenorSoek/soekFormPartials/SummertSkattegrunnlag'

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
								paths={[
									'beregnetSkatt.inntektsaar',
									'beregnetSkatt.oppgjoerstype',
									'beregnetSkatt.pensjonsgivendeInntekt',
								]}
								getValues={getValues}
								emptyCategory={emptyCategory}
							/>
						</Table.HeaderCell>
					</Table.ExpandableRow>
					<Table.ExpandableRow content={<SummertSkattegrunnlag handleChange={handleChange} />}>
						<Table.HeaderCell>
							<Header
								title="Summert skattegrunnlag"
								paths={[
									'summertSkattegrunnlag.inntektsaar',
									'summertSkattegrunnlag.stadietype',
									'summertSkattegrunnlag.oppgjoerstype',
									'summertSkattegrunnlag.tekniskNavn',
									'summertSkattegrunnlag.alminneligInntektFoerSaerfradragBeloep.fraOgMed',
									'summertSkattegrunnlag.alminneligInntektFoerSaerfradragBeloep.tilOgMed',
								]}
								getValues={getValues}
								emptyCategory={emptyCategory}
							/>
						</Table.HeaderCell>
					</Table.ExpandableRow>
					<Table.ExpandableRow content={<Skattemelding handleChange={handleChange} />}>
						<Table.HeaderCell>
							<Header
								title="Skattemelding"
								paths={['skattemelding.inntektsaar', 'skattemelding.skattemeldingstype']}
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
