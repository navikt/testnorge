import { AlertStripeInfo } from 'nav-frontend-alertstriper'
import React from 'react'

const dollySlack = <a href="https://nav-it.slack.com/archives/CA3P9NGA2">#dolly</a>
export const DollyErrorAlert = error => {
	return (
		<AlertStripeInfo type="feil" className="tps-advarsel">
			Noe gikk galt under visning av elementet. Vennligst kontakt Team Dolly på {dollySlack} dersom
			problemet vedvarer etter en refresh
			<br />
			<q>{error.toString()}</q>
		</AlertStripeInfo>
	)
}
