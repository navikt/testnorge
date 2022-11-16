import React from 'react'
import _get from 'lodash/get'
import { Alert } from '@navikt/ds-react'
import {
	useArenaEnvironments,
	usePensjonEnvironments,
	useInstEnvironments,
} from '~/utils/hooks/useEnvironments'
import Formatters from '~/utils/DataFormatter'
import { filterMiljoe } from '~/components/miljoVelger/MiljoeInfo/TilgjengeligeMiljoer'

export const MiljoeInfo = ({ bestillingsdata, dollyEnvironments }) => {
	const { arenaEnvironments, loading: loadingArena, error: errorArena } = useArenaEnvironments()
	const {
		pensjonEnvironments,
		loading: loadingPensjon,
		error: errorPensjon,
	} = usePensjonEnvironments()
	const { instEnvironments, loading: loadingInst, error: errorInst } = useInstEnvironments()
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
	) {
		return null
	}

	const getMiljoer = (environments, loading, error) => {
		if (loading) {
			return 'Laster tilgjengelige miljøer..'
		} else if (error && (!environments || environments.length === 0)) {
			return 'Noe gikk galt i henting av miljøer'
		} else {
			return Formatters.arrayToString(filterMiljoe(dollyEnvironments, environments))
		}
	}

	return (
		<Alert variant={'info'}>
			Du har valgt egenskaper som ikke blir distribuert til alle miljøer. For hver av følgende
			egenskaper må derfor ett eller flere av miljøene under velges:
			<ul>
				{instdata && (
					<li>
						Institusjonsopphold:&nbsp;
						<span>{getMiljoer(instEnvironments, loadingInst, errorInst)}</span>
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
						<span>{getMiljoer(arenaEnvironments, loadingArena, errorArena)}</span>
					</li>
				)}

				{(pensjonforvalter?.inntekt || pensjonforvalter?.tp) && (
					<li>
						Pensjon ({pensjonforvalter?.inntekt && 'POPP'}
						{pensjonforvalter?.inntekt && pensjonforvalter?.tp && ', '}
						{pensjonforvalter?.tp && 'TP'}
						):&nbsp;
						<span>{getMiljoer(pensjonEnvironments, loadingPensjon, errorPensjon)}</span>
					</li>
				)}

				{sykemelding && <li>Sykemelding: Q1 må velges</li>}
			</ul>
		</Alert>
	)
}
