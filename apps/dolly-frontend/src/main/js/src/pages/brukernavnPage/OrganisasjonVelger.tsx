import React from 'react'
import NavButton from '~/components/ui/button/NavButton/NavButton'
import { DollyFieldArrayWrapper } from '~/components/ui/form/fieldArray/DollyFieldArray'
import { ErrorBoundary } from '~/components/ui/appError/ErrorBoundary'
import { Organisasjon } from '~/pages/brukernavnPage/BrukernavnModel'

type OrganisasjonVelgerProps = {
	orgdata: Organisasjon[]
	onClick: (org: Organisasjon) => void
}

export default ({ orgdata, onClick }: OrganisasjonVelgerProps) => {
	return (
		<React.Fragment>
			<h3>
				Du er nå logget inn med BankID. Velg hvilken organisasjon du representerer for å ta i bruk
				Dolly selvbetjeningsløsing.
			</h3>

			<ErrorBoundary>
				<DollyFieldArrayWrapper>
					{orgdata.map((org: Organisasjon, idx: number) => {
						return (
							<React.Fragment key={idx}>
								<NavButton className="org-button" onClick={() => onClick(org)}>
									{org.navn}
								</NavButton>
							</React.Fragment>
						)
					})}
				</DollyFieldArrayWrapper>
			</ErrorBoundary>
		</React.Fragment>
	)
}
