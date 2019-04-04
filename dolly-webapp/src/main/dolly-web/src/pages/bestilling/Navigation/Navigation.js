import React, { PureComponent } from 'react'
import PropTypes from 'prop-types'
import Knapp from 'nav-frontend-knapper'
import NavButton from '~/components/button/NavButton/NavButton'
import { isPage } from '~/pages/bestilling/Utils'
import BestillingMapper from '~/utils/BestillingMapper'

export default class Navigation extends PureComponent {
	static propTypes = {
		isSubmitting: PropTypes.bool,
		currentPage: PropTypes.number.isRequired,
		abortBestilling: PropTypes.func.isRequired,
		onClickNext: PropTypes.func.isRequired
	}

	static defaultProps = {
		isSubmitting: false
	}

	render() {
		const {
			currentPage,
			isSubmitting,
			onClickNext,
			abortBestilling,
			onClickPrevious,
			FormikProps,
			identOpprettesFra,
			eksisterendeIdentListe
		} = this.props

		const resetBestilling = () => {}

		let harAdresse = false
		let harGyldigAdresse = false

		if (FormikProps)
			if ('boadresse_gateadresse' in FormikProps.values) {
				harAdresse = true
				if (
					FormikProps.values.boadresse_gateadresse &&
					FormikProps.values.boadresse_husnummer &&
					FormikProps.values.boadresse_kommunenr &&
					FormikProps.values.boadresse_postnr
				) {
					harGyldigAdresse = true
				}
			}

		return (
			<div className="step-navknapper">
				<Knapp type="standard" onClick={abortBestilling}>
					AVBRYT
				</Knapp>

				<div className="step-navknapper--right">
					{!isPage.first(currentPage) && (
						<NavButton direction="backward" onClick={onClickPrevious} />
					)}

					{!isPage.last(currentPage) &&
						(identOpprettesFra !== BestillingMapper('EKSIDENT') ||
							(identOpprettesFra === BestillingMapper('EKSIDENT') &&
								eksisterendeIdentListe.length > 0)) &&
						(!harAdresse || (harAdresse && harGyldigAdresse)) && (
							<NavButton direction="forward" onClick={onClickNext} />
						)}

					{!isPage.last(currentPage) &&
						(identOpprettesFra !== BestillingMapper('EKSIDENT') ||
							(identOpprettesFra === BestillingMapper('EKSIDENT') &&
								eksisterendeIdentListe.length > 0)) &&
						harAdresse &&
						!harGyldigAdresse && <NavButton disabled direction="forward" onClick={onClickNext} />}

					{isPage.last(currentPage) && (
						<Knapp type="hoved" onClick={onClickNext} disabled={isSubmitting}>
							OPPRETT
						</Knapp>
					)}
				</div>
			</div>
		)
	}
}
