import '@/styles/variables.less'
import React, { useEffect, useState } from 'react'
import { Accordion, Button } from '@navikt/ds-react'
import { ResultatVisning } from '@/pages/dollySoek/ResultatVisning'
import * as _ from 'lodash-es'
import { TestComponentSelectors } from '#/mocks/Selectors'
import { Form, FormProvider, useForm } from 'react-hook-form'
import { Buttons, Header, Soekefelt, SoekefeltWrapper } from '@/components/ui/soekForm/SoekForm'
import {
	AdresserPaths,
	AnnetPaths,
	FamilierelasjonerPaths,
	IdentifikasjonPaths,
	PersoniformasjonPaths,
} from '@/pages/dollySoek/paths'
import { DollyApi } from '@/service/Api'
import { dollySoekInitialValues } from '@/pages/dollySoek/dollySoekInitialValues'
import { Fagsystemer } from '@/pages/dollySoek/soekFormPartials/Fagsystemer'
import { Personinformasjon } from '@/pages/dollySoek/soekFormPartials/Personinformasjon'
import { Adresser } from '@/pages/dollySoek/soekFormPartials/Adresser'
import { Familierelasjoner } from '@/pages/dollySoek/soekFormPartials/Familierelasjoner'
import { Identifikasjon } from '@/pages/dollySoek/soekFormPartials/Identifikasjon'
import { Annet } from '@/pages/dollySoek/soekFormPartials/Annet'

export const dollySoekLocalStorageKey = 'dollySoek'
export const personPath = 'personRequest'
export const adressePath = 'personRequest.adresse'

export const SoekForm = () => {
	const localStorageValue = localStorage.getItem(dollySoekLocalStorageKey)
	const initialValues = localStorageValue ? JSON.parse(localStorageValue) : dollySoekInitialValues

	const [formRequest, setFormRequest] = useState(initialValues)
	const [result, setResult] = useState(null)
	const [soekPaagaar, setSoekPaagaar] = useState(false)
	const [soekError, setSoekError] = useState(null)
	const [visAntall, setVisAntall] = useState(10)

	const setRequest = (request: any) => {
		localStorage.setItem(dollySoekLocalStorageKey, JSON.stringify(request))
		setFormRequest(request)
	}

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
		DollyApi.personerSearch(formRequest).then((response) => {
			if (response.error) {
				setResult(null)
				setSoekError(response.error)
				setSoekPaagaar(false)
			} else {
				setResult(response.data)
				setSoekPaagaar(false)
			}
		})
	}, [formRequest])

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
			_.set(requestClone, path, _.get(dollySoekInitialValues, path))
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
		reset(dollySoekInitialValues)
		setRequest(dollySoekInitialValues)
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
												<Fagsystemer handleChangeList={handleChangeList} />
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
												<Personinformasjon handleChange={handleChange} />
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
												<Adresser handleChangeAdresse={handleChangeAdresse} />
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
												<Familierelasjoner
													handleChange={handleChange}
													handleChangeAdresse={handleChangeAdresse}
												/>
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
												<Identifikasjon handleChange={handleChange} />
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
												<Annet handleChange={handleChange} />
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
