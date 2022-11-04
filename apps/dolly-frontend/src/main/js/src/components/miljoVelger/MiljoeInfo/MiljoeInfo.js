import React from 'react'
import _get from 'lodash/get'
import { ArenaApi, InstApi, PensjonApi } from '~/service/Api'
import TilgjengeligeMiljoer from './TilgjengeligeMiljoer'
import { Alert } from '@navikt/ds-react'

export const MiljoeInfo = ({ bestillingsdata, dollyEnvironments }) => {
	const { instdata, pdldata, arenaforvalter, pensjonforvalter, sykemelding, aareg } =
		bestillingsdata
	if (
		!aareg &&
		!instdata &&
		!arenaforvalter &&
		!pensjonforvalter &&
		!sykemelding &&
		!_get(pdldata, 'bostedsadresse') &&
		!_get(pdldata, 'fullmakt') &&
		!_get(pdldata, 'falskIdentitet') &&
		!_get(pdldata, 'utenlandskIdentifikasjonsnummer') &&
		!_get(pdldata, 'kontaktinformasjonForDoedsbo')
	)
		return null

	return (
		<Alert variant={'info'}>
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

				{pdldata?.person?.fullmakt && <li>Fullmakt: Q2</li>}
				{pdldata?.person?.falskIdentitet && <li>Falsk identitet: Q2</li>}
				{pdldata?.person?.utenlandskIdentifikasjonsnummer && (
					<li>Utenlandsk identifikasjonsnummer: Q2</li>
				)}
				{pdldata?.person?.kontaktinformasjonForDoedsbo && (
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

				{(pensjonforvalter?.inntekt || pensjonforvalter?.tp) && (
					<li>
						Pensjon ({pensjonforvalter?.inntekt && 'POPP'}
						{pensjonforvalter?.inntekt && pensjonforvalter?.tp && ', '}
						{pensjonforvalter?.tp && 'TP'}
						):&nbsp;
						<TilgjengeligeMiljoer
							endepunkt={PensjonApi.getTilgjengeligeMiljoer}
							dollyEnvironments={dollyEnvironments}
						/>
					</li>
				)}

				{sykemelding && <li>Sykemelding: Q1 må velges</li>}
				{aareg && (
					<li>Arbeidsforhold på felles organisasjoner: Alle arbeidsforhold blir sendt til Q2</li>
				)}
			</ul>
		</Alert>
	)
}
