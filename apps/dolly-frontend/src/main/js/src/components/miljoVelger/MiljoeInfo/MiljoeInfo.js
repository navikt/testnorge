import React from 'react'
import _get from 'lodash/get'
import { AlertStripeInfo } from 'nav-frontend-alertstriper'
import { ArenaApi, InstApi, PensjonApi } from '~/service/Api'
import TilgjengeligeMiljoer from './TilgjengeligeMiljoer'

export const MiljoeInfo = ({ bestillingsdata, dollyEnvironments }) => {
	const {
		instdata,
		pdlforvalter,
		pdldata,
		arenaforvalter,
		pensjonforvalter,
		sykemelding,
		udistub,
	} = bestillingsdata
	if (
		!instdata &&
		!pdlforvalter &&
		!pdldata &&
		!arenaforvalter &&
		!pensjonforvalter &&
		!sykemelding &&
		!_get(udistub, 'oppholdStatus')
	)
		return null

	return (
		<AlertStripeInfo>
			Du har valgt egenskaper som ikke blir distribuert til alle miljøer. For hver av følgende
			egenskaper må derfor ett eller flere av miljøene under velges:
			<ul>
				{instdata && (
					<li>
						Institusjonsopphold:&nbsp;
						<TilgjengeligeMiljoer
							endepunkt={InstApi.getTilgjengeligeMiljoer}
							dollyEnvironments={dollyEnvironments}
						/>
					</li>
				)}

				{pdldata && pdldata.person?.bostedsadresse && <li>Utenlandsk boadresse: Q2</li>}
				{pdldata && pdldata.person?.fullmakt && <li>Fullmakt: Q2</li>}
				{pdldata && pdldata.person?.falskIdentitet && <li>Falsk identitet: Q2</li>}
				{pdldata && pdldata.person?.utenlandskIdentifikasjonsnummer && (
					<li>Utenlandsk identifikasjonsnummer: Q2</li>
				)}
				{pdlforvalter && pdlforvalter.kontaktinformasjonForDoedsbo && (
					<li>Kontaktinformasjon for dødsbo: Q2</li>
				)}

				{arenaforvalter && (
					<li>
						Arena:&nbsp;
						<TilgjengeligeMiljoer
							endepunkt={ArenaApi.getTilgjengeligeMiljoer}
							dollyEnvironments={dollyEnvironments}
						/>
					</li>
				)}

				{pensjonforvalter && (
					<li>
						POPP:&nbsp;
						<TilgjengeligeMiljoer
							endepunkt={PensjonApi.getTilgjengeligeMiljoer}
							dollyEnvironments={dollyEnvironments}
						/>
					</li>
				)}

				{sykemelding && <li>Sykemelding: Både Q1 og Q2 må velges</li>}

				{udistub && udistub.oppholdStatus && (
					<li>
						Oppholdsstatus i PDL: Q1, Q2 (Dersom du kun ønsker å sende oppholdsstatus til UDI-stub
						er det ingen krav til valg av miljø.)
					</li>
				)}
			</ul>
		</AlertStripeInfo>
	)
}
