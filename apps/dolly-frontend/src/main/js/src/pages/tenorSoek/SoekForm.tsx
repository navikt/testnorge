import { Form, Formik } from 'formik'
import { initialValues } from '@/pages/tenorSoek/InitialValues'
import styled from 'styled-components'
import { Accordion } from '@navikt/ds-react'
import { InntektAordningen } from '@/pages/tenorSoek/soekFormPartials/InntektAordningen'
import React, { useState } from 'react'
import { useTenorIdent, useTenorOversikt, useTenorSoek } from '@/utils/hooks/useTenorSoek'
import { SoekRequest } from '@/pages/dollySoek/DollySoekTypes'
import * as _ from 'lodash-es'
import { TreffListe } from '@/pages/tenorSoek/resultatVisning/TreffListe'
import { Header, requestIsEmpty } from '@/components/ui/soekForm/SoekForm'
import DisplayFormikState from '@/utils/DisplayFormikState'
import { EnhetsregisteretForetaksregisteret } from '@/pages/tenorSoek/soekFormPartials/EnhetsregisteretForetaksregisteret'
import { FolkeregisteretIdentifikasjonStatus } from '@/pages/tenorSoek/soekFormPartials/FolkeregisteretIdentifikasjonStatus'
import { FolkeregisteretStatsborgerskap } from '@/pages/tenorSoek/soekFormPartials/FolkeregisteretStatsborgerskap'
import { FolkeregisteretNavn } from '@/pages/tenorSoek/soekFormPartials/FolkeregisteretNavn'
import { FolkeregisteretAdresse } from '@/pages/tenorSoek/soekFormPartials/FolkeregisteretAdresse'
import { FolkeregisteretRelasjoner } from '@/pages/tenorSoek/soekFormPartials/FolkeregisteretRelasjoner'
import { FolkeregisteretHendelser } from '@/pages/tenorSoek/soekFormPartials/FolkeregisteretHendelser'
import { isEmpty } from '@/components/fagsystem/pdlf/form/partials/utils'

const SoekefeltWrapper = styled.div`
	display: flex;
	flex-direction: column;
	margin-bottom: 20px;
	background-color: white;
	border: 1px @color-bg-grey-border;
	border-radius: 4px;
`

const Soekefelt = styled.div`
	padding: 20px 15px 5px 15px;
`

export const SoekForm = ({ request, setRequest, mutate }) => {
	function getUpdatedRequest(request: any) {
		console.log('request: ', request) //TODO - SLETT MEG
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
				{/*<Formik initialValues={initialValues} onSubmit={(request) => handleSubmit(request)}>*/}
				<Formik initialValues={{}} onSubmit={() => console.log('submit...')}>
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
								if (attr || attr === false) {
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
												<Header title="Folkeregisteret - identifikasjon og status" antall={0} />
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
												<Header title="Folkeregisteret - navn" antall={0} />
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
												<Header title="Folkeregisteret - adresser" antall={0} />
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
												<Header title="Folkeregisteret - relasjoner" antall={0} />
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
												<Header title="Folkeregisteret - hendelser" antall={0} />
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
												<Header title="Inntekt A-ordningen" antall={0} />
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
												<Header title="Enhetsregisteret og Foretaksregisteret" antall={0} />
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
