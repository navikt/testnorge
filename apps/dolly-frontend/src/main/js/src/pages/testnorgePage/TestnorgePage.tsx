import React, { useState } from 'react'
import Title from '~/components/Title'
import { Formik } from 'formik'
import SearchContainer from '~/components/SearchContainer'
import { SearchOptions } from './search/SearchOptions'
import PersonSearch from '~/service/services/personsearch'
import SearchViewConnector from '~/pages/testnorgePage/search/SearchViewConnector'
import { initialValues, getSearchValues } from '~/pages/testnorgePage/utils'
import ContentContainer from '~/components/ui/contentContainer/ContentContainer'
import { Exception } from 'sass'
import '../gruppe/PersonVisning/PersonVisning.less'
import { PdlData } from '~/pages/gruppe/PersonVisning/PersonMiljoeinfo/PdlDataTyper'

export default () => {
	const [items, setItems] = useState<PdlData[]>([])
	const [loading, setLoading] = useState(false)
	const [valgtePersoner, setValgtePersoner] = useState([])
	const [startedSearch, setStartedSearch] = useState(false)
	const [error, setError] = useState(null)

	const search = (seed: string, values: any) => {
		setError(null)
		setStartedSearch(true)
		setLoading(true)
		PersonSearch.searchPdlPerson(getSearchValues(seed, values))
			.then((response) => {
				setItems(response.items)
				setLoading(false)
			})
			.catch((e: Exception) => {
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

	return (
		<div>
			<Title title="Søk og import fra Test-Norge" />
			<p>
				Test-Norge er en felles offentlig testdatapopulasjon, som ble laget av Skatteetaten i
				forbindelse med nytt folkeregister. Populasjonen er levende, og endrer seg fortløpende ved
				at personer fødes, dør, får barn, osv. Hele Test-Norge er tilgjengelig i PDL.
				<br />
				<br />
				I søket nedenfor kan man søke opp Test-Norge-identer, velge identer man ønsker å ta i bruk,
				velge ekstra informasjon man ønsker lagt til på identene og importere dem inn i en ønsket
				gruppe i Dolly. Søket returnerer maks 100 tilfeldige Test-Norge-identer som passer
				søkekriteriene og som ikke allerede er importert til en gruppe i Dolly.
				<br />
				<br />
				For å finne mer spesifikke identer kan Skatteetaten sin testdatasøkeløsning {tenor} brukes.
				Tenor er ikke koblet opp mot Dolly, men det er mulig å søke opp identer man fant i Tenor her
				i Dolly og så importere dem.
			</p>

			<Formik initialValues={initialValues} onSubmit={onSubmit}>
				{(formikBag) => (
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
									/>
								)}
							</>
						}
						onSubmit={formikBag.handleSubmit}
					/>
				)}
			</Formik>
		</div>
	)
}
