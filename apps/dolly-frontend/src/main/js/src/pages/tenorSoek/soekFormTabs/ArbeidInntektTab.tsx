import { Table, Tabs } from '@navikt/ds-react'
import { InntektAordningen } from '@/pages/tenorSoek/soekFormPartials/InntektAordningen'
import { Header } from '@/components/ui/soekForm/SoekFormWrapper'
import { Arbeidsforhold } from '@/pages/tenorSoek/soekFormPartials/Arbeidsforhold'

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
						content={
							<InntektAordningen
								handleChange={handleChange}
								handleChangeList={handleChangeList}
								getValue={watch}
							/>
						}
						defaultOpen={true}
					>
						<Table.HeaderCell>
							<Header
								title="Inntekt A-ordningen"
								paths={[
									'inntekt.periode.fraOgMed',
									'inntekt.periode.tilOgMed',
									'inntekt.opplysningspliktig',
									'inntekt.inntektstyper',
									'inntekt.forskuddstrekk',
									'inntekt.beskrivelse',
									'inntekt.harHistorikk',
								]}
								getValues={getValues}
								emptyCategory={emptyCategory}
							/>
						</Table.HeaderCell>
					</Table.ExpandableRow>
					<Table.ExpandableRow content={<Arbeidsforhold handleChange={handleChange} />}>
						<Table.HeaderCell>
							<Header
								title="Arbeidsforhold"
								paths={[
									'arbeidsforhold.startDatoPeriode.fraOgMed',
									'arbeidsforhold.startDatoPeriode.tilOgMed',
									'arbeidsforhold.sluttDatoPeriode.fraOgMed',
									'arbeidsforhold.sluttDatoPeriode.tilOgMed',
									'arbeidsforhold.harPermisjoner',
									'arbeidsforhold.harPermitteringer',
									'arbeidsforhold.harArbeidsgiver',
									'arbeidsforhold.harTimerMedTimeloenn',
									'arbeidsforhold.harUtenlandsopphold',
									'arbeidsforhold.harHistorikk',
									'arbeidsforhold.arbeidsforholdstype',
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
