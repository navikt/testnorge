import { Form, FormProvider, useForm } from 'react-hook-form'
import styled from 'styled-components'
import { Table } from '@navikt/ds-react'
import React from 'react'
import { Header } from '@/components/ui/soekForm/SoekFormWrapper'
import { EnhetsregisteretForetaksregisteret } from '@/pages/tenorSoek/soekFormPartials/EnhetsregisteretForetaksregisteret'
import { FolkeregisteretIdentifikasjonStatus } from '@/pages/tenorSoek/soekFormPartials/FolkeregisteretIdentifikasjonStatus'
import { FolkeregisteretStatsborgerskap } from '@/pages/tenorSoek/soekFormPartials/FolkeregisteretStatsborgerskap'
import { FolkeregisteretNavn } from '@/pages/tenorSoek/soekFormPartials/FolkeregisteretNavn'
import { FolkeregisteretAdresse } from '@/pages/tenorSoek/soekFormPartials/FolkeregisteretAdresse'
import { FolkeregisteretRelasjoner } from '@/pages/tenorSoek/soekFormPartials/FolkeregisteretRelasjoner'
import { FolkeregisteretHendelser } from '@/pages/tenorSoek/soekFormPartials/FolkeregisteretHendelser'
import { isDate } from 'date-fns'
import { fixTimezone } from '@/components/ui/form/formUtils'
import { Tjenestepensjonsavtale } from '@/pages/tenorSoek/soekFormPartials/Tjenestepensjonsavtale'
import { Skattemelding } from '@/pages/tenorSoek/soekFormPartials/Skattemelding'
import { InntektAordningen } from '@/pages/tenorSoek/soekFormPartials/InntektAordningen'
import DisplayFormState from '@/utils/DisplayFormState'
import { erDollyAdmin } from '@/utils/DollyAdmin'
import { Arbeidsforhold } from '@/pages/tenorSoek/soekFormPartials/Arbeidsforhold'

const SoekefeltWrapper = styled.div`
	display: flex;
	flex-direction: column;
	margin-bottom: 20px;
	background-color: white;
	border: 1px solid @color-bg-grey-border;
	border-radius: 4px;
	margin-top: -70px;
	width: 100%;
`

const Soekefelt = styled.div`
	padding: 15px;
`

export const SoekForm = ({ request, setRequest, setMarkertePersoner, mutate }: any) => {
	const formMethods = useForm({
		mode: 'onChange',
		defaultValues: request || {},
	})

	const { getValues, control, setValue, watch }: any = formMethods

	function getUpdatedRequest(request: any) {
		for (let key of Object.keys(request)) {
			if (request[key] === '' || request[key] === null || request[key] === undefined) {
				delete request[key]
			} else if (typeof request[key] === 'object' && !(request[key] instanceof Date)) {
				request[key] = getUpdatedRequest(request[key])
				if (Object.keys(request[key]).length === 0) {
					delete request[key]
				} else {
					request[key] = getUpdatedRequest(request[key])
				}
			}
		}
		return Array.isArray(request) ? request.filter((val) => val) : request
	}

	const handleChange = (value: any, path: string) => {
		if (isDate(value)) {
			value = fixTimezone(value)
		}
		setValue(path, value)
		const request = getUpdatedRequest(watch())
		setRequest({ ...request })
		setMarkertePersoner([])
		mutate()
	}

	const handleChangeList = (value: any, path: string) => {
		const list = value.map((item: any) => item.value)
		setValue(path, list)
		const request = getUpdatedRequest(watch())
		setRequest({ ...request })
		setMarkertePersoner([])
		mutate()
	}

	const emptyCategory = (paths: Array<string>) => {
		paths.forEach((path) => {
			setValue(path, undefined)
		})
		const request = getUpdatedRequest(watch())
		setRequest({ ...request })
		setMarkertePersoner([])
		mutate()
	}

	const devEnabled =
		window.location.hostname.includes('localhost') ||
		window.location.hostname.includes('dolly-frontend-dev')

	return (
		<SoekefeltWrapper>
			<Soekefelt>
				<FormProvider {...formMethods}>
					<>
						<Form control={control} className="flexbox--flex-wrap">
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
												title="Folkeregisteret - identifikasjon og status"
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
												title="Folkeregisteret - statsborgerskap"
												paths={['harNorskStatsborgerskap', 'harFlereStatsborgerskap']}
												getValues={getValues}
												emptyCategory={emptyCategory}
											/>
										</Table.HeaderCell>
									</Table.ExpandableRow>
									<Table.ExpandableRow
										content={<FolkeregisteretNavn handleChange={handleChange} />}
									>
										<Table.HeaderCell>
											<Header
												title="Folkeregisteret - navn"
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
									<Table.ExpandableRow
										content={<FolkeregisteretAdresse handleChange={handleChange} />}
									>
										<Table.HeaderCell>
											<Header
												title="Folkeregisteret - adresser"
												paths={[
													'adresser.adresseGradering',
													'adresser.kommunenummer',
													'adresser.harAdresseSpesialtegn',
													'adresser.harBostedsadresse',
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
									<Table.ExpandableRow
										content={<FolkeregisteretRelasjoner handleChange={handleChange} />}
									>
										<Table.HeaderCell>
											<Header
												title="Folkeregisteret - relasjoner"
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
									<Table.ExpandableRow
										content={<FolkeregisteretHendelser handleChange={handleChange} />}
									>
										<Table.HeaderCell>
											<Header
												title="Folkeregisteret - hendelser"
												paths={['hendelser.hendelse', 'hendelser.sisteHendelse']}
												getValues={getValues}
												emptyCategory={emptyCategory}
											/>
										</Table.HeaderCell>
									</Table.ExpandableRow>
									<Table.ExpandableRow
										content={
											<Tjenestepensjonsavtale handleChange={handleChange} getValue={getValues} />
										}
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
									<Table.ExpandableRow
										content={
											<EnhetsregisteretForetaksregisteret handleChangeList={handleChangeList} />
										}
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
									<Table.ExpandableRow
										content={
											<Arbeidsforhold
												handleChange={handleChange}
												handleChangeList={handleChangeList}
												getValue={watch}
											/>
										}
									>
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
						</Form>
						{(devEnabled || erDollyAdmin()) && <DisplayFormState />}
					</>
				</FormProvider>
			</Soekefelt>
		</SoekefeltWrapper>
	)
}
