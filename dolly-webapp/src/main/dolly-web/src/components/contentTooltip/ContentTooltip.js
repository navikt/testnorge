import React, { PureComponent, Fragment } from 'react'
import PropTypes from 'prop-types'
import Tooltip from 'rc-tooltip'
import HjelpeTekst from 'nav-frontend-hjelpetekst'
import Icon from '~/components/icon/Icon'
import Button from '~/components/button/Button'

import './ContentTooltip.less'

export default class ContentTooltip extends PureComponent {
	render() {
		//TODO: STØTTE ANDRE TYPER CONTENT I TOOLTIP
		return (
			<HjelpeTekst className="content-tooltip" id="hjelpetekst" type="under-hoyre">
				<Fragment>
					<b>Hvordan fungerer Dolly?</b>
					<br />
					<br />
					Når du trykker på “Testdata”, kommer du som default inn på “Mine” testdatagrupper. Det vil
					si alle testdatagrupper du selv har opprettet eller som du har lagt til som favoritt
					(markert med en stjerne). For å se alle testdatagrupper, trykk på “Alle”. Her kan du søke
					etter en spesifikk testdatagruppe eller se om det er noen som er relevante for deg. Hvis
					du trykker på stjerneikonet, legger du testdatagruppen til som en favoritt. Den vil da
					dukke opp under “Mine” testdatagrupper.
					<br />
					<br />
					Alle testdatagrupper må være tilknyttet til et team. Hvis teamet du skal opprette en
					testdatagruppe for ikke finnes enda, må du først opprette teamet før du kan opprette en ny
					testdatagruppe. Du kan administrere og opprette nye teams ved å trykke på brukeren din
					oppe i høyre hjørne og deretter gå videre til teams.
					<br />
					<br />
					For å opprette en ny testdatagruppe, trykk på den blå legg til-knappen nedenfor. Den vil
					alltid ligge nederst i skjermbildet, uansett hvor mange testdatagrupper som finnes. For å
					finne tilbake til denne informasjonsboksen, trykk på info-ikonet ved siden av overskriften
					“Testdatagrupper”.
				</Fragment>
			</HjelpeTekst>
		)
	}
}
