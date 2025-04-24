import '@/styles/variables.less'
import { FormCheckbox } from '@/components/ui/form/inputs/checbox/Checkbox'
import React, { SyntheticEvent, useEffect, useState } from 'react'
import { FormSelect } from '@/components/ui/form/inputs/select/Select'
import { SelectOptionsManager as Options } from '@/service/SelectOptions'
import { Accordion, Button } from '@navikt/ds-react'
import { AdresseKodeverk, GtKodeverk } from '@/config/kodeverk'
import { ResultatVisning } from '@/pages/dollySoek/ResultatVisning'
import * as _ from 'lodash-es'
import { TestComponentSelectors } from '#/mocks/Selectors'
import { Form, FormProvider, useForm } from 'react-hook-form'
import {
	Buttons,
	Header,
	Soekefelt,
	SoekefeltWrapper,
	SoekKategori,
} from '@/components/ui/soekForm/SoekForm'
import { Hjelpetekst } from '@/components/hjelpetekst/Hjelpetekst'
import { usePersonerTyper } from '@/utils/hooks/useDollySearch'
import { FormTextInput } from '@/components/ui/form/inputs/textInput/TextInput'
import {
	AdresserPaths,
	AnnetPaths,
	FamilierelasjonerPaths,
	IdentifikasjonPaths,
	PersoniformasjonPaths,
} from '@/pages/dollySoek/paths'
import { DollyApi } from '@/service/Api'

const initialValues = {
	side: 0,
	antall: 10,
	seed: null,
	registreRequest: [],
	miljoer: [],
	personRequest: {
		identtype: null,
		kjoenn: null,
		alderFom: null,
		alderTom: null,
		sivilstand: null,
		erLevende: false,
		erDoed: false,
		harBarn: false,
		harForeldre: false,
		harDoedfoedtBarn: false,
		harForeldreAnsvar: false,
		harVerge: false,
		harInnflytting: false,
		harUtflytting: false,
		harKontaktinformasjonForDoedsbo: false,
		harUtenlandskIdentifikasjonsnummer: false,
		harFalskIdentitet: false,
		harTilrettelagtKommunikasjon: false,
		harSikkerhetstiltak: false,
		harOpphold: false,
		statsborgerskap: null,
		personStatus: null,
		harNyIdentitet: false,
		harSkjerming: false,
		adresse: {
			addressebeskyttelse: null,
			kommunenummer: null,
			postnummer: null,
			bydelsnummer: null,
			harBydelsnummer: false,
			harUtenlandsadresse: false,
			harMatrikkeladresse: false,
			harUkjentAdresse: false,
			harDeltBosted: false,
			harBostedsadresse: false,
			harKontaktadresse: false,
			harOppholdsadresse: false,
		},
	},
}

export const SoekForm = () => {
	const [request, setRequest] = useState(initialValues)
	const [result, setResult] = useState(null)
	const [soekPaagaar, setSoekPaagaar] = useState(false)
	const [soekError, setSoekError] = useState(null)
	const [visAntall, setVisAntall] = useState(10)

	const { typer, loading: loadingTyper } = usePersonerTyper()

	const personPath = 'personRequest'
	const adressePath = 'personRequest.adresse'
	const maxTotalHits = 10000

	const initialValuesClone = _.cloneDeep(initialValues)
	const formMethods = useForm({
		mode: 'onChange',
		defaultValues: initialValuesClone,
	})
	const { watch, reset, control, getValues } = formMethods
	const values = watch()

	useEffect(() => {
		setSoekPaagaar(true)
		setSoekError(null)
		DollyApi.personerSearch(request).then((response) => {
			if (response.error) {
				setResult(null)
				setSoekError(response.error)
				setSoekPaagaar(false)
			} else {
				setResult(response.data)
				setSoekPaagaar(false)
			}
		})
	}, [request])

	const handleChange = (value: any, path: string) => {
		const updatedPersonRequest = { ...values.personRequest, [path]: value }
		const updatedRequest = { ...values, personRequest: updatedPersonRequest, side: 0, seed: null }
		reset(updatedRequest)
		setRequest(updatedRequest)
	}

	const handleChangeAdresse = (value: any, path: string) => {
		const updatedAdresseRequest = { ...values.personRequest.adresse, [path]: value }
		const updatedRequest = {
			...values,
			side: 0,
			seed: null,
		}
		_.set(updatedRequest, 'personRequest.adresse', updatedAdresseRequest)
		reset(updatedRequest)
		setRequest(updatedRequest)
	}

	const handleChangeList = (value: any, path: string) => {
		const list = value.map((item: any) => item.value)
		const updatedRequest = { ...values, [path]: list, side: 0, seed: null }
		reset(updatedRequest)
		setRequest(updatedRequest)
	}

	const handleChangeSide = (side: number) => {
		const updatedRequest = { ...values, side: side - 1, seed: result?.seed }
		setRequest(updatedRequest)
	}

	const handleChangeAntall = (antall: { value: number }) => {
		setVisAntall(antall.value)
		const updatedRequest = { ...values, antall: antall.value, side: 0, seed: result?.seed }
		reset(updatedRequest)
		setRequest(updatedRequest)
	}

	const emptyCategory = (paths: string[]) => {
		const requestClone = { ...values }
		paths.forEach((path) => {
			_.set(requestClone, path, _.get(initialValues, path))
			if (path === 'personRequest.harSkjerming') {
				_.set(
					requestClone,
					'registreRequest',
					watch('registreRequest')?.filter((item: string) => item !== 'SKJERMING'),
				)
			}
		})
		const updatedRequest = { ...requestClone, side: 0, seed: null }
		reset(updatedRequest)
		setRequest(updatedRequest)
	}

	const emptySearch = () => {
		setVisAntall(10)
		reset(initialValues)
		setRequest(initialValues)
	}

	const getNewResult = () => {
		const updatedRequest = { ...values, side: 0, seed: null }
		setRequest(updatedRequest)
	}

	const antallFagsystemer = watch('registreRequest')?.length

	const getAntallRequest = (liste: Array<string>) => {
		let antall = 0
		liste.forEach((item) => {
			watch(item) && antall++
		})
		if (
			liste?.includes('personRequest.harSkjerming') &&
			watch('registreRequest')?.includes('SKJERMING')
		) {
			antall++
		}
		return antall
	}

	return (
		<>
			<SoekefeltWrapper>
				<Soekefelt>
					<FormProvider {...formMethods}>
						<Form control={control}>
							<>
								<div className="flexbox--flex-wrap">
									<Accordion size="small" headingSize="xsmall" className="flexbox--full-width">
										<Accordion.Item defaultOpen={true}>
											<Accordion.Header>
												<Header
													title="Fagsystemer"
													antall={antallFagsystemer}
													paths={['registreRequest', 'miljoer']}
													emptyCategory={emptyCategory}
												/>
											</Accordion.Header>
											<Accordion.Content>
												<div className="flexbox--full-width" style={{ fontSize: 'medium' }}>
													<FormSelect
														name="registreRequest"
														placeholder={
															loadingTyper ? 'Laster fagsystemer ...' : 'Velg fagsystemer ...'
														}
														title="Fagsystemer"
														options={typer}
														isMulti={true}
														size="grow"
														onChange={(val: SyntheticEvent) => {
															handleChangeList(val, 'registreRequest')
														}}
													/>
												</div>
												<div className="flexbox--full-width" style={{ fontSize: 'medium' }}>
													<FormSelect
														name="miljoer"
														placeholder="Velg miljøer ..."
														title="Miljøer"
														options={Options('miljoer')}
														isMulti={true}
														size="large"
														onChange={(val: SyntheticEvent) => {
															handleChangeList(val, 'miljoer')
														}}
													/>
												</div>
											</Accordion.Content>
										</Accordion.Item>
										<Accordion.Item>
											<Accordion.Header
												data-testid={TestComponentSelectors.EXPANDABLE_PERSONINFORMASJON}
											>
												<Header
													title="Personinformasjon"
													antall={getAntallRequest(PersoniformasjonPaths)}
													paths={PersoniformasjonPaths}
													getValues={getValues}
													emptyCategory={emptyCategory}
												/>
											</Accordion.Header>
											<Accordion.Content>
												<SoekKategori>
													<FormSelect
														classNamePrefix="select-kjoenn"
														name={`${personPath}.kjoenn`}
														options={Options('kjoenn')}
														size="small"
														placeholder="Velg kjønn ..."
														onChange={(val: SyntheticEvent) =>
															handleChange(val?.value || null, 'kjoenn')
														}
													/>
													<FormSelect
														name={`${personPath}.statsborgerskap`}
														kodeverk={AdresseKodeverk.StatsborgerskapLand}
														size="large"
														placeholder="Velg statsborgerskap ..."
														onChange={(val: SyntheticEvent) =>
															handleChange(val?.value || null, 'statsborgerskap')
														}
													/>
													<FormSelect
														name={`${personPath}.personStatus`}
														options={Options('personstatus')}
														size="medium"
														placeholder="Velg personstatus ..."
														onChange={(val: SyntheticEvent) =>
															handleChange(val?.value || null, 'personStatus')
														}
													/>
													<FormTextInput
														name={`${personPath}.alderFom`}
														placeholder="Skriv inn alder f.o.m ..."
														type="number"
														value={watch(`${personPath}.alderFom`)}
														onBlur={(val: SyntheticEvent) =>
															handleChange(val?.target?.value || null, 'alderFom')
														}
													/>
													<FormTextInput
														name={`${personPath}.alderTom`}
														placeholder="Skriv inn alder t.o.m ..."
														type="number"
														value={watch(`${personPath}.alderTom`)}
														onBlur={(val: SyntheticEvent) =>
															handleChange(val?.target?.value || null, 'alderTom')
														}
													/>
													<FormCheckbox
														name={`${personPath}.erLevende`}
														label="Er levende"
														onChange={(val: SyntheticEvent) =>
															handleChange(val.target.checked, 'erLevende')
														}
														disabled={watch(`${personPath}.erDoed`)}
													/>
													<FormCheckbox
														name={`${personPath}.erDoed`}
														label="Er død"
														onChange={(val: SyntheticEvent) =>
															handleChange(val.target.checked, 'erDoed')
														}
														disabled={watch(`${personPath}.erLevende`)}
													/>
													<FormCheckbox
														data-testid={TestComponentSelectors.TOGGLE_HAR_VERGE}
														name={`${personPath}.harVerge`}
														label="Har verge"
														onChange={(val: SyntheticEvent) =>
															handleChange(val.target.checked, 'harVerge')
														}
													/>
													<FormCheckbox
														name={`${personPath}.harInnflytting`}
														label="Har innflytting"
														onChange={(val: SyntheticEvent) =>
															handleChange(val.target.checked, 'harInnflytting')
														}
													/>
													<FormCheckbox
														name={`${personPath}.harUtflytting`}
														label="Har utflytting"
														onChange={(val: SyntheticEvent) =>
															handleChange(val.target.checked, 'harUtflytting')
														}
													/>
													<FormCheckbox
														name={`${personPath}.harSikkerhetstiltak`}
														label="Har sikkerhetstiltak"
														onChange={(val: SyntheticEvent) =>
															handleChange(val.target.checked, 'harSikkerhetstiltak')
														}
													/>
													<FormCheckbox
														name={`${personPath}.harTilrettelagtKommunikasjon`}
														label="Har tilrettelagt kommunikasjon"
														onChange={(val: SyntheticEvent) =>
															handleChange(val.target.checked, 'harTilrettelagtKommunikasjon')
														}
													/>
													<FormCheckbox
														name={`${personPath}.harSkjerming`}
														label="Har skjerming / er egen ansatt"
														checked={watch('registreRequest')?.includes('SKJERMING')}
														onChange={(val: SyntheticEvent) => {
															const registreValues = val.target.checked
																? [...watch('registreRequest'), 'SKJERMING']
																: watch('registreRequest')?.filter(
																		(item: string) => item !== 'SKJERMING',
																	)
															const updatedRequest = {
																...values,
																registreRequest: registreValues,
																side: 0,
																seed: null,
															}
															reset(updatedRequest)
															setRequest(updatedRequest)
														}}
													/>
													<div style={{ marginLeft: '-20px', marginTop: '3px' }}>
														<Hjelpetekst>
															Tilsvarer søk på Skjermingsregisteret under fagsystemer.
														</Hjelpetekst>
													</div>
												</SoekKategori>
											</Accordion.Content>
										</Accordion.Item>
										<Accordion.Item>
											<Accordion.Header>
												<Header
													title="Adresser"
													antall={getAntallRequest(AdresserPaths)}
													paths={AdresserPaths}
													getValues={getValues}
													emptyCategory={emptyCategory}
												/>
											</Accordion.Header>
											<Accordion.Content>
												<SoekKategori>
													<div className="flexbox--full-width">
														<div className="flexbox--flex-wrap">
															<FormSelect
																name={`${adressePath}.kommunenummer`}
																kodeverk={AdresseKodeverk.Kommunenummer}
																size="large"
																placeholder="Velg kommunenummer ..."
																onChange={(val: SyntheticEvent) =>
																	handleChangeAdresse(val?.value || null, 'kommunenummer')
																}
															/>
															<FormSelect
																name={`${adressePath}.postnummer`}
																kodeverk={AdresseKodeverk.Postnummer}
																size="large"
																placeholder="Velg postnummer ..."
																onChange={(val: SyntheticEvent) =>
																	handleChangeAdresse(val?.value || null, 'postnummer')
																}
															/>
															<FormSelect
																name={`${adressePath}.bydelsnummer`}
																kodeverk={GtKodeverk.BYDEL}
																size="large"
																placeholder="Velg bydelsnummer ..."
																onChange={(val: SyntheticEvent) =>
																	handleChangeAdresse(val?.value || null, 'bydelsnummer')
																}
															/>
															<FormSelect
																name={`${adressePath}.addressebeskyttelse`}
																options={Options('gradering')}
																size="xlarge"
																placeholder="Velg adressebeskyttelse (kode 6/7) ..."
																onChange={(val: SyntheticEvent) =>
																	handleChangeAdresse(val?.value || null, 'addressebeskyttelse')
																}
															/>
														</div>
													</div>
													<FormCheckbox
														name={`${adressePath}.harBostedsadresse`}
														label="Har bostedsadresse"
														onChange={(val: SyntheticEvent) =>
															handleChangeAdresse(val.target.checked, 'harBostedsadresse')
														}
													/>
													<FormCheckbox
														name={`${adressePath}.harOppholdsadresse`}
														label="Har oppholdsadresse"
														onChange={(val: SyntheticEvent) =>
															handleChangeAdresse(val.target.checked, 'harOppholdsadresse')
														}
													/>
													<FormCheckbox
														name={`${adressePath}.harKontaktadresse`}
														label="Har kontaktadresse"
														onChange={(val: SyntheticEvent) =>
															handleChangeAdresse(val.target.checked, 'harKontaktadresse')
														}
													/>
													{/*//Soek paa matrikkeladresse fungerer for tiden ikke*/}
													{/*<FormCheckbox*/}
													{/*	name={`${adressePath}.harMatrikkelAdresse`}*/}
													{/*	label="Har matrikkeladresse"*/}
													{/*	onChange={(val: SyntheticEvent) =>*/}
													{/*		handleChangeAdresse(val.target.checked, 'harMatrikkelAdresse')*/}
													{/*	}*/}
													{/*/>*/}
													<FormCheckbox
														name={`${adressePath}.harUtenlandsadresse`}
														label="Har utenlandsadresse"
														onChange={(val: SyntheticEvent) =>
															handleChangeAdresse(val.target.checked, 'harUtenlandsadresse')
														}
													/>
													<FormCheckbox
														name={`${adressePath}.harUkjentAdresse`}
														label="Har ukjent adresse"
														onChange={(val: SyntheticEvent) =>
															handleChangeAdresse(val.target.checked, 'harUkjentAdresse')
														}
													/>
													<FormCheckbox
														name={`${adressePath}.harBydelsnummer`}
														label="Har bydelsnummer"
														onChange={(val: SyntheticEvent) =>
															handleChangeAdresse(val.target.checked, 'harBydelsnummer')
														}
													/>
												</SoekKategori>
											</Accordion.Content>
										</Accordion.Item>
										<Accordion.Item>
											<Accordion.Header>
												<Header
													title="Familierelasjoner"
													antall={getAntallRequest(FamilierelasjonerPaths)}
													paths={FamilierelasjonerPaths}
													getValues={getValues}
													emptyCategory={emptyCategory}
												/>
											</Accordion.Header>
											<Accordion.Content>
												<SoekKategori>
													<FormSelect
														name={`${personPath}.sivilstand`}
														options={Options('sivilstandType')?.filter(
															(item) => item.value !== 'SAMBOER',
														)}
														size="large"
														placeholder="Velg sivilstand ..."
														onChange={(val: SyntheticEvent) =>
															handleChange(val?.value || null, 'sivilstand')
														}
													/>
													<FormCheckbox
														name={`${personPath}.harBarn`}
														label="Har barn"
														onChange={(val: SyntheticEvent) =>
															handleChange(val.target.checked, 'harBarn')
														}
													/>
													<FormCheckbox
														name={`${personPath}.harForeldre`}
														label="Har foreldre"
														onChange={(val: SyntheticEvent) =>
															handleChange(val.target.checked, 'harForeldre')
														}
													/>
													<FormCheckbox
														name={`${personPath}.harDoedfoedtBarn`}
														label="Har dødfødt barn"
														onChange={(val: SyntheticEvent) =>
															handleChange(val.target.checked, 'harDoedfoedtBarn')
														}
													/>
													<FormCheckbox
														name={`${personPath}.harForeldreAnsvar`}
														label="Har foreldreansvar"
														onChange={(val: SyntheticEvent) =>
															handleChange(val.target.checked, 'harForeldreAnsvar')
														}
													/>
													<FormCheckbox
														name={`${adressePath}.harDeltBosted`}
														label="Har delt bosted"
														onChange={(val: SyntheticEvent) =>
															handleChangeAdresse(val.target.checked, 'harDeltBosted')
														}
													/>
												</SoekKategori>
											</Accordion.Content>
										</Accordion.Item>
										<Accordion.Item>
											<Accordion.Header>
												<Header
													title="Identifikasjon"
													antall={getAntallRequest(IdentifikasjonPaths)}
													paths={IdentifikasjonPaths}
													getValues={getValues}
													emptyCategory={emptyCategory}
												/>
											</Accordion.Header>
											<Accordion.Content>
												<SoekKategori>
													<FormSelect
														name={`${personPath}.identtype`}
														options={Options('identtype')}
														size="small"
														placeholder="Velg identtype ..."
														onChange={(val: SyntheticEvent) =>
															handleChange(val?.value || null, 'identtype')
														}
													/>
													<FormCheckbox
														name={`${personPath}.harFalskIdentitet`}
														label="Har falsk identitet"
														onChange={(val: SyntheticEvent) =>
															handleChange(val.target.checked, 'harFalskIdentitet')
														}
													/>
													<FormCheckbox
														name={`${personPath}.harUtenlandskIdentifikasjonsnummer`}
														label="Har utenlandsk identitet"
														onChange={(val: SyntheticEvent) =>
															handleChange(val.target.checked, 'harUtenlandskIdentifikasjonsnummer')
														}
													/>
													<FormCheckbox
														name={`${personPath}.harNyIdentitet`}
														label="Har ny identitet"
														onChange={(val: SyntheticEvent) =>
															handleChange(val.target.checked, 'harNyIdentitet')
														}
													/>
												</SoekKategori>
											</Accordion.Content>
										</Accordion.Item>
										<Accordion.Item>
											<Accordion.Header>
												<Header
													title="Annet"
													antall={getAntallRequest(AnnetPaths)}
													paths={AnnetPaths}
													getValues={getValues}
													emptyCategory={emptyCategory}
												/>
											</Accordion.Header>
											<Accordion.Content>
												<SoekKategori>
													<FormCheckbox
														name={`${personPath}.harKontaktinformasjonForDoedsbo`}
														label="Har kontaktinformasjon for dødsbo"
														onChange={(val: SyntheticEvent) =>
															handleChange(val.target.checked, 'harKontaktinformasjonForDoedsbo')
														}
													/>
													<FormCheckbox
														name={`${personPath}.harOpphold`}
														label="Har opphold"
														onChange={(val: SyntheticEvent) =>
															handleChange(val.target.checked, 'harOpphold')
														}
													/>
												</SoekKategori>
											</Accordion.Content>
										</Accordion.Item>
									</Accordion>
								</div>
								<Buttons className="flexbox--flex-wrap">
									<Button
										onClick={getNewResult}
										variant="primary"
										disabled={soekPaagaar || !result}
										title={result?.totalHits < maxTotalHits ? 'Alle treff vises' : ''}
										loading={soekPaagaar}
										type="submit"
									>
										Hent nye treff
									</Button>
									<Button
										data-testid={TestComponentSelectors.BUTTON_NULLSTILL_SOEK}
										onClick={emptySearch}
										variant="secondary"
										disabled={!result}
									>
										Tøm felter
									</Button>
									{result && !soekPaagaar && (
										<p style={{ marginRight: 0, marginLeft: 'auto' }}>
											Viser {result?.personer?.length} av {result?.totalHits} treff
										</p>
									)}
								</Buttons>
							</>
						</Form>
					</FormProvider>
				</Soekefelt>
			</SoekefeltWrapper>
			<ResultatVisning
				resultat={result}
				loading={soekPaagaar}
				soekError={soekError}
				visAntall={visAntall}
				handleChangeSide={handleChangeSide}
				handleChangeAntall={handleChangeAntall}
			/>
		</>
	)
}
