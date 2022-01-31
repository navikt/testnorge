import React, { useEffect, useState } from 'react'
import Title from '~/components/Title'
import { Formik } from 'formik'
import SearchContainer from '~/components/SearchContainer'
import SearchOptions from './search/SearchOptions'
import PersonSearch from '~/service/services/personsearch'
import { Person } from '~/service/services/personsearch/types'
import SearchViewConnector from '~/pages/testnorgePage/search/SearchViewConnector'

const initialValues = {
	personinformasjon: {
		alder: {
			fra: '18',
			til: '70',
		},
		barn: {
			barn: false,
			doedfoedtBarn: false,
		},
		identitet: {
			falskIdentitet: false,
			utenlandskIdentitet: false,
		},
		diverse: {
			utflyttet: false,
			innflyttet: false,
		},
	},
}

const getSearchValues = (page: number, pageSize: number, values: any) => {
	return {
		pageing: {
			page: page,
			pageSize: pageSize,
		},
		kjoenn: values?.personinformasjon?.diverse?.kjoenn,
		foedsel: {
			fom: values?.personinformasjon?.alder?.foedselsdato?.fom,
			tom: values?.personinformasjon?.alder?.foedselsdato?.tom,
		},
		statsborgerskap: {
			land: values?.personinformasjon?.statsborgerskap?.land,
		},
		sivilstand: {
			type: values?.personinformasjon?.sivilstand?.type,
		},
		alder: {
			fra: values?.personinformasjon?.alder?.fra,
			til: values?.personinformasjon?.alder?.til,
		},
		identer: [values?.personinformasjon?.ident?.ident],
		identitet: {
			falskIdentitet: values?.personinformasjon?.identitet?.falskIdentitet,
			utenlandskIdentitet: values?.personinformasjon?.identitet?.utenlandskIdentitet,
		},
		barn: {
			barn: values?.personinformasjon?.barn?.barn,
			doedfoedtBarn: values?.personinformasjon?.barn?.doedfoedtBarn,
		},
		utflyttingFraNorge: {
			utflyttet: values?.personinformasjon?.diverse?.utflyttet,
		},
		innflyttingTilNorge: {
			innflytting: values?.personinformasjon?.diverse?.innflyttet,
		},
		tag: 'TESTNORGE',
		excludeTag: 'DOLLY',
	}
}

export default () => {
	const [items, setItems] = useState<Person[]>([])
	const [page, setPage] = useState(1)
	const [pageSize] = useState(20)
	const [numberOfItems, setNumberOfItems] = useState<number | null>(null)

	const search = (page: number, values: any) =>
		PersonSearch.search(getSearchValues(page, pageSize, values)).then((response) => {
			setPage(page)
			setItems(response.items)
			setNumberOfItems(response.numerOfItems)
		})

	useEffect(() => {
		search(1, initialValues)
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
				<br />
				<br />
				Søket viser kun Testnorge-identer som ikke allerede er importert til en gruppe i Dolly.
			</p>

			<Formik initialValues={initialValues} onSubmit={onSubmit}>
				{({ handleSubmit, values }) => (
					<SearchContainer
						left={<SearchOptions />}
						right={
							<SearchViewConnector
								items={items}
								pageSize={pageSize}
								page={page}
								numberOfItems={numberOfItems}
								onChange={(pageNumber: number) => search(pageNumber, values)}
							/>
						}
						onSubmit={handleSubmit}
					/>
				)}
			</Formik>
		</div>
	)
}
