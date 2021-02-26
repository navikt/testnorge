import React, { useState } from 'react'
import _get from 'lodash/get'
import { SearchOptions } from '~/pages/soekMiniNorge/search/SearchOptions'
import { Formik } from 'formik'
import { SearchResult } from '~/pages/soekMiniNorge/search/SearchResult'
import './Search.less'
import '../../gruppe/PersonVisning/PersonVisning.less'
import { AlertStripeInfo } from 'nav-frontend-alertstriper'
import { getSoekOptions, initialValues } from '~/pages/soekMiniNorge/search/utils'
import { Feedback } from '~/components/feedback'
import Button from '~/components/ui/button/Button'
import useBoolean from '~/utils/hooks/useBoolean'
import SearchContainer from '~/components/SearchContainer'

export const Search = () => {
	const [soekOptions, setSoekOptions] = useState('')
	const [isSearchActive, searchIsActive, searchIsNotActive] = useBoolean(false)
	const [isFeedbackShowing, showFeedback, dontShowFeedback] = useBoolean(false)
	const [soekNummer, setSoekNummer] = useState(0)

	const _onSubmit = (values: any) => {
		setSoekOptions(getSoekOptions(values))
		searchIsActive()
		showFeedback()
		setSoekNummer(soekNummer + 1)
	}

	return (
		<>
			<p>
				Syntetiske testdata er tilgjengelig i Dolly gjennom tekniske APIer og Mini-Norge. Mini-Norge
				er NAVs syntetiske basispopulasjon med egenskaper tilsvarende normalen i Norge. Antall
				innbyggere er pr mars 2020 ca 200 000. Befolkningen er dynamisk og det gjøres løpende
				endringer – nye personer fødes, folk skifter jobb osv. Mini-Norge kan brukes av alle og er
				godt egnet til basistester, volumtester m.m. der det ikke er behov for spesialtilfeller.
				Mini-Norge er kun tilgjengelig i Q2 som er et helsyntetisk testmiljø.
				<br />
				<br />
				Beta-versjon for søk i Mini-Norge jobbes med fortløpende.
			</p>
			<div className="search-content">
				<Formik onSubmit={_onSubmit} initialValues={initialValues} enableReinitialize>
					{formikBag => {
						return (
							<div className="search-field">
								<SearchContainer
									onSubmit={() => _onSubmit(formikBag.values)}
									left={<SearchOptions />}
									right={
										<SearchResult
											key={soekNummer}
											soekOptions={soekOptions}
											searchActive={isSearchActive}
											soekNummer={soekNummer}
											antallResultat={_get(formikBag.values, 'antallResultat')}
										/>
									}
								/>
								{isFeedbackShowing && (
									<div className="feedback-container">
										<div className="feedback-container__close-button">
											<Button kind="remove-circle" onClick={() => dontShowFeedback} />
										</div>
										<Feedback
											label="Hvordan var din opplevelse med bruk av Søk i Mini-Norge?"
											feedbackFor="Bruk av Søk i Mini Norge"
										/>
									</div>
								)}
							</div>
						)
					}}
				</Formik>
			</div>
		</>
	)
}
