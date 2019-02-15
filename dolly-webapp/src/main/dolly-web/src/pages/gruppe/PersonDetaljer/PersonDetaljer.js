import React, { PureComponent, Fragment } from 'react'
import PropTypes from 'prop-types'
import PersonInfoBlock from '~/components/personInfoBlock/PersonInfoBlock'
import AttributtManager from '~/service/kodeverk/AttributtManager/AttributtManager'
import Button from '~/components/button/Button'
import Modal from 'react-modal'
import ConfirmTooltip from '~/components/confirmTooltip/ConfirmTooltip'
import Loading from '~/components/loading/Loading'
import './PersonDetaljer.less'
// import { mapBestillingData } from '~/pages/gruppe/BestillingListe/BestillingDetaljer/BestillingDataMapper'

const AttributtManagerInstance = new AttributtManager()

const customStyles = {
	content: {
		top: '50%',
		left: '50%',
		right: 'auto',
		bottom: 'auto',
		marginRight: '-50%',
		transform: 'translate(-50%, -50%)',
		width: '25%',
		minWidth: '500px',
		overflow: 'inherit'
	}
}

Modal.setAppElement('#root')

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
		// if (this.props.testidenter && this.props.testidenter.length) {
		// 	this.props.getTPSFTestbrukere()
		// }
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
		if (!personData) return null
		// console.log('this.props :', this.props)
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
					<Button onClick={this._onToggleModal} className="flexbox--align-center">
						{/* kind="file-new" */}
						BESTILLINGSDETALJER
					</Button>
					{this._renderModal()}

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

	_renderModal = () => {
		// console.log('RENDER MODAL')
		// console.log('state.bestillingStatuser :', state.bestillingStatuser)
		// console.log('this.props :', this.props)
		// const { bestilling } = this.props
		// const data = mapBestillingData(bestilling)
		// console.log('bestilling :', bestilling)
		// console.log('data :', data)
		// const { personData } = this.props

		return (
			<Modal
				isOpen={this.state.modalOpen}
				onRequestClose={this._onToggleModal}
				shouldCloseOnEsc
				style={customStyles}
			>
				<div className="openam-modal" style={{ paddingLeft: 20, paddingRight: 20 }}>
					<h1>Bestillingsdetaljer</h1>
				</div>
			</Modal>
		)
	}

	_onToggleModal = () => {
		// console.log('klikket!')
		this.setState({ modalOpen: !this.state.modalOpen })
	}
}
