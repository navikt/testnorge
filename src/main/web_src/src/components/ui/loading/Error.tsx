import { AlertStripeInfo } from 'nav-frontend-alertstriper'
import React from 'react'

const dollySlack = <a href="https://nav-it.slack.com/archives/CA3P9NGA2">#dolly</a>
export const Error = ({ errorMessage }) => {
	console.log(errorMessage)
	console.log(dollySlack)
	return (
		<AlertStripeInfo type="feil" className="tps-advarsel">
			Noe gikk galt under visning av elementet. Vennligst kontakt Team Dolly på
			{dollySlack} dersom problemet vedvarer, med følgende feilmelding: <br />
			{errorMessage}
		</AlertStripeInfo>
	)
}
