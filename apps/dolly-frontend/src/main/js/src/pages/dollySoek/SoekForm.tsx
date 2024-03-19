import '@/styles/variables.less'
import { FormCheckbox } from '@/components/ui/form/inputs/checbox/Checkbox'
import React, { SyntheticEvent, useState } from 'react'
import { FormSelect } from '@/components/ui/form/inputs/select/Select'
import { SelectOptionsManager as Options } from '@/service/SelectOptions'
import { Accordion, Button } from '@navikt/ds-react'
import { AdresseKodeverk, GtKodeverk } from '@/config/kodeverk'
import { useSoekIdenter } from '@/utils/hooks/usePersonSoek'
import { ResultatVisning } from '@/pages/dollySoek/ResultatVisning'
import _ from 'lodash'
import { CypressSelector } from '../../../cypress/mocks/Selectors'
import { Form, FormProvider, useForm } from 'react-hook-form'
import {
	Buttons,
	Header,
	requestIsEmpty,
	Soekefelt,
	SoekefeltWrapper,
	SoekKategori,
} from '@/components/ui/soekForm/SoekForm'

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
	const [request, setRequest] = useState(null as any)
	const { result, loading, error, mutate } = useSoekIdenter(request)

	const personPath = 'personRequest'

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

	const antallFagsystemer = watch('typer')?.length

	const getAntallRequest = (liste: Array<string>) => {
		let antall = 0
		liste.forEach((item) => {
			watch(`personRequest.${item}`) && antall++
		})
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
														name="typer"
														placeholder="Velg fagsystemer ..."
														title="Fagsystemer"
														options={Options('registerTyper')}
														isMulti={true}
														size="grow"
														onChange={(val: SyntheticEvent) => {
															handleChangeList(val, 'typer')
														}}
													/>
												</div>
											</Accordion.Content>
										</Accordion.Item>
										<Accordion.Item>
											<Accordion.Header data-cy={CypressSelector.EXPANDABLE_PERSONINFORMASJON}>
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
													<FormCheckbox
														data-cy={CypressSelector.TOGGLE_HAR_VERGE}
														name={`${personPath}.harVerge`}
														label="Har verge"
														onChange={(val: SyntheticEvent) =>
															handleChange(val.target.checked, `${personPath}.harVerge`)
														}
													/>
													<FormCheckbox
														name={`${personPath}.harFullmakt`}
														label="Har fullmakt"
														onChange={(val: SyntheticEvent) =>
															handleChange(val.target.checked, `${personPath}.harFullmakt`)
														}
													/>
													<FormCheckbox
														name={`${personPath}.harDoedsfall`}
														label="Har dødsfall"
														onChange={(val: SyntheticEvent) =>
															handleChange(val.target.checked, `${personPath}.harDoedsfall`)
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
															<FormSelect
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
															/>
															<FormSelect
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
															/>
															<FormSelect
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
															/>
															<FormSelect
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
															/>
														</div>
													</div>
													<FormCheckbox
														name={`${personPath}.bostedsadresse.harBydelsnummer`}
														label="Har bydelsnummer"
														onChange={(val: SyntheticEvent) =>
															handleChange(
																val.target.checked,
																`${personPath}.bostedsadresse.harBydelsnummer`,
															)
														}
													/>
													<FormCheckbox
														name={`${personPath}.bostedsadresse.harUtenlandsadresse`}
														label="Har utenlandsadresse"
														onChange={(val: SyntheticEvent) =>
															handleChange(
																val.target.checked,
																`${personPath}.bostedsadresse.harUtenlandsadresse`,
															)
														}
													/>
													<FormCheckbox
														name={`${personPath}.bostedsadresse.harMatrikkelAdresse`}
														label="Har matrikkeladresse"
														onChange={(val: SyntheticEvent) =>
															handleChange(
																val.target.checked,
																`${personPath}.bostedsadresse.harMatrikkelAdresse`,
															)
														}
													/>
													<FormCheckbox
														name={`${personPath}.bostedsadresse.harUkjentAdresse`}
														label="Har ukjent adresse"
														onChange={(val: SyntheticEvent) =>
															handleChange(
																val.target.checked,
																`${personPath}.bostedsadresse.harUkjentAdresse`,
															)
														}
													/>
													<FormCheckbox
														name={`${personPath}.harKontaktadresse`}
														label="Har kontaktadresse"
														onChange={(val: SyntheticEvent) =>
															handleChange(val.target.checked, `${personPath}.harKontaktadresse`)
														}
													/>
													<FormCheckbox
														name={`${personPath}.harOppholdsadresse`}
														label="Har oppholdsadresse"
														onChange={(val: SyntheticEvent) =>
															handleChange(val.target.checked, `${personPath}.harOppholdsadresse`)
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
														'harDeltBosted',
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
														name={`${personPath}.harDeltBosted`}
														label="Har delt bosted"
														onChange={(val: SyntheticEvent) =>
															handleChange(val.target.checked, `${personPath}.harDeltBosted`)
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
										data-cy={CypressSelector.BUTTON_NULLSTILL_SOEK}
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
