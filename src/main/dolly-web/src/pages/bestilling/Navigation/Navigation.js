import React, { PureComponent } from 'react'
import PropTypes from 'prop-types'
import Knapp from 'nav-frontend-knapper'
import NavButton from '~/components/button/NavButton/NavButton'
import { isPage } from '~/pages/bestilling/Utils'

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
			FormikProps
		} = this.props

		// må endres slik at kun adresse-values blir berørt!!
		let propsValues
		let harGyldigAdresse = false

		if (FormikProps) {
			propsValues = FormikProps.values
			console.log('propsValues :', propsValues)
			if (
				propsValues.boadresse_gateadresse &&
				propsValues.boadresse_husnummer &&
				propsValues.boadresse_kommunenr &&
				propsValues.boadresse_postnr
				// lag en prop for å ha valgt gyldig adresse!!
			) {
				harGyldigAdresse = true
			}
		}

		// console.log('this navigation:', this)

		const resetBestilling = () => {}

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
						!propsValues && <NavButton direction="forward" onClick={onClickNext} />}

					{!isPage.last(currentPage) &&
						propsValues &&
						harGyldigAdresse && <NavButton direction="forward" onClick={onClickNext} />}

					{!isPage.last(currentPage) &&
						propsValues &&
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
