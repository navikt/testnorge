import React from 'react'
import { AlertStripeInfo } from 'nav-frontend-alertstriper'
import TilgjengeligeMiljoer from '~/components/tilgjengeligeMiljoer/TilgjengeligeMiljoer'
import { InstApi } from '~/service/Api'
import { ArenaApi } from '~/service/Api'

export const MiljoeInfo = ({ formikBag }) => {
	const { instdata, pdlforvalter, arenaforvalter } = formikBag.values
	if (!instdata && !pdlforvalter && !arenaforvalter) return null

	return (
		<AlertStripeInfo>
			Du har valgt attributter som ikke blir distribuert til alle miljøer. For hvert av følgende
			attributter må derfor ett eller flere av miljøene under velges:
			<ul>
				{instdata && (
					<li>
						Institusjonsopphold:&nbsp;
						<TilgjengeligeMiljoer endepunkt={InstApi.getTilgjengeligeMiljoer} />
					</li>
				)}
				{pdlforvalter && <li>Kontaktinformasjon for dødsbo: Q2</li>}
				{arenaforvalter && (
					<li>
						Arena:&nbsp;
						<TilgjengeligeMiljoer endepunkt={ArenaApi.getTilgjengeligeMiljoer} />
					</li>
				)}
			</ul>
		</AlertStripeInfo>
	)
}
