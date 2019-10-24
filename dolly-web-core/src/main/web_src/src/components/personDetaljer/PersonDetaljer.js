// import React from 'react'
import React, { PureComponent } from 'react'
import TpsfVisning from '~/components/fagsystem/tpsf/visning/TpsfVisning'
import '~/pages/gruppe/PersonDetaljer/PersonDetaljer.less' // flytte denne

// export default function PersonDetaljer() {
export default class PersonDetaljer extends PureComponent {
	// ikke bruk denne, bruk function!!!
	constructor(props) {
		super(props)

		console.log('this.props :', this.props)
	}
	// ikke render i function component!!
	render() {
		return (
			<div className="person-details_content">
				<TpsfVisning data={this.props.personData} />
				{/* <ArenaVisning /> */}
			</div>
		)
	}
}
