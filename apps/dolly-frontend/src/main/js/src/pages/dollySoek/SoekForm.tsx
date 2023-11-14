import '@/styles/variables.less'
import styled from 'styled-components'
import { Form, Formik } from 'formik'
import { FormikCheckbox } from '@/components/ui/form/inputs/checbox/Checkbox'
import React, { useState } from 'react'
import { FormikSelect } from '@/components/ui/form/inputs/select/Select'
import { SelectOptionsManager as Options } from '@/service/SelectOptions'
import { Accordion, Button } from '@navikt/ds-react'
import { AdresseKodeverk, GtKodeverk } from '@/config/kodeverk'
import { useSoekIdenter } from '@/utils/hooks/usePersonSoek'
import { SoekRequest } from '@/pages/dollySoek/DollySoekTypes'

const SoekefeltWrapper = styled.div`
	display: flex;
	flex-direction: column;
	margin-bottom: 10px;
	background-color: white;
	border: 1px @color-bg-grey-border;
	border-radius: 4px;
`

const Soekefelt = styled.div`
	padding: 10px;
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
		nyIdentitet: false,
		bostedsadresse: {
			kommunenummer: null,
			postnummer: null,
			bydelsnummer: null,
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
	const { result, loading, error } = useSoekIdenter(request)

	const handleSubmit = (request: SoekRequest) => {
		setRequest(request)
	}

	const personPath = 'personRequest'

	return (
		<SoekefeltWrapper>
			<Soekefelt>
				<Formik
					initialValues={initialValues}
					onSubmit={(request: SoekRequest) => handleSubmit(request)}
				>
					{(formikBag) => {
						return (
							<>
								<Form className="flexbox--flex-wrap" autoComplete="off">
									<div className="flexbox--full-width" style={{ marginBottom: '10px' }}>
										<FormikSelect
											name="typer"
											label="Registre"
											options={Options('registerTyper')}
											isMulti={true}
											size="grow"
											fastfield={false}
										/>
									</div>
									<Accordion size="small" headingSize="xsmall" className="flexbox--full-width">
										<Accordion.Item>
											<Accordion.Header>Personinformasjon</Accordion.Header>
											<Accordion.Content>
												<div className="flexbox--flex-wrap">
													<FormikSelect
														name={`${personPath}.statsborgerskap`}
														kodeverk={AdresseKodeverk.StatsborgerskapLand}
														size="large"
														placeholder="Statsborgerskap"
													/>
													<FormikCheckbox name={`${personPath}.harVerge`} label="Har verge" />
													<FormikCheckbox name={`${personPath}.harFullmakt`} label="Har fullmakt" />
													<FormikCheckbox
														name={`${personPath}.harDoedsfall`}
														label="Har dødsfall"
													/>
													<FormikCheckbox
														name={`${personPath}.harInnflytting`}
														label="Har innflytting"
													/>
													<FormikCheckbox
														name={`${personPath}.harUtflytting`}
														label="Har utflytting"
													/>
													<FormikCheckbox
														name={`${personPath}.harSikkerhetstiltak`}
														label="Har sikkerhetstiltak"
													/>
													<FormikCheckbox
														name={`${personPath}.harTilrettelagtKommunikasjon`}
														label="Har tilrettelagt kommunikasjon"
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
													/>
													<FormikSelect
														name={`${personPath}.bostedsadresse.postnummer`}
														kodeverk={AdresseKodeverk.Postnummer}
														size="large"
														placeholder="Postnummer"
													/>
													<FormikSelect
														name={`${personPath}.bostedsadresse.bydelsnummer`}
														kodeverk={GtKodeverk.BYDEL}
														size="large"
														placeholder="Bydelsnummer"
													/>
													<FormikSelect
														name={`${personPath}.addressebeskyttelse`}
														options={Options('gradering')}
														size="large"
														placeholder="Adressebeskyttelse"
													/>
													<FormikCheckbox
														name={`${personPath}.bostedsadresse.harUtenlandsadresse`}
														label="Har utenlandsadresse"
													/>
													<FormikCheckbox
														name={`${personPath}.bostedsadresse.harMatrikkelAdresse`}
														label="Har matrikkeladresse"
													/>
													<FormikCheckbox
														name={`${personPath}.bostedsadresse.harUkjentAdresse`}
														label="Har ukjent adresse"
													/>
													<FormikCheckbox
														name={`${personPath}.harKontaktadresse`}
														label="Har kontaktadresse"
													/>
													<FormikCheckbox
														name={`${personPath}.harOppholdsadresse`}
														label="Har oppholdsadresse"
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
													/>
													<FormikCheckbox name={`${personPath}.harBarn`} label="Har barn" />
													<FormikCheckbox name={`${personPath}.harForeldre`} label="Har foreldre" />
													<FormikCheckbox
														name={`${personPath}.harDoedfoedtBarn`}
														label="Har dødfødt barn"
													/>
													<FormikCheckbox
														name={`${personPath}.harForeldreAnsvar`}
														label="Har foreldreansvar"
													/>
													<FormikCheckbox
														name={`${personPath}.harDeltBosted`}
														label="Har delt bosted"
													/>
												</div>
											</Accordion.Content>
										</Accordion.Item>
										<Accordion.Item>
											<Accordion.Header>Identifikasjon</Accordion.Header>
											<Accordion.Content>
												<div className="flexbox--flex-wrap">
													<FormikCheckbox
														name={`${personPath}.harFalskIdentitet`}
														label="Har falsk identitet"
													/>
													<FormikCheckbox
														name={`${personPath}.harUtenlandskIdentifikasjonsnummer`}
														label="Har utenlandsk identifikasjonsnummer"
													/>
													<FormikCheckbox
														name={`${personPath}.nyIdentitet`}
														label="Har ny identitet"
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
													/>
													<FormikCheckbox name={`${personPath}.harOpphold`} label="Har opphold" />
												</div>
											</Accordion.Content>
										</Accordion.Item>
									</Accordion>
								</Form>
								<Buttons className="flexbox--flex-wrap">
									<Button
										onClick={() => formikBag.handleSubmit()}
										variant="primary"
										disabled={loading}
										loading={loading}
										type="submit"
									>
										Søk
									</Button>
									<Button onClick={() => formikBag.setValues(initialValues)} variant="secondary">
										Tøm
									</Button>
								</Buttons>
							</>
						)
					}}
				</Formik>
			</Soekefelt>
		</SoekefeltWrapper>
	)
}
