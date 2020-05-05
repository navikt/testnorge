import React from 'react'
import { SearchOptions } from '~/pages/soekMiniNorge/search/SearchOptions'
import { Formik } from 'formik'
import { stateModifierFns } from '~/components/bestillingsveileder/stateModifier'
import { SearchResultVisning } from '~/pages/soekMiniNorge/search/SearchResultVisning'
import ContentContainer from '~/components/ui/contentContainer/ContentContainer'
import './Search.less'
import { AlertStripeInfo } from 'nav-frontend-alertstriper'

export const Search = () => {
	const _onSubmit = (values: any) => {}
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
		// @ts-ignore
		<div className="search-content">
			<Formik onSubmit={_onSubmit} initialValues={initialValues} enableReinitialize>
				{formikBag => {
					const stateModifier = stateModifierFns(formikBag.values, formikBag.setValues)
					return (
						<div className="search-field">
							<AlertStripeInfo>{infoTekst}</AlertStripeInfo>
							<div className="flexbox">
								<div className="search-field_options">
									<SearchOptions formikBag={formikBag} />
								</div>
								<div className="search-field_resultat">
									<SearchResultVisning personListe={[]} searchActive={false} />
								</div>
							</div>
						</div>
					)
				}}
			</Formik>
		</div>
	)
}