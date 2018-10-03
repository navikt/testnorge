import React, { PureComponent, Fragment } from 'react'
import Overskrift from '~/components/overskrift/Overskrift'
import './TpsEndring.less'
import { EnvironmentManager } from '~/service/Kodeverk'
import SendFoedselsmelding from './SendFoedselsmelding'
import SendDoedsmelding from './SendDoedsmelding'

export default class TPSEndring extends PureComponent {
	constructor() {
		super()
		this.evns = new EnvironmentManager().getAllDropdownEnvironments()
	}

	render() {
		return (
			<Fragment>
				<Overskrift label={'Send TPS-endringsmelding'} />
				<SendFoedselsmelding dropdownMiljoe={this.evns} />
				<SendDoedsmelding dropdownMiljoe={this.evns} />
			</Fragment>
		)
	}
}
