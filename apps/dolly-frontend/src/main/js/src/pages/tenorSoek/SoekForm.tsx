import { Form, Formik } from 'formik'
import styled from 'styled-components'
import { Accordion } from '@navikt/ds-react'
import { InntektAordningen } from '@/pages/tenorSoek/soekFormPartials/InntektAordningen'
import React from 'react'
import * as _ from 'lodash-es'
import { Header } from '@/components/ui/soekForm/SoekForm'
import DisplayFormikState from '@/utils/DisplayFormikState'
import { EnhetsregisteretForetaksregisteret } from '@/pages/tenorSoek/soekFormPartials/EnhetsregisteretForetaksregisteret'
import { FolkeregisteretIdentifikasjonStatus } from '@/pages/tenorSoek/soekFormPartials/FolkeregisteretIdentifikasjonStatus'
import { FolkeregisteretStatsborgerskap } from '@/pages/tenorSoek/soekFormPartials/FolkeregisteretStatsborgerskap'
import { FolkeregisteretNavn } from '@/pages/tenorSoek/soekFormPartials/FolkeregisteretNavn'
import { FolkeregisteretAdresse } from '@/pages/tenorSoek/soekFormPartials/FolkeregisteretAdresse'
import { FolkeregisteretRelasjoner } from '@/pages/tenorSoek/soekFormPartials/FolkeregisteretRelasjoner'
import { FolkeregisteretHendelser } from '@/pages/tenorSoek/soekFormPartials/FolkeregisteretHendelser'

const SoekefeltWrapper = styled.div`
	display: flex;
	flex-direction: column;
	margin-bottom: 20px;
	background-color: white;
	border: 1px @color-bg-grey-border;
	border-radius: 4px;
`

const Soekefelt = styled.div`
	padding: 20px 15px;
`

export const SoekForm = ({ request, setRequest, mutate }) => {
	function getUpdatedRequest(request: any) {
		for (let key of Object.keys(request)) {
			if (request[key] === '' || request[key] === null || request[key] === undefined) {
				delete request[key]
			} else if (typeof request[key] === 'object' && !(request[key] instanceof Date)) {
				request[key] = getUpdatedRequest(request[key])
				if (Object.keys(request[key]).length === 0) delete request[key]
			}
		}
		return Array.isArray(request) ? request.filter((val) => val) : request
	}

	return (
		<SoekefeltWrapper>
			<Soekefelt>
				<Formik initialValues={{}} onSubmit={() => console.log('submit ...')}>
					{(formikBag) => {
						const handleChange = (value: any, path: string) => {
							const request = _.set(formikBag.values, path, value)
							getUpdatedRequest(request)
							setRequest(request)
							formikBag.setValues(request)
							mutate()
						}

						const handleChangeList = (value: any, path: string) => {
							const list = value.map((item: any) => item.value)
							const request = _.set(formikBag.values, path, list)
							getUpdatedRequest(request)
							setRequest(request)
							formikBag.setValues(request)
							mutate()
						}

						const getValue = (path: string) => {
							return _.get(formikBag.values, path)
						}

						const getAntallRequest = (liste: Array<string>) => {
							let antall = 0
							liste.forEach((item) => {
								const attr = _.get(formikBag.values, item)
								if (Array.isArray(attr)) {
									antall += attr.length
								} else if (attr || attr === false) {
									antall++
								}
							})
							return antall
						}

						const devEnabled =
							window.location.hostname.includes('localhost') ||
							window.location.hostname.includes('dolly-frontend-dev')

						return (
							<>
								<Form className="flexbox--flex-wrap" autoComplete="off">
									<Accordion size="small" headingSize="xsmall" className="flexbox--full-width">
										<Accordion.Item defaultOpen={true}>
											<Accordion.Header>
												<Header
													title="Folkeregisteret - identifikasjon og status"
													antall={getAntallRequest([
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
													])}
												/>
											</Accordion.Header>
											<Accordion.Content style={{ paddingRight: '0' }}>
												<FolkeregisteretIdentifikasjonStatus
													formikBag={formikBag}
													handleChange={handleChange}
													handleChangeList={handleChangeList}
													getValue={getValue}
												/>
											</Accordion.Content>
										</Accordion.Item>
										<Accordion.Item>
											<Accordion.Header>
												<Header
													title="Folkeregisteret - statsborgerskap"
													antall={getAntallRequest([
														'harNorskStatsborgerskap',
														'harFlereStatsborgerskap',
													])}
												/>
											</Accordion.Header>
											<Accordion.Content style={{ paddingRight: '0' }}>
												<FolkeregisteretStatsborgerskap
													formikBag={formikBag}
													handleChange={handleChange}
													getValue={getValue}
												/>
											</Accordion.Content>
										</Accordion.Item>
										<Accordion.Item>
											<Accordion.Header>
												<Header
													title="Folkeregisteret - navn"
													antall={getAntallRequest([
														'navn.navnLengde.fraOgMed',
														'navn.navnLengde.tilOgMed',
														'navn.harFlereFornavn',
														'navn.harNavnSpesialtegn',
														'navn.harMellomnavn',
													])}
												/>
											</Accordion.Header>
											<Accordion.Content style={{ paddingRight: '0' }}>
												<FolkeregisteretNavn
													formikBag={formikBag}
													handleChange={handleChange}
													getValue={getValue}
												/>
											</Accordion.Content>
										</Accordion.Item>
										<Accordion.Item>
											<Accordion.Header>
												<Header
													title="Folkeregisteret - adresser"
													antall={getAntallRequest([
														'adresser.adresseGradering',
														'adresser.kommunenummer',
														'adresser.harAdresseSpesialtegn',
														'adresser.harBostedsadresse',
														'adresser.harOppholdAnnetSted',
														'adresser.harPostadresseNorge',
														'adresser.harPostadresseUtland',
														'adresser.harKontaktadresseDoedsbo',
													])}
												/>
											</Accordion.Header>
											<Accordion.Content style={{ paddingRight: '0' }}>
												<FolkeregisteretAdresse
													formikBag={formikBag}
													handleChange={handleChange}
													getValue={getValue}
												/>
											</Accordion.Content>
										</Accordion.Item>
										<Accordion.Item>
											<Accordion.Header>
												<Header
													title="Folkeregisteret - relasjoner"
													antall={getAntallRequest([
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
													])}
												/>
											</Accordion.Header>
											<Accordion.Content style={{ paddingRight: '0' }}>
												<FolkeregisteretRelasjoner
													formikBag={formikBag}
													handleChange={handleChange}
													getValue={getValue}
												/>
											</Accordion.Content>
										</Accordion.Item>
										<Accordion.Item>
											<Accordion.Header>
												<Header
													title="Folkeregisteret - hendelser"
													antall={getAntallRequest([
														'hendelser.hendelse',
														'hendelser.sisteHendelse',
													])}
												/>
											</Accordion.Header>
											<Accordion.Content style={{ paddingRight: '0' }}>
												<FolkeregisteretHendelser
													formikBag={formikBag}
													handleChange={handleChange}
													getValue={getValue}
												/>
											</Accordion.Content>
										</Accordion.Item>
										<Accordion.Item>
											<Accordion.Header>
												<Header
													title="Inntekt A-ordningen"
													antall={getAntallRequest([
														'inntekt.periode.fraOgMed',
														'inntekt.periode.tilOgMed',
														'inntekt.opplysningspliktig',
														'inntekt.inntektstyper',
														'inntekt.forskuddstrekk',
														'inntekt.beskrivelse',
														'inntekt.harHistorikk',
													])}
												/>
											</Accordion.Header>
											<Accordion.Content style={{ paddingRight: '0' }}>
												<InntektAordningen
													formikBag={formikBag}
													handleChange={handleChange}
													handleChangeList={handleChangeList}
													getValue={getValue}
												/>
											</Accordion.Content>
										</Accordion.Item>
										<Accordion.Item>
											<Accordion.Header>
												<Header
													title="Enhetsregisteret og Foretaksregisteret"
													antall={getAntallRequest(['roller'])}
												/>
											</Accordion.Header>
											<Accordion.Content style={{ paddingRight: '0' }}>
												<EnhetsregisteretForetaksregisteret
													formikBag={formikBag}
													handleChangeList={handleChangeList}
													getValue={getValue}
												/>
											</Accordion.Content>
										</Accordion.Item>
									</Accordion>
								</Form>
								{devEnabled && <DisplayFormikState {...formikBag} />}
							</>
							// TODO sett inn chips her?
						)
					}}
				</Formik>
			</Soekefelt>
		</SoekefeltWrapper>
	)
}
