import React, { useState } from 'react'
import _get from 'lodash/get'
import { SearchOptions } from '~/pages/soekMiniNorge/search/SearchOptions'
import { Formik } from 'formik'
import { SearchResult } from '~/pages/soekMiniNorge/search/SearchResult'
import './Search.less'
import '../../gruppe/PersonVisning/PersonVisning.less'
import { AlertStripeInfo } from 'nav-frontend-alertstriper'
import { getSoekOptions, initialValues, infoTekst } from '~/pages/soekMiniNorge/search/utils'
import { Feedback } from '~/components/feedback'
import Button from '~/components/ui/button/Button'
import useBoolean from '~/utils/hooks/useBoolean'

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
		<div className="search-content">
			<Formik onSubmit={_onSubmit} initialValues={initialValues} enableReinitialize>
				{formikBag => {
					return (
						<div className="search-field">
							<AlertStripeInfo>{infoTekst}</AlertStripeInfo>
							<div className="flexbox">
								<div className="search-field__options-container">
									<SearchOptions formikBag={formikBag} onSubmit={_onSubmit} />
								</div>
								<div className="search-field__resultat">
									<SearchResult
										key={soekNummer}
										soekOptions={soekOptions}
										searchActive={isSearchActive}
										soekNummer={soekNummer}
										antallResultat={_get(formikBag.values, 'antallResultat')}
									/>
								</div>
							</div>
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
	)
}
