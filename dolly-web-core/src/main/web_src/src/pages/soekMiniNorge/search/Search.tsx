import React, { useState } from 'react'
import { SearchOptions } from '~/pages/soekMiniNorge/search/SearchOptions'
import { Formik } from 'formik'
import { SearchResult } from '~/pages/soekMiniNorge/search/SearchResult'
import './Search.less'
import { AlertStripeInfo } from 'nav-frontend-alertstriper'
import { getSoekOptions, initialValues, infoTekst } from '~/pages/soekMiniNorge/search/utils'
import useBoolean from '~/utils/hooks/useBoolean'
import _get from 'lodash/get'

export const Search = () => {
	const [soekOptions, setSoekOptions] = useState('')
	const [isSearchActive, searchIsActive, searchIsNotActive] = useBoolean(false)
	const [soekNummer, setSoekNummer] = useState(0)

	const _onSubmit = (values: any) => {
		setSoekOptions(getSoekOptions(values))
		searchIsActive()
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
										soekOptions={soekOptions}
										searchActive={isSearchActive}
										soekNummer={soekNummer}
										antallResultat={_get(formikBag.values, 'antallResultat')}
									/>
								</div>
							</div>
						</div>
					)
				}}
			</Formik>
		</div>
	)
}