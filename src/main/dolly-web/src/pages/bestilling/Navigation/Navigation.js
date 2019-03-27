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
		const { currentPage, 
				isSubmitting, 
				onClickNext, 
				abortBestilling, 
				onClickPrevious,
				identOpprettesFra,
				eksisterendeIdentListe } = this.props

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
						(identOpprettesFra !== BestillingMapper('EKSIDENT') || 
						(identOpprettesFra === BestillingMapper('EKSIDENT') && eksisterendeIdentListe.length > 0)) &&
							<NavButton direction="forward" onClick={onClickNext} />}

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
