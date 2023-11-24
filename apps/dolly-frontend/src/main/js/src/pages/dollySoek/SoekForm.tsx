import '@/styles/variables.less'
import styled from 'styled-components'
import { Form, Formik } from 'formik'
import { FormikCheckbox } from '@/components/ui/form/inputs/checbox/Checkbox'
import React, { SyntheticEvent, useState } from 'react'
import { FormikSelect } from '@/components/ui/form/inputs/select/Select'
import { SelectOptionsManager as Options } from '@/service/SelectOptions'
import { Accordion, Button } from '@navikt/ds-react'
import { AdresseKodeverk, GtKodeverk } from '@/config/kodeverk'
import { useSoekIdenter } from '@/utils/hooks/usePersonSoek'
import { SoekRequest } from '@/pages/dollySoek/DollySoekTypes'
import { ResultatVisning } from '@/pages/dollySoek/ResultatVisning'
import * as _ from 'lodash-es'

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

const Buttons = styled.div`
	margin: 15px 0 10px 0;
	&& {
		button {
			margin-right: 10px;
		}
	}
`

const initialValues = {
	typer: [],
	personRequest: {
		identtype: null,
		kjoenn: null,
		sivilstand: null,
		addressebeskyttelse: null,
		harBarn: false,
		harForeldre: false,
		harDoedfoedtBarn: false,
		harForeldreAnsvar: false,
		harVerge: false,
		harFullmakt: false,
		harDoedsfall: false,
		harInnflytting: false,
		harUtflytting: false,
		harKontaktinformasjonForDoedsbo: false,
		harUtenlandskIdentifikasjonsnummer: false,
		harFalskIdentitet: false,
		harTilrettelagtKommunikasjon: false,
		harSikkerhetstiltak: false,
		harOpphold: false,
		statsborgerskap: null,
		harNyIdentitet: false,
		bostedsadresse: {
			kommunenummer: null,
			postnummer: null,
			bydelsnummer: null,
			harBydelsnummer: false,
			harUtenlandsadresse: false,
			harMatrikkelAdresse: false,
			harUkjentAdresse: false,
		},
		harDeltBosted: false,
		harKontaktadresse: false,
		harOppholdsadresse: false,
	},
}

export const SoekForm = () => {
	const [request, setRequest] = useState(null)
	const { result, loading, error, mutate } = useSoekIdenter(request)

	const handleSubmit = (request: SoekRequest) => {
		setRequest(request)
		mutate()
	}

	const personPath = 'personRequest'

	const initialValuesClone = _.cloneDeep(initialValues)

	return (
		<>
			<SoekefeltWrapper>
				<Soekefelt>
					<Formik
						initialValues={initialValuesClone}
						onSubmit={(request: SoekRequest) => handleSubmit(request)}
					>
						{(formikBag) => {
							const handleChange = (value: any, path: string) => {
								const updatedRequest = _.set(formikBag.values, path, value)
								setRequest(updatedRequest)
								formikBag.setFieldValue(path, value)
								mutate()
							}

							const handleChangeList = (value: any, path: string) => {
								const list = value.map((item: any) => item.value)
								const updatedRequest = _.set(formikBag.values, path, list)
								setRequest(updatedRequest)
								mutate()
							}

							const getValue = (path: string) => {
								return _.get(formikBag.values, path)
							}

							return (
								<>
									<Form className="flexbox--flex-wrap" autoComplete="off">
										<Accordion size="small" headingSize="xsmall" className="flexbox--full-width">
											<Accordion.Item defaultOpen={true}>
												<Accordion.Header>Fagsystemer</Accordion.Header>
												<Accordion.Content>
													<div className="flexbox--full-width" style={{ marginBottom: '10px' }}>
														<FormikSelect
															name="typer"
															placeholder="Velg fagsystemer..."
															options={Options('registerTyper')}
															isMulti={true}
															size="grow"
															fastfield={false}
															onChange={(val: SyntheticEvent) => handleChangeList(val, 'typer')}
														/>
													</div>
												</Accordion.Content>
											</Accordion.Item>
											<Accordion.Item>
												<Accordion.Header>Personinformasjon</Accordion.Header>
												<Accordion.Content>
													<div className="flexbox--flex-wrap">
														<FormikSelect
															name={`${personPath}.kjoenn`}
															options={Options('kjoenn')}
															size="large"
															placeholder="Kjønn"
															onChange={(val: SyntheticEvent) =>
																handleChange(val?.value || null, `${personPath}.kjoenn`)
															}
															value={getValue(`${personPath}.kjoenn`)}
														/>
														<FormikSelect
															name={`${personPath}.statsborgerskap`}
															kodeverk={AdresseKodeverk.StatsborgerskapLand}
															size="large"
															placeholder="Statsborgerskap"
															onChange={(val: SyntheticEvent) =>
																handleChange(val?.value || null, `${personPath}.statsborgerskap`)
															}
															value={getValue(`${personPath}.statsborgerskap`)}
														/>
														<FormikCheckbox
															name={`${personPath}.harVerge`}
															label="Har verge"
															onChange={(val: SyntheticEvent) =>
																handleChange(val.target.checked, `${personPath}.harVerge`)
															}
															value={getValue(`${personPath}.harVerge`)}
														/>
														<FormikCheckbox
															name={`${personPath}.harFullmakt`}
															label="Har fullmakt"
															onChange={(val: SyntheticEvent) =>
																handleChange(val.target.checked, `${personPath}.harFullmakt`)
															}
															value={getValue(`${personPath}.harFullmakt`)}
														/>
														<FormikCheckbox
															name={`${personPath}.harDoedsfall`}
															label="Har dødsfall"
															onChange={(val: SyntheticEvent) =>
																handleChange(val.target.checked, `${personPath}.harDoedsfall`)
															}
															value={getValue(`${personPath}.harDoedsfall`)}
														/>
														<FormikCheckbox
															name={`${personPath}.harInnflytting`}
															label="Har innflytting"
															onChange={(val: SyntheticEvent) =>
																handleChange(val.target.checked, `${personPath}.harInnflytting`)
															}
															value={getValue(`${personPath}.harInnflytting`)}
														/>
														<FormikCheckbox
															name={`${personPath}.harUtflytting`}
															label="Har utflytting"
															onChange={(val: SyntheticEvent) =>
																handleChange(val.target.checked, `${personPath}.harUtflytting`)
															}
															value={getValue(`${personPath}.harUtflytting`)}
														/>
														<FormikCheckbox
															name={`${personPath}.harSikkerhetstiltak`}
															label="Har sikkerhetstiltak"
															onChange={(val: SyntheticEvent) =>
																handleChange(
																	val.target.checked,
																	`${personPath}.harSikkerhetstiltak`,
																)
															}
															value={getValue(`${personPath}.harSikkerhetstiltak`)}
														/>
														<FormikCheckbox
															name={`${personPath}.harTilrettelagtKommunikasjon`}
															label="Har tilrettelagt kommunikasjon"
															onChange={(val: SyntheticEvent) =>
																handleChange(
																	val.target.checked,
																	`${personPath}.harTilrettelagtKommunikasjon`,
																)
															}
															value={getValue(`${personPath}.harTilrettelagtKommunikasjon`)}
														/>
													</div>
												</Accordion.Content>
											</Accordion.Item>
											<Accordion.Item>
												<Accordion.Header>Adresser</Accordion.Header>
												<Accordion.Content>
													<div className="flexbox--flex-wrap">
														<FormikSelect
															name={`${personPath}.bostedsadresse.kommunenummer`}
															kodeverk={AdresseKodeverk.Kommunenummer}
															size="large"
															placeholder="Kommunenummer"
															onChange={(val: SyntheticEvent) =>
																handleChange(
																	val?.value || null,
																	`${personPath}.bostedsadresse.kommunenummer`,
																)
															}
															value={getValue(`${personPath}.bostedsadresse.kommunenummer`)}
														/>
														<FormikSelect
															name={`${personPath}.bostedsadresse.postnummer`}
															kodeverk={AdresseKodeverk.Postnummer}
															size="large"
															placeholder="Postnummer"
															onChange={(val: SyntheticEvent) =>
																handleChange(
																	val?.value || null,
																	`${personPath}.bostedsadresse.postnummer`,
																)
															}
															value={getValue(`${personPath}.bostedsadresse.postnummer`)}
														/>
														<FormikSelect
															name={`${personPath}.bostedsadresse.bydelsnummer`}
															kodeverk={GtKodeverk.BYDEL}
															size="large"
															placeholder="Bydelsnummer"
															onChange={(val: SyntheticEvent) =>
																handleChange(
																	val?.value || null,
																	`${personPath}.bostedsadresse.bydelsnummer`,
																)
															}
															value={getValue(`${personPath}.bostedsadresse.bydelsnummer`)}
														/>
														<FormikSelect
															name={`${personPath}.addressebeskyttelse`}
															options={Options('gradering')}
															size="large"
															placeholder="Adressebeskyttelse"
															onChange={(val: SyntheticEvent) =>
																handleChange(
																	val?.value || null,
																	`${personPath}.addressebeskyttelse`,
																)
															}
															value={getValue(`${personPath}.addressebeskyttelse`)}
														/>
														<FormikCheckbox
															name={`${personPath}.bostedsadresse.harBydelsnummer`}
															label="Har bydelsnummer"
															onChange={(val: SyntheticEvent) =>
																handleChange(
																	val.target.checked,
																	`${personPath}.bostedsadresse.harBydelsnummer`,
																)
															}
															value={getValue(`${personPath}.bostedsadresse.harBydelsnummer`)}
														/>
														<FormikCheckbox
															name={`${personPath}.bostedsadresse.harUtenlandsadresse`}
															label="Har utenlandsadresse"
															onChange={(val: SyntheticEvent) =>
																handleChange(
																	val.target.checked,
																	`${personPath}.bostedsadresse.harUtenlandsadresse`,
																)
															}
															value={getValue(`${personPath}.bostedsadresse.harUtenlandsadresse`)}
														/>
														<FormikCheckbox
															name={`${personPath}.bostedsadresse.harMatrikkelAdresse`}
															label="Har matrikkeladresse"
															onChange={(val: SyntheticEvent) =>
																handleChange(
																	val.target.checked,
																	`${personPath}.bostedsadresse.harMatrikkelAdresse`,
																)
															}
															value={getValue(`${personPath}.bostedsadresse.harMatrikkelAdresse`)}
														/>
														<FormikCheckbox
															name={`${personPath}.bostedsadresse.harUkjentAdresse`}
															label="Har ukjent adresse"
															onChange={(val: SyntheticEvent) =>
																handleChange(
																	val.target.checked,
																	`${personPath}.bostedsadresse.harUkjentAdresse`,
																)
															}
															value={getValue(`${personPath}.bostedsadresse.harUkjentAdresse`)}
														/>
														<FormikCheckbox
															name={`${personPath}.harKontaktadresse`}
															label="Har kontaktadresse"
															onChange={(val: SyntheticEvent) =>
																handleChange(val.target.checked, `${personPath}.harKontaktadresse`)
															}
															value={getValue(`${personPath}.harKontaktadresse`)}
														/>
														<FormikCheckbox
															name={`${personPath}.harOppholdsadresse`}
															label="Har oppholdsadresse"
															onChange={(val: SyntheticEvent) =>
																handleChange(val.target.checked, `${personPath}.harOppholdsadresse`)
															}
															value={getValue(`${personPath}.harOppholdsadresse`)}
														/>
													</div>
												</Accordion.Content>
											</Accordion.Item>
											<Accordion.Item>
												<Accordion.Header>Familierelasjoner</Accordion.Header>
												<Accordion.Content>
													<div className="flexbox--flex-wrap">
														<FormikSelect
															name={`${personPath}.sivilstand`}
															options={Options('sivilstandType')}
															size="large"
															placeholder="Sivilstand"
															onChange={(val: SyntheticEvent) =>
																handleChange(val?.value || null, `${personPath}.sivilstand`)
															}
															value={getValue(`${personPath}.sivilstand`)}
														/>
														<FormikCheckbox
															name={`${personPath}.harBarn`}
															label="Har barn"
															onChange={(val: SyntheticEvent) =>
																handleChange(val.target.checked, `${personPath}.harBarn`)
															}
															value={getValue(`${personPath}.harBarn`)}
														/>
														<FormikCheckbox
															name={`${personPath}.harForeldre`}
															label="Har foreldre"
															onChange={(val: SyntheticEvent) =>
																handleChange(val.target.checked, `${personPath}.harForeldre`)
															}
															value={getValue(`${personPath}.harForeldre`)}
														/>
														<FormikCheckbox
															name={`${personPath}.harDoedfoedtBarn`}
															label="Har dødfødt barn"
															onChange={(val: SyntheticEvent) =>
																handleChange(val.target.checked, `${personPath}.harDoedfoedtBarn`)
															}
															value={getValue(`${personPath}.harDoedfoedtBarn`)}
														/>
														<FormikCheckbox
															name={`${personPath}.harForeldreAnsvar`}
															label="Har foreldreansvar"
															onChange={(val: SyntheticEvent) =>
																handleChange(val.target.checked, `${personPath}.harForeldreAnsvar`)
															}
															value={getValue(`${personPath}.harForeldreAnsvar`)}
														/>
														<FormikCheckbox
															name={`${personPath}.harDeltBosted`}
															label="Har delt bosted"
															onChange={(val: SyntheticEvent) =>
																handleChange(val.target.checked, `${personPath}.harDeltBosted`)
															}
															value={getValue(`${personPath}.harDeltBosted`)}
														/>
													</div>
												</Accordion.Content>
											</Accordion.Item>
											<Accordion.Item>
												<Accordion.Header>Identifikasjon</Accordion.Header>
												<Accordion.Content>
													<div className="flexbox--flex-wrap">
														<FormikSelect
															name={`${personPath}.identtype`}
															options={Options('identtype')}
															size="large"
															placeholder="Identtype"
															onChange={(val: SyntheticEvent) =>
																handleChange(val?.value || null, `${personPath}.identtype`)
															}
															value={getValue(`${personPath}.identtype`)}
														/>
														<FormikCheckbox
															name={`${personPath}.harFalskIdentitet`}
															label="Har falsk identitet"
															onChange={(val: SyntheticEvent) =>
																handleChange(val.target.checked, `${personPath}.harFalskIdentitet`)
															}
															value={getValue(`${personPath}.harFalskIdentitet`)}
														/>
														<FormikCheckbox
															name={`${personPath}.harUtenlandskIdentifikasjonsnummer`}
															label="Har utenlandsk identifikasjonsnummer"
															onChange={(val: SyntheticEvent) =>
																handleChange(
																	val.target.checked,
																	`${personPath}.harUtenlandskIdentifikasjonsnummer`,
																)
															}
															value={getValue(`${personPath}.harUtenlandskIdentifikasjonsnummer`)}
														/>
														<FormikCheckbox
															name={`${personPath}.harNyIdentitet`}
															label="Har ny identitet"
															onChange={(val: SyntheticEvent) =>
																handleChange(val.target.checked, `${personPath}.harNyIdentitet`)
															}
															value={getValue(`${personPath}.harNyIdentitet`)}
														/>
													</div>
												</Accordion.Content>
											</Accordion.Item>
											<Accordion.Item>
												<Accordion.Header>Annet</Accordion.Header>
												<Accordion.Content>
													<div className="flexbox--flex-wrap">
														<FormikCheckbox
															name={`${personPath}.harKontaktinformasjonForDoedsbo`}
															label="Har kontaktinformasjon for dødsbo"
															onChange={(val: SyntheticEvent) =>
																handleChange(
																	val.target.checked,
																	`${personPath}.harKontaktinformasjonForDoedsbo`,
																)
															}
															value={getValue(`${personPath}.harKontaktinformasjonForDoedsbo`)}
														/>
														<FormikCheckbox
															name={`${personPath}.harOpphold`}
															label="Har opphold"
															onChange={(val: SyntheticEvent) =>
																handleChange(val.target.checked, `${personPath}.harOpphold`)
															}
															value={getValue(`${personPath}.harOpphold`)}
														/>
													</div>
												</Accordion.Content>
											</Accordion.Item>
										</Accordion>
									</Form>
									<Buttons className="flexbox--flex-wrap">
										<Button
											onClick={() => formikBag.handleSubmit()}
											variant="primary"
											disabled={loading || !result}
											loading={loading}
											type="submit"
										>
											Hent nye treff
										</Button>
										<Button
											onClick={() => {
												setRequest(null)
												formikBag.setValues(initialValuesClone)
											}}
											variant="secondary"
											disabled={!result}
										>
											Nullstill søk
										</Button>
									</Buttons>
								</>
							)
						}}
					</Formik>
				</Soekefelt>
			</SoekefeltWrapper>
			<ResultatVisning resultat={result} />
		</>
	)
}
