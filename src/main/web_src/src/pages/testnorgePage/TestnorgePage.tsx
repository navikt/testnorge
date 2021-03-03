import React, { useEffect, useState } from 'react'
import Title from '~/components/Title'
import { Formik } from 'formik'
import SearchContainer from '~/components/SearchContainer'
import SearchOptions from './SearchOptions'
import PersonSearch from '~/service/services/personsearch'
import SearchView from '~/pages/testnorgePage/SearchView'
import _get from 'lodash'
import { Person } from '~/service/services/personsearch/types'

export default () => {
	const [items, setItems] = useState<Person[]>([])
	const [page, setPage] = useState(1)
	const [pageSize] = useState(20)
	const [numberOfItems, setNumberOfItems] = useState<number | null>(null)

	const search = (page: number, values: any) =>
		PersonSearch.search({
			pageing: {
				page: page,
				pageSize: pageSize
			},
			kjoenn: values?.personinformasjon?.diverse?.kjoenn,
			foedsel: {
				fom: values?.personinformasjon?.alder?.foedselsdato?.fom,
				tom: values?.personinformasjon?.alder?.foedselsdato?.tom
			},
			statsborgerskap: {
				land: values?.personinformasjon?.statsborgerskap?.land
			},
			sivilstand: {
				type: values?.personinformasjon?.sivilstand?.type
			},
			alder: {
				fra: values?.personinformasjon?.alder?.fra,
				til: values?.personinformasjon?.alder?.til
			},
			tag: 'TESTNORGE'
		}).then(response => {
			setPage(page)
			setItems(response.items)
			setNumberOfItems(response.numerOfItems)
		})

	useEffect(() => {
		search(1, null)
	}, [])

	const onSubmit = (values: any) => search(1, values)

	return (
		<div>
			<Title title="Søk i Testnorge" beta={true} />
			<p>
				Testnorge er en felles offentlig testdatapopulasjon, som ble laget i forbindelse med nytt
				folkeregister. Populasjonen er levende, og endrer seg fortløpende ved at personer fødes,
				dør, får barn, osv. Pr. i dag støttes kun personer, men fremover vil det også komme støtte
				for organisasjoner og arbeidsforhold.
				<br />
				<br />
				Testnorge er tilgjengelig i PDL.
			</p>

			<Formik initialValues={{}} onSubmit={onSubmit}>
				{({ handleSubmit, values }) => (
					<SearchContainer
						left={<SearchOptions />}
						right={
							<SearchView
								items={items}
								pageing={{
									pageSize: pageSize,
									page: page
								}}
								numberOfItems={numberOfItems}
								onChange={page => search(page, values)}
							/>
						}
						onSubmit={handleSubmit}
					/>
				)}
			</Formik>
		</div>
	)
}
