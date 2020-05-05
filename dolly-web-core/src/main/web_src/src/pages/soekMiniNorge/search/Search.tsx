import React, { useState } from 'react'
import { SearchOptions } from '~/pages/soekMiniNorge/search/SearchOptions'
import { Formik } from 'formik'
import { stateModifierFns } from '~/components/bestillingsveileder/stateModifier'
import { SearchResultVisning } from '~/pages/soekMiniNorge/search/SearchResultVisning'
import ContentContainer from '~/components/ui/contentContainer/ContentContainer'
import './Search.less'
import { AlertStripeInfo } from 'nav-frontend-alertstriper'
import _ from 'lodash'
import { DollyApi, HodejegerenApi, TpsfApi } from '~/service/Api'
import { DollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'
import { TitleValue } from '~/components/ui/titleValue/TitleValue'
import LoadableComponent from '~/components/ui/loading/LoadableComponent'

export const Search = () => {
	const [soekOptions, setSoekOptions] = useState('')
	const [searchActive, setSearchActive] = useState(false)

	const _onSubmit = (values: any) => {
		let newSoekOptions = ''
		for (let key in values) {
			if (Object.prototype.toString.call(values[key]) === '[object Object]') {
				for (let innerKey in values[key]) {
					const value = (values[key][innerKey] + '').toUpperCase()
					if (value !== '') {
						if (newSoekOptions === '') newSoekOptions = key + '.' + innerKey + '=' + value
						else newSoekOptions = newSoekOptions + '&' + key + '.' + innerKey + '=' + value
					}
				}
			} else {
				const value = values[key] + ''.toUpperCase()
				if (value !== '') {
					if (newSoekOptions === '') newSoekOptions = key + '=' + value
					else newSoekOptions = newSoekOptions + '&' + key + '=' + value
					newSoekOptions = newSoekOptions + '&' + key + '=' + value
				}
			}
		}
		setSoekOptions(newSoekOptions)
		setSearchActive(true)
	}

	const initialValues = {
		personIdent: {
			id: '',
			type: '',
			status: ''
		},
		personInfo: {
			kjoenn: '',
			datoFoedt: ''
		},
		navn: {
			fornavn: '',
			mellomnavn: '',
			slektsnavn: ''
		},
		sivilstand: {
			type: ''
		},
		statsborger: {
			land: ''
		},
		boadresse: {
			adresse: '',
			land: '',
			kommune: '',
			postnr: ''
		},
		relasjoner: {
			rolle: ''
		}
	}

	const infoTekst =
		'Syntetiske testdata er tilgjengelig i Dolly gjennom tekniske APIer og Mini-Norge. Mini-Norge er NAVs syntetiske basispopulasjon med egenskaper tilsvarende normalen i Norge. Antall innbyggere er pr mars 2020 ca 200 000. Befolkningen er dynamisk og det gjøres løpende endringer – nye personer fødes, folk skifter jobb osv. Mini-Norge kan brukes av alle og er godt egnet til basistester, volumtester m.m. der det ikke er behov for spesialtilfeller.\n\n' +
		'Mini-Norge er kun tilgjengelig i Q2 som er et helsyntetisk testmiljø.\n\n' +
		'Beta-versjon for søk i Mini-Norge jobbes med fortløpende.'

	return (
		<div className="search-content">
			<Formik onSubmit={_onSubmit} initialValues={initialValues} enableReinitialize>
				{formikBag => {
					const stateModifier = stateModifierFns(formikBag.values, formikBag.setValues)
					return (
						<div className="search-field">
							<AlertStripeInfo>{infoTekst}</AlertStripeInfo>
							<div className="flexbox">
								<div className="search-field_options">
									<SearchOptions formikBag={formikBag} onSubmit={_onSubmit} />
								</div>
								<div className="search-field_resultat">
									{!searchActive ? (
										<ContentContainer>Ingen søk er gjort</ContentContainer>
									) : searchActive && soekOptions === '' ? (
										<ContentContainer>
											Vennligst fyll inne en eller flere verdier å søke på.
										</ContentContainer>
									) : (
										<LoadableComponent
											onFetch={() =>
												HodejegerenApi.soek(soekOptions).then(response =>{
													return response.data.length > 0 ? response.data[0]: null}
												)
											}
											render={(data: Array<Response>) =>
												<SearchResultVisning personListe={data} />
											}
										/>
									)}
								</div>
							</div>
						</div>
					)
				}}
			</Formik>
		</div>
	)
}