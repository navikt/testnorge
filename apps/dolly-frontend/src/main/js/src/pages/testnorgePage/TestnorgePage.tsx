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

export const getRandomSeed = () => {
	let randomNumber = ''
	for (let i = 0; i < 19; i++) {
		randomNumber += Math.floor(Math.random() * 10)
	}
	return randomNumber
}

export default () => {
	const [items, setItems] = useState<Person[]>([])
	const [page, setPage] = useState(1)
	const [pageSize] = useState(20)
	const [numberOfItems, setNumberOfItems] = useState<number | null>(null)
	const [loading, setLoading] = useState(false)
	const [valgtePersoner, setValgtePersoner] = useState([])
	const [startedSearch, setStartedSearch] = useState(false)
	const [randomSeed, setRandomSeed] = useState(getRandomSeed)

	const search = (searchPage: number, seed: string, values: any) => {
		setStartedSearch(true)
		setLoading(true)
		PersonSearch.search(getSearchValues(searchPage, pageSize, seed, values)).then((response) => {
			setPage(searchPage)
			setItems(response.items)
			setNumberOfItems(response.numerOfItems)
			setValgtePersoner([])
			setLoading(false)
		})
	}

	const onSubmit = (values: any) => {
		const seed = getRandomSeed()
		search(1, seed, values)
		setRandomSeed(seed)
	}

	return (
		<div>
			<Title title="Søk i Testnorge" />
			<p>
				Testnorge er en felles offentlig testdatapopulasjon, som ble laget i forbindelse med nytt
				folkeregister. Populasjonen er levende, og endrer seg fortløpende ved at personer fødes,
				dør, får barn, osv. Pr. i dag støttes kun personer, men fremover vil det også komme støtte
				for organisasjoner og arbeidsforhold.
				<br />
				<br />
				Testnorge er tilgjengelig i PDL.
				<br />
				<br />
				Søket viser kun Testnorge-identer som ikke allerede er importert til en gruppe i Dolly.
			</p>

			<Formik initialValues={initialValues} onSubmit={onSubmit}>
				{(formikBag) => (
					<SearchContainer
						left={<SearchOptions formikBag={formikBag} />}
						right={
							<>
								{!startedSearch && <ContentContainer>Ingen søk er gjort</ContentContainer>}
								{startedSearch && (
									<SearchViewConnector
										items={items}
										loading={loading}
										valgtePersoner={valgtePersoner}
										setValgtePersoner={setValgtePersoner}
										pageSize={pageSize}
										page={page}
										numberOfItems={numberOfItems}
										onChange={(pageNumber: number) =>
											search(pageNumber, randomSeed, formikBag.values)
										}
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
