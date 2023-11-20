import React, { Fragment, useState } from 'react'
import Title from '@/components/Title'
import SearchContainer from './search/searchContainer/SearchContainer'
import { SearchOptions } from './search/SearchOptions'
import PersonSearch from '@/service/services/personsearch'
import SearchViewConnector from '@/pages/testnorgePage/search/SearchViewConnector'
import { getSearchValues, initialValues } from '@/pages/testnorgePage/utils'
import ContentContainer from '@/components/ui/contentContainer/ContentContainer'
import { Exception } from 'sass'
import '@/pages/gruppe/PersonVisning/PersonVisning.less'
import { PdlData } from '@/pages/gruppe/PersonVisning/PersonMiljoeinfo/PdlDataTyper'
import './TestnorgePage.less'
import { ifPresent, validate } from '@/utils/YupValidations'
import * as Yup from 'yup'
import DisplayFormikState from '@/utils/DisplayFormikState'
import { Gruppe } from '@/utils/hooks/useGruppe'
import { Hjelpetekst } from '@/components/hjelpetekst/Hjelpetekst'
import { bottom } from '@popperjs/core'
import { CypressSelector } from '../../../cypress/mocks/Selectors'
import { Form, useForm } from 'react-hook-form'

type TestnorgePageProps = {
	gruppe?: Gruppe
}

export default ({ gruppe }: TestnorgePageProps) => {
	const _validate = (values: any) => validate(values, validation)

	const [items, setItems] = useState<PdlData[]>([])
	const [loading, setLoading] = useState(false)
	const [valgtePersoner, setValgtePersoner] = useState([])
	const [startedSearch, setStartedSearch] = useState(false)
	const [error, setError] = useState(null)
	const formMethods = useForm({ defaultValues: initialValues, resolver: _validate(validation) })

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

	const devEnabled =
		window.location.hostname.includes('localhost') ||
		window.location.hostname.includes('dolly-frontend-dev')

	return (
		<div>
			<div className="testnorge-page-header flexbox--align-center--justify-start">
				<Title data-cy={CypressSelector.TITLE_TESTNORGE} title="Søk og import fra Test-Norge" />
				<Hjelpetekst placement={bottom}>
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

			<Form onSubmit={onSubmit}>
				<Fragment>
					{devEnabled && <DisplayFormikState {...formMethods} />}
					<SearchContainer
						left={<SearchOptions formMethods={formMethods} />}
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
						formMethods={formMethods}
						onSubmit={formMethods.handleSubmit}
						onEmpty={formMethods.reset}
					/>
				</Fragment>
			</Form>
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
				.test('len', 'Ident må være et tall med 11 siffer', (val) => !val || val.length === 11),
		),
	),
})
