import React, { PureComponent, Fragment } from 'react'
import SendFoedselsmelding from './SendFoedselsmelding'
import SendDoedsmelding from './SendDoedsmelding'
import './TpsEndring.less'
import { AlertStripeInfo } from 'nav-frontend-alertstriper'
import { dollySlack } from '~/components/dollySlack/dollySlack'

export default class TPSEndring extends PureComponent {
	render() {
		return (
			<Fragment>
				<h1>Send endringsmelding</h1>
				<AlertStripeInfo type="advarsel" className="tps-advarsel">
					Denne siden kommer til å bli fjernet. Det vil fortsatt være mulig å sende fødselsmeldinger
					og dødsmeldinger i Dolly:
					<ul>
						<li>
							<b>Sende fødselsmelding:</b> Gå til testdatagruppe og finn personen som skal være
							forelder. Her finnes to alternativer:
							<ul>
								<li>
									Velg "Legg til/endre", og huk av for "Barn" i første steg. I neste steg kan et
									utvalg av egenskaper velges for barnet.
								</li>
								<li>
									Hvis barnet (og eventuelt den andre forelderen) allerede er opprettet i
									testdatagruppen: Velg "Legg til relasjoner" og legg til barn.
								</li>
							</ul>
						</li>
						<li>
							<b>Sende dødsmelding:</b> Gå til testdatagruppe og finn personen det skal sendes
							dødsmelding på. Velg "Legg til/endre", og huk av for "Dødsdato" i første steg. I neste
							steg kan dødsdatoen settes, før dødsmeldingen sendes som en vanlig bestilling. For å
							sende dødsmelding på personens partner/barn må partner eller barn hukes av i første
							steg. Da vil det være mulig å sette dødsdato på eksisterende relasjoener i steg to.
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
