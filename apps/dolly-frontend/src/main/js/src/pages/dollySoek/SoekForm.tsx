import '@/styles/variables.less'
import { FormCheckbox } from '@/components/ui/form/inputs/checbox/Checkbox'
import React, { SyntheticEvent, useState } from 'react'
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
	requestIsEmpty,
	Soekefelt,
	SoekefeltWrapper,
	SoekKategori,
} from '@/components/ui/soekForm/SoekForm'
import { Hjelpetekst } from '@/components/hjelpetekst/Hjelpetekst'
import { usePersonerSearch, usePersonerTyper } from '@/utils/hooks/useDollySearch'
import { FormTextInput } from '@/components/ui/form/inputs/textInput/TextInput'
import {
	AdresserPaths,
	AnnetPaths,
	FamilierelasjonerPaths,
	IdentifikasjonPaths,
	PersoniformasjonPaths,
} from '@/pages/dollySoek/paths'

const initialValues = {
	side: 0,
	antall: 10,
	seed: null,
	registreRequest: [],
	personRequest: {
		ident: null,
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
	const [request, setRequest] = useState(null as any)
	const [visAntall, setVisAntall] = useState(10)
	const [soekPaagaar, setSoekPaagaar] = useState(false)
	const { result, loading, error, mutate } = usePersonerSearch(request)
	const { typer, loading: loadingTyper } = usePersonerTyper()

	const personPath = 'personRequest'
	const adressePath = 'personRequest.adresse'

	const initialValuesClone = _.cloneDeep(initialValues)
	const formMethods = useForm({
		mode: 'onChange',
		defaultValues: initialValuesClone,
	})
	const { trigger, watch, handleSubmit, reset, control, setValue, getValues } = formMethods

	const handleChange = (value: any, path: string) => {
		setSoekPaagaar(true)
		setValue(path, value)
		setValue('side', 0)
		setValue('seed', null)
		const updatedRequest = watch()
		if (
			requestIsEmpty({
				personRequest: updatedRequest.personRequest,
				registreRequest: updatedRequest.registreRequest,
			})
		) {
			setRequest(null)
			setVisAntall(10)
		} else {
			setRequest(updatedRequest)
		}
		mutate().then(() => setSoekPaagaar(false))
	}

	const handleChangeList = (value: any, path: string) => {
		const list = value.map((item: any) => item.value)
		handleChange(list, path)
	}

	const handleChangeSide = (side: number) => {
		setValue('side', side - 1)
		setValue('seed', result.seed)
		const updatedRequest = watch()
		setRequest(updatedRequest)
		mutate()
	}

	const handleChangeAntall = (antall: { value: number }) => {
		setValue('antall', antall.value)
		setValue('side', 0)
		setValue('seed', result.seed)
		setVisAntall(antall.value)
		const updatedRequest = watch()
		setRequest(updatedRequest)
		mutate()
	}

	const emptyCategory = (paths: string[]) => {
		paths.forEach((path) => {
			setValue(path, _.get(initialValues, path))
			if (path === 'personRequest.harSkjerming') {
				setValue(
					'registreRequest',
					watch('registreRequest')?.filter((item: string) => item !== 'SKJERMING'),
				)
			}
		})
		setValue('side', 0)
		setValue('seed', null)
		const updatedRequest = watch()
		if (
			requestIsEmpty({
				personRequest: updatedRequest.personRequest,
				registreRequest: updatedRequest.registreRequest,
			})
		) {
			setRequest(null)
			setVisAntall(10)
		} else {
			setRequest(updatedRequest)
		}
		mutate()
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
						<Form control={control} onSubmit={handleSubmit}>
							<>
								<div className="flexbox--flex-wrap">
									<Accordion size="small" headingSize="xsmall" className="flexbox--full-width">
										<Accordion.Item defaultOpen={true}>
											<Accordion.Header>
												<Header
													title="Fagsystemer"
													antall={antallFagsystemer}
													paths={['registreRequest']}
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
															handleChange(val?.value || null, `${personPath}.kjoenn`)
														}
													/>
													<FormSelect
														name={`${personPath}.statsborgerskap`}
														kodeverk={AdresseKodeverk.StatsborgerskapLand}
														size="large"
														placeholder="Velg statsborgerskap ..."
														onChange={(val: SyntheticEvent) =>
															handleChange(val?.value || null, `${personPath}.statsborgerskap`)
														}
													/>
													<FormSelect
														name={`${personPath}.personStatus`}
														options={Options('personstatus')}
														size="medium"
														placeholder="Velg personstatus ..."
														onChange={(val: SyntheticEvent) =>
															handleChange(val?.value || null, `${personPath}.personStatus`)
														}
													/>
													<FormTextInput
														name={`${personPath}.alderFom`}
														placeholder="Skriv inn alder f.o.m ..."
														type="number"
														value={watch(`${personPath}.alderFom`)}
														onChange={(val: SyntheticEvent) =>
															handleChange(val?.target?.value || null, `${personPath}.alderFom`)
														}
													/>
													<FormTextInput
														name={`${personPath}.alderTom`}
														placeholder="Skriv inn alder t.o.m ..."
														type="number"
														value={watch(`${personPath}.alderTom`)}
														onChange={(val: SyntheticEvent) =>
															handleChange(val?.target?.value || null, `${personPath}.alderTom`)
														}
													/>
													<FormCheckbox
														name={`${personPath}.erLevende`}
														label="Er levende"
														onChange={(val: SyntheticEvent) =>
															handleChange(val.target.checked, `${personPath}.erLevende`)
														}
														disabled={watch(`${personPath}.erDoed`)}
													/>
													<FormCheckbox
														name={`${personPath}.erDoed`}
														label="Er død"
														onChange={(val: SyntheticEvent) =>
															handleChange(val.target.checked, `${personPath}.erDoed`)
														}
														disabled={watch(`${personPath}.erLevende`)}
													/>
													<FormCheckbox
														data-testid={TestComponentSelectors.TOGGLE_HAR_VERGE}
														name={`${personPath}.harVerge`}
														label="Har verge"
														onChange={(val: SyntheticEvent) =>
															handleChange(val.target.checked, `${personPath}.harVerge`)
														}
													/>
													<FormCheckbox
														name={`${personPath}.harInnflytting`}
														label="Har innflytting"
														onChange={(val: SyntheticEvent) =>
															handleChange(val.target.checked, `${personPath}.harInnflytting`)
														}
													/>
													<FormCheckbox
														name={`${personPath}.harUtflytting`}
														label="Har utflytting"
														onChange={(val: SyntheticEvent) =>
															handleChange(val.target.checked, `${personPath}.harUtflytting`)
														}
													/>
													<FormCheckbox
														name={`${personPath}.harSikkerhetstiltak`}
														label="Har sikkerhetstiltak"
														onChange={(val: SyntheticEvent) =>
															handleChange(val.target.checked, `${personPath}.harSikkerhetstiltak`)
														}
													/>
													<FormCheckbox
														name={`${personPath}.harTilrettelagtKommunikasjon`}
														label="Har tilrettelagt kommunikasjon"
														onChange={(val: SyntheticEvent) =>
															handleChange(
																val.target.checked,
																`${personPath}.harTilrettelagtKommunikasjon`,
															)
														}
													/>
													<FormCheckbox
														name={`${personPath}.harSkjerming`}
														label="Har skjerming / er egen ansatt"
														checked={watch('registreRequest')?.includes('SKJERMING')}
														onChange={(val: SyntheticEvent) => {
															setValue(
																'registreRequest',
																val.target.checked
																	? [...watch('registreRequest'), 'SKJERMING']
																	: watch('registreRequest')?.filter(
																			(item: string) => item !== 'SKJERMING',
																		),
															)
															trigger('registreRequest')
															const updatedRequest = watch()
															if (
																requestIsEmpty({
																	personRequest: updatedRequest.personRequest,
																	registreRequest: updatedRequest.registreRequest,
																})
															) {
																setRequest(null)
															} else {
																setRequest(updatedRequest)
															}
															mutate()
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
																	handleChange(val?.value || null, `${adressePath}.kommunenummer`)
																}
															/>
															<FormSelect
																name={`${adressePath}.postnummer`}
																kodeverk={AdresseKodeverk.Postnummer}
																size="large"
																placeholder="Velg postnummer ..."
																onChange={(val: SyntheticEvent) =>
																	handleChange(val?.value || null, `${adressePath}.postnummer`)
																}
															/>
															<FormSelect
																name={`${adressePath}.bydelsnummer`}
																kodeverk={GtKodeverk.BYDEL}
																size="large"
																placeholder="Velg bydelsnummer ..."
																onChange={(val: SyntheticEvent) =>
																	handleChange(val?.value || null, `${adressePath}.bydelsnummer`)
																}
															/>
															<FormSelect
																name={`${adressePath}.addressebeskyttelse`}
																options={Options('gradering')}
																size="xlarge"
																placeholder="Velg adressebeskyttelse (kode 6/7) ..."
																onChange={(val: SyntheticEvent) =>
																	handleChange(
																		val?.value || null,
																		`${adressePath}.addressebeskyttelse`,
																	)
																}
															/>
														</div>
													</div>
													<FormCheckbox
														name={`${adressePath}.harBostedsadresse`}
														label="Har bostedsadresse"
														onChange={(val: SyntheticEvent) =>
															handleChange(val.target.checked, `${adressePath}.harBostedsadresse`)
														}
													/>
													<FormCheckbox
														name={`${adressePath}.harOppholdsadresse`}
														label="Har oppholdsadresse"
														onChange={(val: SyntheticEvent) =>
															handleChange(val.target.checked, `${adressePath}.harOppholdsadresse`)
														}
													/>
													<FormCheckbox
														name={`${adressePath}.harKontaktadresse`}
														label="Har kontaktadresse"
														onChange={(val: SyntheticEvent) =>
															handleChange(val.target.checked, `${adressePath}.harKontaktadresse`)
														}
													/>
													<FormCheckbox
														name={`${adressePath}.harMatrikkelAdresse`}
														label="Har matrikkeladresse"
														onChange={(val: SyntheticEvent) =>
															handleChange(val.target.checked, `${adressePath}.harMatrikkelAdresse`)
														}
													/>
													<FormCheckbox
														name={`${adressePath}.harUtenlandsadresse`}
														label="Har utenlandsadresse"
														onChange={(val: SyntheticEvent) =>
															handleChange(val.target.checked, `${adressePath}.harUtenlandsadresse`)
														}
													/>
													<FormCheckbox
														name={`${adressePath}.harUkjentAdresse`}
														label="Har ukjent adresse"
														onChange={(val: SyntheticEvent) =>
															handleChange(val.target.checked, `${adressePath}.harUkjentAdresse`)
														}
													/>
													<FormCheckbox
														name={`${adressePath}.harBydelsnummer`}
														label="Har bydelsnummer"
														onChange={(val: SyntheticEvent) =>
															handleChange(val.target.checked, `${adressePath}.harBydelsnummer`)
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
														options={Options('sivilstandType')}
														size="large"
														placeholder="Velg sivilstand ..."
														onChange={(val: SyntheticEvent) =>
															handleChange(val?.value || null, `${personPath}.sivilstand`)
														}
													/>
													<FormCheckbox
														name={`${personPath}.harBarn`}
														label="Har barn"
														onChange={(val: SyntheticEvent) =>
															handleChange(val.target.checked, `${personPath}.harBarn`)
														}
													/>
													<FormCheckbox
														name={`${personPath}.harForeldre`}
														label="Har foreldre"
														onChange={(val: SyntheticEvent) =>
															handleChange(val.target.checked, `${personPath}.harForeldre`)
														}
													/>
													<FormCheckbox
														name={`${personPath}.harDoedfoedtBarn`}
														label="Har dødfødt barn"
														onChange={(val: SyntheticEvent) =>
															handleChange(val.target.checked, `${personPath}.harDoedfoedtBarn`)
														}
													/>
													<FormCheckbox
														name={`${personPath}.harForeldreAnsvar`}
														label="Har foreldreansvar"
														onChange={(val: SyntheticEvent) =>
															handleChange(val.target.checked, `${personPath}.harForeldreAnsvar`)
														}
													/>
													<FormCheckbox
														name={`${adressePath}.harDeltBosted`}
														label="Har delt bosted"
														onChange={(val: SyntheticEvent) =>
															handleChange(val.target.checked, `${adressePath}.harDeltBosted`)
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
													<FormTextInput
														name={`${personPath}.ident`}
														placeholder="Skriv inn ident ..."
														size="large"
														value={watch(`${personPath}.ident`)}
														onChange={(val: SyntheticEvent) =>
															handleChange(val?.target?.value || null, `${personPath}.ident`)
														}
													/>
													<FormSelect
														name={`${personPath}.identtype`}
														options={Options('identtype')}
														size="small"
														placeholder="Velg identtype ..."
														onChange={(val: SyntheticEvent) =>
															handleChange(val?.value || null, `${personPath}.identtype`)
														}
													/>
													<FormCheckbox
														name={`${personPath}.harFalskIdentitet`}
														label="Har falsk identitet"
														onChange={(val: SyntheticEvent) =>
															handleChange(val.target.checked, `${personPath}.harFalskIdentitet`)
														}
													/>
													<FormCheckbox
														name={`${personPath}.harUtenlandskIdentifikasjonsnummer`}
														label="Har utenlandsk identitet"
														onChange={(val: SyntheticEvent) =>
															handleChange(
																val.target.checked,
																`${personPath}.harUtenlandskIdentifikasjonsnummer`,
															)
														}
													/>
													<FormCheckbox
														name={`${personPath}.harNyIdentitet`}
														label="Har ny identitet"
														onChange={(val: SyntheticEvent) =>
															handleChange(val.target.checked, `${personPath}.harNyIdentitet`)
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
															handleChange(
																val.target.checked,
																`${personPath}.harKontaktinformasjonForDoedsbo`,
															)
														}
													/>
													<FormCheckbox
														name={`${personPath}.harOpphold`}
														label="Har opphold"
														onChange={(val: SyntheticEvent) =>
															handleChange(val.target.checked, `${personPath}.harOpphold`)
														}
													/>
												</SoekKategori>
											</Accordion.Content>
										</Accordion.Item>
									</Accordion>
								</div>
								<Buttons className="flexbox--flex-wrap">
									<Button
										onClick={handleSubmit}
										variant="primary"
										disabled={loading || soekPaagaar || !result}
										loading={loading || soekPaagaar}
										type="submit"
									>
										Hent nye treff
									</Button>
									<Button
										data-testid={TestComponentSelectors.BUTTON_NULLSTILL_SOEK}
										onClick={() => {
											setRequest(null)
											reset()
										}}
										variant="secondary"
										disabled={!result}
									>
										Nullstill søk
									</Button>
									{result && !loading && !soekPaagaar && (
										<p style={{ marginRight: 0, marginLeft: 'auto' }}>
											Viser {result?.personer?.length} av totalt {result?.totalHits} treff
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
				loading={loading || soekPaagaar}
				soekError={error}
				visAntall={visAntall}
				handleChangeSide={handleChangeSide}
				handleChangeAntall={handleChangeAntall}
			/>
		</>
	)
}
