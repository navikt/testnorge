import '@/styles/variables.less'
import { FormCheckbox } from '@/components/ui/form/inputs/checbox/Checkbox'
import React, { SyntheticEvent, useState } from 'react'
import { FormSelect } from '@/components/ui/form/inputs/select/Select'
import { SelectOptionsManager as Options } from '@/service/SelectOptions'
import { Accordion, Button } from '@navikt/ds-react'
import { AdresseKodeverk, GtKodeverk } from '@/config/kodeverk'
// import { useSoekIdenter, useSoekTyper } from '@/utils/hooks/usePersonSoek'
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

const initialValues = {
	registreRequest: [],
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
	// const { result, loading, error, mutate } = useSoekIdenter(request)
	const { result, loading, error, mutate } = usePersonerSearch(request)

	// const { typer, loading: loadingTyper } = useSoekTyper()
	const { typer, loading: loadingTyper } = usePersonerTyper()

	const personPath = 'personRequest'
	const adressePath = 'personRequest.adresse'

	const initialValuesClone = _.cloneDeep(initialValues)
	const formMethods = useForm({
		mode: 'onChange',
		defaultValues: initialValuesClone,
	})
	const { trigger, watch, handleSubmit, reset, control, setValue } = formMethods

	const preSubmit = () => {
		setRequest(watch())
		mutate()
	}
	const handleChange = (value: any, path: string) => {
		setValue(path, value)
		trigger(path)
		const updatedRequest = watch()
		if (requestIsEmpty(updatedRequest)) {
			setRequest(null)
		} else {
			setRequest(updatedRequest)
		}
		mutate()
	}

	const handleChangeList = (value: any, path: string) => {
		const list = value.map((item: any) => item.value)
		setValue(path, list)
		trigger(path)
		const updatedRequest = watch()
		if (requestIsEmpty(updatedRequest)) {
			setRequest(null)
		} else {
			setRequest(updatedRequest)
		}
		mutate()
	}

	const antallFagsystemer = watch('registreRequest')?.length

	const getAntallRequest = (liste: Array<string>) => {
		let antall = 0
		liste.forEach((item) => {
			watch(`personRequest.${item}`) && antall++
		})
		if (liste?.includes('harSkjerming') && watch('registreRequest')?.includes('SKJERMING')) {
			antall++
		}
		return antall
	}

	return (
		<>
			<SoekefeltWrapper>
				<Soekefelt>
					<FormProvider {...formMethods}>
						<Form control={control} onSubmit={() => handleSubmit(preSubmit(request))}>
							<>
								<div className="flexbox--flex-wrap">
									<Accordion size="small" headingSize="xsmall" className="flexbox--full-width">
										<Accordion.Item defaultOpen={true}>
											<Accordion.Header>
												<Header title="Fagsystemer" antall={antallFagsystemer} />
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
													antall={getAntallRequest([
														'kjoenn',
														'statsborgerskap',
														'personStatus',
														'alderFom',
														'alderTom',
														'erLevende',
														'erDoed',
														'harVerge',
														'harInnflytting',
														'harUtflytting',
														'harSikkerhetstiltak',
														'harTilrettelagtKommunikasjon',
														'harSkjerming',
													])}
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
														onChange={(val: SyntheticEvent) =>
															handleChange(val?.target?.value || null, `${personPath}.alderFom`)
														}
													/>
													<FormTextInput
														name={`${personPath}.alderTom`}
														placeholder="Skriv inn alder t.o.m ..."
														type="number"
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
															if (requestIsEmpty(updatedRequest)) {
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
													antall={getAntallRequest([
														'adresse.kommunenummer',
														'adresse.postnummer',
														'adresse.bydelsnummer',
														'adresse.addressebeskyttelse',
														'adresse.harBydelsnummer',
														'adresse.harBostedsadresse',
														'adresse.harUtenlandsadresse',
														'adresse.harMatrikkelAdresse',
														'adresse.harUkjentAdresse',
														'adresse.harKontaktadresse',
														'adresse.harOppholdsadresse',
													])}
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
																size="large"
																placeholder="Velg adressebeskyttelse ..."
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
													antall={getAntallRequest([
														'sivilstand',
														'harBarn',
														'harForeldre',
														'harDoedfoedtBarn',
														'harForeldreAnsvar',
														'adresse.harDeltBosted',
													])}
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
													antall={getAntallRequest([
														'harOpphold',
														'harKontaktinformasjonForDoedsbo',
													])}
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
										onClick={() => handleSubmit(preSubmit())}
										variant="primary"
										disabled={loading || !result}
										loading={loading}
										type="submit"
									>
										Hent nye treff
									</Button>
									<Button
										data-testid={TestComponentSelectors.BUTTON_NULLSTILL_SOEK}
										onClick={() => {
											setRequest(null)
											reset(null)
										}}
										variant="secondary"
										disabled={!result}
									>
										Nullstill søk
									</Button>
									{result && (
										<p style={{ marginRight: 0, marginLeft: 'auto' }}>
											Viser {result?.identer?.length} av totalt {result?.totalHits} treff
										</p>
									)}
								</Buttons>
							</>
						</Form>
					</FormProvider>
				</Soekefelt>
			</SoekefeltWrapper>
			<ResultatVisning resultat={result} soekError={error} />
		</>
	)
}
