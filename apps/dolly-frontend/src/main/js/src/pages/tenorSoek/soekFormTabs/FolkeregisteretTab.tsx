import { Table, Tabs } from '@navikt/ds-react'
import { FolkeregisteretIdentifikasjonStatus } from '@/pages/tenorSoek/soekFormPartials/FolkeregisteretIdentifikasjonStatus'
import { Header } from '@/components/ui/soekForm/SoekFormWrapper'
import { FolkeregisteretStatsborgerskap } from '@/pages/tenorSoek/soekFormPartials/FolkeregisteretStatsborgerskap'
import { FolkeregisteretNavn } from '@/pages/tenorSoek/soekFormPartials/FolkeregisteretNavn'
import { FolkeregisteretAdresse } from '@/pages/tenorSoek/soekFormPartials/FolkeregisteretAdresse'
import { FolkeregisteretRelasjoner } from '@/pages/tenorSoek/soekFormPartials/FolkeregisteretRelasjoner'
import { FolkeregisteretHendelser } from '@/pages/tenorSoek/soekFormPartials/FolkeregisteretHendelser'

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
								paths={[
									'identifikator',
									'identifikatorType',
									'foedselsdato.fraOgMed',
									'foedselsdato.tilOgMed',
									'doedsdato.fraOgMed',
									'doedsdato.tilOgMed',
									'kjoenn',
									'personstatus',
									'sivilstand',
									'identitetsgrunnlagStatus',
									'adressebeskyttelse',
									'harFalskIdentitet',
									'utenlandskPersonIdentifikasjon',
									'harLegitimasjonsdokument',
								]}
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
								paths={[
									'harNorskStatsborgerskap',
									'harFlereStatsborgerskap',
									'harNordenStatsborgerskap',
									'harEuEoesStatsborgerskap',
									'harTredjelandStatsborgerskap',
									'harUtgaattStatsborgerskap',
									'harStatsborgerskapHistorikk',
								]}
								getValues={getValues}
								emptyCategory={emptyCategory}
							/>
						</Table.HeaderCell>
					</Table.ExpandableRow>
					<Table.ExpandableRow content={<FolkeregisteretNavn handleChange={handleChange} />}>
						<Table.HeaderCell>
							<Header
								title="Navn"
								paths={[
									'navn.navnLengde.fraOgMed',
									'navn.navnLengde.tilOgMed',
									'navn.harFlereFornavn',
									'navn.harNavnSpesialtegn',
									'navn.harMellomnavn',
								]}
								getValues={getValues}
								emptyCategory={emptyCategory}
							/>
						</Table.HeaderCell>
					</Table.ExpandableRow>
					<Table.ExpandableRow content={<FolkeregisteretAdresse handleChange={handleChange} />}>
						<Table.HeaderCell>
							<Header
								title="Adresser"
								paths={[
									'adresser.adresseGradering',
									'adresser.kommunenummer',
									'adresser.harAdresseSpesialtegn',
									'adresser.harBostedsadresse',
									'avansert.harBostedsadresseHistorikk',
									'adresser.harOppholdAnnetSted',
									'adresser.harPostadresseNorge',
									'adresser.harPostadresseUtland',
									'adresser.harKontaktadresseDoedsbo',
								]}
								getValues={getValues}
								emptyCategory={emptyCategory}
							/>
						</Table.HeaderCell>
					</Table.ExpandableRow>
					<Table.ExpandableRow content={<FolkeregisteretRelasjoner handleChange={handleChange} />}>
						<Table.HeaderCell>
							<Header
								title="Relasjoner"
								paths={[
									'relasjoner.relasjon',
									'relasjoner.antallBarn.fraOgMed',
									'relasjoner.antallBarn.tilOgMed',
									'relasjoner.relasjonMedFoedselsaar.fraOgMed',
									'relasjoner.relasjonMedFoedselsaar.tilOgMed',
									'relasjoner.harForeldreAnsvar',
									'relasjoner.harDeltBosted',
									'relasjoner.harVergemaalEllerFremtidsfullmakt',
									'relasjoner.borMedMor',
									'relasjoner.borMedFar',
									'relasjoner.borMedMedmor',
									'relasjoner.foreldreHarSammeAdresse',
								]}
								getValues={getValues}
								emptyCategory={emptyCategory}
							/>
						</Table.HeaderCell>
					</Table.ExpandableRow>
					<Table.ExpandableRow content={<FolkeregisteretHendelser handleChange={handleChange} />}>
						<Table.HeaderCell>
							<Header
								title="Hendelser"
								paths={['hendelser.hendelse', 'hendelser.sisteHendelse']}
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
