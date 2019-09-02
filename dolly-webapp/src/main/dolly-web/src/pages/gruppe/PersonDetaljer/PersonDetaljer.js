import React, { PureComponent, Fragment } from 'react'
import PropTypes from 'prop-types'
import PersonInfoBlock from '~/components/personInfoBlock/PersonInfoBlock'
import AttributtManager from '~/service/kodeverk/AttributtManager/AttributtManager'
import Button from '~/components/button/Button'
import ConfirmTooltip from '~/components/confirmTooltip/ConfirmTooltip'
import Loading from '~/components/loading/Loading'
import './PersonDetaljer.less'
import DollyModal from '~/components/modal/DollyModal'
import BestillingDetaljerSammendrag from '~/components/bestillingDetaljerSammendrag/BestillingDetaljerSammendrag'
import { getSuccessEnv, getPdlforvalterStatusOK } from '~/ducks/bestillingStatus/utils'
import ContentTooltip from '~/components/contentTooltip/ContentTooltip'

const AttributtManagerInstance = new AttributtManager()

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
		this.props.testIdent.sigrunstubStatus === 'OK' && this.props.getSigrunTestbruker()
		this.props.testIdent.sigrunstubStatus === 'OK' && this.props.getSigrunSekvensnr()
		this.props.testIdent.krrstubStatus === 'OK' && this.props.getKrrTestbruker()
		this.props.testIdent.pdlforvalterStatus &&
			getPdlforvalterStatusOK(this.props.testIdent.pdlforvalterStatus) &&
			this.props.getPdlfTestbruker()
		this.props.testIdent.arenaforvalterStatus && this.props.getArenaTestbruker()

		const aaregSuccessEnvs = getSuccessEnv(this.props.testIdent.aaregStatus)
		aaregSuccessEnvs.length > 0 && this.props.getAaregTestbruker(aaregSuccessEnvs[0])

		const instSuccessEnvs = getSuccessEnv(this.props.testIdent.instdataStatus)
		instSuccessEnvs.length > 0 && this.props.getInstTestbruker(instSuccessEnvs[0])
	}

	render() {
		const { personData, editAction, frigjoerTestbruker, bestilling } = this.props
		const { modalOpen } = this.state
		if (!personData) return null
		return (
			<div className="person-details">
				{personData.map((i, idx) => {
					if (i === null) return null
					if (i === undefined) return null
					if (i.data.length < 0) return null
					if (i.data.length > 0) {
						if (i.data[0].id == 'bestillingID') {
							return (
								<div key={idx} className="tidligere-bestilling-panel">
									<h4>{i.header}</h4>
									<div>{i.data[0].value}</div>
								</div>
							)
						} else {
							return (
								<div key={idx} className="person-details_content">
									<h3 className="flexbox--align-center">
										{i.header}
										{i.informasjonstekst && (
											<ContentTooltip hideText>{i.informasjonstekst} </ContentTooltip>
										)}
									</h3>

									{this._renderPersonInfoBlockHandler(i)}
								</div>
							)
						}
					}
				})}
				<div className="flexbox--align-center--justify-end">
					<Button onClick={this.openModal} className="flexbox--align-center" kind="details">
						BESTILLINGSDETALJER
					</Button>
					<DollyModal
						isOpen={modalOpen}
						onRequestClose={this.closeModal}
						closeModal={this.closeModal}
						content={<BestillingDetaljerSammendrag bestilling={bestilling} type="modal" />}
						width={'60%'}
					/>
					<Button onClick={editAction} className="flexbox--align-center" kind="edit">
						REDIGER
					</Button>

					{this.props.isFrigjoering ? (
						<Loading label="Frigjør testbruker ..." panel />
					) : (
						<ConfirmTooltip
							className="flexbox--align-center"
							message="Er du sikker på at du vil frigjøre denne testidenten fra testdatagruppen?"
							label="FRIGJØR"
							onClick={frigjoerTestbruker}
						/>
					)}
				</div>
			</div>
		)
	}

	openModal = () => {
		this.setState({ modalOpen: true })
	}

	closeModal = () => {
		this.setState({ modalOpen: false })
	}

	// render loading for krr og sigrun
	_renderPersonInfoBlockHandler = i => {
		const {
			isFetchingKrr,
			isFetchingSigrun,
			isFetchingAareg,
			isFetchingArena,
			isFetchingInst
		} = this.props
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
		} else if (i.header === 'Arbeidsforhold') {
			return isFetchingAareg ? (
				<Loading label="Henter data fra Aareg" panel />
			) : (
				this._renderPersonInfoBlock(i)
			)
		} else if (i.header === 'Arena') {
			return isFetchingArena ? (
				<Loading label="Henter data fra Arena" panel />
			) : (
				this._renderPersonInfoBlock(i)
			)
		} else if (i.header === 'Institusjonsopphold') {
			return isFetchingInst ? (
				<Loading label="Henter data fra Inst" panel />
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
}
