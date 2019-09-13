import React, { PureComponent, Fragment } from 'react'
import PropTypes from 'prop-types'
import PersonInfoBlock from '~/components/personInfoBlock/PersonInfoBlock'
import AttributtManager from '~/service/kodeverk/AttributtManager/AttributtManager'
import Button from '~/components/button/Button'
import ConfirmTooltip from '~/components/confirmTooltip/ConfirmTooltip'
import Loading from '~/components/loading/Loading'
import './PersonDetaljer.less'
import DollyModal from '~/components/modal/DollyModal'
import BestillingSammendrag from '~/components/bestilling/sammendrag/Sammendrag'
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
		// TODO Tilpass denne når vi får tilbake udistub-status fra backend:
		// this.props.testIdent.udistubStatus === 'OK' &&
		this.props.getUdiTestbruker()
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
						content={<BestillingSammendrag bestilling={bestilling} modal />}
						width={'60%'}
					/>
					<Button onClick={editAction} className="flexbox--align-center" kind="edit">
						REDIGER
					</Button>

					{this.props.isFrigjoering ? (
						<Loading label="Sletter testbruker" panel />
					) : (
						<ConfirmTooltip
							className="flexbox--align-center"
							message="Er du sikker på at du vil slette denne testidenten?"
							label="SLETT"
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
	_renderPersonInfoBlockHandler = item => {
		const { header } = item
		const {
			isFetchingKrr,
			isFetchingSigrun,
			isFetchingAareg,
			isFetchingArena,
			isFetchingInst,
			isFetchingUdi
		} = this.props

		if (isFetchingSigrun && header === 'Inntekter') {
			return <Loading label="Henter data fra Sigrun-stub" panel />
		} else if (isFetchingKrr && header === 'Kontaktinformasjon og reservasjon') {
			return <Loading label="Henter data fra Krr" panel />
		} else if (isFetchingAareg && header === 'Arbeidsforhold') {
			return <Loading label="Henter data fra Aareg" panel />
		} else if (isFetchingArena && header === 'Arena') {
			return <Loading label="Henter data fra Arena" panel />
		} else if (isFetchingInst && header === 'Institusjonsopphold') {
			return <Loading label="Henter data fra Inst" panel />
		} else if (isFetchingUdi && header === 'UDI') {
			return <Loading label="Henter data fra UDI-stub" panel />
		}
		return this._renderPersonInfoBlock(item)
	}

	_renderPersonInfoBlock = i => (
		<PersonInfoBlock
			data={i.data}
			multiple={i.multiple}
			attributtManager={AttributtManagerInstance}
		/>
	)
}
