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
	// const handleSubmit = (request: SoekRequest) => {
	// 	setRequest(request)
	// 	mutate()
	// }

	// const getUpdatedRequest = (request: any) => {
	// 	let updatedRequest = {}
	// 	for (const i in request) {
	// 		if (
	// 			typeof request[i] === 'object' &&
	// 			!(request[i] instanceof Date) &&
	// 			!Array.isArray(request[i])
	// 		) {
	// 			const temp = getUpdatedRequest(request[i])
	// 			for (const j in temp) {
	// 				updatedRequest[i + '.' + j] = temp[j]
	// 			}
	// 		} else {
	// 			updatedRequest[i] = request[i]
	// 		}
	// 	}
	// 	return updatedRequest
	// }

	// const getUpdatedRequest = (request: any) => {
	// 	for (const i in request) {
	// 		if (!request[i] || typeof request[i] !== 'object') {
	// 			continue
	// 		}
	// 		getUpdatedRequest(request[i])
	// 		if (
	// 			Object.keys(request[i]).length === 0 ||
	// 			Object.values(request[i]).every((x) => x === null || x === '')
	// 		) {
	// 			delete request[i]
	// 		}
	// 	}
	// 	return request
	// }

	function getUpdatedRequest(request: any) {
		for (let key of Object.keys(request)) {
			if (request[key] === '' || request[key] === null) {
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
							// const kategori = path.split('.')[0]
							const request = _.set(formikBag.values, path, value)
							getUpdatedRequest(request)
							setRequest(request)
							formikBag.setValues(request)
							mutate()
						}

						const handleChangeGammel = (value: any, path: string) => {
							// console.log('value: ', value) //TODO - SLETT MEG
							const testitest = getUpdatedRequest(formikBag.values)
							console.log('testitest: ', testitest) //TODO - SLETT MEG

							const kategori = path.split('.')[0]
							const updatedRequest =
								value !== null && value !== ''
									? _.set(formikBag.values, path, value)
									: _.omit(formikBag.values, path)
							const kategoriData = _.get(updatedRequest, kategori)
							// console.log('isEmpty(updatedRequest): ', isEmpty(updatedRequest)) //TODO - SLETT MEG
							if (isEmpty(updatedRequest)) {
								setRequest({})
								formikBag.setValues({})
							} else if (kategoriData && Object.keys(kategoriData)?.length === 0) {
								// if (isEmpty(updatedRequest)) {
								setRequest(_.omit(updatedRequest, kategori))
								// setRequest(_.omit(updatedRequest, path))
								formikBag.setFieldValue(kategori, undefined)
								// formikBag.setFieldValue(path, undefined)
							} else {
								setRequest(updatedRequest)
								if (value !== null && value !== '') {
									formikBag.setFieldValue(path, value)
								} else {
									formikBag.setFieldValue(path, undefined)
								}
							}
							//TODO: Funker ish, men om vi har objekt på flere nivåer + ande kategorier så blir det feil
							//TODO: Lag en slags findEmptyObjects, som går igjennom hele requesten?
							// console.log('isEmpty(updatedRequest): ', isEmpty(updatedRequest)) //TODO - SLETT MEG
							// console.log('updatedRequest: ', updatedRequest) //TODO - SLETT MEG
							// console.log('formikBag.values: ', formikBag.values) //TODO - SLETT MEG
							mutate()
							// TODO: sjekk om alle verdier OG underkategorier er tomme
						}

						const handleChangeList = (value: any, path: string) => {
							// console.log('value: ', value) //TODO - SLETT MEG
							const list = value.map((item: any) => item.value)
							console.log('list: ', list) //TODO - SLETT MEG
							const updatedRequest =
								list?.length > 0
									? _.set(formikBag.values, path, list)
									: _.omit(formikBag.values, path)
							// if (requestIsEmpty(updatedRequest)) {
							// 	setRequest(null)
							// } else {
							setRequest(updatedRequest)
							if (list?.length > 0) {
								formikBag.setFieldValue(path, list)
							} else {
								formikBag.setFieldValue(path, undefined)
							}
							// }
							mutate()
							// TODO: tilpass denne også
						}

						const handleChangeBoolean = (value: boolean, path: string) => {
							const updatedRequest = _.set(formikBag.values, path, value)
							setRequest(updatedRequest)
							formikBag.setFieldValue(path, value)
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
													handleChangeBoolean={handleChangeBoolean}
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
													handleChangeBoolean={handleChangeBoolean}
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
													handleChangeBoolean={handleChangeBoolean}
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
													handleChangeBoolean={handleChangeBoolean}
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
													handleChangeBoolean={handleChangeBoolean}
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
