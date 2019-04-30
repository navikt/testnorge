import React, { PureComponent } from 'react'
import PropTypes from 'prop-types'
import Knapp from 'nav-frontend-knapper'
import NavButton from '~/components/button/NavButton/NavButton'
import { isPage } from '~/pages/bestilling/Utils'
import BestillingMapper from '~/utils/BestillingMapper'
import PostadresseSjekk from '~/utils/SjekkPostadresse'

export default class Navigation extends PureComponent {
	constructor(props) {
		super(props)
		this._isMounted = false
		this._harBoAdresse = false
		this._harPostAdresse = false
	}

	state = {
		harGyldigBoAdresse: false,
		harGyldigPostAdresse: false
	}

	static propTypes = {
		isSubmitting: PropTypes.bool,
		currentPage: PropTypes.number.isRequired,
		abortBestilling: PropTypes.func.isRequired,
		onClickNext: PropTypes.func.isRequired
	}

	static defaultProps = {
		isSubmitting: false
	}

	componentDidMount() {
		this._isMounted = true
	}

	componentWillUnmount() {
		this._isMounted = false
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
			// values
		} = this.props

		const resetBestilling = () => {}
		var videreKnapp = <NavButton direction="forward" onClick={onClickNext} />

		if (FormikProps) {
			if ('boadresse_gateadresse' in FormikProps.values) {
				this._harBoAdresse = true
				if (
					FormikProps.values.boadresse_gateadresse &&
					FormikProps.values.boadresse_husnummer &&
					FormikProps.values.boadresse_kommunenr &&
					FormikProps.values.boadresse_postnr
				) {
					this.setState({ harGyldigBoAdresse: true })
				} else this.setState({ harGyldigBoAdresse: false })
			}
			if ('postLand' in FormikProps.values) {
				this._harPostAdresse = true
				this._sjekkGyldigAdresse(FormikProps.values)
			}
		}

		if (
			(this._harBoAdresse && !this.state.harGyldigBoAdresse) ||
			(this._harPostAdresse && !this.state.harGyldigPostAdresse)
		) {
			videreKnapp = <NavButton disabled direction="forward" onClick={onClickNext} />
		} else videreKnapp = <NavButton direction="forward" onClick={onClickNext} />

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
								eksisterendeIdentListe.length > 0)) && <span>{videreKnapp} </span>}
					{isPage.last(currentPage) && (
						<Knapp type="hoved" onClick={onClickNext} disabled={isSubmitting}>
							OPPRETT
						</Knapp>
					)}
				</div>
			</div>
		)
	}

	_sjekkGyldigAdresse = async values => {
		if ((await PostadresseSjekk.sjekkPostadresse(values)) === true) {
			if (this._isMounted) {
				this.setState({ harGyldigPostAdresse: true })
			}
		} else {
			if (this._isMounted) {
				this.setState({ harGyldigPostAdresse: false })
			}
		}
	}
}
