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

	return (
		<>
			<SoekefeltWrapper>
				<Soekefelt>
					{/*<Formik initialValues={initialValues} onSubmit={(request) => handleSubmit(request)}>*/}
					<Formik initialValues={{}} onSubmit={() => console.log('submit...')}>
						{(formikBag) => {
							const handleChangeGammel = (value: any, path: string) => {
								console.log('value: ', value) //TODO - SLETT MEG
								const updatedRequest = value
									? _.set(formikBag.values, path, value)
									: _.omit(formikBag.values, path)
								// console.log('formikBag.values: ', formikBag.values) //TODO - SLETT MEG
								// console.log('updatedRequest: ', updatedRequest) //TODO - SLETT MEG
								setRequest(updatedRequest)
								if (value) {
									formikBag.setFieldValue(path, value)
								} else {
									formikBag.setFieldValue(path, undefined)
								}
								mutate()
							}

							const handleChange = (value: any, path: string) => {
								console.log('value: ', value === '') //TODO - SLETT MEG
								const kategori = path.split('.')[0]
								const updatedRequest =
									value !== null && value !== ''
										? _.set(formikBag.values, path, value)
										: _.omit(formikBag.values, path)
								if (Object.keys(_.get(updatedRequest, kategori))?.length === 0) {
									setRequest(_.omit(updatedRequest, kategori))
									formikBag.setFieldValue(kategori, undefined)
								} else {
									setRequest(updatedRequest)
									if (value !== null && value !== '') {
										formikBag.setFieldValue(path, value)
									} else {
										formikBag.setFieldValue(path, undefined)
									}
								}
								mutate()
								// TODO: sjekk om alle verdier OG inderkategorier er tomme
							}

							// const handleChange = (value: any, path: string, kategori: string) => {
							// 	if (value || value === false) {
							// 		formikBag.setFieldValue(path, value)
							// 	} else {
							// 		formikBag.setFieldValue(path, undefined)
							// 	}
							// 	if (Object.keys(_.get(formikBag.values, kategori))?.length === 0) {
							// 		formikBag.setFieldValue(kategori, undefined)
							// 	}
							// 	setRequest(formikBag.values)
							// 	console.log('request: ', request) //TODO - SLETT MEG
							// 	mutate()
							// }

							const handleChangeList = (value: any, path: string) => {
								// console.log('value: ', value) //TODO - SLETT MEG
								const list = value.map((item: any) => item.value)
								const updatedRequest = _.set(formikBag.values, path, list)
								// if (requestIsEmpty(updatedRequest)) {
								// 	setRequest(null)
								// } else {
								setRequest(updatedRequest)
								// }
								mutate()
							}

							const getValue = (path: string) => {
								return _.get(formikBag.values, path)
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
													<Header title="Inntekt A-ordningen" antall={0} />
												</Accordion.Header>
												<Accordion.Content>
													<InntektAordningen
														formikBag={formikBag}
														handleChange={handleChange}
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
		</>
	)
}
