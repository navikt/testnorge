import React, { useState } from 'react'
import Title from '~/components/Title'
import { Formik } from 'formik'
import SearchContainer from '~/components/SearchContainer'
import { SearchOptions } from './search/SearchOptions'
import PersonSearch from '~/service/services/personsearch'
import { Person } from '~/service/services/personsearch/types'
import SearchViewConnector from '~/pages/testnorgePage/search/SearchViewConnector'
import { initialValues, getSearchValues } from '~/pages/testnorgePage/utils'
import ContentContainer from '~/components/ui/contentContainer/ContentContainer'
import { Exception } from 'sass'

export default () => {
	const [items, setItems] = useState<Person[]>([])
	const [loading, setLoading] = useState(false)
	const [valgtePersoner, setValgtePersoner] = useState([])
	const [startedSearch, setStartedSearch] = useState(false)
	const [error, setError] = useState(null)

	const search = (seed: string, values: any) => {
		setError(null)
		setStartedSearch(true)
		setLoading(true)
		PersonSearch.search(getSearchValues(seed, values))
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

	return (
		<div>
			<Title title="Søk og importer fra Testnorge" />
			<p>
				Testnorge er en felles offentlig testdatapopulasjon, som ble laget i forbindelse med nytt
				folkeregister. Populasjonen er levende, og endrer seg fortløpende ved at personer fødes,
				dør, får barn, osv. Hele Testnorge er tilgjengelig i PDL.
				<br />
				<br />
				I søket nedenfor kan du søke opp Testnorge-identer, velge de du ønsker og så importere dem
				til en gruppe i Dolly. Søket returnerer maks 100 tilfeldige Testnorge-identer som passer
				søkekriteriene og som ikke allerede er importert til en gruppe i Dolly.
				<br />
				<br />
				For å finne mer spesifikke identer kan skatteetaten sin søkeløsning Tenor brukes. Tenor er
				ikke koblet opp mot Dolly, men du kan søke opp identer du fant i Tenor her i Dolly og så
				importere dem.
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
