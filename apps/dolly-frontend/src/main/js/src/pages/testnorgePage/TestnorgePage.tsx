import React, { Fragment, useState } from 'react'
import Title from '~/components/Title'
import { Formik } from 'formik'
import SearchContainer from './search/searchContainer/SearchContainer'
import { SearchOptions } from './search/SearchOptions'
import PersonSearch from '~/service/services/personsearch'
import SearchViewConnector from '~/pages/testnorgePage/search/SearchViewConnector'
import { getSearchValues, initialValues } from '~/pages/testnorgePage/utils'
import ContentContainer from '~/components/ui/contentContainer/ContentContainer'
import { Exception } from 'sass'
import '../gruppe/PersonVisning/PersonVisning.less'
import { PdlData } from '~/pages/gruppe/PersonVisning/PersonMiljoeinfo/PdlDataTyper'
import Hjelpetekst from '~/components/hjelpetekst'
import { PopoverOrientering } from 'nav-frontend-popover'
import './TestnorgePage.less'
import { ifPresent, validate } from '~/utils/YupValidations'
import * as Yup from 'yup'
import DisplayFormikState from '~/utils/DisplayFormikState'
import { Gruppe } from '~/utils/hooks/useGruppe'

type TestnorgePageProps = {
	gruppe?: Gruppe
}

export default ({ gruppe }: TestnorgePageProps) => {
	const [items, setItems] = useState<PdlData[]>([])
	const [loading, setLoading] = useState(false)
	const [valgtePersoner, setValgtePersoner] = useState([])
	const [startedSearch, setStartedSearch] = useState(false)
	const [error, setError] = useState(null)

	const search = (seed: string, values: any) => {
		setError(null)
		setStartedSearch(true)
		setLoading(true)
		const harFlereBarn = values?.relasjoner?.harBarn === 'M'
		PersonSearch.searchPdlPerson(getSearchValues(seed, values), harFlereBarn)
			.then((response) => {
				setItems(response.items)
				setLoading(false)
			})
			.catch((_e: Exception) => {
				setLoading(false)
				setError('Noe gikk galt med søket. Ta kontakt med Dolly hvis feilen vedvarer.')
			})
	}

	const onSubmit = (values: any) => {
		const seed = Math.random() + ''
		search(seed, values)
		setValgtePersoner([])
	}

	const tenor = (
		<a href="https://www.skatteetaten.no/skjema/testdata" target="_blank">
			Tenor
		</a>
	)

	const _validate = (values: any) => validate(values, validation)

	return (
		<div>
			<div className="testnorge-page-header flexbox--align-center--justify-start">
				<Title title="Søk og import fra Test-Norge" />
				<Hjelpetekst hjelpetekstFor="Test-Norge" type={PopoverOrientering.Under}>
					Test-Norge er en felles offentlig testdatapopulasjon, som ble laget av Skatteetaten i
					forbindelse med nytt folkeregister. Populasjonen er levende, og endrer seg fortløpende ved
					at personer fødes, dør, får barn, osv. Hele Test-Norge er tilgjengelig i PDL.
					<br />
					<br />
					I søket nedenfor kan man søke opp Test-Norge-identer, velge identer man ønsker å ta i
					bruk, velge ekstra informasjon man ønsker lagt til på identene og importere dem inn i en
					ønsket gruppe i Dolly. Søket returnerer maks 100 tilfeldige Test-Norge-identer som passer
					søkekriteriene og som ikke allerede er importert til en gruppe i Dolly.
					<br />
					<br />
					For å finne mer spesifikke identer kan Skatteetaten sin testdatasøkeløsning {tenor}{' '}
					brukes. Tenor er ikke koblet opp mot Dolly, men det er mulig å søke opp identer man fant i
					Tenor her i Dolly og så importere dem.
				</Hjelpetekst>
			</div>

			<Formik initialValues={initialValues} validate={_validate} onSubmit={onSubmit}>
				{(formikBag) => {
					const devEnabled =
						window.location.hostname.includes('localhost') ||
						window.location.hostname.includes('dolly-frontend-dev')

					return (
						<Fragment>
							{devEnabled && <DisplayFormikState {...formikBag} />}
							<SearchContainer
								left={<SearchOptions formikBag={formikBag} />}
								right={
									<>
										{error && <ContentContainer>{error}</ContentContainer>}
										{!startedSearch && <ContentContainer>Ingen søk er gjort</ContentContainer>}
										{startedSearch && !error && (
											<SearchViewConnector
												items={items}
												loading={loading}
												valgtePersoner={valgtePersoner}
												setValgtePersoner={setValgtePersoner}
												gruppe={gruppe}
											/>
										)}
									</>
								}
								formikBag={formikBag}
								onSubmit={formikBag.handleSubmit}
								onEmpty={formikBag.resetForm}
							/>
						</Fragment>
					)
				}}
			</Formik>
		</div>
	)
}

const validation = Yup.object({
	identer: ifPresent(
		'$identer',
		Yup.array().of(
			Yup.string()
				.nullable()
				.transform((curr, orig) => (orig === '' ? null : curr))
				.matches(/^\d*$/, 'Ident må være et tall med 11 siffer')
				.test('len', 'Ident må være et tall med 11 siffer', (val) => !val || val.length === 11)
		)
	),
})
