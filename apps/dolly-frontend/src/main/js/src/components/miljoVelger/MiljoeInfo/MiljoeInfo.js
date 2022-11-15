import React from 'react'
import _get from 'lodash/get'
import { InstApi } from '~/service/Api'
import TilgjengeligeMiljoer from './TilgjengeligeMiljoer'
import { Alert } from '@navikt/ds-react'
import { useArenaEnvironments, usePensjonEnvironments } from '~/utils/hooks/useEnvironments'
import Formatters from '~/utils/DataFormatter'

export const MiljoeInfo = ({ bestillingsdata, dollyEnvironments }) => {
	const { arenaEnvironments, arenaLoading } = useArenaEnvironments()
	const { pensjonEnvironments, pensjonLoading } = usePensjonEnvironments()
	const { instdata, pdldata, arenaforvalter, pensjonforvalter, sykemelding } = bestillingsdata
	if (
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
						<span>
							{arenaLoading
								? 'Laster tilgjengelige miljøer..'
								: Formatters.arrayToString(arenaEnvironments)}
						</span>
					</li>
				)}

				{(pensjonforvalter?.inntekt || pensjonforvalter?.tp) && (
					<li>
						Pensjon ({pensjonforvalter?.inntekt && 'POPP'}
						{pensjonforvalter?.inntekt && pensjonforvalter?.tp && ', '}
						{pensjonforvalter?.tp && 'TP'}
						):&nbsp;
						<span>
							{pensjonLoading
								? 'Laster tilgjengelige miljøer..'
								: Formatters.arrayToString(pensjonEnvironments)}
						</span>
					</li>
				)}

				{sykemelding && <li>Sykemelding: Q1 må velges</li>}
			</ul>
		</Alert>
	)
}
