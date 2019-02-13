import React, { PureComponent, Fragment } from 'react'
import PropTypes from 'prop-types'
import PersonInfoBlock from '~/components/personInfoBlock/PersonInfoBlock'
import AttributtManager from '~/service/kodeverk/AttributtManager/AttributtManager'
import Button from '~/components/button/Button'
import ConfirmTooltip from '~/components/confirmTooltip/ConfirmTooltip'
import Loading from '~/components/loading/Loading'
import Panel from '~/components/panel/Panel'

import './PersonDetaljer.less'

const AttributtManagerInstance = new AttributtManager()

export default class PersonDetaljer extends PureComponent {
	static propTypes = {
		editAction: PropTypes.func
	}

	componentDidMount() {
		this.props.getSigrunTestbruker()
		this.props.getKrrTestbruker()
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

		return (
			<div className="person-details">
				{personData.map((i, idx) => {
					if (i.data.length < 0) return null
					if (i.data[0].id == 'bestillingID') {
						return (
							<div key={idx} className="panel panel--border">
								<h4 className="panel-bestilling-id">{i.header}</h4>
								{this._renderPersonInfoBlockHandler(i)}
							</div>
						)
					}
					return (
						<div key={idx} className="person-details_content">
							<h3>{i.header}</h3>
							{this._renderPersonInfoBlockHandler(i)}
						</div>
					)
				})}
				<div className="flexbox--align-center--justify-end">
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
}
