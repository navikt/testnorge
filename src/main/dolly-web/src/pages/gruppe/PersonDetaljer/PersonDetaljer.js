import React, { PureComponent, Fragment } from 'react'
import PropTypes from 'prop-types'
import PersonInfoBlock from '~/components/personInfoBlock/PersonInfoBlock'
import AttributtManager from '~/service/kodeverk/AttributtManager/AttributtManager'
import Button from '~/components/button/Button'
import ConfirmTooltip from '~/components/confirmTooltip/ConfirmTooltip'

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

	render() {
		const { personData, editAction, frigjoerTestbruker } = this.props

		if (!personData) return null

		return (
			<div className="person-details">
				{personData.map((i, idx) => {
					if (i.data.length < 0) return null

					return (
						<div key={idx} className="person-details_content">
							<h3>{i.header}</h3>
							<PersonInfoBlock
								data={i.data}
								multiple={i.multiple}
								attributtManager={AttributtManagerInstance}
							/>
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
