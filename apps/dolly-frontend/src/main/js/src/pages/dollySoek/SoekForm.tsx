import '@/styles/variables.less'
import styled from 'styled-components'
import { Form, Formik } from 'formik'
import { FormikCheckbox } from '@/components/ui/form/inputs/checbox/Checkbox'
import React from 'react'
import { FormikSelect } from '@/components/ui/form/inputs/select/Select'
import { SelectOptionsManager as Options } from '@/service/SelectOptions'
import Button from '@/components/ui/button/Button'

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
	const personPath = 'personRequest'
	return (
		<SoekefeltWrapper>
			<Soekefelt>
				<Formik initialValues={initialValues} onSubmit={() => console.log('submit...')}>
					{() => (
						<Form className="flexbox--flex-wrap" autoComplete="off">
							<div className="flexbox--full-width">
								<FormikSelect
									name="typer"
									label="Registre"
									options={Options('registerTyper')}
									isMulti={true}
									size="grow"
									fastfield={false}
								/>
							</div>
							<div className="flexbox--full-width">
								<Button onClick={null} kind={'chevron-down'}>
									PERSONINFORMASJON
								</Button>
								<div className="flexbox--flex-wrap">
									<FormikCheckbox name={`${personPath}.harVerge`} label="Har verge" />
									<FormikCheckbox name={`${personPath}.harFullmakt`} label="Har fullmakt" />
									<FormikCheckbox name={`${personPath}.harDoedsfall`} label="Har dødsfall" />
									<FormikCheckbox name={`${personPath}.harInnflytting`} label="Har innflytting" />
									<FormikCheckbox name={`${personPath}.harUtflytting`} label="Har utflytting" />
									<FormikCheckbox
										name={`${personPath}.harTilrettelagtKommunikasjon`}
										label="Har tilrettelagt kommunikasjon"
									/>
								</div>
							</div>
							<div className="flexbox--full-width">
								<Button onClick={null} kind={'chevron-down'}>
									FAMILIERELASJONER
								</Button>
								<div className="flexbox--flex-wrap">
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
								</div>
							</div>
							<div className="flexbox--full-width">
								<Button onClick={null} kind={'chevron-down'}>
									IDENTIFIKASJON
								</Button>
								<div className="flexbox--flex-wrap">
									<FormikCheckbox
										name={`${personPath}.harFalskIdentitet`}
										label="Har falsk identitet"
									/>
									<FormikCheckbox
										name={`${personPath}.harUtenlandskIdentifikasjonsnummer`}
										label="Har utenlandsk identifikasjonsnummer"
									/>
									<FormikCheckbox name={`${personPath}.nyIdentitet`} label="Har ny identitet" />
								</div>
							</div>

							{/*<FormikCheckbox*/}
							{/*	name={`${personPath}.harKontaktinformasjonForDoedsbo`}*/}
							{/*	label="Har kontaktinformasjon for dødsbo"*/}
							{/*/>*/}

							{/*<FormikCheckbox*/}
							{/*	name={`${personPath}.harSikkerhetstiltak`}*/}
							{/*	label="Har sikkerhetstiltak"*/}
							{/*/>*/}
							{/*<FormikCheckbox name={`${personPath}.harOpphold`} label="Har opphold" />*/}

							{/*<FormikCheckbox name={`${personPath}.harDeltBosted`} label="Har delt bosted" />*/}
							{/*<FormikCheckbox name={`${personPath}.harKontaktadresse`} label="Har kontaktadresse" />*/}
							{/*<FormikCheckbox*/}
							{/*	name={`${personPath}.harOppholdsadresse`}*/}
							{/*	label="Har oppholdsadresse"*/}
							{/*/>*/}
						</Form>
					)}
				</Formik>
			</Soekefelt>
		</SoekefeltWrapper>
	)
}
