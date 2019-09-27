import React, { Fragment } from 'react'
import Header from '~/components/bestilling/sammendrag/header/Header'
import Systemfeil from './Systemfeil'
import Fagsystemfeil from './Fagsystemfeil'

import './Feilmelding.less'

const mapStatusFeil = bestillingstatus => {
	return bestillingstatus.reduce((acc, curr) => {
		return acc.concat(
			curr.statuser.filter(status => status.melding !== 'OK').map(status => {
				const feil = {
					navn: curr.navn,
					melding: status.melding
				}

				if (status.identer) {
					feil.identer = [
						{
							miljo: null,
							identer: status.identer
						}
					]
				}

				if (status.detaljert) {
					feil.identer = status.detaljert
				}

				return feil
			})
		)
	}, [])
}

export default function Feilmelding(props) {
	const { bestilling } = props
	const fagsystemFeil = mapStatusFeil(bestilling.status)

	if (fagsystemFeil.length === 0 && !bestilling.feil) return false

	return (
		<Fragment>
			<Header iconKind="report-problem-triangle" label="Feilmeldinger" />
			<Systemfeil bestilling={bestilling} />
			<Fagsystemfeil fagsystemFeil={fagsystemFeil} />
		</Fragment>
	)
}
