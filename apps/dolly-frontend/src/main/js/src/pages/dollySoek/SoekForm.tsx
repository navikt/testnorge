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

const SoekKategori = styled.div`
	display: flex;
	flex-wrap: wrap;
	font-size: medium;
	&& {
		.dolly-form-input {
			min-width: 0;
			flex-grow: 0;
		}
	}
	&& {
		.navds-checkbox__icon {
			margin-top: -4px;
		}
	}
`

const Buttons = styled.div`
	margin: 15px 0 10px 0;
	&& {
		button {
			margin-right: 10px;
		}
	}
`

const KategoriHeader = styled.div`
	display: flex;
	align-items: center;
`

const KategoriCircle = styled.div`
	display: flex;
	width: 20px;
	height: 20px;
	border-radius: 50%;
	margin-left: 10px;
	background-color: #0067c5ff;
	&& {
		p {
			margin: auto;
			margin-top: -1px;
			font-size: 15px;
			font-weight: bold;
			color: white;
			padding-bottom: 5px;
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

const Header = ({ title, antall }) => (
	<KategoriHeader>
		<span>{title}</span>
		{antall > 0 && (
			<KategoriCircle>
				<p>{antall}</p>
			</KategoriCircle>
		)}
	</KategoriHeader>
)

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

							const antallFagsystemer = _.get(formikBag.values, 'typer')?.length

							const getAntallRequest = (liste: Array<string>) => {
								let antall = 0
								liste.forEach((item) => {
									_.get(formikBag.values.personRequest, item) && antall++
								})
								return antall
							}

							return (
								<>
									<Form className="flexbox--flex-wrap" autoComplete="off">
										<Accordion size="small" headingSize="xsmall" className="flexbox--full-width">
											<Accordion.Item defaultOpen={true}>
												<Accordion.Header>
													<Header title="Fagsystemer" antall={antallFagsystemer} />
												</Accordion.Header>
												<Accordion.Content>
													<div className="flexbox--full-width" style={{ fontSize: 'medium' }}>
														<FormikSelect
															name="typer"
															placeholder="Velg fagsystemer ..."
															title="Fagsystemer"
															options={Options('registerTyper')}
															isMulti={true}
															size="grow"
															// fastfield={false}
															onChange={(val: SyntheticEvent) => handleChangeList(val, 'typer')}
															value={getValue('typer')}
														/>
													</div>
												</Accordion.Content>
											</Accordion.Item>
											<Accordion.Item>
												<Accordion.Header>
													<Header
														title="Personinformasjon"
														antall={getAntallRequest([
															'kjoenn',
															'statsborgerskap',
															'harVerge',
															'harFullmakt',
															'harDoedsfall',
															'harInnflytting',
															'harUtflytting',
															'harSikkerhetstiltak',
															'harTilrettelagtKommunikasjon',
														])}
													/>
												</Accordion.Header>
												<Accordion.Content>
													<SoekKategori>
														<FormikSelect
															name={`${personPath}.kjoenn`}
															options={Options('kjoenn')}
															size="small"
															placeholder="Velg kjønn ..."
															onChange={(val: SyntheticEvent) =>
																handleChange(val?.value || null, `${personPath}.kjoenn`)
															}
															value={getValue(`${personPath}.kjoenn`)}
														/>
														<FormikSelect
															name={`${personPath}.statsborgerskap`}
															kodeverk={AdresseKodeverk.StatsborgerskapLand}
															size="large"
															placeholder="Velg statsborgerskap ..."
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
													</SoekKategori>
												</Accordion.Content>
											</Accordion.Item>
											<Accordion.Item>
												<Accordion.Header>
													<Header
														title="Adresser"
														antall={getAntallRequest([
															'bostedsadresse.kommunenummer',
															'bostedsadresse.postnummer',
															'bostedsadresse.bydelsnummer',
															'addressebeskyttelse',
															'bostedsadresse.harBydelsnummer',
															'bostedsadresse.harUtenlandsadresse',
															'bostedsadresse.harMatrikkelAdresse',
															'bostedsadresse.harUkjentAdresse',
															'harKontaktadresse',
															'harOppholdsadresse',
														])}
													/>
												</Accordion.Header>
												<Accordion.Content>
													<SoekKategori>
														<div className="flexbox--full-width">
															<div className="flexbox--flex-wrap">
																<FormikSelect
																	name={`${personPath}.bostedsadresse.kommunenummer`}
																	kodeverk={AdresseKodeverk.Kommunenummer}
																	size="large"
																	placeholder="Velg kommunenummer ..."
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
																	placeholder="Velg postnummer ..."
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
																	placeholder="Velg bydelsnummer ..."
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
																	placeholder="Velg adressebeskyttelse ..."
																	onChange={(val: SyntheticEvent) =>
																		handleChange(
																			val?.value || null,
																			`${personPath}.addressebeskyttelse`,
																		)
																	}
																	value={getValue(`${personPath}.addressebeskyttelse`)}
																/>
															</div>
														</div>
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
													</SoekKategori>
												</Accordion.Content>
											</Accordion.Item>
											<Accordion.Item>
												<Accordion.Header>
													<Header
														title="Familierelasjoner"
														antall={getAntallRequest([
															'sivilstand',
															'harBarn',
															'harForeldre',
															'harDoedfoedtBarn',
															'harForeldreAnsvar',
															'harDeltBosted',
														])}
													/>
												</Accordion.Header>
												<Accordion.Content>
													<SoekKategori>
														<FormikSelect
															name={`${personPath}.sivilstand`}
															options={Options('sivilstandType')}
															size="large"
															placeholder="Velg sivilstand ..."
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
													</SoekKategori>
												</Accordion.Content>
											</Accordion.Item>
											<Accordion.Item>
												<Accordion.Header>
													<Header
														title="Identifikasjon"
														antall={getAntallRequest([
															'identtype',
															'harFalskIdentitet',
															'harUtenlandskIdentifikasjonsnummer',
															'harNyIdentitet',
														])}
													/>
												</Accordion.Header>
												<Accordion.Content>
													<SoekKategori>
														<FormikSelect
															name={`${personPath}.identtype`}
															options={Options('identtype')}
															size="small"
															placeholder="Velg identtype ..."
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
															label="Har utenlandsk identitet"
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
													</SoekKategori>
												</Accordion.Content>
											</Accordion.Item>
											<Accordion.Item>
												<Accordion.Header>
													<Header
														title="Annet"
														antall={getAntallRequest([
															'harOpphold',
															'harKontaktinformasjonForDoedsbo',
														])}
													/>
												</Accordion.Header>
												<Accordion.Content>
													<SoekKategori>
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
													</SoekKategori>
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
