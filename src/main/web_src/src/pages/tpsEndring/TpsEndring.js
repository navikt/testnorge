import React, { PureComponent, Fragment } from 'react'
import SendFoedselsmelding from './SendFoedselsmelding'
import SendDoedsmelding from './SendDoedsmelding'
import './TpsEndring.less'
import { AlertStripeInfo } from 'nav-frontend-alertstriper'

export default class TPSEndring extends PureComponent {
	render() {
		const dollySlack = <a href="https://nav-it.slack.com/archives/CA3P9NGA2">#dolly</a>

		return (
			<Fragment>
				<h1>Send endringsmelding</h1>
				<AlertStripeInfo type="advarsel" className="tps-advarsel">
					Denne siden kommer til å bli fjernet. Det vil fortsatt være mulig å sende fødselsmeldinger
					og dødsmeldinger i Dolly:
					<ul>
						<li>
							<b>Sende fødselsmelding:</b> Gå til testdatagruppe og finn personen som skal være
							forelder. Velg "Legg til relasjoner" og legg til barn.
						</li>
						<li>
							<b>Sende dødsmelding:</b> Gå til testdatagruppe og finn personen det skal sendes
							dødsmelding på. Velg "Legg til", og huk av for "Dødsdato" i første steg. I neste steg
							kan dødsdatoen settes, før dødsmeldingen sendes som en vanlig bestilling.
						</li>
						<li>
							For personer som ikke eksisterer i Dolly må disse først hentes inn ved å opprette
							person, velge "Eksisterende person" og skrive inn ident.
						</li>
					</ul>
					Kontakt oss på {dollySlack} dersom du har noen spørsmål.
				</AlertStripeInfo>
				<SendFoedselsmelding />
				<SendDoedsmelding />
			</Fragment>
		)
	}
}
