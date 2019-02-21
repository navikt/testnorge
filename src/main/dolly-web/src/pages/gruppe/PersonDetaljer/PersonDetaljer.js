import React, { PureComponent, Fragment } from 'react'
import PropTypes from 'prop-types'
import PersonInfoBlock from '~/components/personInfoBlock/PersonInfoBlock'
import AttributtManager from '~/service/kodeverk/AttributtManager/AttributtManager'
import Button from '~/components/button/Button'
import ConfirmTooltip from '~/components/confirmTooltip/ConfirmTooltip'
import Loading from '~/components/loading/Loading'
import './PersonDetaljer.less'
import { mapBestillingData } from '~/pages/gruppe/BestillingListe/BestillingDetaljer/BestillingDataMapper'
import cn from 'classnames'
import StaticValue from '~/components/fields/StaticValue/StaticValue'
import DollyModal from '~/components/modal/DollyModal'

const AttributtManagerInstance = new AttributtManager()

// const customStyles = {
// 	content: {
// 		top: '50%',
// 		left: '50%',
// 		right: 'auto',
// 		bottom: 'auto',
// 		marginRight: '-50%',
// 		transform: 'translate(-50%, -50%)',
// 		width: '60%',
// 		minWidth: '500px',
// 		overflow: 'inherit'
// 	}
// }

export default class PersonDetaljer extends PureComponent {
	constructor(props) {
		super(props)
		this.state = {
			modalOpen: false
		}
	}

	static propTypes = {
		editAction: PropTypes.func
	}
	componentDidMount() {
		this.props.getSigrunTestbruker()
		this.props.getKrrTestbruker()
	}
	openModal = () => {
		this.setState({ modalOpen: true })
	}
	closeModal = () => {
		this.setState({ modalOpen: false })
	}

	// render loading for krr og sigrun
	_renderPersonInfoBlockHandler = i => {
		const { isFetchingKrr, isFetchingSigrun } = this.props
		if (i.header === 'Inntekter') {
			return isFetchingSigrun ? (
				<Loading label="Henter data fra Sigrun-stub" panel />
			) : (
				this._renderPersonInfoBlock(i)
			)
		} else if (i.header === 'Kontaktinformasjon og reservasjon') {
			return isFetchingKrr ? (
				<Loading label="Henter data fra Krr" panel />
			) : (
				this._renderPersonInfoBlock(i)
			)
		} else {
			return this._renderPersonInfoBlock(i)
		}
	}

	_renderPersonInfoBlock = i => (
		<PersonInfoBlock
			data={i.data}
			multiple={i.multiple}
			attributtManager={AttributtManagerInstance}
		/>
	)

	render() {
		const { personData, editAction, frigjoerTestbruker } = this.props
		const { modalOpen } = this.state

		if (!personData) return null
		return (
			<div className="person-details">
				{personData.map((i, idx) => {
					if (i.data.length < 0) return null
					return (
						<div key={idx} className="person-details_content">
							<h3>{i.header}</h3>
							{this._renderPersonInfoBlockHandler(i)}
						</div>
					)
				})}
				<div className="flexbox--align-center--justify-end">
					<Button onClick={this.openModal} className="flexbox--align-center" kind="details">
						BESTILLINGSDETALJER
					</Button>
					<DollyModal
						isOpen={modalOpen}
						onRequestClose={this.closeModal}
						closeModal={this.closeModal}
						content={this._renderBestillingModal()}
						width={'60%'}
					/>
					<Button onClick={editAction} className="flexbox--align-center" kind="edit">
						REDIGER
					</Button>
					<ConfirmTooltip
						className="flexbox--align-center"
						message="Er du sikker på at du vil frigjøre denne testidenten fra testdatagruppen?"
						label="FRIGJØR"
						onClick={frigjoerTestbruker}
					/>
				</div>
			</div>
		)
	}

	_renderBestillingModal = () => {
		const ident = this.props.bestillingId
		const { bestillinger } = this.props
		const bestilling = bestillinger.data.find(i => i.id == ident)
		const data = mapBestillingData(bestilling)

		return (
			<Fragment>
				<div className="dollymodal" style={{ paddingLeft: 20, paddingRight: 20 }}>
					<h1>Bestilling #{ident}</h1>
					<div className={'bestilling-modal'}>
						{data ? (
							data.map((kategori, j) => {
								const bottomBorder = j != data.length - 1
								const cssClass = cn('flexbox--align-center bestilling-details', {
									'bottom-border': bottomBorder
								})
								if (kategori.header) {
									return (
										<Fragment key={j}>
											<h4>{kategori.header} </h4>
											<div className={cssClass}>
												{kategori.items.map((attributt, i) => {
													if (attributt.value) {
														return (
															<StaticValue
																header={attributt.label}
																size="small"
																value={attributt.value}
																key={i}
															/>
														)
													}
												})}
											</div>
										</Fragment>
									)
								}
							})
						) : (
							<p>Kunne ikke hente bestillingsdata</p>
						)}
					</div>
				</div>
			</Fragment>
		)
	}
}
