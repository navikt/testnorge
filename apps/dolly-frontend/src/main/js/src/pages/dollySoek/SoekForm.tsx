import '@/styles/variables.less'
import React, { useEffect, useState } from 'react'
import { Button, Table } from '@navikt/ds-react'
import { ResultatVisning } from '@/pages/dollySoek/ResultatVisning'
import * as _ from 'lodash-es'
import { TestComponentSelectors } from '#/mocks/Selectors'
import { Form, FormProvider } from 'react-hook-form'
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
import { runningE2ETest } from '@/service/services/Request'

export const dollySoekLocalStorageKey = 'dollySoek'
export const personPath = 'personRequest'
export const adressePath = 'personRequest.adresse'

export const SoekForm = ({
	formMethods,
	localStorageValue,
	handleChange,
	handleChangeAdresse,
	setRequest,
	formRequest,
}) => {
	//TODO: Felter/kategorier som maa fikses
	// Fagsystemer: alle
	// Personinformasjon: skjerming

	const [result, setResult] = useState(null)
	const [soekPaagaar, setSoekPaagaar] = useState(false)
	const [soekError, setSoekError] = useState(null)
	const [visAntall, setVisAntall] = useState(10)

	const maxTotalHits = 10000

	const { watch, reset, control, getValues } = formMethods
	const values = watch()

	useEffect(() => {
		const localStorageRequest = localStorageValue ? JSON.parse(localStorageValue) : null
		_.set(localStorageRequest, 'seed', result?.seed ?? null)
		if (localStorageRequest) {
			localStorage.setItem(dollySoekLocalStorageKey, JSON.stringify(localStorageRequest))
		}
	}, [result])

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
		//TODO: Toem lagresoek
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
		//TODO: Toem lagresoek
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
								<Table size="small">
									<Table.Body>
										<Table.ExpandableRow
											content={<Fagsystemer handleChangeList={handleChangeList} />}
											defaultOpen={true}
										>
											<Table.HeaderCell>
												<Header
													title="Fagsystemer"
													antall={antallFagsystemer}
													paths={['registreRequest', 'miljoer']}
													emptyCategory={emptyCategory}
												/>
											</Table.HeaderCell>
										</Table.ExpandableRow>
										<Table.ExpandableRow
											content={<Personinformasjon handleChange={handleChange} />}
											data-testid={TestComponentSelectors.EXPANDABLE_PERSONINFORMASJON}
											expandOnRowClick={runningE2ETest()}
										>
											<Table.HeaderCell>
												<Header
													title="Personinformasjon"
													antall={getAntallRequest(PersoniformasjonPaths)}
													paths={PersoniformasjonPaths}
													getValues={getValues}
													emptyCategory={emptyCategory}
												/>
											</Table.HeaderCell>
										</Table.ExpandableRow>
										<Table.ExpandableRow
											content={<Adresser handleChangeAdresse={handleChangeAdresse} />}
										>
											<Table.HeaderCell>
												<Header
													title="Adresser"
													antall={getAntallRequest(AdresserPaths)}
													paths={AdresserPaths}
													getValues={getValues}
													emptyCategory={emptyCategory}
												/>
											</Table.HeaderCell>
										</Table.ExpandableRow>
										<Table.ExpandableRow
											content={
												<Familierelasjoner
													handleChange={handleChange}
													handleChangeAdresse={handleChangeAdresse}
												/>
											}
										>
											<Table.HeaderCell>
												<Header
													title="Familierelasjoner"
													antall={getAntallRequest(FamilierelasjonerPaths)}
													paths={FamilierelasjonerPaths}
													getValues={getValues}
													emptyCategory={emptyCategory}
												/>
											</Table.HeaderCell>
										</Table.ExpandableRow>
										<Table.ExpandableRow content={<Identifikasjon handleChange={handleChange} />}>
											<Table.HeaderCell>
												<Header
													title="Identifikasjon"
													antall={getAntallRequest(IdentifikasjonPaths)}
													paths={IdentifikasjonPaths}
													getValues={getValues}
													emptyCategory={emptyCategory}
												/>
											</Table.HeaderCell>
										</Table.ExpandableRow>
										<Table.ExpandableRow content={<Annet handleChange={handleChange} />}>
											<Table.HeaderCell>
												<Header
													title="Annet"
													antall={getAntallRequest(AnnetPaths)}
													paths={AnnetPaths}
													getValues={getValues}
													emptyCategory={emptyCategory}
												/>
											</Table.HeaderCell>
										</Table.ExpandableRow>
									</Table.Body>
								</Table>
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
