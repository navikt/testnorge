import React, { Component } from 'react'
import { AttributtManager } from '~/service/Kodeverk'
import FormEditor from '~/components/formEditor/FormEditor'

export default class RedigerTestbruker extends Component {
	constructor() {
		super()
		this.AttributtManager = new AttributtManager()
		this.AttributtListe = this.AttributtManager.listEditable()
	}

	componentDidMount() {
		this.props.getTestbruker()
	}

	onClickUpdateHandler = () => {
		const { testbruker, updateTestbruker } = this.props
		const updateObject = {
			fornavn: 'NyttNavnSattAvUpdate',
			etternavn: 'etternavnSattPåNytt'
		}

		console.log('før', testbruker)
		const update = Object.assign(testbruker, updateObject)
		console.log('etter', update)
		updateTestbruker(update)
	}

	render() {
		const { testbruker } = this.props

		if (!testbruker) return null

		console.log(this.AttributtListe)

		return (
			<div>
				Rediger
				<button onClick={this.onClickUpdateHandler}>test oppdater navn!</button>
			</div>
		)
	}
}
